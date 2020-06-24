package com.example.calendarapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SubscribeActivity extends AppCompatActivity {
    Button btn_next;
    LinearLayout geekhaven,effe,tesla,sarasva,aparoksha,asmita,gymkhana,rangtarangini,genitix,ams,nirmiti,virtuosi,spirit,iiic;
    ImageView geekhaven_image,effe_image,tesla_image,sarasva_image,aparoksha_image,asmita_image,gymkhana_image,rangtarangini_image,genitix_image,ams_image,nirmiti_image,virtuosi_image,spirit_image,iiic_image;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        btn_next=findViewById(R.id.extended_fab);
        final int[] x = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        geekhaven=findViewById(R.id.geekhaven);
        geekhaven_image=findViewById(R.id.geekhaven_logo);
        effe=findViewById(R.id.effe);
        effe_image=findViewById(R.id.effe_logo);
        effe_image.setColorFilter(Color.parseColor("#FFFFFF"));
        tesla=findViewById(R.id.tesla);
        tesla_image=findViewById(R.id.tesla_logo);
        tesla_image.setColorFilter(Color.parseColor("#FFFFFF"));
        sarasva=findViewById(R.id.sarvasa);
        sarasva_image=findViewById(R.id.sarvasa_logo);
        aparoksha=findViewById(R.id.aparoksha);
        aparoksha_image=findViewById(R.id.aparoksha_logo);
        asmita=findViewById(R.id.asmita);
        asmita_image=findViewById(R.id.asmita_logo);
        asmita_image.setColorFilter(Color.parseColor("#FFFFFF"));
        gymkhana=findViewById(R.id.gymkhana);
        gymkhana_image=findViewById(R.id.gymkhana_logo);
        gymkhana_image.setColorFilter(Color.parseColor("#FFFFFF"));
        rangtarangini=findViewById(R.id.rangtarini);
        rangtarangini_image=findViewById(R.id.rangtarini_logo);
        rangtarangini_image.setColorFilter(Color.parseColor("#FFFFFF"));
        genitix=findViewById(R.id.geniticx);
        genitix_image=findViewById(R.id.geniticx_logo);
//        genitix_image.setColorFilter(Color.parseColor("#FFFFFF"));
        ams=findViewById(R.id.ams);
        ams_image=findViewById(R.id.ams_logo);
        nirmiti=findViewById(R.id.nirmiti);
        nirmiti_image=findViewById(R.id.nirmiti_logo);
        virtuosi=findViewById(R.id.virtuosi);
        virtuosi_image=findViewById(R.id.virtuosi_logo);
        iiic=findViewById(R.id.iiic);
        iiic_image=findViewById(R.id.iiic_logo);
        spirit=findViewById(R.id.spirit);
        spirit_image=findViewById(R.id.spirit_logo);
        geekhaven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[0] %2==0) {
                    geekhaven_image.setImageResource(R.drawable.ic_gh_yellow2);
                    geekhaven.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Geekhaven");
                    x[0]++;
                }
                else{
                    geekhaven_image.setImageResource(R.drawable.ic_gh_white);
                    geekhaven.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Geekhaven");
                    x[0]++;
                }
            }
        });
        effe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[1] %2==0) {
                    effe_image.setColorFilter(Color.parseColor("#F5D22B"));
                    effe.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Effervescence");
                    x[1]++;
                }
                else{
                    effe_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    effe.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Effervescence");
                    x[1]++;
                }
            }
        });
        tesla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[2] %2==0) {
                    tesla_image.setColorFilter(Color.parseColor("#F5D22B"));
                    tesla.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Tesla");
                    x[2]++;
                }
                else{
                    tesla_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    tesla.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Tesla");
                    x[2]++;
                }
            }
        });
        sarasva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[3] %2==0) {
                    sarasva_image.setColorFilter(Color.parseColor("#F5D22B"));
                    sarasva.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Sarasva");
                    x[3]++;
                }
                else{
                    sarasva_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    sarasva.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Sarasva");
                    x[3]++;
                }
            }
        });
        aparoksha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[4] %2==0) {
                    aparoksha_image.setColorFilter(Color.parseColor("#F5D22B"));
                    aparoksha.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Aparoksha");
                    x[4]++;
                }
                else{
                    aparoksha_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    aparoksha.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Aparoksha");
                    x[4]++;
                }
            }
        });
        asmita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[5] %2==0) {
                    asmita_image.setColorFilter(Color.parseColor("#F5D22B"));
                    asmita.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Asmita");
                    x[5]++;
                }
                else{
                    asmita_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    asmita.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Asmita");
                    x[5]++;
                }
            }
        });
        gymkhana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[6] %2==0) {
                    gymkhana_image.setColorFilter(Color.parseColor("#F5D22B"));
                    gymkhana.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Gymkhana");
                    x[6]++;
                }
                else{
                    gymkhana_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    gymkhana.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Gymkhana");
                    x[6]++;
                }
            }
        });
        rangtarangini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[7] %2==0) {
                    rangtarangini_image.setColorFilter(Color.parseColor("#F5D22B"));
                    rangtarangini.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Rangtarangini");
                    x[7]++;
                }
                else{
                    rangtarangini_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    rangtarangini.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Rangtarangini");
                    x[7]++;
                }
            }
        });
        genitix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[8] %2==0) {
                    genitix_image.setImageResource(R.drawable.geneticx_yellow);
                    genitix.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Geneticx");
                    x[8]++;
                }
                else{
                    genitix_image.setImageResource(R.drawable.geneticx_white);
                    genitix.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Geneticx");
                    x[8]++;
                }
            }
        });
        ams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[9] %2==0) {
                    ams_image.setColorFilter(Color.parseColor("#F5D22B"));
                    ams.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("AMS");
                    x[9]++;
                }
                else{
                    ams_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    ams.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("AMS");
                    x[9]++;
                }
            }
        });
        nirmiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[10] %2==0) {
                    nirmiti_image.setColorFilter(Color.parseColor("#F5D22B"));
                    nirmiti.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Nirmiti");
                    x[10]++;
                }
                else{
                    nirmiti_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    nirmiti.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Nirmiti");
                    x[10]++;
                }
            }
        });
        virtuosi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[11] %2==0) {
                    virtuosi_image.setImageResource(R.drawable.virtusoi_yellow);
                    virtuosi.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Virtuosi");
                    x[11]++;
                }
                else{
                    virtuosi_image.setImageResource(R.drawable.virtuosi);
                    virtuosi.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Virtuosi");
                    x[11]++;
                }
            }
        });
        iiic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[12] %2==0) {
                    iiic_image.setColorFilter(Color.parseColor("#F5D22B"));
                    iiic.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("IIIC");
                    x[12]++;
                }
                else{
                    iiic_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    iiic.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("IIIC");
                    x[12]++;
                }
            }
        });
        spirit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x[13] %2==0) {
                    spirit_image.setColorFilter(Color.parseColor("#F5D22B"));
                    spirit.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("Spirit");
                    x[13]++;
                }
                else{
                    spirit_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    spirit.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Spirit");
                    x[13]++;
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubscribeActivity.this,MainActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
    }
}
