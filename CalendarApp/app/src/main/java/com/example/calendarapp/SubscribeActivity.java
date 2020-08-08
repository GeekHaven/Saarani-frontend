package com.example.calendarapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
    int value;
    Button btn_next;
    LinearLayout geekhaven, effe, tesla, sarasva, aparoksha, asmita, gymkhana, rangtarangini, genitix, ams, nirmiti, virtuosi, spirit, iiic;
    ImageView geekhaven_image, effe_image, tesla_image, sarasva_image, aparoksha_image, asmita_image, gymkhana_image, rangtarangini_image, genitix_image, ams_image, nirmiti_image, virtuosi_image, spirit_image, iiic_image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setAnimation();
        setContentView(R.layout.activity_subscribe);
        btn_next = findViewById(R.id.extended_fab);
        Intent intent = getIntent();
        value = intent.getExtras().getInt("val");
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        final int[] x = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        geekhaven = findViewById(R.id.geekhaven);
        geekhaven_image = findViewById(R.id.geekhaven_logo);
        if (pref.getBoolean("geekhaven", false)) {
            x[0]++;
            geekhaven_image.setImageResource(R.drawable.ic_gh_yellow2);
            geekhaven.setBackgroundResource(R.drawable.society_card_highlight);
        }
        effe = findViewById(R.id.effe);
        effe_image = findViewById(R.id.effe_logo);
        effe_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if (pref.getBoolean("effervescence", false)) {
            x[1]++;
            effe_image.setColorFilter(Color.parseColor("#F5D22B"));
            effe.setBackgroundResource(R.drawable.society_card_highlight);
        }
        tesla = findViewById(R.id.tesla);
        tesla_image = findViewById(R.id.tesla_logo);
        tesla_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if (pref.getBoolean("tesla", false)) {
            x[2]++;
            tesla_image.setColorFilter(Color.parseColor("#F5D22B"));
            tesla.setBackgroundResource(R.drawable.society_card_highlight);
        }
        sarasva = findViewById(R.id.sarvasa);
        sarasva_image = findViewById(R.id.sarvasa_logo);
        if (pref.getBoolean("sarasva", false)) {
            x[3]++;
            sarasva_image.setColorFilter(Color.parseColor("#F5D22B"));
            sarasva.setBackgroundResource(R.drawable.society_card_highlight);
        }
        aparoksha = findViewById(R.id.aparoksha);
        aparoksha_image = findViewById(R.id.aparoksha_logo);
        if (pref.getBoolean("aparoksha", false)) {
            x[4]++;
            aparoksha_image.setColorFilter(Color.parseColor("#F5D22B"));
            aparoksha.setBackgroundResource(R.drawable.society_card_highlight);
        }
        asmita = findViewById(R.id.asmita);
        asmita_image = findViewById(R.id.asmita_logo);
        asmita_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if (pref.getBoolean("asmita", false)) {
            x[5]++;
            asmita_image.setColorFilter(Color.parseColor("#F5D22B"));
            asmita.setBackgroundResource(R.drawable.society_card_highlight);
        }
        gymkhana = findViewById(R.id.gymkhana);
        gymkhana_image = findViewById(R.id.gymkhana_logo);
        gymkhana_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if (pref.getBoolean("gymkhana", false)) {
            x[6]++;
            gymkhana_image.setColorFilter(Color.parseColor("#F5D22B"));
            gymkhana.setBackgroundResource(R.drawable.society_card_highlight);
        }
        rangtarangini = findViewById(R.id.rangtarini);
        rangtarangini_image = findViewById(R.id.rangtarini_logo);
        rangtarangini_image.setColorFilter(Color.parseColor("#FFFFFF"));
        if (pref.getBoolean("rangtarangini", false)) {
            x[7]++;
            rangtarangini_image.setColorFilter(Color.parseColor("#F5D22B"));
            rangtarangini.setBackgroundResource(R.drawable.society_card_highlight);
        }
        genitix = findViewById(R.id.geniticx);
        genitix_image = findViewById(R.id.geniticx_logo);
        if (pref.getBoolean("dance", false)) {
            x[8]++;
            genitix_image.setImageResource(R.drawable.geneticx_yellow);
            genitix.setBackgroundResource(R.drawable.society_card_highlight);
        }
        ams = findViewById(R.id.ams);
        ams_image = findViewById(R.id.ams_logo);
        if (pref.getBoolean("ams", false)) {
            x[9]++;
            ams_image.setColorFilter(Color.parseColor("#F5D22B"));
            ams.setBackgroundResource(R.drawable.society_card_highlight);
        }
        nirmiti = findViewById(R.id.nirmiti);
        nirmiti_image = findViewById(R.id.nirmiti_logo);
        if (pref.getBoolean("nirmiti", false)) {
            x[10]++;
            nirmiti_image.setColorFilter(Color.parseColor("#F5D22B"));
            nirmiti.setBackgroundResource(R.drawable.society_card_highlight);
        }
        virtuosi = findViewById(R.id.virtuosi);
        virtuosi_image = findViewById(R.id.virtuosi_logo);
        if (pref.getBoolean("virtuosi", false)) {
            x[11]++;
            virtuosi_image.setImageResource(R.drawable.virtusoi_yellow);
            virtuosi.setBackgroundResource(R.drawable.society_card_highlight);
        }
        iiic = findViewById(R.id.iiic);
        iiic_image = findViewById(R.id.iiic_logo);
        if (pref.getBoolean("iiic", false)) {
            x[12]++;
            iiic_image.setColorFilter(Color.parseColor("#F5D22B"));
            iiic.setBackgroundResource(R.drawable.society_card_highlight);
        }
        spirit = findViewById(R.id.spirit);
        spirit_image = findViewById(R.id.spirit_logo);
        if (pref.getBoolean("sports", false)) {
            x[13]++;
            spirit_image.setColorFilter(Color.parseColor("#F5D22B"));
            spirit.setBackgroundResource(R.drawable.society_card_highlight);
        }
        geekhaven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[0] % 2 == 0) {
                    geekhaven_image.setImageResource(R.drawable.ic_gh_yellow2);
                    geekhaven.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("geekhaven");
                    x[0]++;
                    editor.putBoolean("geekhaven", true);
                } else {
                    geekhaven_image.setImageResource(R.drawable.ic_gh_white);
                    geekhaven.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("geekhaven");
                    x[0]++;
                    editor.putBoolean("geekhaven", false);
                }
            }
        });
        effe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[1] % 2 == 0) {
                    effe_image.setColorFilter(Color.parseColor("#F5D22B"));
                    effe.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("effervescence");
                    x[1]++;
                    editor.putBoolean("effervescence", true);
                } else {
                    effe_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    effe.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("effervescence");
                    x[1]++;
                    editor.putBoolean("effervescence", false);
                }
            }
        });
        tesla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[2] % 2 == 0) {
                    tesla_image.setColorFilter(Color.parseColor("#F5D22B"));
                    tesla.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("tesla");
                    x[2]++;
                    editor.putBoolean("tesla", true);
                } else {
                    tesla_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    tesla.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("tesla");
                    x[2]++;
                    editor.putBoolean("tesla", false);
                }
            }
        });
        sarasva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[3] % 2 == 0) {
                    sarasva_image.setColorFilter(Color.parseColor("#F5D22B"));
                    sarasva.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("sarasva");
                    x[3]++;
                    editor.putBoolean("sarasva", true);
                } else {
                    sarasva_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    sarasva.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("sarasva");
                    x[3]++;
                    editor.putBoolean("sarasva", false);
                }
            }
        });
        aparoksha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[4] % 2 == 0) {
                    aparoksha_image.setColorFilter(Color.parseColor("#F5D22B"));
                    aparoksha.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("aparoksha");
                    x[4]++;
                    editor.putBoolean("aparoksha", true);
                } else {
                    aparoksha_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    aparoksha.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("aparoksha");
                    x[4]++;
                    editor.putBoolean("aparoksha", false);
                }
            }
        });
        asmita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[5] % 2 == 0) {
                    asmita_image.setColorFilter(Color.parseColor("#F5D22B"));
                    asmita.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("asmita");
                    x[5]++;
                    editor.putBoolean("asmita", true);
                } else {
                    asmita_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    asmita.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("asmita");
                    x[5]++;
                    editor.putBoolean("asmita", false);
                }
            }
        });
        gymkhana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[6] % 2 == 0) {
                    gymkhana_image.setColorFilter(Color.parseColor("#F5D22B"));
                    gymkhana.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("gymkhana");
                    x[6]++;
                    editor.putBoolean("gymkhana", true);
                } else {
                    gymkhana_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    gymkhana.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("gymkhana");
                    x[6]++;
                    editor.putBoolean("gymkhana", false);
                }
            }
        });
        rangtarangini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[7] % 2 == 0) {
                    rangtarangini_image.setColorFilter(Color.parseColor("#F5D22B"));
                    rangtarangini.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("rangtarangini");
                    x[7]++;
                    editor.putBoolean("rangtarangini", true);
                } else {
                    rangtarangini_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    rangtarangini.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("rangtarangini");
                    x[7]++;
                    editor.putBoolean("rangtarangini", false);
                }
            }
        });
        genitix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[8] % 2 == 0) {
                    genitix_image.setImageResource(R.drawable.geneticx_yellow);
                    genitix.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("dance");
                    x[8]++;
                    editor.putBoolean("dance", true);
                } else {
                    genitix_image.setImageResource(R.drawable.geneticx_white);
                    genitix.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("dance");
                    x[8]++;
                    editor.putBoolean("dance", false);
                }
            }
        });
        ams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[9] % 2 == 0) {
                    ams_image.setColorFilter(Color.parseColor("#F5D22B"));
                    ams.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("ams");
                    x[9]++;
                    editor.putBoolean("ams", true);
                } else {
                    ams_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    ams.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("ams");
                    x[9]++;
                    editor.putBoolean("ams", false);
                }
            }
        });
        nirmiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[10] % 2 == 0) {
                    nirmiti_image.setColorFilter(Color.parseColor("#F5D22B"));
                    nirmiti.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("nirmiti");
                    x[10]++;
                    editor.putBoolean("nirmiti", true);
                } else {
                    nirmiti_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    nirmiti.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("nirmiti");
                    x[10]++;
                    editor.putBoolean("nirmiti", false);
                }
            }
        });
        virtuosi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[11] % 2 == 0) {
                    virtuosi_image.setImageResource(R.drawable.virtusoi_yellow);
                    virtuosi.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("virtuosi");
                    x[11]++;
                    editor.putBoolean("virtuosi", true);
                } else {
                    virtuosi_image.setImageResource(R.drawable.virtuosi);
                    virtuosi.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("virtuosi");
                    x[11]++;
                    editor.putBoolean("virtuosi", false);
                }
            }
        });
        iiic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[12] % 2 == 0) {
                    iiic_image.setColorFilter(Color.parseColor("#F5D22B"));
                    iiic.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("iiic");
                    x[12]++;
                    editor.putBoolean("iiic", true);
                } else {
                    iiic_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    iiic.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("iiic");
                    x[12]++;
                    editor.putBoolean("iiic", false);
                }
            }
        });
        spirit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x[13] % 2 == 0) {
                    spirit_image.setColorFilter(Color.parseColor("#F5D22B"));
                    spirit.setBackgroundResource(R.drawable.society_card_highlight);
                    FirebaseMessaging.getInstance().subscribeToTopic("sports");
                    x[13]++;
                    editor.putBoolean("sports", true);
                } else {
                    spirit_image.setColorFilter(Color.parseColor("#FFFFFF"));
                    spirit.setBackgroundResource(R.drawable.society_card);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("sports");
                    x[13]++;
                    editor.putBoolean("sports", false);
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.apply();
                Intent intent1=new Intent(SubscribeActivity.this, MainActivity2.class);
                intent1.putExtra("action","request_call");
                startActivity(intent1);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (value == 1) {
            Intent homeScreenIntent = new Intent(Intent.ACTION_MAIN);
            homeScreenIntent.addCategory(Intent.CATEGORY_HOME);
            homeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeScreenIntent);
        } else if (value == 2) {
            Intent intent = new Intent(getApplication(), MainActivity2.class);
            startActivity(intent);
        }
    }

    public void setAnimation() {
        if (Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(400);
            slide.setInterpolator(new DecelerateInterpolator());
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(slide);
        }
    }
}
