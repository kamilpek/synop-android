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
    View rootView;

    public MeasurementsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_measurements, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";
        ((MainActivity) getActivity()).setActionBarTitle("Dane synoptyczne");
        viewAll();
        return rootView;
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
                        "\nTemperatura: " + temperature + (char) 0x00B0 + "C - Opady " + rainfall + "mm");
                measurementsIDs.add(id);
            }
        }

        ListView measurementsListView = (ListView) rootView.findViewById(R.id.measurements_list_view);
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
}
