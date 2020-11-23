package com.geekhaven.caliiita.activites;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.geekhaven.caliiita.R;

public class AboutUsActivity extends AppCompatActivity {
    ImageView back;
    ImageView cl1,cl2,cl3,cl4,cl5,cl6;
    ImageView i1,i2,i3,i4,i5,i6;

    private void openGithubHandle(String githubId){
        String url="https://www.github.com/"+githubId;
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void openFacebookHandle(String x){
        Uri uri = Uri.parse(x);
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + x);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        back=findViewById(R.id.backArrow);
        cl1=findViewById(R.id.github_vb);
        cl2=findViewById(R.id.github_vt);
        cl3=findViewById(R.id.github_sg);
        cl4=findViewById(R.id.github_cg);
        cl5=findViewById(R.id.github_an);
        cl6=findViewById(R.id.github_mt);

        i1=findViewById(R.id.fb_vb);
        i2=findViewById(R.id.fb_vt);
        i3=findViewById(R.id.fb_sg);
        i4=findViewById(R.id.fb_cg);
        i5=findViewById(R.id.fb_an);
        i6=findViewById(R.id.fb_mt);

        cl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubHandle("starboi02");
            }
        });
        cl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubHandle("VarunT11");
            }
        });
        cl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubHandle("sggts04");
            }
        });
        cl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubHandle("untrulynoxiusmj");
            }
        });
        cl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubHandle("infinity-star");
            }
        });
        cl6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGithubHandle("mitishaa");
            }
        });


        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookHandle("https://www.facebook.com/varunbhardwaj.064");
            }
        });
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookHandle("https://www.facebook.com/varun.t11");
            }
        });
        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookHandle("https://www.facebook.com/shreyashwr");
            }
        });
        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookHandle("https://www.facebook.com/chetangarg365");
            }
        });
        i5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookHandle("https://www.facebook.com/anip.kumar.330");
            }
        });
        i6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebookHandle("https://www.facebook.com/mitisha.agarwal.374");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AboutUsActivity.this, MainActivity.class);
                intent.putExtra("action","db");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
