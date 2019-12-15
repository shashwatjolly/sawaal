package com.sgsj.sawaal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AboutActivity extends AppCompatActivity {
    private LinearLayout dev1, dev2, feedback, bugreport, codebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");
        dev1 = findViewById(R.id.dev1);
        dev2 = findViewById(R.id.dev2);
        feedback = findViewById(R.id.feedback);
        bugreport = findViewById(R.id.bugreport);
        codebase = findViewById(R.id.codebase);
        dev1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.facebook.com/shashwatjolly/"));
                startActivity(intent);
            }
        });
        dev2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.facebook.com/shaurya.gomber.9"));
                startActivity(intent);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSeeZtafJi2IbE6n6BHIT12M58t0VRmg0iBRRRYi7-rVvi6R4A/viewform?usp=sf_link"));
                startActivity(intent);
            }
        });
        bugreport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/shashwatjolly/sawaal/issues/new"));
                startActivity(intent);
            }
        });
        codebase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/shashwatjolly/sawaal"));
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}