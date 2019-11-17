package com.example.marvel.locationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description
 *
 * @author jianghe
 * @since 2019-11-16.
 */

public class LocationCore {
    private static final int REMOVE_LISTNEER = 0;
    private static final int REGISTER_LISTNEER = 1;
    private static volatile LocationCore instance;
    private Context mContext;
    private LocationManager locationManager;
    private LocationListener gpsListener;
    private HandlerThread handlerThread;
    private Handler handler;
    private AtomicInteger regOrUnreg = new AtomicInteger(0);

    private LocationCore(Context context) {
        this.mContext = context;
        handlerThread = new HandlerThread("LocationThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @SuppressLint("MissingPermission")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REMOVE_LISTNEER:
                        locationManager.removeUpdates(gpsListener);
                        break;

                    case REGISTER_LISTNEER:
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                0, 0, gpsListener, handlerThread.getLooper());
                }
            }
        };
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Android接口", location.getLongitude() + ", " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    /**
     * Get singleton class
     *
     * @param context Context from caller
     * @return Singleton class
     */
    public static LocationCore getInstance(Context context) {
        if (instance == null) {
            synchronized (LocationCore.class) {
                if (instance == null) {
                    instance = new LocationCore(context);
                }
            }
        }
        return instance;
    }

    /**
     * Get regOrUnreg
     *
     * @return regOrUnreg
     */
    public int getRegOrUnreg() {
        return regOrUnreg.get();
    }

    /**
     * Get location permission
     */
    public void getPermision() {
        if (!checkPermission()) {
            requestLocalPermission();
            return;
        }
    }

    /**
     * Register or unregister listener
     */
    public void getLocation() {
        changeListener();
    }

    private void changeListener() {
        if (regOrUnreg.get() == 0) {
            sendMyMessage(REGISTER_LISTNEER);
            regOrUnreg.getAndIncrement();
        } else {
            sendMyMessage(REMOVE_LISTNEER);
            regOrUnreg.getAndDecrement();
        }
    }

    private void sendMyMessage(int msgWhat) {
        Message msg = Message.obtain();
        msg.what = msgWhat;
        handler.sendMessage(msg);
    }

    private void requestLocalPermission() {
        ((Activity) mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
    }

    private boolean checkPermission() {
        return mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
