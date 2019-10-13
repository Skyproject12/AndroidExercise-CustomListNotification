package com.example.customlist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static com.example.customlist.NotificationService.CHANNEL_ID;
import static com.example.customlist.NotificationService.CHANNEL_NAME;
import static com.example.customlist.NotificationService.REPLY_ACTION;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String KEY_NOTIFICATION_ID= "key_notification_id";
    private static String KEY_MESSAGE_ID="key_message_id";

    public static Intent getReplyMessageIntent(Context context, int notifyId, int messageId){
        Intent intent= new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra(KEY_NOTIFICATION_ID, notifyId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        intent.setAction(REPLY_ACTION);
        return intent;
    }


    // diginakan untuk menerima response dari remoteinput
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(REPLY_ACTION.equals(intent.getAction())){
            // mengambil funstion dari notification service untuk mengecek remote input
            CharSequence message= NotificationService.getReplyMessage(intent);
            // menerima intent dari notification service
            int messageId= intent.getIntExtra(KEY_MESSAGE_ID,0);
            Toast.makeText(context, "Message ID:"+ messageId+"Message:"+message, Toast.LENGTH_SHORT).show();
            int notifyId= intent.getIntExtra(KEY_NOTIFICATION_ID,1);
            updateNotification(context, notifyId);
        }
    }
    // notification akan muncul ketika notification direspon
    // merupakan tanggapan ketika terdapat notification
    public void updateNotification(Context context, int notifyId){
        // melakukan update notification ketika mresponse
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shop)
                .setContentTitle(context.getString(R.string.notif_title_sent))
                .setContentText(context.getString(R.string.notif_content));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mBuilder.setChannelId(CHANNEL_ID);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }
        }
        Notification notification= mBuilder.build();
        if(notificationManager!=null){
            notificationManager.notify(notifyId, notification);
        }
    }
    public NotificationBroadcastReceiver(){

    }
}
