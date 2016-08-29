package com.example.shubham.socketchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Shubham on 3/15/2016.
 */
public class PushReciever extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String message = (intent.getStringExtra("message") == null) ? "failed" : intent.getStringExtra("message");
        createNotification();
        Log.d("TAG", "The notification thing works");
        Toast.makeText(context,"The thing works",Toast.LENGTH_SHORT).show();
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.add_person);
        builder.setContentTitle("Pushy notification");
        builder.setContentText("The pushy thing works in each case");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
