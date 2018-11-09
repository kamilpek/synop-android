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
    View rootView;

    public GiossFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gioss, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";
        ((MainActivity) getActivity()).setActionBarTitle("Jakość powietrza");
        viewAll();
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

        ListView giossListView = (ListView) rootView.findViewById(R.id.gioss_list_view);
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
                            bundle.putString("last_fragment", "GiossFragment");

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
}
