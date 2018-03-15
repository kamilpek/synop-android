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

    public ForecastsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_forecasts, container, false);
        myDb = new DatabaseHelper(getActivity());
        MainActivity.mainFragment_active = false;
        MainActivity.lastFragment = "MainFragment";

        if (isNetworkAvailable() == true){
            try {
                loadForecastsFromAPI("http://synoptyk.kp-software.pl/api/v1/forecasts.json");
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

        ListView forecasts = (ListView) getView().findViewById(R.id.forecasts_list_view);
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

    private void loadForecastsFromAPI(String url) {
        ForecastsFragment.GetForecasts getForecasts = new ForecastsFragment.GetForecasts(getActivity());
        getForecasts.setMessageLoading("Pobieranie prognozy...");
        myDb.deleteDataMForecastsAll();
        getForecasts.execute(url);
    }

    private class GetForecasts extends UrlJsonAsyncTask {
        public GetForecasts(Context context) {
            super(context);
        }
        boolean isInserted;

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                JSONArray jsonTickets = json.getJSONObject("data").getJSONArray("forecasts");
                int length = jsonTickets.length();

                for (int i = 0; i < length; i++) {
                    String hour = jsonTickets.getJSONObject(i).getString("hour");
                    String date = jsonTickets.getJSONObject(i).getString("date");
                    String next = jsonTickets.getJSONObject(i).getString("next");
                    String times_from = jsonTickets.getJSONObject(i).getString("times_from");
                    String times_to = jsonTickets.getJSONObject(i).getString("times_to");
                    String temperatures = jsonTickets.getJSONObject(i).getString("temperatures");
                    String wind_speeds = jsonTickets.getJSONObject(i).getString("wind_speeds");
                    String wind_directs = jsonTickets.getJSONObject(i).getString("wind_directs");
                    String preasures = jsonTickets.getJSONObject(i).getString("preasures");
                    String situations = jsonTickets.getJSONObject(i).getString("situations");
                    String precipitations = jsonTickets.getJSONObject(i).getString("precipitations");
                    String station = jsonTickets.getJSONObject(i).getString("station_number");
                    isInserted = myDb.insertDataForecasts(hour, date, next, times_from, times_to, temperatures, wind_speeds, wind_directs, preasures, situations, precipitations, station);
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
