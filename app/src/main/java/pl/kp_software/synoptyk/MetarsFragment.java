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

public class MetarsFragment extends Fragment {
    DatabaseHelper myDb;

    public MetarsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_metars, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";

        if (isNetworkAvailable() == true){
            try {
                loadMetarsFromAPI("http://synoptyk.kp-software.pl/api/v1/metar_raports.json");
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

    public void viewAll() {
        List<String> metarsList = new ArrayList<String>();
        final List<String> metarsIDs = new ArrayList<String>();
        Cursor metarsCursor = myDb.getAllDataMetarRaports();
        if(metarsCursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(metarsCursor.moveToNext()) {
                String station = metarsCursor.getString(0);
                String day = metarsCursor.getString(1);
                String hour = metarsCursor.getString(2);
                String date = metarsCursor.getString(3);
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
                metarsList.add(station + " - " + date + " - " + hour + " UTC" +
                        " | Temperatura: " + temperature + (char) 0x00B0 + "C - Prędkość wiatru " + wind_speed + "m/s");
                metarsIDs.add(id);
            }
        }

        ListView metarsListView = (ListView) getView().findViewById(R.id.metars_list_view);
        if (metarsListView != null) {
            metarsListView.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, metarsList));
            metarsListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            String metar_id = metarsIDs.get((int) id).toString();

                            Bundle bundle = new Bundle();
                            bundle.putString("metar_id", metar_id);

                            MetarFragment metarFragment = new MetarFragment();
                            metarFragment.setArguments(bundle);
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, metarFragment);
                            fragmentTransaction.commit();
                        }
                    }
            );
        } else {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_LONG).show();
        }
    }

    private void loadMetarsFromAPI(String url) {
        MetarsFragment.GetMetars getMeasurements = new MetarsFragment.GetMetars(getActivity());
        getMeasurements.setMessageLoading("Pobieranie pomiarów...");
        myDb.deleteDataMeasurementsAll();
        getMeasurements.execute(url);
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
