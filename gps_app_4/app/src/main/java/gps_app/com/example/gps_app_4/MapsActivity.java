package gps_app.com.example.gps_app_4;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private ArrayList<Location> route;
    private SupportMapFragment mapFragment;
    private DatabaseHelper myDb = new DatabaseHelper(this);
    private LocationManager locationManager2;
    private LocationListener listener2;
    private Boolean isProviderEnabled = false;
    private boolean isActivityOpen = false;
    private DateFormat df;
    private boolean isDataBase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String values[] = message.split("#");
        route = new ArrayList<Location>();

        isActivityOpen = true;
        if(values[0].equals("1"))
        {
            String[] values2 = values[1].split("@");
            createActivityWithListener(values2);
        }
        else if(values[0].equals("2"))
        {
            isDataBase = true;
            createActivityWithDataBase(values[1]);
        }
        //String

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void createActivityWithListener(String[] values)
    {
        df = new SimpleDateFormat("dd-MM-yyyy");




        //isActivityOpen=true;
        //Toast.makeText(MapsActivity.this, message, Toast.LENGTH_LONG).show();
        longitude = Double.parseDouble(values[1]);
        latitude = Double.parseDouble(values[0]);

        locationManager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener2 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(isActivityOpen==true) {
                    route.add(location);
                    String date = df.format(Calendar.getInstance().getTime());
                    myDb.insertData(location.getLatitude(), location.getLongitude(), date);
                    drawPrimaryLinePath();
                }
                //Toast.makeText(MapsActivity.this, "Zmieniono lokalizacje. " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                isProviderEnabled=true;
                Toast.makeText(MapsActivity.this, "Uzyskano połączenie GPS.", Toast.LENGTH_LONG).show();
                Log.d(" isProviderEnabled: ",isProviderEnabled.toString());
            }

            @Override
            public void onProviderDisabled(String provider) {
                isProviderEnabled=false;
                Toast.makeText(MapsActivity.this, "Utracono połączenie GPS.", Toast.LENGTH_LONG).show();
                Log.d(" isProviderEnabled: ",isProviderEnabled.toString());
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager2.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener2);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(isActivityOpen==true) {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
            if(isDataBase) {
                drawPrimaryLinePath();
            }
        }
    }

    public void createActivityWithDataBase(String date)
    {
        Cursor cur = myDb.getData(date);
        if (cur.getCount() == 0) {
            Toast.makeText(MapsActivity.this, "Nie wykonano żadnych operacji", Toast.LENGTH_LONG).show();
        } else {
            StringBuffer historyString = new StringBuffer();
            //double latitiude;
            //double longitiude;
            while (cur.moveToNext()) {
                latitude = Double.valueOf(cur.getString(0));
                longitude = Double.valueOf(cur.getString(1));
                Location temporary = new Location("");
                temporary.setLatitude(latitude);
                temporary.setLongitude(longitude);
                route.add(temporary);
            }
        }
    }



    private void drawPrimaryLinePath( )
    {
        if ( mMap == null )
        {
            return;
        }

        if ( route.size() > 2 ) {


            mMap.clear();

            PolylineOptions options = new PolylineOptions();

            options.color(Color.parseColor("#CC0000FF"));
            options.width(5);
            options.visible(true);

            for (int i = 1; i < route.size() - 1; i++) {
                Location loc = route.get(i);
                options.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            }
            mMap.addPolyline(options);
        }

        //addMarker(); //add Marker in current position

    }

    private void closeActivity(View view)
    {
        finish();
    }

}
