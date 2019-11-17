package com.example.marvel.locationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.aMapGetLocation)
    Button aMapGetLocation;
    @BindView(R.id.getPermission)
    Button getPermission;
    @BindView(R.id.myGetLocation)
    Button myGetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.aMapGetLocation)
    public void aMapGetLocation() {
        if (AMapLocationCore.getInstance(getApplicationContext()).getRegOrUnreg() == 0){
            aMapGetLocation.setText(R.string.amap_stop_get_location);
        }else {
            aMapGetLocation.setText(R.string.amap_get_location);
        }
        AMapLocationCore.getInstance(getApplicationContext()).startGetLocation();
    }

    @OnClick(R.id.getPermission)
    public void getPermission() {
        LocationCore.getInstance(MainActivity.this).getPermision();
    }

    @OnClick(R.id.myGetLocation)
    public void myGetLocation() {
        if (LocationCore.getInstance(MainActivity.this).getRegOrUnreg() == 0){
            myGetLocation.setText(R.string.stop_get_location);
        }else {
            myGetLocation.setText(R.string.get_location);
        }
        LocationCore.getInstance(MainActivity.this).getLocation();
    }
}
