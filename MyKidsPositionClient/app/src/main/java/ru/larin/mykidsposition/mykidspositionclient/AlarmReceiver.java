package ru.larin.mykidsposition.mykidspositionclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String LOG_CATEGORY = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_CATEGORY, "On Receive start");
        TrackerApplication.instance().start(context);
        Log.d(LOG_CATEGORY, "On Receive finish");
    }

}
