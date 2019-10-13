package com.example.customlist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import static com.example.customlist.NotificationService.CHANNEL_ID;
import static com.example.customlist.NotificationService.CHANNEL_NAME;
import static com.example.customlist.NotificationService.REPLY_ACTION;

public class ReplyActivity extends AppCompatActivity {
    private static String KEY_MESSAGE_ID="key_message_id";
    private static final String KEY_NOTIFY_ID="key_notify_id";
    private int mMessageId;
    private int mNotifyId;
    private EditText mEditReply;

    public static Intent getReplyMessageIntent(Context context, int notifyId, int messageId){
        Intent intent= new Intent(context, ReplyActivity.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        intent.putExtra(KEY_NOTIFY_ID, notifyId);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_reply);
        Intent intent= getIntent();
        if(REPLY_ACTION.equals(intent.getAction())){
            // mengambil KEY_MESSAGE_ID
            mMessageId= intent.getIntExtra(KEY_MESSAGE_ID,0);
            mNotifyId= intent.getIntExtra(KEY_NOTIFY_ID,0);
        }
        mEditReply= findViewById(R.id.edit_reply);
        ImageButton sendButton= findViewById(R.id.button_send);
        // ketika sendButton di klik
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mNotifyId, mMessageId);
            }
        });
    }
    private void sendMessage(int notifyId, int messageId){
        updateNotification(notifyId);
        // take value of edittext
        String message= mEditReply.getText().toString().trim();
        Toast.makeText(this, messageId+"Message"+message, Toast.LENGTH_SHORT).show();
        finish();
    }
    private void updateNotification(int notifyId){
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // customise builder
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notif_title_sent))
                .setContentText(getString(R.string.notif_content));

        // ketika version android lebih dari android  o
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000,1000,1000,1000});
            builder.setChannelId(CHANNEL_ID);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
            }

        }
        // execution builder
        Notification notification= builder.build();
        if(notificationManager!=null){
            // make notification use notifyid and builder
            notificationManager.notify(notifyId,notification);
        }

    }
}
