package com.example.calendarapp;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class SocietyFragment extends Fragment {

    private SocietyViewModel mViewModel;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAdapter adapter;

    public static SocietyFragment newInstance() {
        return new SocietyFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.society_fragment, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout=view.findViewById(R.id.tablayout);
        viewPager=view.findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);

        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new CulturalFragment(), "Cultural");
        adapter.addFragment(new TechnicalFragment(), "Technical");

        viewPager.setAdapter(adapter);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SocietyViewModel.class);
        // TODO: Use the ViewModel
    }

}