package com.example.arrivalnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private Thread mthread;
    private int mCount = 0;

    private IBinder mbinder = new Mybinder();
    public class Mybinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mbinder;
    }

    public int getCount(){
        return mCount;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if("startForeground".equals((intent.getAction()))){
            startForegroundService();
        }else if (mthread == null) {
            mthread = new Thread("MyThread") {
                @Override
                public void run() {
                }
            };
            mthread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if( mthread != null){
            mthread.interrupt();
            mthread = null;
            mCount = 0;
        }
    }

    private void startForegroundService(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("도착알림이");
        builder.setContentText("도착알림이 실행중");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default","기본", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1,builder.build());
    }
}