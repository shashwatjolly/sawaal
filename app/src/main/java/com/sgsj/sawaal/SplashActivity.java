package com.sgsj.sawaal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private String PREFS_NAME;
    private String PREF_VERSION_CODE_KEY;
    private int DOESNT_EXIST;
    Integer currentVersionCode;
    SharedPreferences prefs;
    int savedVersionCode;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        if(checkFirstRun()==1) {
            getWindow().setStatusBarColor(Color.parseColor("#678FB4"));
            PaperOnboardingPage scr1 = new PaperOnboardingPage("Welcome Maggus",
                    "Sawaal is your one stop destination for all examination papers that you will ever need",
                    Color.parseColor("#678FB4"), R.drawable.nerd, R.drawable.ic_panorama_fish_eye_black_24dp);
            PaperOnboardingPage scr2 = new PaperOnboardingPage("Search",
                    "You can search for papers by course, and even additional filters of year and type",
                    Color.parseColor("#65B0B4"), R.drawable.ic_search_black_128dp, R.drawable.ic_help_outline_black_24dp);
            PaperOnboardingPage scr3 = new PaperOnboardingPage("Upload",
                    "The database is crowdsourced from the IITG Junta itself. We believe in the motto \"Maggo and let Maggo\"",
                    Color.parseColor("#9B90BC"), R.drawable.ic_cloud_upload_black_128dp, R.drawable.ic_add_circle_outline_black_24dp);
            PaperOnboardingPage scr4 = new PaperOnboardingPage("Hack",
                    "You can report any paper you find to be faulty so we can improve our database and keep it up to date",
                    Color.parseColor("#81c784"), R.drawable.ic_report_black_128dp, R.drawable.ic_info_outline_black_24dp);
            PaperOnboardingPage scr5 = new PaperOnboardingPage("Let's Get Started!",
                    "Swipe right to begin :)",
                    Color.parseColor("#ffee58"), R.drawable.ic_navigate_next_black_128dp, R.drawable.ic_favorite_border_black_24dp);

            ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
            elements.add(scr1);
            elements.add(scr2);
            elements.add(scr3);
            elements.add(scr4);
            elements.add(scr5);
//
//            PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.splashtexttemp), elements, getApplicationContext());
//
//            engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
//                @Override
//                public void onPageChanged(int oldElementIndex, int newElementIndex) {
//                    Toast.makeText(getApplicationContext(), "Swiped from " + oldElementIndex + " to " + newElementIndex, Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
//                @Override
//                public void onRightOut() {
//                    // Probably here will be your exit action
//                    Toast.makeText(getApplicationContext(), "Swiped out right", Toast.LENGTH_SHORT).show();
//                }
//            });


            PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.onboarding_container, onBoardingFragment);
            fragmentTransaction.commit();
            onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
                @Override
                public void onRightOut() {
                    // Update the shared preferences with the current version code
                    prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            });
            onBoardingFragment.setOnChangeListener(new PaperOnboardingOnChangeListener() {
                @Override
                public void onPageChanged(int oldIndex, int newIndex) {
                    if(newIndex==0) getWindow().setStatusBarColor(Color.parseColor("#678FB4"));
                    if(newIndex==1) getWindow().setStatusBarColor(Color.parseColor("#65B0B4"));
                    if(newIndex==2) getWindow().setStatusBarColor(Color.parseColor("#9B90BC"));
                    if(newIndex==3) getWindow().setStatusBarColor(Color.parseColor("#81c784"));
                    if(newIndex==4) getWindow().setStatusBarColor(Color.parseColor("#ffee58"));

                }
            });
        }
        else {
            if (auth.getCurrentUser() != null) {
                auth.getCurrentUser().reload()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (auth.getCurrentUser() != null) {
                                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Log.e("TAG", "onSuccess: rasta 1");
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            } else {
                Log.e("TAG", "onCreate: rasta 2");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }


            setContentView(R.layout.activity_splash);
        }

    }

    private int checkFirstRun() {

        PREFS_NAME = "MyPrefsFile";
        PREF_VERSION_CODE_KEY = "version_code";
        DOESNT_EXIST = -1;

        // Get current version code
        currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return 0;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)

            return 1;

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
            return 2;
        }



        return -1;
    }
}
