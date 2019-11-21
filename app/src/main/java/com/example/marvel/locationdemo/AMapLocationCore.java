package com.example.marvel.locationdemo;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description
 *
 * @author jianghe
 * @since 2019-11-17.
 */
public class AMapLocationCore {
    private static final String TAG = "高德SDK";
    private static volatile AMapLocationCore instance;
    private Context mContext;
    private AMapLocationClient mLocationClient;
    private AMapLocationListener mAMapLocationListener;
    private AtomicInteger regOrUnreg = new AtomicInteger(0);

    public AMapLocationCore(Context context) {
        this.mContext = context;
        mLocationClient = new AMapLocationClient(mContext);
        mAMapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        Log.d(TAG, amapLocation.getLongitude() + ", " + amapLocation.getLatitude());
                    } else {
                        Log.e(TAG, "ErrorCode is not 0, because " + amapLocation.getErrorInfo());
                    }
                }
            }
        };
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
    }

    public static AMapLocationCore getInstance(Context context) {
        if (instance == null) {
            synchronized (AMapLocationCore.class) {
                if (instance == null) {
                    instance = new AMapLocationCore(context);
                }
            }
        }
        return instance;
    }


    public int getRegOrUnreg() {
        return regOrUnreg.get();
    }

    public void startGetLocation() {
        changeListener();
    }

    public void activate(final LocationSource.OnLocationChangedListener onLocationChangedListener) {
        AMapLocationListener mapListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null
                        && aMapLocation.getErrorCode() == 0) {
                    Log.d("activate onLocationChanged",
                            aMapLocation.getLatitude() + ", " + aMapLocation.getLongitude());
                    onLocationChangedListener.onLocationChanged(aMapLocation);
                } else {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                    Log.e("AmapErr", errText);
                }
            }
        };
        mLocationClient.setLocationListener(mapListener);
        mLocationClient.startLocation();
    }

    public void deactivate() {
        mLocationClient.stopLocation();
    }

    private void changeListener() {
        if (regOrUnreg.get() == 0) {
            mLocationClient.startLocation();
            mLocationClient.setLocationListener(mAMapLocationListener);
            regOrUnreg.getAndIncrement();
        } else {
            mLocationClient.stopLocation();
            regOrUnreg.getAndDecrement();
        }
    }
}
