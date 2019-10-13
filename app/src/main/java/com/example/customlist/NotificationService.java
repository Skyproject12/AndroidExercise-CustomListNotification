package com.example.customlist;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;


public class NotificationService extends IntentService {

    public static String REPLY_ACTION="com.example.customlist.REPLY_ACTION";
    private static final String KEY_REPLY="key_reply_message";
    public static String CHANNEL_ID= "channel_01";
    public static CharSequence CHANNEL_NAME="dicoding channel";
    private int mNotificationId;
    private int mMessageId;

    public NotificationService() {
        super("NotificationService");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null){
            showNotification();
        }
    }

    // membuat aplikasi tetap berjalan walaupun aplikasi di destroy
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void showNotification(){
        mNotificationId=1;
        mMessageId=123;
        // menampung key dan lainnya digunakan untuk mengamnbil input dari direct reply
        String replyLabel= getString(R.string.notif_action_reply);
        RemoteInput remoteInput= new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();
        // berfungsi menghubungkan action dan remoteinput
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                // give intent when service run
                R.drawable.ic_sentiment, replyLabel, getReplyPendingIntent())
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();
        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sentiment)
                .setContentTitle(getString(R.string.notif_title))
                .setContentText(getString(R.string.notif_content))
                .setShowWhen(true)
                .addAction(replyAction);
        NotificationManager mNotificationManager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            if(mNotificationManager!=null){
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        Notification notification= mBuilder.build();
        if(mNotificationManager!=null){
            mNotificationManager.notify(mNotificationId, notification);
        }
    }
    // pending intent berfungsi memberi akses ketika kita akan melakukan action di home sreent atau lainnya
    public PendingIntent getReplyPendingIntent(){
        Intent intent;
        // langsung menampilakan direct reply di notifikasi
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            // menangkap  data berupa KEY_NOTIFICATION_ID dari broadcast
            intent= NotificationBroadcastReceiver.getReplyMessageIntent(this, mNotificationId, mMessageId);
            // melakukan operasi dari method getReplayMessageIntent yaitu mengirim data berupa messageId, KEY_NOTIFICATION
            return PendingIntent.getBroadcast(getApplicationContext(),100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // memanggil NotificationBroadcast ketika version sdk lebih dari n
        }
        // harus membuka replyactivity agar pengguna bisa membalas notification masuk
        else{
            // mengambil method replayMessage dari replyactivity
            intent=ReplyActivity.getReplyMessageIntent(this, mNotificationId, mMessageId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            // memanggil activity reply ketika sdk dibawah dari n
        }
    }
    // melakukan pengecekan remote input
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static CharSequence getReplyMessage(Intent intent){
        Bundle remoteInput= RemoteInput.getResultsFromIntent(intent);
        if(remoteInput!=null){
            // mengimrim key reply ke dalam broadcast
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
}
