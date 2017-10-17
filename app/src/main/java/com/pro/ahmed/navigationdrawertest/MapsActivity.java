package com.pro.ahmed.navigationdrawertest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    double curr_latitude, curr_longitude;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    //firebase database ref;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it or cancel to get location manually")
                .setCancelable(false)
                .setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(MapsActivity.this, "Please Click on your location on map ", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        curr_latitude = location.getLatitude();
        curr_longitude = location.getLongitude();
        lastlocation = location;
        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            CityAsyncTask cityAsyncTask = new CityAsyncTask(this, curr_latitude, curr_longitude);
            cityAsyncTask.execute();
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        CityAsyncTask cst = new CityAsyncTask(MapsActivity.this,
                point.latitude, point.longitude);
        cst.execute();
    }

    public class CityAsyncTask extends AsyncTask<String, String, String> {
        Activity act;
        double latitude;
        double longitude;

        public CityAsyncTask(Activity act, double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.act = act;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            String address = "";
            Geocoder geocoder = new Geocoder(act, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                Log.e("Addresses", "-->" + addresses);
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(String address) {
            // TODO Auto-generated method stub
            super.onPostExecute(address);
            Log.d("Address", address);
            Log.d("UserName2", RegistrationActivity.getUserName());
            if (currentLocationmMarker != null) {
                currentLocationmMarker.remove();
            }
            LatLng point = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(point);
            markerOptions.title(address);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentLocationmMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
            //Here Firbase Code
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("latitude").setValue(latitude);
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("longitude").setValue(longitude);
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("address").setValue(address);
            startActivity(new Intent(act, LoginActivity.class));

        }
    }
}
