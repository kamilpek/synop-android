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

public class GiosFragment extends Fragment {
    DatabaseHelper myDb;
    View rootView = null;

    public GiosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDb = new DatabaseHelper(getActivity());
        rootView = inflater.inflate(R.layout.fragment_gios, container, false);
        String measur_id = getArguments().getString("measur_id");
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "GiossFragment";

        viewMeasurement(measur_id);

        return rootView;
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

    public void viewMeasurement(String measur_id){
        Cursor giosCursor = myDb.getDataGiosMeasurment(measur_id);
        if(giosCursor.getCount() == 0){
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(giosCursor.moveToNext()){
                String station = giosCursor.getString(0);
                String calc_date = giosCursor.getString(1);
                String st_index;
                try{
                    st_index = get_index_level(Integer.parseInt(giosCursor.getString(2)));
                } catch(NumberFormatException ex){
                    st_index = get_index_level(6);
                }

                TextView textView_station = rootView.findViewById(R.id.gios_station);
                TextView textView_calc_date = rootView.findViewById(R.id.gios_calc_date);
                TextView textView_st_index = rootView.findViewById(R.id.gios_st_index);

                textView_station.setText(station);
                textView_calc_date.setText(calc_date);
                textView_st_index.setText(st_index);
            }
        }
    }

}
