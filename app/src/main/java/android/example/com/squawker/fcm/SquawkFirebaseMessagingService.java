package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {


    private static String LOG_TAG = SquawkFirebaseMessagingService.class.getSimpleName();
    private static int NOTIFICATION_MAX_MESSAGE= 30;

    public SquawkFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> data = remoteMessage.getData();
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "Message data payload: " + data);
            sendNotification(data);
        }
    }

    private void sendNotification(Map<String, String> data){
        // Creating an Intent, wrapping it inside a PendingIntent for the Notification
        Intent intent= new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent= PendingIntent.getActivity(this,
                0,
                intent,
                // The following flag means that the PendingIntent will be only used once
                PendingIntent.FLAG_ONE_SHOT);

        String author= data.get(SquawkContract.COLUMN_AUTHOR);
        String message= data.get(SquawkContract.COLUMN_MESSAGE);

        if(message.length() > NOTIFICATION_MAX_MESSAGE){
            message= message.substring(0, NOTIFICATION_MAX_MESSAGE) +"\u2026"; //horizontal ellipsis
        }

        // Notification sound
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Build the Notification
        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
        Log.d(LOG_TAG, "notification sent...");
    }
}
