package com.example.user22.servicebind;

/*
*  ****************************************************************************
*  * Created by : Md. Azizul Islam on 3/1/2018 at 3:18 PM.
*  * Email : azizul@w3engineers.com
*  * 
*  * Last edited by : Md. Azizul Islam on 3/1/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class BindService extends Service{
    public static final String START_FOREGROUND_ACTION = "io.left.meshim.action.startforeground";
    public static final String STOP_FOREGROUND_ACTION = "io.left.meshim.action.stopforeground";
    public static final int FOREGROUND_SERVICE_ID = 101;
    private Notification mServiceNotification;
    private boolean mIsBound = false;
    private boolean mIsForeground = false;
    @Override
    public void onCreate() {
        super.onCreate();
        Intent stopForegroundIntent = new Intent(this, BindService.class);
        stopForegroundIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,stopForegroundIntent,0);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID);
        } else {
            //noinspection deprecation
            builder = new NotificationCompat.Builder(this);
        }
        mServiceNotification = builder.setAutoCancel(false)
                .setTicker("Bindservice")
                .setContentTitle("Bindservice Running")
                .setContentText("Tap to go offline.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setNumber(100)
                .build();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mIsBound = true;
        return myBindService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mIsBound = false;
        return super.onUnbind(intent);
    }

    private final IMyBindService.Stub myBindService = new IMyBindService.Stub() {
        @Override
        public void setForground(boolean value) throws RemoteException {
            if(value){
                startInForeground();
                mIsForeground = true;
            }else {
                stopForeground(true);
                mIsForeground = false;
            }
        }

        @Override
        public String getName() throws RemoteException {
            return null;
        }
    };

    private void startInForeground() {
        startForeground(FOREGROUND_SERVICE_ID, mServiceNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (action != null && action.equals(STOP_FOREGROUND_ACTION)) {
            if (mIsBound) {
                stopForeground(true);
            } else {
                stopSelf();
            }
        } else if (action != null && action.equals(START_FOREGROUND_ACTION)) {
            startInForeground();
        }
        return START_STICKY;
    }
}
