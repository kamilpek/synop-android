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

public class MetarFragment extends Fragment {
    DatabaseHelper myDb;
    View rootView = null;

    public MetarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDb = new DatabaseHelper(getActivity());
        rootView = inflater.inflate(R.layout.fragment_metar, container, false);
        String metar_id = getArguments().getString("metar_id");
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MetarsFragment";
        viewMeasurement(metar_id);
        return rootView;
    }

    public void viewMeasurement(String metar_id){
        Cursor metarCursor = myDb.getDataMetarRaport(metar_id);
        if(metarCursor.getCount() == 0){
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(metarCursor.moveToNext()){
                String station = metarCursor.getString(0);
                String day = metarCursor.getString(1);
                String hour = metarCursor.getString(2);
                String metar = metarCursor.getString(3);
                String message = metarCursor.getString(4);
//                String created_at = metarCursor.getString(5);
                String situation = metarCursor.getString(6);
                String visibility = metarCursor.getString(7);
                String cloud_cover = metarCursor.getString(8);
                String wind_direct = metarCursor.getString(9);
                String wind_speed = metarCursor.getString(10);
                String tempreature = metarCursor.getString(11);
                String pressure = metarCursor.getString(12);

                TextView textView_day = rootView.findViewById(R.id.metar_day);
                TextView textView_hour = rootView.findViewById(R.id.metar_hour);
                TextView textView_metar = rootView.findViewById(R.id.metar_metar);
                TextView textView_message = rootView.findViewById(R.id.metar_message);
                TextView textView_visibility = rootView.findViewById(R.id.metar_visibility);
                TextView textView_cloud_cover = rootView.findViewById(R.id.metar_cloud_cover);
                TextView textView_wind_direct = rootView.findViewById(R.id.metar_wind_direct);
                TextView textView_wind_speed = rootView.findViewById(R.id.metar_wind_speed);
                TextView textView_tempreature = rootView.findViewById(R.id.metar_tempreature);
                TextView textView_preasure = rootView.findViewById(R.id.metar_preasure);
                TextView textView_situation = rootView.findViewById(R.id.metar_situation);
//                TextView textView_created_at = rootView.findViewById(R.id.metar_created_at);
                TextView textView_station = rootView.findViewById(R.id.metar_station);

                textView_tempreature.setText(tempreature);
                textView_station.setText(station);
                textView_day.setText(day);
                textView_hour.setText(hour);
                textView_message.setText(message);
                textView_visibility.setText(visibility);
                textView_cloud_cover.setText(cloud_cover);
                textView_wind_direct.setText(wind_direct);
                textView_wind_speed.setText(wind_speed);
                textView_preasure.setText(pressure);
                textView_situation.setText(situation);
                textView_metar.setText(metar);
            }
        }
    }
}

