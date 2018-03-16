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
    View rootView;

    public MetarsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_metars, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";
        viewAll();
        return rootView;
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
                String metar = metarsCursor.getString(3);
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
                metarsList.add(station + " - " + hour + " UTC\n" +
                        "Temperatura: " + temperature + "\nPrędkość wiatru " + wind_speed + "m/s\n" + metar);
                metarsIDs.add(id);
            }
        }

        ListView metarsListView = (ListView) rootView.findViewById(R.id.metars_list_view);
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

}
