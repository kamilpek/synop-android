package pl.kp_software.synoptyk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GiosFragment extends Fragment {
    DatabaseHelper myDb;
    View rootView = null;

    public GiosFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myDb = new DatabaseHelper(getActivity());
        rootView = inflater.inflate(R.layout.fragment_gios, container, false);
        String measur_id = getArguments().getString("measur_id");
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "GiossFragment";
        ((MainActivity) getActivity()).setActionBarTitle("Jakość powietrza");

        try {
            viewMeasurement(measur_id);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewMeasurement(String measur_id) throws ParseException {
        Cursor giosCursor = myDb.getDataGiosMeasurment(measur_id);
        if(giosCursor.getCount() == 0){
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(giosCursor.moveToNext()){
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat date_destination = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                String measur = "";
                String station = giosCursor.getString(0);
                String calc_date = giosCursor.getString(1);
                String st_index;
                try{
                    st_index = get_index_level(Integer.parseInt(giosCursor.getString(2)));
                } catch(NumberFormatException ex){
                    st_index = get_index_level(6);
                }
                float co_value;
                try{
                    co_value = Float.parseFloat(giosCursor.getString(10));
                } catch(NumberFormatException ex){
                    co_value = 0;
                }
                if(co_value != 0){
                    String co_date = giosCursor.getString(17);
//                    if(co_date != "0"){
//                        Date co_date_parse = date_format.parse(co_date);
//                        co_date = date_destination.format(co_date_parse);
//                    }
                    String co_index;
                    try{
                        co_index = get_index_level(Integer.parseInt(giosCursor.getString(3)));
                    } catch(NumberFormatException ex){
                        co_index = get_index_level(6);
                    }
                    measur = ("CO: " + co_value + " µg/m3\nData pomiaru: " + co_date + "\nIndeks: " + co_index + "\n\n");
                }
                float pm10_value;
                try{
                    pm10_value = Float.parseFloat(giosCursor.getString(11));
                } catch(NumberFormatException ex){
                    pm10_value = 0;
                }
                if(pm10_value != 0){
                    String pm10_date = giosCursor.getString(18);
//                    if(pm10_date != "0"){
//                        Date pm10_date_parse = date_format.parse(pm10_date);
//                        pm10_date = date_destination.format(pm10_date_parse);
//                    }
                    String pm10_index;
                    try{
                        pm10_index = get_index_level(Integer.parseInt(giosCursor.getString(4)));
                    } catch(NumberFormatException ex){
                        pm10_index = get_index_level(6);
                    }
                    measur = (measur + "PM10: " + pm10_value + " µg/m3\nData pomiaru: " + pm10_date + "\nIndeks: " + pm10_index + "\n\n");
                }
                float c6h6_value;
                try{
                    c6h6_value = Float.parseFloat(giosCursor.getString(12));
                } catch(NumberFormatException ex){
                    c6h6_value = 0;
                }
                if(c6h6_value != 0){
                    String c6h6_date = giosCursor.getString(18);
//                    if(c6h6_date != "0"){
//                        Date c6h6_date_parse = date_format.parse(c6h6_date);
//                        c6h6_date = date_destination.format(c6h6_date_parse);
//                    }
                    String c6h6_index;
                    try{
                        c6h6_index = get_index_level(Integer.parseInt(giosCursor.getString(5)));
                    } catch(NumberFormatException ex){
                        c6h6_index = get_index_level(6);
                    }
                    measur = (measur + "C6H6: " + c6h6_value + " µg/m3\nData pomiaru: " + c6h6_date + "\nIndeks: " + c6h6_index + "\n\n");
                }
                float no2_value;
                try{
                    no2_value = Float.parseFloat(giosCursor.getString(13));
                } catch(NumberFormatException ex){
                    no2_value = 0;
                }
                if(no2_value != 0){
                    String no2_date = giosCursor.getString(18);
//                    if(no2_date != "0"){
//                        Date no2_date_parse = date_format.parse(no2_date);
//                        no2_date = date_destination.format(no2_date_parse);
//                    }
                    String no2_index;
                    try{
                        no2_index = get_index_level(Integer.parseInt(giosCursor.getString(6)));
                    } catch(NumberFormatException ex){
                        no2_index = get_index_level(6);
                    }
                    measur = (measur + "NO2: " + no2_value + " µg/m3\nData pomiaru: " + no2_date + "\nIndeks: " + no2_index + "\n\n");
                }
                float pm25_value;
                try{
                    pm25_value = Float.parseFloat(giosCursor.getString(14));
                } catch(NumberFormatException ex){
                    pm25_value = 0;
                }
                if(pm25_value != 0){
                    String pm25_date = giosCursor.getString(18);
//                    if(pm25_date != "0"){
//                        Date pm25_date_parse = date_format.parse(pm25_date );
//                        pm25_date = date_destination.format(pm25_date_parse);
//                    }
                    String pm25_index;
                    try{
                        pm25_index = get_index_level(Integer.parseInt(giosCursor.getString(7)));
                    } catch(NumberFormatException ex){
                        pm25_index = get_index_level(6);
                    }
                    measur = (measur + "PM25: " + pm25_value + " µg/m3\nData pomiaru: " + pm25_date + "\nIndeks: " + pm25_index + "\n\n");
                }
                float o3_value;
                try{
                    o3_value = Float.parseFloat(giosCursor.getString(15));
                } catch(NumberFormatException ex){
                    o3_value = 0;
                }
                if(o3_value != 0){
                    String o3_date = giosCursor.getString(18);
//                    if(o3_date != "0"){
//                        Date o3_date_parse = date_format.parse(o3_date);
//                        o3_date = date_destination.format(o3_date_parse);
//                    }
                    String o3_index;
                    try{
                        o3_index = get_index_level(Integer.parseInt(giosCursor.getString(8)));
                    } catch(NumberFormatException ex){
                        o3_index = get_index_level(6);
                    }
                    measur = (measur + "O3: " + o3_value + " µg/m3 \nData pomiaru: " + o3_date  + "\nIndeks: " + o3_index + "\n\n");
                }
                float so2_value;
                try{
                    so2_value = Float.parseFloat(giosCursor.getString(16));
                } catch(NumberFormatException ex){
                    so2_value = 0;
                }
                if(so2_value != 0){
                    String so2_date = giosCursor.getString(18);
//                    if(so2_date != "0"){
//                        Date so2_date_parse = date_format.parse(so2_date);
//                        so2_date = date_destination.format(so2_date_parse);
//                    }
                    String so2_index;
                    try{
                        so2_index = get_index_level(Integer.parseInt(giosCursor.getString(9)));
                    } catch(NumberFormatException ex){
                        so2_index = get_index_level(6);
                    }
                    measur = (measur + "SO2: " + so2_value + " µg/m3 \nData pomiaru: " + so2_date + "\nIndeks: " + so2_index + "\n");
                }

                TextView textView_station = rootView.findViewById(R.id.gios_station);
                TextView textView_calc_date = rootView.findViewById(R.id.gios_calc_date);
                TextView textView_st_index = rootView.findViewById(R.id.gios_st_index);
                TextView textView_measur = rootView.findViewById(R.id.gios_measur);

                textView_station.setText(station);
                textView_calc_date.setText(calc_date);
                textView_st_index.setText(st_index);
                textView_measur.setText(measur);
            }
        }
    }
}
