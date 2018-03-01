package com.example.user22.servicebind;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private IMyBindService iMyBindService = null;


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyBindService = IMyBindService.Stub.asInterface(service);
            try {
                iMyBindService.setForground(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyBindService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToService();
    }

    private void connectToService() {
        Intent serviceIntent = new Intent(this, BindService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectFromService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFromService();
    }

    private void disconnectFromService() {
        if (iMyBindService != null) {
            try {
                iMyBindService.setForground(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(serviceConnection);
            iMyBindService = null;
        }
    }

}
