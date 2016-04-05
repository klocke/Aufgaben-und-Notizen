package com.example.aufgabenundnotizen.other;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.activities.ItemListActivity;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Tim on 28.03.16.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(com.example.aufgabenundnotizen.R.drawable.ic_all)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(intent.getStringExtra("item_title"))
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, ItemListActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context,0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = intent.getIntExtra("not_id", 1);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        updateHashMap(context, intent.getStringExtra("item_id"));
    }

    private void updateHashMap(Context context, String ITEM_ID){
        Gson gson = new Gson();
        String hmS = PreferenceManager.getDefaultSharedPreferences(context).getString("ITEM_HM", "");

            MapWrapper wrapper = gson.fromJson(hmS, MapWrapper.class);
            HashMap hm = wrapper.getHm();

        hm.remove(ITEM_ID);

        wrapper.setHm(hm);
        String serializedMap = gson.toJson(wrapper);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ITEM_HM", serializedMap).commit();
    }
}

