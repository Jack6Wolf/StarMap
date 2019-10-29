package com.startimes.jack;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.startimes.startmap.location.StarLocation;
import com.startimes.startmap.location.StarLocationListener;
import com.startimes.startmap.map.MapPlace;
import com.startimes.startmap.map.StarMapUtils;
import com.startimes.startmap.persimmion.PersimionsManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private Button tv, tv1, bt;
    private TextView tv3, tv4;
    private int i = 0;
    private double latitude;
    private double longitude;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PersimionsManager.isPersimmions(this)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 107);
            }
        }
        if (StarMapUtils.isGooglePlayServicesAvailable(this)) {
            Toast.makeText(MainActivity.this, "支持谷歌服务", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "不支持谷歌服务", Toast.LENGTH_SHORT).show();
        }
        loadMap();

        tv = findViewById(R.id.tv);
        tv1 = findViewById(R.id.tv1);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        bt = findViewById(R.id.bt);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultLocation();
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diyLocation();
            }
        });

        goSearch();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 加载谷歌地图
     */
    private void loadMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * 默认定位配置
     */
    private void defaultLocation() {
        StarLocation.setTimeOut(20000);
        StarLocation.getLocation(this, new StarLocationListener() {
            @Override
            public void onLocationResults(int resultCode, int msgCode, String msg, @Nullable BDLocation bdLocation) {
                Log.e("JACK", "resultCode:" + resultCode + "  msgCode:" + msgCode + "   msg:" + msg);
                if (bdLocation != null) {
                    latitude = bdLocation.getLatitude();
                    longitude = bdLocation.getLongitude();
                    tv3.setText("Latitude:" + latitude + ",Longitude:" + longitude + "\n" + "address:" + bdLocation.getAddrStr());
                }
            }

            @Override
            public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
                Log.e("JACK", "locType:" + locType + "  diagnosticType:" + diagnosticType + "   diagnosticMessage:" + diagnosticMessage);
            }
        });
    }

    /**
     * 自定义定位配置
     */
    private void diyLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(2000);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);

        //设置自定义定位配置
        StarLocation.setLocationOption(option);

        //设置超时机制（注意：连续定位设置超时无效）
        //StartLocation.setTimeOut(20000);

        StarLocation.getLocation(this, new StarLocationListener() {
            @Override
            public void onLocationResults(int resultCode, int msgCode, String msg, @Nullable BDLocation bdLocation) {
                Log.e("JACK", "resultCode:" + resultCode + "  msgCode:" + msgCode + "   msg:" + msg);

                Toast.makeText(MainActivity.this, "连续定位：" + i++, Toast.LENGTH_SHORT).show();
                if (bdLocation != null) {
                    latitude = bdLocation.getLatitude();
                    longitude = bdLocation.getLongitude();
                    tv4.setText("Latitude:" + latitude + ",Longitude:" + longitude + "\n" + "address:" + bdLocation.getAddrStr() + ",country:" + bdLocation.getCountry() + "," + bdLocation.getCountryCode());


                    LatLng mylocation = new LatLng(latitude, longitude);
                    if (mMap != null) {
                        mMap.addMarker(new MarkerOptions().position(mylocation).title("Marker"));
                        moveTo(latitude, longitude);
                    }
                }
            }

            @Override
            public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
                Log.e("JACK", "locType:" + locType + "  diagnosticType:" + diagnosticType + "   diagnosticMessage:" + diagnosticMessage);
            }
        });

    }

    @Override
    protected void onDestroy() {
        //连续定位手动调用停止
        StarLocation.stopLocation();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));
    }

    /**
     * 移动到定位坐标系已它为中心
     */
    private void moveTo(double lat, double Lng) {
        CameraPosition cameraPosition
                = new CameraPosition.Builder().target(new LatLng(lat, Lng))
                .zoom(15f).bearing(0).tilt(25).build();
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    /**
     * 去当前定位位置
     */
    private void goSearch() {
        //搜索地点，调起三方地图
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> deviceMapApps = StarMapUtils.getDeviceMapApps(MainActivity.this);
                createDialog(deviceMapApps);
            }
        });
    }


    public void createDialog(final List<String> deviceMapApps) {
        if (!deviceMapApps.isEmpty()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择地图");
            final String[] apps = new String[deviceMapApps.size()];
            for (int i = 0; i < deviceMapApps.size(); i++) {
                apps[i] = deviceMapApps.get(i);
            }
            builder.setItems(apps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //打开地图 对外经贸大学|latlng:39.98871,116.43234
                    MapPlace mapPlace = new MapPlace(MapPlace.GCJ, 116.43234, 39.98871);
                    mapPlace.setAddress("对外经贸大学");
                    StarMapUtils.startMap(MainActivity.this, deviceMapApps.get(which), mapPlace);
                }
            });
            builder.show();
        } else {
            Log.e("JACK", "没有安装地图");
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorCode() + ":" + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
