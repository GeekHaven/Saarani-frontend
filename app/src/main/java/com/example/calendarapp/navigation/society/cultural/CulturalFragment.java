package com.example.calendarapp.navigation.society.cultural;

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
 * Use the {@link CulturalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CulturalFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    LinearLayout dance,music,ams,sports,lit,fas,drama,effe,asmita,gymkhana,iiic;
    TextView dance_btn,music_btn,ams_btn,sports_btn,lit_btn,fas_btn,drama_btn,effe_btn,asmita_btn,gymkhan_btn,iiic_btn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CulturalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CulturalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CulturalFragment newInstance(String param1, String param2) {
        CulturalFragment fragment = new CulturalFragment();
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
        View view=inflater.inflate(R.layout.fragment_cultural, container, false);
        initialize(view);
        checkSubs();
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
    public void checkSubs(){
        final SharedPreferences pref = getContext().getSharedPreferences("subscriptions", MODE_PRIVATE);
        if(pref.getBoolean("dance", false))
            changeIt(dance_btn);
        if(pref.getBoolean("ams",false))
            changeIt(ams_btn);
        if(pref.getBoolean("virtuosi",false))
            changeIt(music_btn);
        if(pref.getBoolean("rangtarangini",false))
            changeIt(drama_btn);
        if(pref.getBoolean("sarasva",false))
            changeIt(lit_btn);
        if(pref.getBoolean("sports",false))
            changeIt(sports_btn);
        if(pref.getBoolean("nirmiti",false))
            changeIt(fas_btn);
        if(pref.getBoolean("effervescence",false))
            changeIt(effe_btn);
        if(pref.getBoolean("asmita",false))
            changeIt(asmita_btn);
        if(pref.getBoolean("iiic",false))
            changeIt(iiic_btn);
        if(pref.getBoolean("gymkhana",false))
            changeIt(gymkhan_btn);
    }
    public void initialize(View view){

        dance=view.findViewById(R.id.geniticx);
        dance.setContentDescription("dance");
        dance.setOnClickListener(this);

        dance_btn=view.findViewById(R.id.geniticx_btn);
        dance_btn.setOnClickListener(this);
        dance_btn.setContentDescription("dance");

        music=view.findViewById(R.id.virtuosi);
        music.setOnClickListener(this);
        music.setContentDescription("virtuosi");

        music_btn=view.findViewById(R.id.virtuosi_btn);
        music_btn.setOnClickListener(this);
        music_btn.setContentDescription("virtuosi");

        ams=view.findViewById(R.id.ams);
        ams.setOnClickListener(this);
        ams.setContentDescription("ams");

        ams_btn=view.findViewById(R.id.ams_btn);
        ams_btn.setOnClickListener(this);
        ams_btn.setContentDescription("ams");

        sports=view.findViewById(R.id.spirit);
        sports.setOnClickListener(this);
        sports.setContentDescription("sports");

        sports_btn=view.findViewById(R.id.spirit_btn);
        sports_btn.setOnClickListener(this);
        sports_btn.setContentDescription("sports");

        lit=view.findViewById(R.id.sarvasa);
        lit.setOnClickListener(this);
        lit.setContentDescription("sarasva");

        lit_btn=view.findViewById(R.id.sarvasa_btn);
        lit_btn.setOnClickListener(this);
        lit_btn.setContentDescription("sarasva");

        fas=view.findViewById(R.id.nirmiti);
        fas.setOnClickListener(this);
        fas.setContentDescription("nirmiti");

        fas_btn=view.findViewById(R.id.nirmiti_btn);
        fas_btn.setOnClickListener(this);
        fas_btn.setContentDescription("nirmiti");

        drama=view.findViewById(R.id.rang);
        drama.setOnClickListener(this);
        drama.setContentDescription("rangtarangini");

        drama_btn=view.findViewById(R.id.rangBtn);
        drama_btn.setOnClickListener(this);
        drama_btn.setContentDescription("rangtarangini");

        effe=view.findViewById(R.id.effe);
        effe.setContentDescription("effervescence");
        effe.setOnClickListener(this);

        effe_btn=view.findViewById(R.id.effe_btn);
        effe_btn.setContentDescription("effervescence");
        effe_btn.setOnClickListener(this);

        asmita=view.findViewById(R.id.asmita);
        asmita.setContentDescription("asmita");
        asmita.setOnClickListener(this);

        asmita_btn=view.findViewById(R.id.asmita_btn);
        asmita_btn.setContentDescription("asmita");
        asmita_btn.setOnClickListener(this);

        gymkhana=view.findViewById(R.id.gymkhana);
        gymkhana.setContentDescription("gymkhana");
        gymkhana.setOnClickListener(this);

        gymkhan_btn=view.findViewById(R.id.gymkhana_btn);
        gymkhan_btn.setContentDescription("gymkhana");
        gymkhan_btn.setOnClickListener(this);

        iiic=view.findViewById(R.id.iiic);
        iiic.setOnClickListener(this);
        iiic.setContentDescription("iiic");

        iiic_btn=view.findViewById(R.id.iiic_btn);
        iiic_btn.setContentDescription("iiic");
        iiic_btn.setOnClickListener(this);
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