package com.example.calendarapp.navigation.society.technical;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.calendarapp.R;
import com.example.calendarapp.activites.SocShowActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TechnicalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TechnicalFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    LinearLayout apk,gh,tesla,gravity;
    TextView apk_btn,gh_btn,tesla_btn,gravity_btn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TechnicalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TechnicalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TechnicalFragment newInstance(String param1, String param2) {
        TechnicalFragment fragment = new TechnicalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_technical, container, false);
        apk=view.findViewById(R.id.apk);
        apk.setContentDescription("aparoksha");
        apk.setOnClickListener(this);

        apk_btn=view.findViewById(R.id.apk_btn);
        apk_btn.setOnClickListener(this);
        apk_btn.setContentDescription("aparoksha");

        gh=view.findViewById(R.id.geekhaven);
        gh.setContentDescription("geekhaven");
        gh.setOnClickListener(this);

        gh_btn=view.findViewById(R.id.gh_btn);
        gh_btn.setContentDescription("geekhaven");
        gh_btn.setOnClickListener(this);

        tesla=view.findViewById(R.id.tesla);
        tesla.setOnClickListener(this);
        tesla.setContentDescription("tesla");

        tesla_btn=view.findViewById(R.id.t_btn);
        tesla_btn.setOnClickListener(this);
        tesla_btn.setContentDescription("tesla");

        gravity=view.findViewById(R.id.gravity);
        gravity.setOnClickListener(this);
        gravity.setContentDescription("gravity");

        gravity_btn=view.findViewById(R.id.g_btn);
        gravity_btn.setOnClickListener(this);
        gravity_btn.setContentDescription("gravity");

        checkSub();
        return view;
    }
    public void changePref(String msg,Boolean val){
        final SharedPreferences pref = getContext().getSharedPreferences("subscriptions", MODE_PRIVATE);
        final SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean(msg,val);
        editor.apply();
    }
    public void unchangeIt(TextView textView){
        textView.setText("Follow");
        textView.setBackgroundResource(R.drawable.custom_view_border);
        textView.setTextColor(Color.parseColor("#F0D453"));
    }
    public void unSub(String val){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(val);
    }
    public void sub(String val){
        FirebaseMessaging.getInstance().subscribeToTopic(val);
    }
    public void changeIt(TextView textView){
        textView.setBackgroundResource(R.drawable.custom_view_selected);
        textView.setText("Following");
        textView.setTextColor(Color.parseColor("#2C2B2B"));
    }
    public void checkSub(){
        final SharedPreferences pref = getContext().getSharedPreferences("subscriptions", MODE_PRIVATE);
        if(pref.getBoolean("aparoksha",false))
            changeIt(apk_btn);
        if(pref.getBoolean("geekhaven",false))
            changeIt(gh_btn);
        if(pref.getBoolean("tesla",false))
            changeIt(tesla_btn);
        if(pref.getBoolean("gravity",false))
            changeIt(gravity_btn);
    }
    @Override
    public void onClick(View v) {
        if(v instanceof TextView){
            final SharedPreferences pref = getContext().getSharedPreferences("subscriptions", MODE_PRIVATE);
            if(pref.getBoolean((String) v.getContentDescription(), false)){
                unchangeIt((TextView)v);
                unSub((String)v.getContentDescription());
                changePref((String)v.getContentDescription(),false);

            }
            else {
                changeIt((TextView)v);
                sub((String)v.getContentDescription());
                changePref((String) v.getContentDescription(),true);
            }
        }
        else if(v instanceof LinearLayout){
            Intent intent =new Intent(getContext(), SocShowActivity.class);
            intent.putExtra("id",(String) v.getContentDescription());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}