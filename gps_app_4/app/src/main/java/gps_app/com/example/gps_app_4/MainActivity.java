package gps_app.com.example.gps_app_4;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    public final static String EXTRA_MESSAGE = "gps_app.com.example.gps_app_4";
    private SupportMapFragment map;
    private boolean isFlashOn = false;
    private Location mLastLocation;
    private Sensor mLight;
    private SensorManager LightManager;
    private Sensor mGyro;
    private SensorManager GyroManager;
    private SensorManager CompassManager;
    private Sensor Compass;
    private String latitude;
    private String longitude;
    private GoogleApiClient mGoogleApiClient;
    private boolean isLocalizationSetted = false;
    private LocationManager locationManager;
    private LocationListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LightManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = LightManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        GyroManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyro = GyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        CompassManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Compass = CompassManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                TextView tvLat = (TextView) findViewById(R.id.tvLAT);
                TextView tvLong = (TextView) findViewById(R.id.tvLON);
                tvLong.setText(String.valueOf(round(location.getLongitude(), 6)));
                tvLat.setText(String.valueOf(round(location.getLatitude(), 6)));
                mapRefresher();
                isLocalizationSetted = true;
                //Toast.makeText(MapsActivity.this, "Zmieniono lokalizacje. " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                //isProviderEnabled=true;
                Toast.makeText(MainActivity.this, "Uzyskano połączenie GPS.", Toast.LENGTH_LONG).show();
                //Log.d(" isProviderEnabled: ",isProviderEnabled.toString());
            }

            @Override
            public void onProviderDisabled(String provider) {
                //isProviderEnabled=false;
                Toast.makeText(MainActivity.this, "Utracono połączenie GPS.", Toast.LENGTH_LONG).show();
                //Log.d(" isProviderEnabled: ",isProviderEnabled.toString());
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

    }

    public void mapRefresher() {
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        map.getMapAsync(this);//remember getMap() is deprecated!
    }

    public void openMap(View v) {
        sendMessage(v, "");
    }

    public void sendMessage(View view, String mess) {
        if (isLocalizationSetted == true) {
            Intent intent = new Intent(this, MapsActivity.class);
            String message = mess;
            intent.putExtra(EXTRA_MESSAGE, "1#" + latitude + '@' + longitude); //wysyłanie wiadomości do podrzednej activity
            startActivity(intent); //intent to tylko handler do activity, więc musimy wywołać metode start activity / wywołanie metody onCreate()
        } else {
            Toast.makeText(MainActivity.this, "Zaczekaj na ustabilizowanie gps, aby otworzyć mapę.", Toast.LENGTH_LONG);
        }
    }

    public void turnOnFlash(View v) {
        String cameraId[];
        CameraManager camera = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (isFlashOn == false) {
                isFlashOn = true;
                cameraId = camera.getCameraIdList();
                camera.setTorchMode(cameraId[0], isFlashOn);
            } else {
                isFlashOn = false;
                cameraId = camera.getCameraIdList();
                camera.setTorchMode(cameraId[0], isFlashOn);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    protected void onStop() {
        //mGoogleApiClient.disconnect();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
        super.onStop();
    }


    protected void onResume() {
        super.onResume();
        LightManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        GyroManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
        // locationManager.registerListener(this,mLastLocation,SensorManager.SENSOR_DELAY_NORMAL);
        CompassManager.registerListener(this, Compass, SensorManager.SENSOR_DELAY_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LightManager.unregisterListener(this);
        GyroManager.unregisterListener(this);
        // locationManager.unregisterListener(this);
        CompassManager.unregisterListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //locationManager.removeUpdates(listener);

    }

    @Override
    protected void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            TextView tvLight = (TextView) findViewById(R.id.tvLightIntensity);
            tvLight.setText(String.valueOf(lux));
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            double azX = event.values[0];
            double azY = event.values[1];
            double azZ = event.values[2];
            TextView tvGyro = (TextView) findViewById(R.id.tvGyroscopeValue);
            tvGyro.setText("Azimuth X: " + String.valueOf((int) azX) + "\nAzimuth Y " + String.valueOf((int) azY) + "\nAzimuth Z " + String.valueOf((int) azZ));
        } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {


        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            double azX = event.values[0];
            double azY = event.values[1];
            double azZ = event.values[2];

            //GeomagneticField compass = new GeomagneticField((float) azX, (float) azY, (float) azZ, time);
            TextView tvCompassValue = (TextView) findViewById(R.id.tvCompassValue);
            tvCompassValue.setText("Azimuth X: " + String.valueOf((int) azX) + "\nAzimuth Y " + String.valueOf((int) azY) + "\nAzimuth Z " + String.valueOf((int) azZ));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Toast.makeText(MainActivity.this, "Dokładność pomiaru została zmieniona.", Toast.LENGTH_LONG).show();
    }


    public void refreshMap(View v) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Nie mozna odświezyć, brak uprawnień.", Toast.LENGTH_LONG).show();
            return;
        }

            map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            map.getMapAsync(this);//remember getMap() is deprecated!
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.clear();
        LatLng sydney = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //map.setMyLocationEnabled(true);
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void showHistory(View view) {
        Intent intent = new Intent(this, ShowHistory.class); //wysyłanie wiadomości do podrzednej activity
        startActivity(intent); //intent to tylko handler do activity, więc musimy wywołać metode start activity / wywołanie metody onCreate()
    }


}
