package com.ukn.edu.weatherstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ukn.edu.weatherstation.weather.WeatherData;
import com.ukn.edu.weatherstation.weather.WeatherView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    WeatherView wv;
    TextView locationTextView;
    RecyclerView hourWeatherRecyclerView;
    RecyclerView tenDaysWeatherRecyclerView;
    private final int REQUEST_PERMISSION_CODE = 100;
    private final String[] MANIFEST_PERMISSION = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();

        if (checkPermission()) {
            requestLocationUpdate();
        }

    }

    private void initLayout() {
        wv = findViewById(R.id.wv);
        locationTextView = findViewById(R.id.locationTextView);
//        hourWeatherRecyclerView = findViewById(R.id.hourWeatherRecyclerView);
//        tenDaysWeatherRecyclerView = findViewById(R.id.tenDaysWeatherRecyclerView);
    }

    private WeatherData readWeatherJson(){
        AssetFileReader assetFileReader = new AssetFileReader();
        String weatherJson = assetFileReader.readAssetFile(this, "weather.json");
        Log.i("readWeatherJson1", weatherJson);

        return new Gson().fromJson(weatherJson, WeatherData.class);
    }

    private void updateLocationInfo(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                String adminArea = address.getAdminArea();
                locationTextView.setText(cityName);

                updateUI(adminArea);
                Log.i("updateLocationInfo", "cityName: " + cityName + ", adminArea: " + adminArea);
            }
        } catch (IOException e) {
            Log.e("updateLocationInfo", e.toString());
        }
    }

    private void updateUI(String adminArea) {
        WeatherData.Location location = getWeatherByCity(adminArea);
        WeatherData.WeatherElement wxElement = getWeatherElementByName(location, "Wx");
        if(wxElement != null && wxElement.getTime().size() > 0){
            String datetimeAreaString = wxElement.getTime().get(0).getStartTime() + "~" + wxElement.getTime().get(0).getEndTime();
            wv.datetimeTextView.setText(datetimeAreaString);
            wv.currentWeatherTextView.setText(wxElement.getTime().get(0).getParameter().getParameterName());
        }

        WeatherData.WeatherElement minTElement = getWeatherElementByName(location, "MinT");
        WeatherData.WeatherElement maxTElement = getWeatherElementByName(location, "MaxT");
        if(minTElement != null && maxTElement != null && minTElement.getTime().size() > 0 && maxTElement.getTime().size() > 0) {
            String maxTString = maxTElement.getTime().get(0).getParameter().getParameterName()
                    + "°" + maxTElement.getTime().get(0).getParameter().getParameterUnit();
            String minTString = minTElement.getTime().get(0).getParameter().getParameterName()
                    + "°" + minTElement.getTime().get(0).getParameter().getParameterUnit();
            wv.highLowTemperatureTextView.setText(maxTString + "/" + minTString);
        }

        WeatherData.WeatherElement popElement = getWeatherElementByName(location, "PoP");
        if(popElement != null && popElement.getTime().size() > 0){
            String rainfallChanceString = popElement.getTime().get(0).getParameter().getParameterName();
            wv.rainfallChanceTextView.setText("降雨機率：" + rainfallChanceString + "%");
        }

        WeatherData.WeatherElement ciElement = getWeatherElementByName(location, "CI");
        if(ciElement != null && ciElement.getTime().size() > 0){
            String ciString = ciElement.getTime().get(0).getParameter().getParameterName();
            wv.ciTextView.setText(ciString);
        }

    }

    private WeatherData.Location getWeatherByCity(String city) {
        WeatherData weatherData = readWeatherJson();
        if (weatherData != null) {
            //找到符合的縣市
            for (WeatherData.Location location : weatherData.getRecords().getLocation()){
                if (location.getLocationName().equals(city)){
                    return location;
                }
            }
        }

        return null;
    }

    //Wx, 多雲
    //PoP, 降雨機率 10%
    //MinT, 17
    //CI, 稍有寒意至舒適
    //MaxT, 23C
    private WeatherData.WeatherElement getWeatherElementByName(WeatherData.Location location, String elementName) {
        if (location != null) {
            //找到符合的縣市
            for (WeatherData.WeatherElement weatherElement : location.getWeatherElement()){
                if (weatherElement.getElementName().equals(elementName)){
                    return weatherElement;
                }
            }
        }

        return null;
    }

    private void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.5f, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                        Log.i("onLocationChanged", location.getLatitude() + "," + location.getLongitude());
                    updateLocationInfo(location.getLatitude(), location.getLongitude());
                }
            });

        }


    }

    private boolean checkPermission() {
        List<String> permissionList = new ArrayList<>();

        for (String permission : MANIFEST_PERMISSION) {
            int resultCode = ActivityCompat.checkSelfPermission(this, permission);
            if (resultCode != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);

            return false;
        }

        return true;
    }

    private void showNoGrantedPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.granted_permission_title))
                .setMessage(getString(R.string.granted_permission_message))
                .setPositiveButton(getString(R.string.button_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.button_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            Boolean isAllPermissionGranted = true;
            for (int i = 0;i < grantResults.length;i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionGranted = false;
                }
            }

            if (!isAllPermissionGranted) {
                showNoGrantedPermissionDialog();
            } else {
                requestLocationUpdate();
            }
        }
    }
}