package ru.larin.mykidsposition.mykidspositionclient;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrackerApplication {
    private static final String LOG_CATEGORY = TrackerApplication.class.getSimpleName();
    private static TrackerApplication instance = new TrackerApplication();
    private static String CONFIG_FILE = "config";
    private static String INTERVAL_CONFIG = "interval";
    private static String PERSON_CONFIG = "person";
    private static String DISTANCE_CONFIG = "distance";
    private static String ACCURACY_CONFIG = "accuracy";

    public static TrackerApplication instance(){
        return instance;
    }
    private TrackerConfig _config;


    private TrackerApplication(){
        Log.d(LOG_CATEGORY, "Create Tracker Application");
    }

    public void start(Context context) {
        Intent intent = new Intent(context, TrackLocationService.class);
        context.startService(intent);
    }

    public void setConfig(Context context, TrackerConfig config) {
        _config = config;

        SharedPreferences settings = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(INTERVAL_CONFIG, config.getInterval());
        editor.putLong(PERSON_CONFIG, config.getPerson());
        editor.putInt(DISTANCE_CONFIG, config.getDistance());
        editor.putString(ACCURACY_CONFIG, config.getAccuracy().toString());
        editor.commit();
    }

    public TrackerConfig getConfig(Context context) {
        if (_config == null) {
            try{
                SharedPreferences settings = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
                _config = new TrackerConfig();
                _config.setInterval(settings.getInt(INTERVAL_CONFIG, 1));
                _config.setPerson(settings.getLong(PERSON_CONFIG, 0));
                _config.setDistance(settings.getInt(DISTANCE_CONFIG, 100));
                _config.setAccuracy(Accuracy.valueOf(settings.getString(ACCURACY_CONFIG, Accuracy.medium.toString())));
            }catch(Exception ex){
                _config = new TrackerConfig();
                _config.setInterval(1);
                _config.setPerson(0);
                _config.setDistance(10);
                _config.setAccuracy(Accuracy.medium);
            }
        }
        return _config;
    }
}
