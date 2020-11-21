package ru.larin.mykidsposition.mykidspositionclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootAlarmReceiver extends BroadcastReceiver {
    private static final String LOG_CATEGORY = BootAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(LOG_CATEGORY, "On Receive start");
            // Запуск сервиса
            TrackerApplication.instance().start(context);
        }
    }
}
