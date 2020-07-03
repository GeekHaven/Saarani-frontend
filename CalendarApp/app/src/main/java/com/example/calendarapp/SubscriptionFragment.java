package com.example.calendarapp;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.Context.MODE_PRIVATE;

public class SubscriptionFragment extends Fragment {
    int value;
    Button btn_next;
    LinearLayout geekhaven,effe,tesla,sarasva,aparoksha,asmita,gymkhana,rangtarangini,genitix,ams,nirmiti,virtuosi,spirit,iiic;
    ImageView geekhaven_image,effe_image,tesla_image,sarasva_image,aparoksha_image,asmita_image,gymkhana_image,rangtarangini_image,genitix_image,ams_image,nirmiti_image,virtuosi_image,spirit_image,iiic_image;
    private SubscriptionViewModel mViewModel;

    public static SubscriptionFragment newInstance() {
        return new SubscriptionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.subscription_fragment, container, false);
        btn_next=view.findViewById(R.id.extended_fab);
        final SharedPreferences pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        final int[] x = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        geekhaven=view.findViewById(R.id.geekhaven);
        geekhaven_image=view.findViewById(R.id.geekhaven_logo);
        if(pref.getBoolean("geekhaven",false)){
            x[0]++;
            geekhaven_image.setImageResource(R.drawable.ic_gh_yellow2);
            geekhaven.setBackgroundResource(R.drawable.society_card_highlight);
        }
        effe=view.findViewById(R.id.effe);
        effe_image=view.findViewById(R.id.effe_logo);
        effe_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if(pref.getBoolean("effe",false)){
            x[1]++;
            effe_image.setColorFilter(Color.parseColor("#F5D22B"));
            effe.setBackgroundResource(R.drawable.society_card_highlight);
        }
        tesla=view.findViewById(R.id.tesla);
        tesla_image=view.findViewById(R.id.tesla_logo);
        tesla_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if(pref.getBoolean("tesla",false)){
            x[2]++;
            tesla_image.setColorFilter(Color.parseColor("#F5D22B"));
            tesla.setBackgroundResource(R.drawable.society_card_highlight);
        }
        sarasva=view.findViewById(R.id.sarvasa);
        sarasva_image=view.findViewById(R.id.sarvasa_logo);
        if(pref.getBoolean("sarasva",false)){
            x[3]++;
            sarasva_image.setColorFilter(Color.parseColor("#F5D22B"));
            sarasva.setBackgroundResource(R.drawable.society_card_highlight);
        }
        aparoksha=view.findViewById(R.id.aparoksha);
        aparoksha_image=view.findViewById(R.id.aparoksha_logo);
        if(pref.getBoolean("aparoksha",false)){
            x[4]++;
            aparoksha_image.setColorFilter(Color.parseColor("#F5D22B"));
            aparoksha.setBackgroundResource(R.drawable.society_card_highlight);
        }
        asmita=view.findViewById(R.id.asmita);
        asmita_image=view.findViewById(R.id.asmita_logo);
        asmita_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if(pref.getBoolean("asmita",false)){
            x[5]++;
            asmita_image.setColorFilter(Color.parseColor("#F5D22B"));
            asmita.setBackgroundResource(R.drawable.society_card_highlight);
        }
        gymkhana=view.findViewById(R.id.gymkhana);
        gymkhana_image=view.findViewById(R.id.gymkhana_logo);
        gymkhana_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if(pref.getBoolean("gymkhana",false)){
            x[6]++;
            gymkhana_image.setColorFilter(Color.parseColor("#F5D22B"));
            gymkhana.setBackgroundResource(R.drawable.society_card_highlight);
        }
        rangtarangini=view.findViewById(R.id.rangtarini);
        rangtarangini_image=view.findViewById(R.id.rangtarini_logo);
        rangtarangini_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if(pref.getBoolean("rang",false)){
            x[7]++;
            rangtarangini_image.setColorFilter(Color.parseColor("#F5D22B"));
            rangtarangini.setBackgroundResource(R.drawable.society_card_highlight);
        }
        genitix=view.findViewById(R.id.geniticx);
        genitix_image=view.findViewById(R.id.geniticx_logo);
        if(pref.getBoolean("geniticx",false)){
            x[8]++;
            genitix_image.setImageResource(R.drawable.geneticx_yellow);
            genitix.setBackgroundResource(R.drawable.society_card_highlight);
        }
        ams=view.findViewById(R.id.ams);
        ams_image=view.findViewById(R.id.ams_logo);
        if(pref.getBoolean("ams",false)){
            x[9]++;
            ams_image.setColorFilter(Color.parseColor("#F5D22B"));
            ams.setBackgroundResource(R.drawable.society_card_highlight);
        }
        nirmiti=view.findViewById(R.id.nirmiti);
        nirmiti_image=view.findViewById(R.id.nirmiti_logo);
        if(pref.getBoolean("nirmiti",false)){
            x[10]++;
            nirmiti_image.setColorFilter(Color.parseColor("#F5D22B"));
            nirmiti.setBackgroundResource(R.drawable.society_card_highlight);
        }
        virtuosi=view.findViewById(R.id.virtuosi);
        virtuosi_image=view.findViewById(R.id.virtuosi_logo);
        if(pref.getBoolean("virtuosi",false)){
            x[11]++;
            virtuosi_image.setImageResource(R.drawable.virtusoi_yellow);
            virtuosi.setBackgroundResource(R.drawable.society_card_highlight);
        }
        iiic=view.findViewById(R.id.iiic);
        iiic_image=view.findViewById(R.id.iiic_logo);
        if(pref.getBoolean("iiic",false)){
            x[12]++;
            iiic_image.setColorFilter(Color.parseColor("#F5D22B"));
            iiic.setBackgroundResource(R.drawable.society_card_highlight);
        }
        spirit=view.findViewById(R.id.spirit);
        spirit_image=view.findViewById(R.id.spirit_logo);
        if(pref.getBoolean("spirit",false)){
            x[13]++;
            spirit_image.setColorFilter(Color.parseColor("#F5D22B"));
            spirit.setBackgroundResource(R.drawable.society_card_highlight);
        }
        geekhaven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[0] %2==0) {
                    geekhaven_image.setImageResource(R.drawable.ic_gh_yellow2);
                    geekhaven.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("geekhaven");
                    x[0]++;
                    editor.putBoolean("geekhaven",true);
                }
                else{
                    geekhaven_image.setImageResource(R.drawable.ic_gh_white);
                    geekhaven.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("geekhaven");
                    x[0]++;
                    editor.putBoolean("geekhaven",false);
                }
            }
        });
        effe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[1] %2==0) {
                    effe_image.setColorFilter(Color.parseColor("#F5D22B"));
                    effe.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("effervescence");
                    x[1]++;
                    editor.putBoolean("effe",true);
                }
                else{
                    effe_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    effe.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("effervescence");
                    x[1]++;
                    editor.putBoolean("effe",false);
                }
            }
        });
        tesla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[2] %2==0) {
                    tesla_image.setColorFilter(Color.parseColor("#F5D22B"));
                    tesla.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("tesla");
                    x[2]++;
                    editor.putBoolean("tesla",true);
                }
                else{
                    tesla_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    tesla.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("tesla");
                    x[2]++;
                    editor.putBoolean("tesla",false);
                }
            }
        });
        sarasva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[3] %2==0) {
                    sarasva_image.setColorFilter(Color.parseColor("#F5D22B"));
                    sarasva.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("sarasva");
                    x[3]++;
                    editor.putBoolean("sarasva",true);
                }
                else{
                    sarasva_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    sarasva.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("sarasva");
                    x[3]++;
                    editor.putBoolean("sarasva",false);
                }
            }
        });
        aparoksha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[4] %2==0) {
                    aparoksha_image.setColorFilter(Color.parseColor("#F5D22B"));
                    aparoksha.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("aparoksha");
                    x[4]++;
                    editor.putBoolean("aparoksha",true);
                }
                else{
                    aparoksha_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    aparoksha.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("aparoksha");
                    x[4]++;
                    editor.putBoolean("aparoksha",false);
                }
            }
        });
        asmita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[5] %2==0) {
                    asmita_image.setColorFilter(Color.parseColor("#F5D22B"));
                    asmita.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("asmita");
                    x[5]++;
                    editor.putBoolean("asmita",true);
                }
                else{
                    asmita_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    asmita.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("asmita");
                    x[5]++;
                    editor.putBoolean("asmita",false);
                }
            }
        });
        gymkhana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[6] %2==0) {
                    gymkhana_image.setColorFilter(Color.parseColor("#F5D22B"));
                    gymkhana.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("gymkhana");
                    x[6]++;
                    editor.putBoolean("gymkhana",true);
                }
                else{
                    gymkhana_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    gymkhana.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("gymkhana");
                    x[6]++;
                    editor.putBoolean("gymkhana",false);
                }
            }
        });
        rangtarangini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[7] %2==0) {
                    rangtarangini_image.setColorFilter(Color.parseColor("#F5D22B"));
                    rangtarangini.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("rangtarangini");
                    x[7]++;
                    editor.putBoolean("rang",true);
                }
                else{
                    rangtarangini_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    rangtarangini.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("rangtarangini");
                    x[7]++;
                    editor.putBoolean("rang",false);
                }
            }
        });
        genitix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[8] %2==0) {
                    genitix_image.setImageResource(R.drawable.geneticx_yellow);
                    genitix.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("dance");
                    x[8]++;
                    editor.putBoolean("geniticx",true);
                }
                else{
                    genitix_image.setImageResource(R.drawable.geneticx_white);
                    genitix.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("dance");
                    x[8]++;
                    editor.putBoolean("geniticx",false);
                }
            }
        });
        ams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[9] %2==0) {
                    ams_image.setColorFilter(Color.parseColor("#F5D22B"));
                    ams.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("ams");
                    x[9]++;
                    editor.putBoolean("ams",true);
                }
                else{
                    ams_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    ams.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("ams");
                    x[9]++;
                    editor.putBoolean("ams",false);
                }
            }
        });
        nirmiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[10] %2==0) {
                    nirmiti_image.setColorFilter(Color.parseColor("#F5D22B"));
                    nirmiti.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("nirmiti");
                    x[10]++;
                    editor.putBoolean("nirmiti",true);
                }
                else{
                    nirmiti_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    nirmiti.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("nirmiti");
                    x[10]++;
                    editor.putBoolean("nirmiti",false);
                }
            }
        });
        virtuosi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[11] %2==0) {
                    virtuosi_image.setImageResource(R.drawable.virtusoi_yellow);
                    virtuosi.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("virtuosi");
                    x[11]++;
                    editor.putBoolean("virtuosi",true);
                }
                else{
                    virtuosi_image.setImageResource(R.drawable.virtuosi);
                    virtuosi.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("virtuosi");
                    x[11]++;
                    editor.putBoolean("virtuosi",false);
                }
            }
        });
        iiic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[12] %2==0) {
                    iiic_image.setColorFilter(Color.parseColor("#F5D22B"));
                    iiic.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("iiic");
                    x[12]++;
                    editor.putBoolean("iiic",true);
                }
                else{
                    iiic_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    iiic.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("iiic");
                    x[12]++;
                    editor.putBoolean("iiic",false);
                }
            }
        });
        spirit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[13] %2==0) {
                    spirit_image.setColorFilter(Color.parseColor("#F5D22B"));
                    spirit.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("sports");
                    x[13]++;
                    editor.putBoolean("spirit",true);
                }
                else{
                    spirit_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    spirit.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("sports");
                    x[13]++;
                    editor.putBoolean("spirit",false);
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.apply();
                startActivity(new Intent(getContext(),MainActivity2.class));
            }
        });


        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SubscriptionViewModel.class);
        // TODO: Use the ViewModel
    }
}