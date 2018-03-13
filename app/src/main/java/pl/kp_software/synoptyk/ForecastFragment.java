package pl.kp_software.synoptyk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForecastFragment extends Fragment {
    DatabaseHelper myDb;
    View rootView = null;

    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDb = new DatabaseHelper(getActivity());
        rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        String forecast_id = getArguments().getString("forecast_id");
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "ForecastsFragment";

        viewForecast(forecast_id);
        try {
            viewAll(forecast_id);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

    public void viewForecast(String forecast_id){
        Cursor forecastCursor = myDb.getDataForecast(forecast_id);
        if(forecastCursor.getCount() == 0){
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(forecastCursor.moveToNext()){
                String hour = forecastCursor.getString(0);
                String date = forecastCursor.getString(1);
                String next = forecastCursor.getString(2);
                String station = forecastCursor.getString(11);
                String date_hour = String.format("%s, %s:00", date, hour);

                TextView textView_date = rootView.findViewById(R.id.forecast_date);
                TextView textView_next = rootView.findViewById(R.id.forecast_next);
                TextView textView_welcome = rootView.findViewById(R.id.welcomeTitle);

                textView_welcome.setText("Prognoza dla stacji " + station + ".");
                textView_date.setText(date_hour);
                textView_next.setText(next);
            }
        }
    }

    //    HOUR, DATE, NEXT, TIMES_FROM, TIMES_TO, TEMPERATURES, WIND_SPEEDS, WIND_DIRECTS,
//                PREASURES, SITUATIONS, PRECIPITATIONS, STATION
    public void viewAll(String forecast_id) throws ParseException {
        String[] times_from = null;
        String[] times_to = null;
        String[] temperatures = null;
        String[] wind_speeds = null;
        String[] wind_directs = null;
        String[] preasures = null;
        String[] situations = null;
        String[] precipitations = null;

        List<String> forecastList = new ArrayList<String>();
        Cursor forecastCursor = myDb.getDataForecast(forecast_id);
        if (forecastCursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while (forecastCursor.moveToNext()) {
                String time_from = forecastCursor.getString(3);
                times_from = time_from.substring(1, time_from.length() - 2).split(",");
                String time_to = forecastCursor.getString(4);
                times_to = time_to.substring(1, time_to.length() - 2).split(",");
                String temperature = forecastCursor.getString(5);
                temperatures = temperature.substring(1, temperature.length() - 1).split(",");
                String wind_speed = forecastCursor.getString(6);
                wind_speeds = wind_speed.substring(1, wind_speed.length() - 1).split(",");
                String wind_direct = forecastCursor.getString(7);
                wind_directs = wind_direct.substring(1, wind_direct.length() - 1).split(",");
                String preasure = forecastCursor.getString(8);
                preasures = preasure.substring(1, preasure.length() - 1).split(",");
                String situation = forecastCursor.getString(9);
                situations = situation.substring(1, situation.length() - 1).split(",");
                String precipitation = forecastCursor.getString(10);
                precipitations = precipitation.substring(1, precipitation.length() - 1).split(",");
            }
            int size = times_from.length;
            for (int i=0; i<size; i++){
                String time_from = times_from[i].substring(1, times_from[i].length() - 11);
                String time_to = times_to[i].substring(1, times_to[i].length() - 11);
                Date date_from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(time_from);
                Date date_to = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(time_to);
                SimpleDateFormat sdfDestination = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                String strdate_from = sdfDestination.format(date_from);
                String strdate_to = sdfDestination.format(date_to);
                String wind_direct = wind_directs[i].substring(0, wind_directs[i].length() - 2);
                String cardinal = cardinals(Integer.parseInt(wind_direct));
                String temperature = temperatures[i].substring(1, temperatures[i].length() - 1);
                String situation = situations[i].substring(1, situations[i].length() - 1);
                forecastList.add(strdate_from + " - " + strdate_to + "  " +
                        "Temperatura: " + temperature + (char) 0x00B0 + "C, Ciśnienie: " + preasures[i] + "hPa, " +
                        "Prędkość wiatru: " + wind_speeds[i] + "m/s, Kierunek wiatru " +
                        "" + cardinal + ", Opady: " +
                        "" + precipitations[i] + "mm, Sytuacja (ang): " + situation + ".");
            }

            try{
                ListView forecast = (ListView) rootView.findViewById(R.id.forecast_list_view);
                if (forecast != null) {
                    forecast.setAdapter(new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, forecastList));
                } else {
                    Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
