package pl.kp_software.synoptyk;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment {
    View rootView;
    DatabaseHelper myDb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = true;
        MainActivity.lastFragment = "empty";
        ((MainActivity) getActivity()).setActionBarTitle("Synoptyk");

        Button measurementsButton = (Button) rootView.findViewById(R.id.measurementsButton);
        measurementsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        MeasurementsFragment measurementsFragment = new MeasurementsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, measurementsFragment);
                        fragmentTransaction.commit();
                    }
                });

        Button forecastsButton = (Button) rootView.findViewById(R.id.forecastsButton);
        forecastsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ForecastsFragment forecastsFragment = new ForecastsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, forecastsFragment);
                        fragmentTransaction.commit();
                    }
                });

        Button metarsButton = (Button) rootView.findViewById(R.id.metarsButton);
        metarsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        MetarsFragment metarsFragment = new MetarsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, metarsFragment);
                        fragmentTransaction.commit();
                    }
                });

        Button giosButton = (Button) rootView.findViewById(R.id.giosButton);
        giosButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        GiossFragment giossFragment = new GiossFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, giossFragment);
                        fragmentTransaction.commit();
                    }
                });

//        getNearest();

        try{
            getNearest();
        } catch (Exception e){
            Log.d("getNearest()", "Nie można wyświetlić najbliższych: " + e);
        }

        return rootView;
    }

    private void getNearest(){
        List<String> measurementsList = new ArrayList<String>();
        List<Float> measurementsDistances = new ArrayList<Float>();
        final List<String> measurementsIDs = new ArrayList<String>();
        Cursor measurementsCursor = myDb.getAllDataMeasurements();
        if(measurementsCursor.getCount() == 0) {
            Log.d("Measurments nearest", "Brak Danych");
            return;
        } else {
            while(measurementsCursor.moveToNext()) {
                String hour = measurementsCursor.getString(0);
                String temperature = measurementsCursor.getString(1);
                String rainfall = measurementsCursor.getString(6);
                String date = measurementsCursor.getString(7);
                String station = measurementsCursor.getString(8);
                String id = measurementsCursor.getString(9);
                measurementsIDs.add(id);
                String station_latitude = measurementsCursor.getString(10);
                String station_longitude = measurementsCursor.getString(11);
                measurementsList.add(station + " - " + date + " - " + hour + " UTC" +
                        "\nTemperatura: " + temperature + (char) 0x00B0 + "C, Opad: " + rainfall + "mm");

                if (WelcomeActivity.latitude > 0){
                    Location location = new Location("");
                    Location mylocation = new Location("");
                    location.setLatitude(Double.parseDouble(station_latitude));
                    location.setLongitude(Double.parseDouble(station_longitude));
                    mylocation.setLatitude(WelcomeActivity.latitude);
                    mylocation.setLongitude(WelcomeActivity.longitude);
                    float distance = mylocation.distanceTo(location);
                    measurementsDistances.add(distance);
                }
            }
        }

        final int minIndex = measurementsDistances.indexOf(Collections.min(measurementsDistances));
        String measurementContent = measurementsList.get(minIndex);
        TextView measurementsTextView = (TextView) rootView.findViewById(R.id.measurementsTextView);
        measurementsTextView.setText(measurementContent);

        measurementsTextView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        String measurement_id = measurementsIDs.get((int) minIndex).toString();
                        bundle.putString("measurement_id", measurement_id);
                        bundle.putString("last_fragment", "MainFragment");
                        MeasurementFragment measurementFragment = new MeasurementFragment();
                        measurementFragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, measurementFragment);
                        fragmentTransaction.commit();
                    }
                });

//        ----------

        List<String> forecastsList = new ArrayList<String>();
        List<Float> forecastsDistances = new ArrayList<Float>();
        final List<String> forecastsIDs = new ArrayList<String>();
        Cursor forecastsCursor = myDb.getAllDataForecasts();
        if(forecastsCursor.getCount() == 0) {
            Log.d("Forecasts nearest", "Brak Danych");
            return;
        } else {
            while(forecastsCursor.moveToNext()) {
                String hour = forecastsCursor.getString(0);
                String date = forecastsCursor.getString(1);
                String next = forecastsCursor.getString(2);
                String station = forecastsCursor.getString(11);
                String id = forecastsCursor.getString(12);
                String station_latitude = forecastsCursor.getString(13);
                String station_longitude = forecastsCursor.getString(14);
                forecastsList.add(station + " - " + date + " - " + hour + " UTC" +
                        "\nNastępna prognoza: " + next );
                forecastsIDs.add(id);

                if (WelcomeActivity.latitude > 0){
                    Location location = new Location("");
                    Location mylocation = new Location("");
                    location.setLatitude(Double.parseDouble(station_latitude));
                    location.setLongitude(Double.parseDouble(station_longitude));
                    mylocation.setLatitude(WelcomeActivity.latitude);
                    mylocation.setLongitude(WelcomeActivity.longitude);
                    float distance = mylocation.distanceTo(location);
                    forecastsDistances.add(distance);
                }
            }
        }

        final int minForecast = forecastsDistances.indexOf(Collections.min(forecastsDistances));
        String forecastContent = forecastsList.get(minIndex);
        TextView forecastTextView = (TextView) rootView.findViewById(R.id.forecastTextView);
        forecastTextView.setText(forecastContent);

        forecastTextView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        String forecast_id = forecastsIDs.get((int) minForecast).toString();
                        bundle.putString("forecast_id", forecast_id);
                        bundle.putString("last_fragment", "MainFragment");
                        ForecastFragment forecastFragment = new ForecastFragment();
                        forecastFragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, forecastFragment);
                        fragmentTransaction.commit();
                    }
                });

