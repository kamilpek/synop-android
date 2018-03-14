package pl.kp_software.synoptyk;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MainActivity.mainFragment_active = true;
        MainActivity.lastFragment = "empty";

        Button measurementsButton = (Button) rootView.findViewById(R.id.measurementsButton);
        measurementsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        MeasurementsFragment measurementsFragment = new MeasurementsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, measurementsFragment);
                        fragmentTransaction.commit();
                    }
                });

        Button forecastsButton = (Button) rootView.findViewById(R.id.forecastsButton);
        forecastsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ForecastsFragment forecastsFragment = new ForecastsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, forecastsFragment);
                        fragmentTransaction.commit();
                    }
                });

        Button metarsButton = (Button) rootView.findViewById(R.id.metarsButton);
        metarsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        MetarsFragment metarsFragment = new MetarsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, metarsFragment);
                        fragmentTransaction.commit();
                    }
                });

        Button giosButton = (Button) rootView.findViewById(R.id.giosButton);
        giosButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        GiossFragment giossFragment = new GiossFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, giossFragment);
                        fragmentTransaction.commit();
                    }
                });

        return rootView;

    }
}
