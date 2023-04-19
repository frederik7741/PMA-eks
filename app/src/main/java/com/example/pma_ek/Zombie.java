package com.example.pma_ek;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;


public class Zombie extends Fragment {

    private Handler handler = new Handler();
    private Runnable showFragment = new Runnable() {
        @Override
        public void run() {
            show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zombie, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        long delayTime = getRandomTime();
        Log.d("Zombie", "Delay time: " + delayTime);
        handler.postDelayed(showFragment, delayTime);
    }



    private void show() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (!isAdded()) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(android.R.id.content, this);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private long getRandomTime() {
        return new Random().nextInt(10000 ) + 20000 ;
    }
}