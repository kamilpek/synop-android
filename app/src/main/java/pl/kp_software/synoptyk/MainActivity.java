package pl.kp_software.synoptyk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    public static boolean mainFragment_active = false;
    public static String lastFragment = null;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        MainFragment fragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (isNetworkAvailable() == true){
            try {
                loadForecastsFromAPI("http://synoptyk.kp-software.pl/api/v1/forecasts.json");
                loadMeasurementsFromAPI("http://synoptyk.kp-software.pl/api/v1/measurements.json");
                loadMetarsFromAPI("http://synoptyk.kp-software.pl/api/v1/metar_raports.json");
                loadMeasursFromAPI("http://synoptyk.kp-software.pl/api/v1/gios_measurements.json");
//                loadStationsFromAPI("http://synoptyk.kp-software.pl/api/v1/stations.json");
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction0 =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction0.replace(R.id.fragment_container, fragment);
            fragmentTransaction0.commit();
            if (lastFragment == "MeasurementsFragment"){
                MeasurementsFragment measurementsFragment = new MeasurementsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, measurementsFragment);
                fragmentTransaction.commit();
            } else if (lastFragment == "ForecastsFragment"){
                ForecastsFragment forecastsFragment = new ForecastsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, forecastsFragment);
                fragmentTransaction.commit();
            } else if (lastFragment == "GiossFragment"){
                GiossFragment giossFragment = new GiossFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, giossFragment);
                fragmentTransaction.commit();
            } else if (lastFragment == "MetarsFragment"){
                MetarsFragment metarsFragment = new MetarsFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, metarsFragment);
                fragmentTransaction.commit();
            } else if (lastFragment == "empty"){
                super.onBackPressed();
            } else {
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            MainFragment fragment = new MainFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_measurements) {
            MeasurementsFragment measurementsFragment = new MeasurementsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, measurementsFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_forecasts) {
            ForecastsFragment forecastsFragment = new ForecastsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, forecastsFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_metars) {
            MetarsFragment metarsFragment = new MetarsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, metarsFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_gios) {
            GiossFragment giossFragment = new GiossFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, giossFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_copyrights) {
            CopyrightsFragment copyrightsFragment = new CopyrightsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, copyrightsFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    private void loadStationsFromAPI(String url) {
//        MainActivity.GetStations getStations = new MainActivity.GetStations(this);
//        getStations.setMessageLoading("Trwa uruchamianie aplikacji...");
//        myDb.deleteDataStationsAll();
//        getStations.execute(url);
//    }
//
//    private class GetStations extends UrlJsonAsyncTask {
//        public GetStations(Context context) {
//            super(context);
//        }
//        boolean isInserted;
//
//        @Override
//        protected void onPostExecute(JSONObject json) {
//            try {
//                JSONArray jsonTickets = json.getJSONObject("data").getJSONArray("stations");
//                int length = jsonTickets.length();
//
//                for (int i = 0; i < length; i++) {
//                    String name = jsonTickets.getJSONObject(i).getString("name");
//                    String number = jsonTickets.getJSONObject(i).getString("number");
//                    String latitude = jsonTickets.getJSONObject(i).getString("latitude");
//                    String longitude = jsonTickets.getJSONObject(i).getString("longitude");
//                    String station_id = jsonTickets.getJSONObject(i).getString("id");
//                    isInserted = myDb.insertDataStations(name, number, latitude, longitude, station_id);
//                }
//                if(isInserted == true) {
//                    Toast.makeText(context, "Pobrano i zapisano stacje", Toast.LENGTH_LONG).show();
//                }
//                else
//                    Toast.makeText(context, String.format( "Nie zapisano stacji."), Toast.LENGTH_LONG).show();
//
//            } catch (Exception e) {
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
//            } finally {
//                super.onPostExecute(json);
//            }
//        }
//    }

    private void loadForecastsFromAPI(String url) {
        MainActivity.GetForecasts getForecasts = new MainActivity.GetForecasts(this);
        getForecasts.setMessageLoading("Pobieranie prognozy...");
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
                    isInserted = myDb.insertDataForecasts(hour, date, next, times_from, times_to, temperatures, wind_speeds, wind_directs, preasures, situations, precipitations, station);
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
        MainActivity.GetMeasurements getMeasurements = new MainActivity.GetMeasurements(this);
        getMeasurements.setMessageLoading("Pobieranie pomiarów...");
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
                    isInserted = myDb.insertDataMeasurements(hour, temperature, wind_speed, wind_direct, humidity, preasure, rainfall, date, station);
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
        MainActivity.GetMeasurs getMeasurs = new MainActivity.GetMeasurs(this);
        getMeasurs.setMessageLoading("Pobieranie pomiarów...");
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
                    isInserted = myDb.insertDataGiosMeasurments(station, calc_date, st_index, co_index,
                            pm10_index, c6h6_index, no2_index, pm25_index, o3_index, so2_index, co_value,
                            pm10_value, c6h6_value, no2_value, pm25_value, o3_value, so2_value, co_date,
                            pm10_date, c6h6_date, no2_date, pm25_date, o3_date, so2_date);
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
        MainActivity.GetMetars getMetars = new MainActivity.GetMetars(this);
        getMetars.setMessageLoading("Pobieranie pomiarów...");
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
                    isInserted = myDb.insertDataMetarRaports(station, day, hour, metar, message, created_at, visibility, cloud_cover, wind_direct, wind_speed, temperature, pressure, situation);
                }
                if(isInserted == true) {
                    Toast.makeText(context, "Pobrano i zapisano Dane", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(context, String.format( "Nie zapisano dnaych."), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }
}
