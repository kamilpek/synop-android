package pl.kp_software.synoptyk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MeasurementFragment extends Fragment {
    DatabaseHelper myDb;
    View rootView = null;

    public MeasurementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDb = new DatabaseHelper(getActivity());
        rootView = inflater.inflate(R.layout.fragment_measurement, container, false);
        String measurement_id = getArguments().getString("measurement_id");
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MeasurementsFragment";
        ((MainActivity) getActivity()).setActionBarTitle("Dane synoptyczne");

        viewMeasurement(measurement_id);

        return rootView;
    }

    public String cardinals(Integer degree){
        String cardinal = null;
        if(0 < degree && degree < 1 ){
            cardinal = "Bezwietrznie";
        } else if(1 < degree && degree < 22){
            cardinal = "Północny";
        } else if(22 < degree && degree < 67){
            cardinal = "Północno Wschodni";
        } else if(67 < degree && degree < 112){
            cardinal = "Wschodni";
        } else if(112 < degree && degree < 157){
            cardinal = "Południowo Wschodni";
        } else if(157 < degree && degree < 202){
            cardinal = "Południowy";
        } else if(202 < degree && degree < 247){
            cardinal = "Południowo Zachodni";
        } else if(247 < degree && degree < 292){
            cardinal = "Zachodni";
        } else if(292 < degree && degree < 337){
            cardinal = "Północno Zachodni";
        } else if(337 < degree && degree < 360){
            cardinal = "Północny";
        } else {
            cardinal = "Brak Danych";
        }
        return cardinal;
    }

    public void viewMeasurement(String measurement_id){
        Cursor measurementCursor = myDb.getDataMeasurement(measurement_id);
        if(measurementCursor.getCount() == 0){
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(measurementCursor.moveToNext()){
                String hour = measurementCursor.getString(0);
                String temperature = measurementCursor.getString(1);
                String wind_speed = measurementCursor.getString(2);
                String wind_direct = measurementCursor.getString(3);
                String humidity = measurementCursor.getString(4);
                String preasure = measurementCursor.getString(5);
                String rainfall = measurementCursor.getString(6);
                String date = measurementCursor.getString(7);
                String station = measurementCursor.getString(8);

                TextView textView_hour = rootView.findViewById(R.id.measurement_hour);
                TextView textView_temperature = rootView.findViewById(R.id.measurement_temperature);
                TextView textView_wind_speed = rootView.findViewById(R.id.measurement_wind_speed);
                TextView textView_wind_direct = rootView.findViewById(R.id.measurement_wind_direct);
                TextView textView_humidity = rootView.findViewById(R.id.measurement_humidity);
                TextView textView_preasure = rootView.findViewById(R.id.measurement_preasure);
                TextView textView_rainfall = rootView.findViewById(R.id.measurement_rainfall);
                TextView textView_date = rootView.findViewById(R.id.measurement_date);
                TextView textView_station = rootView.findViewById(R.id.measurement_station);

                textView_hour.setText(String.format("%s:00",hour));
                textView_temperature.setText(temperature);
                textView_wind_speed.setText(wind_speed);
                textView_wind_direct.setText(cardinals(Integer.parseInt(wind_direct)));
                textView_humidity.setText(humidity);
                textView_preasure.setText(preasure);
                textView_rainfall.setText(rainfall);
                textView_date.setText(date);
                textView_station.setText(station);

            }
        }
    }
}