//        ----------

        List<String> metarsList = new ArrayList<String>();
        List<Float> metarsDistances = new ArrayList<Float>();
        final List<String> metarsIDs = new ArrayList<String>();
        Cursor metarsCursor = myDb.getAllDataMetarRaports();
        if(metarsCursor.getCount() == 0) {
            Log.d("Metars nearest", "Brak Danych");
            return;
        } else {
            while(metarsCursor.moveToNext()) {
                String station = metarsCursor.getString(0);
                String day = metarsCursor.getString(1);
                String hour = metarsCursor.getString(2);
                String metar = metarsCursor.getString(3);
                String message = metarsCursor.getString(4);
                String created_at = metarsCursor.getString(5);
                String situation = metarsCursor.getString(6);
                String visibility = metarsCursor.getString(7);
                String cloud_cover = metarsCursor.getString(8);
                String wind_direct = metarsCursor.getString(9);
                String wind_speed = metarsCursor.getString(10);
                String temperature = metarsCursor.getString(11);
                String pressure = metarsCursor.getString(12);
                String id = metarsCursor.getString(13);
                metarsIDs.add(id);
                String metar_latitude = metarsCursor.getString(14);
                String metar_longitude = metarsCursor.getString(15);
                metarsList.add(station + " - " + created_at + " - " + hour + " UTC\n" +
                        "Temperatura: " + temperature + "\nPrędkość i kierunek wiatru: " + wind_speed +
                        "m/s, " + wind_direct + " stopni\nCiśnienie atmosferyczne: " + pressure +
                        "\nWidzialność pozioma: " + visibility);

                if (WelcomeActivity.latitude > 0){
                    Location location = new Location("");
                    Location mylocation = new Location("");
                    location.setLatitude(Double.parseDouble(metar_latitude));
                    location.setLongitude(Double.parseDouble(metar_longitude));
                    mylocation.setLatitude(WelcomeActivity.latitude);
                    mylocation.setLongitude(WelcomeActivity.longitude);
                    float distance = mylocation.distanceTo(location);
                    metarsDistances.add(distance);
                }
            }
        }

        final int minMetar = metarsDistances.indexOf(Collections.min(metarsDistances));
        String metarContent = metarsList.get(minMetar);
        TextView metarTextView = (TextView) rootView.findViewById(R.id.metarTextView);
        metarTextView.setText(metarContent);

        metarTextView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String metar_id = metarsIDs.get((int) minMetar).toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("metar_id", metar_id);
                        bundle.putString("last_fragment", "MainFragment");
                        MetarFragment metarFragment = new MetarFragment();
                        metarFragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, metarFragment);
                        fragmentTransaction.commit();
                    }
                });

//        ----------

        List<String> giossList = new ArrayList<String>();
        List<Float> giosDistances = new ArrayList<Float>();
        final List<String> giossIDs = new ArrayList<String>();
        Cursor giossCursor = myDb.getAllDataGiosMeasurments();
        if(giossCursor.getCount() == 0) {
            Log.d("GIOS nearest", "Brak Danych");
            return;
        } else {
            while(giossCursor.moveToNext()) {
                String station = giossCursor.getString(0);
                String calc_date = giossCursor.getString(1);
                String gios_latitude = giossCursor.getString(25);
                String gios_longitude = giossCursor.getString(26);
                String id  = giossCursor.getString(3);
                giossIDs.add(id);
                String st_index;
                try{ st_index = get_index_level(Integer.parseInt(giossCursor.getString(2)));
                } catch(NumberFormatException ex) { st_index = get_index_level(6); }
                giossList.add(station + " - " + calc_date + "\nPolski indeks jakości powietrza:\n" + st_index );

                if (WelcomeActivity.latitude > 0){
                    Location location = new Location("");
                    Location mylocation = new Location("");
                    location.setLatitude(Double.parseDouble(gios_latitude));
                    location.setLongitude(Double.parseDouble(gios_longitude));
                    mylocation.setLatitude(WelcomeActivity.latitude);
                    mylocation.setLongitude(WelcomeActivity.longitude);
                    float distance = mylocation.distanceTo(location);
                    giosDistances.add(distance);
                }
            }
        }

        final int minGios = giosDistances.indexOf(Collections.min(giosDistances));
        String giosContent = giossList.get(minGios);
        TextView giosTextView = (TextView) rootView.findViewById(R.id.giosTextView);
        giosTextView.setText(giosContent);

        giosTextView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        String measur_id = giossIDs.get((int) minGios).toString();
                        Bundle bundle = new Bundle();
                        bundle.putString("measur_id", measur_id);
                        bundle.putString("last_fragment", "MainFragment");
                        GiosFragment giosFragment = new GiosFragment();
                        giosFragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, giosFragment);
                        fragmentTransaction.commit();
                    }
                });
    }

    public String get_index_level(Integer level){
        String index_level = null;
        if(level == 0){
            index_level = "Bardzo Dobry";
        } else if(level == 1){
            index_level = "Dobry";
        } else if(level == 2){
            index_level = "Umiarkowany";
        } else if(level == 3){
            index_level = "Dostateczny";
        } else if(level == 4){
            index_level = "Zły";
        } else if(level == 5){
            index_level = "Bardzo Zły";
        } else {
            index_level = "Brak Danych";
        }
        return index_level;
    }
}
