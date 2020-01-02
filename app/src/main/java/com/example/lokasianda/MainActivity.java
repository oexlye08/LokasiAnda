package com.example.lokasianda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted {

    private static final int REQUEST_LOCATION_PERMISSION=1;
    private Button mLocationButton;
    private TextView mLocationTextVIew;
    FusedLocationProviderClient mfusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INISIALISASI
        mLocationButton=findViewById(R.id.button_location);
        mLocationTextVIew=findViewById(R.id.textview_location);
        mfusedLocationClient= LocationServices.getFusedLocationProviderClient(this);

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }else {
            mfusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null) {
                        mLocationTextVIew.setText("Latitude" + location.getLatitude()
                                + "\n Longitude" + location.getLongitude());
                    }
                }
            });
        }


        /**Update Lokasi contoh : GOJEK**/
        mfusedLocationClient.requestLocationUpdates(getLocationRequst(),
                new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult.getLastLocation()!=null){
                    Location location=locationResult.getLastLocation();
                   /* mLocationTextVIew.setText(
                            "Latitude: "+ location.getLatitude()+
                                    "\n Longitude"+location.getLongitude()
                    );*/

                   new FetchAddressTask(MainActivity.this, MainActivity.this)
                           .execute(location);
                    }
                 }
            }, null
        );
    }

    private LocationRequest getLocationRequst(){
        LocationRequest locationRequest= new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        mLocationTextVIew.setText(result);
    }
}
