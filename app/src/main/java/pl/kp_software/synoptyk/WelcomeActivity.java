package pl.kp_software.synoptyk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static double latitude;
    public static double longitude;
    String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        myDb = new DatabaseHelper(this);

        Button enterButton = (Button) this.findViewById(R.id.enterButton);
        enterButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ locationProvider = LocationManager.GPS_PROVIDER; }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){ locationProvider = LocationManager.NETWORK_PROVIDER; }
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){ locationProvider = LocationManager.PASSIVE_PROVIDER; }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            loadData();
            return;
        } else {
            getLocation();
        }

        loadPosition();
        loadData();

        SystemClock.sleep(10000);

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                    loadData();
                }
                return;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

    public void loadData(){
        if (isNetworkAvailable() == true){
            try {
                loadMeasurementsFromAPI("http://synoptyk.kp-software.pl/api/v1/measurements.json");
                loadMetarsFromAPI("http://synoptyk.kp-software.pl/api/v1/metar_raports.json");
                loadMeasursFromAPI("http://synoptyk.kp-software.pl/api/v1/gios_measurements.json");
                loadForecastsFromAPI("http://synoptyk.kp-software.pl/api/v1/forecasts.json");
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void loadPosition(){
        @SuppressLint("MissingPermission")
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadForecastsFromAPI(String url) {
        WelcomeActivity.GetForecasts getForecasts = new WelcomeActivity.GetForecasts(this);
        getForecasts.setMessageLoading("Uruchamianie aplikacji...");
        myDb.deleteDataMForecastsAll();
        getForecasts.execute(url);
    }

    private class GetForecasts extends UrlJsonAsyncTask {
        public GetForecasts(Context context) {
            super(context);
        }
        boolean isInserted;

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonTickets = json.getJSONObject("data").getJSONArray("forecasts");
                int length = jsonTickets.length();

                for (int i = 0; i < length; i++) {
                    String hour = jsonTickets.getJSONObject(i).getString("hour");
                    String date = jsonTickets.getJSONObject(i).getString("date");
                    String next = jsonTickets.getJSONObject(i).getString("next");
                    String times_from = jsonTickets.getJSONObject(i).getString("times_from");
                    String times_to = jsonTickets.getJSONObject(i).getString("times_to");
                    String temperatures = jsonTickets.getJSONObject(i).getString("temperatures");
                    String wind_speeds = jsonTickets.getJSONObject(i).getString("wind_speeds");
                    String wind_directs = jsonTickets.getJSONObject(i).getString("wind_directs");
                    String preasures = jsonTickets.getJSONObject(i).getString("preasures");
                    String situations = jsonTickets.getJSONObject(i).getString("situations");
                    String precipitations = jsonTickets.getJSONObject(i).getString("precipitations");
                    String station = jsonTickets.getJSONObject(i).getString("station_number");
                    String latitude = jsonTickets.getJSONObject(i).getString("latitude");
                    String longitude = jsonTickets.getJSONObject(i).getString("longitude");
                    isInserted = myDb.insertDataForecasts(hour, date, next, times_from, times_to, temperatures, wind_speeds, wind_directs, preasures, situations, precipitations, station, latitude, longitude);
                }
                if(isInserted == true) {
//                    Toast.makeText(context, "Pobrano i zapisano Dane", Toast.LENGTH_LONG).show();
                }
//                else Toast.makeText(context, String.format( "Nie zapisano dnaych."), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

    private void loadMeasurementsFromAPI(String url) {
        WelcomeActivity.GetMeasurements getMeasurements = new WelcomeActivity.GetMeasurements(this);
        getMeasurements.setMessageLoading("Uruchamianie aplikacji...");
        myDb.deleteDataMeasurementsAll();
        getMeasurements.execute(url);
    }

    private class GetMeasurements extends UrlJsonAsyncTask {
        public GetMeasurements(Context context) {
            super(context);
        }
        boolean isInserted;

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonTickets = json.getJSONObject("data").getJSONArray("measurements");
                int length = jsonTickets.length();

                for (int i = 0; i < length; i++) {
                    String hour = jsonTickets.getJSONObject(i).getString("hour");
                    String temperature = jsonTickets.getJSONObject(i).getString("temperature");
                    String wind_speed = jsonTickets.getJSONObject(i).getString("wind_speed");
                    String wind_direct = jsonTickets.getJSONObject(i).getString("wind_direct");
                    String humidity = jsonTickets.getJSONObject(i).getString("humidity");
                    String preasure = jsonTickets.getJSONObject(i).getString("preasure");
                    String rainfall = jsonTickets.getJSONObject(i).getString("rainfall");
                    String date = jsonTickets.getJSONObject(i).getString("date");
                    String station = jsonTickets.getJSONObject(i).getString("station");
                    String station_latitude = jsonTickets.getJSONObject(i).getString("latitude");
                    String station_longitude = jsonTickets.getJSONObject(i).getString("longitude");
                    isInserted = myDb.insertDataMeasurements(hour, temperature, wind_speed, wind_direct, humidity, preasure, rainfall, date, station, station_latitude, station_longitude);

                    if (latitude > 0){
                        Location location = new Location("");
                        Location mylocation = new Location("");
                        location.setLatitude(jsonTickets.getJSONObject(i).getDouble("latitude"));
                        location.setLongitude(jsonTickets.getJSONObject(i).getDouble("longitude"));
                        mylocation.setLatitude(latitude);
                        mylocation.setLongitude(longitude);
                        int distance = Math.round(mylocation.distanceTo(location));
                    }
                }
                if(isInserted == true) {
//                    Toast.makeText(context, "Pobrano i zapisano Dane", Toast.LENGTH_LONG).show();
                }
//                else Toast.makeText(context, String.format( "Nie zapisano dnaych."), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

    private void loadMeasursFromAPI(String url) {
        WelcomeActivity.GetMeasurs getMeasurs = new WelcomeActivity.GetMeasurs(this);
        getMeasurs.setMessageLoading("Uruchamianie aplikacji...");
        myDb.deleteDataGiossAll();
        getMeasurs.execute(url);
    }

    private class GetMeasurs extends UrlJsonAsyncTask {
        public GetMeasurs(Context context) {
            super(context);
        }
        boolean isInserted;

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonMeasurs = json.getJSONObject("data").getJSONArray("gios_measurments");
                int length = jsonMeasurs.length();

                for (int i = 0; i < length; i++) {
                    String station = jsonMeasurs.getJSONObject(i).getString("station");
                    String calc_date = jsonMeasurs.getJSONObject(i).getString("calc_date");
                    String st_index = jsonMeasurs.getJSONObject(i).getString("st_index");
                    String co_index = jsonMeasurs.getJSONObject(i).getString("co_index");
                    String pm10_index = jsonMeasurs.getJSONObject(i).getString("pm10_index");
                    String c6h6_index = jsonMeasurs.getJSONObject(i).getString("c6h6_index");
                    String no2_index = jsonMeasurs.getJSONObject(i).getString("no2_index");
                    String pm25_index = jsonMeasurs.getJSONObject(i).getString("pm25_index");
                    String o3_index = jsonMeasurs.getJSONObject(i).getString("o3_index");
                    String so2_index = jsonMeasurs.getJSONObject(i).getString("so2_index");
                    String co_value = jsonMeasurs.getJSONObject(i).getString("co_value");
                    String pm10_value = jsonMeasurs.getJSONObject(i).getString("pm10_value");
                    String c6h6_value = jsonMeasurs.getJSONObject(i).getString("c6h6_value");
                    String no2_value = jsonMeasurs.getJSONObject(i).getString("no2_value");
                    String pm25_value = jsonMeasurs.getJSONObject(i).getString("pm25_value");
                    String o3_value = jsonMeasurs.getJSONObject(i).getString("o3_value");
                    String so2_value = jsonMeasurs.getJSONObject(i).getString("so2_value");
                    String co_date = jsonMeasurs.getJSONObject(i).getString("co_date");
                    String pm10_date = jsonMeasurs.getJSONObject(i).getString("pm10_date");
                    String c6h6_date = jsonMeasurs.getJSONObject(i).getString("c6h6_date");
                    String no2_date = jsonMeasurs.getJSONObject(i).getString("no2_date");
                    String pm25_date = jsonMeasurs.getJSONObject(i).getString("pm25_date");
                    String o3_date = jsonMeasurs.getJSONObject(i).getString("o3_date");
                    String so2_date = jsonMeasurs.getJSONObject(i).getString("so2_date");
                    String gios_latitude = jsonMeasurs.getJSONObject(i).getString("latitude");
                    String gios_longitude = jsonMeasurs.getJSONObject(i).getString("longitude");
                    isInserted = myDb.insertDataGiosMeasurments(station, calc_date, st_index, co_index,
                            pm10_index, c6h6_index, no2_index, pm25_index, o3_index, so2_index, co_value,
                            pm10_value, c6h6_value, no2_value, pm25_value, o3_value, so2_value, co_date,
                            pm10_date, c6h6_date, no2_date, pm25_date, o3_date, so2_date, gios_latitude, gios_longitude);
                }
                if(isInserted == true) {
//                    Toast.makeText(context, "Pobrano i zapisano Dane", Toast.LENGTH_LONG).show();
                }
//                else Toast.makeText(context, String.format( "Nie zapisano dnaych."), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

    private void loadMetarsFromAPI(String url) {
        WelcomeActivity.GetMetars getMetars = new WelcomeActivity.GetMetars(this);
        getMetars.setMessageLoading("Uruchamianie aplikacji...");
        myDb.deleteDataMetarsAll();
        getMetars.execute(url);
    }

    private class GetMetars extends UrlJsonAsyncTask {
        public GetMetars(Context context) {
            super(context);
        }
        boolean isInserted;

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonMetars = json.getJSONObject("data").getJSONArray("metar_raports");
                int length = jsonMetars.length();

                for (int i = 0; i < length; i++) {
                    String day = jsonMetars.getJSONObject(i).getString("day");
                    String hour = jsonMetars.getJSONObject(i).getString("hour");
                    String metar = jsonMetars.getJSONObject(i).getString("metar");
                    String message = jsonMetars.getJSONObject(i).getString("message");
                    String visibility = jsonMetars.getJSONObject(i).getString("visibility");
                    String cloud_cover = jsonMetars.getJSONObject(i).getString("cloud_cover");
                    String wind_direct = jsonMetars.getJSONObject(i).getString("wind_direct");
                    String wind_speed = jsonMetars.getJSONObject(i).getString("wind_speed");
                    String temperature = jsonMetars.getJSONObject(i).getString("temperature");
                    String pressure = jsonMetars.getJSONObject(i).getString("pressure");
                    String situation = jsonMetars.getJSONObject(i).getString("situation");
                    String created_at = jsonMetars.getJSONObject(i).getString("created_at");
                    String station = jsonMetars.getJSONObject(i).getString("station");
                    String station_latitude = jsonMetars.getJSONObject(i).getString("latitude");
                    String station_longitude = jsonMetars.getJSONObject(i).getString("longitude");
                    isInserted = myDb.insertDataMetarRaports(station, day, hour, metar, message, created_at,
                            visibility, cloud_cover, wind_direct, wind_speed, temperature, pressure, situation,
                            station_latitude, station_longitude);
                }
                if(isInserted == true) {
//                    Toast.makeText(context, "Pobrano i zapisano Dane", Toast.LENGTH_LONG).show();
                }
//                else Toast.makeText(context, String.format( "Nie zapisano dnaych."), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }
}
