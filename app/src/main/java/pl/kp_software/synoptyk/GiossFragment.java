package pl.kp_software.synoptyk;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GiossFragment extends Fragment {
    DatabaseHelper myDb;

    public GiossFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gioss, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";

        if (isNetworkAvailable() == true){
            try {
                loadMetarsFromAPI("http://synoptyk.kp-software.pl/api/v1/gios_measurements.json");
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            viewAll();
        }
        return rootView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public void viewAll() {
        List<String> giossList = new ArrayList<String>();
        final List<String> giossIDs = new ArrayList<String>();
        Cursor giossCursor = myDb.getAllDataGiosMeasurments();
        if(giossCursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(giossCursor.moveToNext()) {
                String station = giossCursor.getString(0);
                String calc_date = giossCursor.getString(1);
                String st_index;
                try{
                    st_index = get_index_level(Integer.parseInt(giossCursor.getString(2)));
                } catch(NumberFormatException ex){
                    st_index = get_index_level(6);
                }
                String id  = giossCursor.getString(3);
                giossList.add(station + "\n" + calc_date +
                        " \nPolski indeks jakości powietrza:\n" + st_index );
                giossIDs.add(id);
            }
        }

        ListView giossListView = (ListView) getView().findViewById(R.id.gioss_list_view);
        if (giossListView != null) {
            giossListView.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, giossList));
            giossListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            String measur_id = giossIDs.get((int) id).toString();

                            Bundle bundle = new Bundle();
                            bundle.putString("measur_id", measur_id);

                            GiosFragment giosFragment = new GiosFragment();
                            giosFragment.setArguments(bundle);
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, giosFragment);
                            fragmentTransaction.commit();
                        }
                    }
            );
        } else {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_LONG).show();
        }
    }

    private void loadMetarsFromAPI(String url) {
        GiossFragment.GetMeasurs getMeasurements = new GiossFragment.GetMeasurs(getActivity());
        getMeasurements.setMessageLoading("Pobieranie pomiarów...");
        myDb.deleteDataGiossAll();
        getMeasurements.execute(url);
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
                    isInserted = myDb.insertDataGiosMeasurments(station, calc_date, st_index, co_index, pm10_index, c6h6_index, no2_index, pm25_index, o3_index, so2_index, co_value, pm10_value, c6h6_value, no2_value, pm25_value, o3_value, so2_value, co_date, pm10_date, c6h6_date, no2_date, pm25_date, o3_date, so2_date);
                }
                if(isInserted == true) {
                    Toast.makeText(getActivity(), "Pobrano i zapisano Dane", Toast.LENGTH_LONG).show();
                    viewAll();
                }
                else
                    Toast.makeText(getActivity(), String.format( "Nie zapisano dnaych."), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

}
