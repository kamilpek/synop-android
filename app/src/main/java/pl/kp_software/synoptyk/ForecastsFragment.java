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

public class ForecastsFragment extends Fragment {
    DatabaseHelper myDb;
    View rootView;

    public ForecastsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_forecasts, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";
        viewAll();
        ((MainActivity) getActivity()).setActionBarTitle("Prognozy warunków meteo");
        return rootView;
    }

    public void viewAll() {
        List<String> forecastsList = new ArrayList<String>();
        final List<String> forecastsIDs = new ArrayList<String>();
        Cursor forecastsCursor = myDb.getAllDataForecasts();
        if(forecastsCursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while(forecastsCursor.moveToNext()) {
                String hour = forecastsCursor.getString(0);
                String date = forecastsCursor.getString(1);
                String next = forecastsCursor.getString(2);
                String station = forecastsCursor.getString(11);
                String id = forecastsCursor.getString(12);
                forecastsList.add(station + " - " + date + " - " + hour + " UTC" +
                        "\nNastępna prognoza: " + next );
                forecastsIDs.add(id);
            }
        }

        ListView forecasts = (ListView) rootView.findViewById(R.id.forecasts_list_view);
        if (forecasts != null) {
            forecasts.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, forecastsList));
            forecasts.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            String forecast_id = forecastsIDs.get((int) id).toString();

                            Bundle bundle = new Bundle();
                            bundle.putString("forecast_id", forecast_id);

                            ForecastFragment forecastFragment = new ForecastFragment();
                            forecastFragment.setArguments(bundle);
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, forecastFragment);
                            fragmentTransaction.commit();
                        }
                    }
            );
        } else {
            Toast.makeText(getActivity(), "Brak Danych", Toast.LENGTH_LONG).show();
        }
    }
}
