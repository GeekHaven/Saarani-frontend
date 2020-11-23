package com.geekhaven.caliiita.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.geekhaven.caliiita.receiver.ButtonReceiver;
import com.geekhaven.caliiita.R;
import com.geekhaven.caliiita.activites.EventActivity;
import com.geekhaven.caliiita.worker.MyWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    FirebaseAuth mAuth;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String value=data.get("del");
        String eventId=data.get("eventID");
        assert value != null;
        Log.d("onMR",value);
        scheduleJob(value,eventId);
    }
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob(String value,String eventId) {
        // [START dispatch_job]
        Data.Builder data = new Data.Builder();
        data.putString("del",value);
        data.putString("eventId",eventId);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setConstraints(constraints)
                .setInputData(data.build())
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Map<String, String> data = remoteMessage.getData();
        Log.d("dataPayload",data.toString());
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("eventId",data.get("eventID"));
        intent.putExtra("type","notif");
        intent.putExtra("screen","home");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, m, intent,PendingIntent.FLAG_ONE_SHOT);

        Intent ignoreIntent = new Intent(this, ButtonReceiver.class);
        ignoreIntent.putExtra("action","ignore");
        ignoreIntent.putExtra("notifid",m);
        PendingIntent ignorePIntent = PendingIntent.getBroadcast(this, m+1, ignoreIntent,0);

        Intent goingIntent =new Intent(this,ButtonReceiver.class);
        goingIntent.putExtra("action","going");
        goingIntent.putExtra("eventId",data.get("eventID"));
        goingIntent.putExtra("notifid",m);
        PendingIntent goingPIntent= PendingIntent.getBroadcast(this,m+2,goingIntent,0);

        final Intent interestedIntent= new Intent(this,ButtonReceiver.class);
        interestedIntent.putExtra("action","interested");
        interestedIntent.putExtra("eventId",data.get("eventID"));
        interestedIntent.putExtra("notifid",m);
        PendingIntent interPIntent= PendingIntent.getBroadcast(this,m+3,interestedIntent,0);



        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap = getBitmapfromUrl(data.get("image"));
        bitmap=getCircleBitmap(bitmap);
        NotificationCompat.Builder notificationBuilder;
        if(data.get("del").equals("0")) {
                    notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_logo7)
//                            .setColor(Color.rgb(0,0,0))
                            .setContentTitle(data.get("title"))
                            .setSubText(data.get("subtext"))
                            .setContentText(data.get("body"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("body")))
                            .setLargeIcon(bitmap)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_EVENT);
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            if(prefs.getString("society", "false").equals("false")) {
                notificationBuilder.addAction(R.string.reject, getString(R.string.reject), interPIntent)
                        .addAction(R.string.accept, getString(R.string.accept), goingPIntent)
                        .addAction(R.string.xxx, getString(R.string.xxx), ignorePIntent);
            }
        }
        else{
                    notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_logo7)
//                            .setColor(Color.rgb(0,0,0))
                            .setContentTitle(data.get("title"))
                            .setSubText(data.get("subtext"))
                            .setContentText(data.get("body"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(data.get("body")))
                            .setLargeIcon(bitmap)
                            .setCategory(NotificationCompat.CATEGORY_EVENT);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(m, notificationBuilder.build());
    }
}
