package pl.kp_software.synoptyk;

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

        getNearest();

        return rootView;
    }

    private void getNearest(){
        List<String> measurementsList = new ArrayList<String>();
        List<Float> measurementsDistances = new ArrayList<Float>();
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

        int minIndex = measurementsDistances.indexOf(Collections.min(measurementsDistances));
        String measurementContent = measurementsList.get(minIndex);
        TextView measurementsTextView = (TextView) rootView.findViewById(R.id.measurementsTextView);
        measurementsTextView.setText(measurementContent);


        List<String> metarsList = new ArrayList<String>();
        List<Float> metarsDistances = new ArrayList<Float>();
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

        int minMetar = metarsDistances.indexOf(Collections.min(metarsDistances));
        String metarContent = metarsList.get(minMetar);
        TextView metarTextView = (TextView) rootView.findViewById(R.id.metarTextView);
        metarTextView.setText(metarContent);

        List<String> giossList = new ArrayList<String>();
        List<Float> giosDistances = new ArrayList<Float>();
        Cursor giossCursor = myDb.getAllDataGiosMeasurments();
        if(giossCursor.getCount() == 0) {
            Log.d("GIOS nearest", "Brak Danych");
            return;
        } else {
            while(giossCursor.moveToNext()) {
                String station = giossCursor.getString(0);
                String calc_date = giossCursor.getString(1);
                String gios_latitude = giossCursor.getString(2);
                String gios_longitude = giossCursor.getString(3);
                String st_index;
                try{ st_index = get_index_level(Integer.parseInt(giossCursor.getString(2)));
                } catch(NumberFormatException ex) { st_index = get_index_level(6); }
                String id  = giossCursor.getString(3);
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

        int minGios = giosDistances.indexOf(Collections.min(giosDistances));
        String giosContent = giossList.get(minGios);
        TextView giosTextView = (TextView) rootView.findViewById(R.id.giosTextView);
        giosTextView.setText(giosContent);
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
