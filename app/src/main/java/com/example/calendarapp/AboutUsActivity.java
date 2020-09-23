package com.example.calendarapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AboutUsActivity extends AppCompatActivity {
    ImageView back;
    ConstraintLayout cl1,cl2,cl3,cl4,cl5,cl6;

    private void openGithubHandle(String githubId){
        String url="https://www.github.com/"+githubId;
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        back=findViewById(R.id.backArrow);
        cl1=findViewById(R.id.cl2);
        cl2=findViewById(R.id.cl3);
        cl3=findViewById(R.id.cl4);
        cl4=findViewById(R.id.cl5);
        cl5=findViewById(R.id.cl6);
        cl6=findViewById(R.id.cl7);

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AboutUsActivity.this,MainActivity2.class);
                intent.putExtra("action","db");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
