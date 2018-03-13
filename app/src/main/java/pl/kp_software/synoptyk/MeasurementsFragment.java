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

public class MeasurementsFragment extends Fragment {
    DatabaseHelper myDb;

    public MeasurementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurements, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";

        if (isNetworkAvailable() == true){
            try {
                loadMeasurementsFromAPI("http://synoptyk.kp-software.pl/api/v1/measurements.json");
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
        List<String> measurementsList = new ArrayList<String>();
        final List<String> measurementsIDs = new ArrayList<String>();
        Cursor measurementsCursor = myDb.getAllDataMeasurements();
        if(measurementsCursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(measurementsCursor.moveToNext()) {
                String hour = measurementsCursor.getString(0);
                String temperature = measurementsCursor.getString(1);
                String rainfall = measurementsCursor.getString(6);
                String date = measurementsCursor.getString(7);
                String station = measurementsCursor.getString(8);
                String id = measurementsCursor.getString(9);
                measurementsList.add(station + " - " + date + " - " + hour + " UTC" +
                        " | Temperatura: " + temperature + (char) 0x00B0 + "C - Opady " + rainfall + "mm");
                measurementsIDs.add(id);
            }
        }

        ListView measurementsListView = (ListView) getView().findViewById(R.id.measurements_list_view);
        if (measurementsListView != null) {
            measurementsListView.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, measurementsList));
            measurementsListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            String measurement_id = measurementsIDs.get((int) id).toString();

                            Bundle bundle = new Bundle();
                            bundle.putString("measurement_id", measurement_id);

                            MeasurementFragment measurementFragment = new MeasurementFragment();
                            measurementFragment.setArguments(bundle);
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, measurementFragment);
                            fragmentTransaction.commit();
                        }
                    }
            );
        } else {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_LONG).show();
        }
    }

    private void loadMeasurementsFromAPI(String url) {
        MeasurementsFragment.GetMeasurements getMeasurements = new MeasurementsFragment.GetMeasurements(getActivity());
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
                    String station = jsonTickets.getJSONObject(i).getString("station_number");
                    isInserted = myDb.insertDataMeasurements(hour, temperature, wind_speed, wind_direct, humidity, preasure, rainfall, date, station);
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
