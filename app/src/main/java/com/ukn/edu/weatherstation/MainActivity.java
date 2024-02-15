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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView locationTextViw;
    TextView currentTemperatureTextView;
    TextView highLowTemperatureTextView;
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

        readWeatherJson();
    }

    private void initLayout() {
        locationTextViw = findViewById(R.id.locationTextView);
        currentTemperatureTextView = findViewById(R.id.currentTemperatureTextView);
        highLowTemperatureTextView = findViewById(R.id.highLowTemperatureTextView);
        hourWeatherRecyclerView = findViewById(R.id.hourWeatherRecyclerView);
        tenDaysWeatherRecyclerView = findViewById(R.id.tenDaysWeatherRecyclerView);
    }

    private void readWeatherJson(){
        AssetFileReader assetFileReader = new AssetFileReader();
        String weatherJson = assetFileReader.readAssetFile(this, "weather.json");
        Log.i("readWeatherJson1", weatherJson);

        Gson gson = new Gson();
        WeatherData weatherData = gson.fromJson(weatherJson, WeatherData.class);
        Log.i("readWeatherJson1", "parser: ");
    }

    private void updateLocationInfo(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                String adminArea = address.getAdminArea();
                locationTextViw.setText(cityName + "," + adminArea);

                Log.i("updateLocationInfo", "cityName: " + cityName + ", adminArea: " + adminArea);
            }
        } catch (IOException e) {
            Log.e("updateLocationInfo", e.toString());
        }
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