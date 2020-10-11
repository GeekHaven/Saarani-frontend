package com.example.calendarapp.navigation.profile;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calendarapp.inflator.ProfileRVFragment;
import com.example.calendarapp.inflator.ProfileRVFragment2;
import com.example.calendarapp.R;
import com.example.calendarapp.adapters.TabAdapter;
import com.example.calendarapp.database.DatabaseHandler;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment{

    private ProfileViewModel mViewModel;

    final int EVENT_INTERESTED=1, EVENT_GOING=2;
    int posIn=0,posGo=0;
    private int eventType=1;
    ActionBar toolbar;
    TabAdapter adapter;
    ViewPager viewPager;
    DatabaseHandler databaseHandler;
    ImageView imgProfileUserPhoto;
    ConstraintLayout constraintLayout;
    TextView tvUserName,tvUserEmailID, tvNoEvent;
    TabLayout tabLayout;
    Date date=new Date();
    final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    //RecyclerView rcvInterestedEvents, rcvGoingEvents;
    //RecyclerView.Adapter interestedEventsAdapter, goingEventsAdapter;
    //RecyclerView.LayoutManager interestedEventsLayoutManager, goingEventsLayoutManager;
    //ArrayList<ListItems> listInterestedEvents, listGoingEvents;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.profile_fragment, container, false);
        databaseHandler=new DatabaseHandler(getContext());
        constraintLayout=view.findViewById(R.id.layout);
        imgProfileUserPhoto=view.findViewById(R.id.imgProfileFragmentUserPhoto);
        tvUserName=view.findViewById(R.id.tvProfileFragmentUserName);
        tvUserEmailID=view.findViewById(R.id.tvProfileFragmentUserEmailID);
        //tvNoEvent=view.findViewById(R.id.tvNoEvent);
        tabLayout=view.findViewById(R.id.tabLayout);
        viewPager=view.findViewById(R.id.viewPager);

        tabLayout.setupWithViewPager(viewPager);

        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileRVFragment(), "Interested");
        adapter.addFragment(new ProfileRVFragment2(), "Going");

        viewPager.setAdapter(adapter);
        //rcvInterestedEvents=view.findViewById(R.id.rcvProfileFragmentInterestedEvents);
        //rcvInterestedEvents.setHasFixedSize(true);
        //rcvGoingEvents=view.findViewById(R.id.rcvProfileFragmentGoingEvents);
        //rcvGoingEvents.setHasFixedSize(true);
//        Window window = getActivity().getWindow();
//        window.setStatusBarColor(getActivity().getResources().getColor(R.color.profile_status_bar));
        String name =FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(name.contains(" ")){
            String[] sep= name.split(" ");
            String nameNew="";
            for(int i=0;i<sep.length;i++) {
                if(i!=0)
                    sep[i] = sep[i].trim();
                sep[i] = sep[i].toLowerCase();
                sep[i] = sep[i].substring(0, 1).toUpperCase() + sep[i].substring(1);
                if(i==sep.length-1)
                    nameNew=nameNew+sep[i];
                else
                    nameNew=nameNew+sep[i]+" ";
            }
            tvUserName.setText(nameNew);
        }
        else{
            name=name.toLowerCase();
            name=name.substring(0, 1).toUpperCase() + name.substring(1);
            tvUserName.setText(name);
        }
        tvUserEmailID.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Picasso
                    .get()
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .resize(110,110)
                    .transform(new CropCircleTransformation())
                    .into(imgProfileUserPhoto);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}