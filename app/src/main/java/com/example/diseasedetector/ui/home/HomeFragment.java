package com.example.diseasedetector.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.diseasedetector.R;

public class HomeFragment extends Fragment {

    LottieAnimationView covid;
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return root;
    }

    private void init(){
        covid = root.findViewById(R.id.covidAnim);
        covid.setVisibility(View.VISIBLE);
        covid.setAnimation(R.raw.covid_positive);
    }
}