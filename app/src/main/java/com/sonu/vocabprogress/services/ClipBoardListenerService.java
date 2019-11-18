package com.sonu.vocabprogress.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.sonu.vocabprogress.R;
import com.sonu.vocabprogress.models.Word;
import com.sonu.vocabprogress.ui.activities.LoginActivity;
import com.sonu.vocabprogress.ui.activities.NotificationDialogActivity;
import com.sonu.vocabprogress.utilities.AppUtils;
import com.sonu.vocabprogress.utilities.datahelpers.SQLiteHelper;
import com.sonu.vocabprogress.utilities.sharedprefs.AppPrefs;

public class ClipBoardListenerService extends Service {
    ClipboardManager clipBoardManager;
    public static boolean isRunning=false;
    SQLiteHelper db;
    private final String FOREGROUND_SERVICE_CHANNEL_ID="foregroundService";

    @Override
    public IBinder onBind(Intent p1) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning=true;
        AppPrefs.getInstance(this).setServiceRunningStatus(true);
        showForegroundServiceNotification();
        db = SQLiteHelper.getSQLiteHelper(this);
        AppUtils.toast(getApplicationContext(),"Clipboard service started");
        clipBoardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipBoardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                String word = clipBoardManager.getText().toString().trim();
                makeNotification(word);
                saveToDb(word);
            }


        });
        return START_NOT_STICKY;
    }

    public void makeNotification(String msg) {
        int nid = 0;
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext());
        nBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        nBuilder.setContentTitle(msg);
        Intent intent = new Intent(ClipBoardListenerService.this, NotificationDialogActivity.class);
        intent.putExtra("word", msg);
        PendingIntent pi =
                PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.addAction(android.R.drawable.ic_menu_view, "Edit", pi);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "your channel id";
            NotificationChannel nc = new NotificationChannel(channelId, "mytag", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(nc);
            nBuilder.setChannelId(channelId);
        }
        nm.notify(nid, nBuilder.build());
    }

    @Override
    public void onDestroy() {
        isRunning=false;
        super.onDestroy();
        AppPrefs.getInstance(this).setServiceRunningStatus(false);
        AppUtils.toast(this,"ClipBoardChangeService stopped");
    }

    //validate copied word if exists or new
    public void saveToDb(String text) {
        try {
            db.insertData(new Word(text, "n/a", "n/a"));
        } catch (SQLiteConstraintException e) {
           e.printStackTrace();
        }
    }

    private void showForegroundServiceNotification(){
        createNotificationChannel();
        Intent intent=new Intent(ClipBoardListenerService.this, LoginActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification nb=new NotificationCompat.Builder(this,FOREGROUND_SERVICE_CHANNEL_ID)
        .setContentTitle("Clipboard service")
        .setContentText("Clipboard service is running")
        .setContentIntent(pendingIntent)
        .build();
        startForeground(1,nb);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel foregroundServiceChannel=new NotificationChannel(FOREGROUND_SERVICE_CHANNEL_ID,
                    "foreground service",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(foregroundServiceChannel);
        }
    }


}
