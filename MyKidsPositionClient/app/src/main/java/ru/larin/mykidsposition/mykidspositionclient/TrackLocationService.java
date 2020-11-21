package ru.larin.mykidsposition.mykidspositionclient;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ru.larin.mykidsposition.mykidspositionclient.Accuracy.*;

public class TrackLocationService extends Service {
    private static final String SERVER_URL = "http://mlarin.no-ip.org:8090/mkp/api.php";
    private static final String LOG_CATEGORY = TrackLocationService.class.getSimpleName();
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private FusedLocationProviderClient locationClient;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int batteryLevel;
    private LocalBinder binder = new LocalBinder();
    private LocationCallback locationCallback;
    private boolean started;
    private BroadcastReceiver batteryLevelReceiver;

    public TrackLocationService() {
    }

    public class LocalBinder extends Binder {
        TrackLocationService getService() {
            // Return this instance of TrackLocationService so clients can call public methods
            return TrackLocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_CATEGORY, "Create service");
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationChacgeListener();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_CATEGORY, "Destroy service");
        //Освобождаем ресурсы
        //Удаляем слушателя положения
        stopTracker();
        //Удаляем слушателя батарейки
        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
        }

        // Запускаем будильник чтоб он запустил наш сервис, сервис должен всегда работать
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0, receiverIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Устанавливаем чтоб сработал через минуту
        am.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60),
                pi);

        super.onDestroy();
    }

    private void stopTracker(){
        if (locationClient != null && locationCallback != null) {
            locationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void startTracker(){
        LocationRequest locationRequest = LocationRequest.create();
        //Приоритет берем из настроек. По умолчанию сбалансированный приоритет
        int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        switch( TrackerApplication.instance().getConfig(this).getAccuracy()){
            case high:
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                break;
            case medium:
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
            case low:
                priority = LocationRequest.PRIORITY_LOW_POWER;
                break;
        }
        locationRequest.setPriority(priority);
        //Редкое обновление, использует энергоемкий метод раз в пол часа
        locationRequest.setInterval(1000 * 60 * 30);
        //Частое обновление, использует не энергоемкий метод, берется из настроек
        locationRequest.setFastestInterval(1000 * 60 * TrackerApplication.instance().getConfig(this).getInterval());
        locationRequest.setSmallestDisplacement(TrackerApplication.instance().getConfig(this).getDistance());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(LOG_CATEGORY, "Not has permissions access to location service");
        } else {
            locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            Log.d(LOG_CATEGORY, "Start service");

            //Слушаем изменение заряда батарейки
            batteryLevelReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    try {
                        int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        int level = -1;
                        if (rawlevel >= 0 && scale > 0) {
                            level = (rawlevel * 100) / scale;
                        }
                        batteryLevel = level;
                        Log.d(LOG_CATEGORY, "Battery Level Changed " + batteryLevel);
                    } catch (Exception ex) {
                        Log.e(LOG_CATEGORY, "Error get battery level", ex);
                    }
                }
            };
            IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryLevelReceiver, batteryLevelFilter);

            startTracker();

            started = true;
        }

        return START_STICKY;
    }

    public void reconfigTracker(){
        Log.d(LOG_CATEGORY, "Call reconfig tracker " + TrackerApplication.instance().getConfig(this));
        stopTracker();
        startTracker();
    }

    public class LocationChacgeListener extends LocationCallback  {

        public LocationChacgeListener(){
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                Log.w(LOG_CATEGORY, "Not has location result in collback");
                return;
            }
            Log.d(LOG_CATEGORY, "Receive Location Result");
            Location bestLocation = getBestLocation(locationResult.getLocations());
            sendLocationData(bestLocation);
        };


        private Location getBestLocation(List<Location> locations) {
            Location result = null;
            for (Location location : locations){
                if (result == null){
                    result = location;
                }else{
                    if (location.getAccuracy() < result.getAccuracy()){
                        result = location;
                    }
                }
            }
            return result;
        }

        /**
         * Получение расстояния между двумя координатами в метрах
         * @param userLat
         * @param userLng
         * @param venueLat
         * @param venueLng
         * @return
         */
        public long calculateDistance(double userLat, double userLng,
                                      double venueLat, double venueLng) {
            double latDistance = Math.toRadians(userLat - venueLat);
            double lngDistance = Math.toRadians(userLng - venueLng);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c) * 1000;
        }
    }

    private void sendLocationData(Location location) {
        Log.w(LOG_CATEGORY, "Send data to server " + location.getProvider());
        PositionData data = new PositionData();
        data.setDate(format.format(new Date(location.getTime())));
        data.setAccuracy(location.getAccuracy());
        data.setLat(location.getLatitude());
        data.setLon(location.getLongitude());
        data.setProv(location.getProvider());
        data.setBatteryLevel(batteryLevel);
        data.setPerson(TrackerApplication.instance().getConfig(TrackLocationService.this).getPerson());

        new AsyncTask<PositionData, Void, PostPositionResult>() {

            @Override
            protected PostPositionResult doInBackground(PositionData ... params) {
                try {
                    // Create a new RestTemplate instance
                    RestTemplate restTemplate = new RestTemplate();

                    // Add the String message converter
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                    // Make the HTTP GET request, marshaling the response to a String
                    PostPositionResult postPositionResult = restTemplate.postForObject(SERVER_URL, params[0], PostPositionResult.class);
                    Log.d(LOG_CATEGORY, "Send data " + params[0]);
                    if (postPositionResult.getError() != null && !postPositionResult.getError().isEmpty()) {
                        Log.e(LOG_CATEGORY, "Error on server " + postPositionResult.getError());
                    }
                    return postPositionResult;
                }catch(Exception ex){
                    Log.e(LOG_CATEGORY, "Error on send position ", ex);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(PostPositionResult result) {
                //Если изменилась конфигурация то записываем ее в настройках
                boolean needSaveConfig = false;
                TrackerConfig config = TrackerApplication.instance().getConfig(TrackLocationService.this);
                if (result.getAccuracy() != null && !Accuracy.valueOf(result.getAccuracy()).equals(config.getAccuracy())){
                    config.setAccuracy(Accuracy.valueOf(result.getAccuracy()));
                    needSaveConfig = true;
                }

                if (result.getDistance() != null && result.getDistance() != config.getDistance()){
                    config.setDistance(result.getDistance());
                    needSaveConfig = true;
                }

                if (result.getInterval() != null && result.getInterval() != config.getInterval()){
                    config.setInterval(result.getInterval());
                    needSaveConfig = true;
                }

                if (needSaveConfig){
                    TrackerApplication.instance().setConfig(TrackLocationService.this, config);
                    //Перезапускаем трекер
                    reconfigTracker();
                }
            }
        }.execute(data);
    }

}
