package com.zb.daily.UI;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.zb.daily.BaseActivity;
import com.zb.daily.Constant;
import com.zb.daily.R;
import com.zb.daily.UI.fragment.IndexRecordInFragment;
import com.zb.daily.UI.fragment.IndexRecordOutFragment;

import java.io.IOException;
import java.util.*;
import android.Manifest;

import static com.zb.daily.Constant.TO_INDEX_FRAGMENT;


public class RecordAddActivity extends BaseActivity {

    private Button preButton;
    private Button outButton;
    private Button inButton;
    private  String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_add);

        preButton = findViewById(R.id.activity_record_btn_pre);
        outButton = findViewById(R.id.activity_record_btn_out);
        inButton = findViewById(R.id.activity_record_btn_in);


        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actionStart(getApplicationContext(), TO_INDEX_FRAGMENT);
            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outButton.setBackgroundResource(R.drawable.button_pressed);
                inButton.setBackgroundResource(R.drawable.button_normal);
                Bundle bundle = new Bundle();
                bundle.putString("data",location);
//首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
                IndexRecordOutFragment myFragment=new IndexRecordOutFragment();
                myFragment.setArguments(bundle);
                replaceFragment(myFragment);
            }
        });

        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outButton.setBackgroundResource(R.drawable.button_normal);
                inButton.setBackgroundResource(R.drawable.button_pressed);
                Bundle bundle = new Bundle();
                bundle.putString("data",location);
//首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
                IndexRecordInFragment myFragment=new IndexRecordInFragment();
                myFragment.setArguments(bundle);
                replaceFragment(myFragment);
            }
        });
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecordAddActivity.this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},1);

        } else {
            location=ShowLocation();
            //默认加载支出记录页面
            Bundle bundle = new Bundle();
            bundle.putString("data",location);
//首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
            IndexRecordOutFragment myFragment=new IndexRecordOutFragment();
            myFragment.setArguments(bundle);
            replaceFragment(myFragment);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String s= grantResults[0]+"";
            Log.i("Running time",s);
            location=ShowLocation();
            //默认加载支出记录页面
            Bundle bundle = new Bundle();
            bundle.putString("data",location);
//首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
            IndexRecordOutFragment myFragment=new IndexRecordOutFragment();
            myFragment.setArguments(bundle);
            replaceFragment(myFragment);

        } else {
            String s= grantResults[0]+"";
            Log.i("Running time",s);
            //TODO:用户拒绝

        }

    }

    @SuppressLint("MissingPermission")
    public String ShowLocation() {
        String provider=LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager;//位置服务
        Location location;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        String msg="加载中";
        List prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            provider=LocationManager.GPS_PROVIDER;
        }
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
        }
        else{
            Double latitude = 37.29789;
            Double longitude = -122.02339;

            Log.i("Locator","location为空");
        }
        new GeocodeAddress().execute(location);
        return msg;
    }

    //启动本活动
    public static void actionStart(Context context){
        Intent intent = new Intent();
        intent.setClass(context, RecordAddActivity.class);
        context.startActivity(intent);
        /*((BaseActivity)context).startActivityForResult(intent,1001);*/
    }

    //动态切换fragment
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_record_content_frame, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.actionStart(getApplicationContext(), TO_INDEX_FRAGMENT);
    }
    public class GeocodeAddress extends AsyncTask<Location, Void, String>
    {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Location... params) {
            // TODO Auto-generated method stub
            if(params[0]!=null)
            {
                Geocoder geocoder=new Geocoder(RecordAddActivity.this);
                try {
                    List<Address> address=geocoder.getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 1);
                    String msg="";
                    if(address.size()>0)
                    {
                        msg+=address.get(0).getAdminArea()+"-";
                        msg+=address.get(0).getLocality()+"-";
                        msg+=address.get(0).getAddressLine(0);
                        return msg;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "北京市-海淀区-西土城路北京邮电大学附近";
                }
            }
            else{
                return "北京市-海淀区-西土城路北京邮电大学附近";
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(result!=null&&result!="")
            {
                location = result;
                Bundle bundle = new Bundle();
                bundle.putString("data",result);
//首先有一个Fragment对象 调用这个对象的setArguments(bundle)传递数据
                IndexRecordOutFragment myFragment=new IndexRecordOutFragment();
                myFragment.setArguments(bundle);
                replaceFragment(myFragment);
            }
        }

    }

}
