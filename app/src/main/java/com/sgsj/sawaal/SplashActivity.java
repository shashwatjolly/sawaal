package com.sgsj.sawaal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private String PREFS_NAME;
    private String PREF_VERSION_CODE_KEY;
    private int DOESNT_EXIST;
    Integer currentVersionCode;
    SharedPreferences prefs;
    int savedVersionCode;
    private DatabaseReference db;


    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser()==null) {
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
            PaperOnboardingPage scr4 = new PaperOnboardingPage("Upvote/Downvote",
                    "Vote on papers to help others by moving the best version to the top",
                    Color.parseColor("#81c784"), R.drawable.ic_thumbs_up_down_black_128dp, R.drawable.ic_info_outline_black_24dp);
            PaperOnboardingPage scr5 = new PaperOnboardingPage("Let's Get Started!",
                    "Swipe right to begin :)",
                    Color.parseColor("#ffee58"), R.drawable.ic_navigate_next_black_128dp, R.drawable.ic_favorite_border_black_24dp);
//            PaperOnboardingPage scr6 = new PaperOnboardingPage("Logging you in",
//                    "Hold on!",
//                    Color.parseColor("#ffee58"), R.drawable.progressbar, R.layout.progressbar);

            ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
            elements.add(scr1);
            elements.add(scr2);
            elements.add(scr3);
            elements.add(scr4);
            elements.add(scr5);
//            elements.add(scr6);

            PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.onboarding_container, onBoardingFragment);
            fragmentTransaction.commit();
            onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
                @Override
                public void onRightOut() {
                    setContentView(R.layout.activity_splash);
                    getWindow().setStatusBarColor(Color.parseColor(getResources().getString(0 + R.color.colorPrimary)));
                    Toast.makeText(SplashActivity.this, "Login to Outlook", Toast.LENGTH_SHORT);
                    // Update the shared preferences with the current version code
//                    prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
                    OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
                    provider.addCustomParameter("tenant", "850aa78d-94e1-4bc6-9cf3-8c11b530701c");
                    auth
                            .startActivityForSignInWithProvider(/* activity= */ SplashActivity.this, provider.build())
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            if(authResult.getAdditionalUserInfo().isNewUser()) {
                                                final HashMap<String, String> info = new HashMap<>();
                                                Log.e("Here email", auth.getCurrentUser().getEmail());
                                                Log.e("Here name", auth.getCurrentUser().getDisplayName());
                                                Log.e("Here uid", auth.getCurrentUser().getUid());
                                                Log.e("User profile", authResult.getAdditionalUserInfo().getProfile().get("surname").toString());
                                                info.put("Email", auth.getCurrentUser().getEmail());
                                                info.put("Name", auth.getCurrentUser().getDisplayName());
                                                info.put("Rollno", authResult.getAdditionalUserInfo().getProfile().get("surname").toString());
                                                info.put("Score", "0");
                                                db.child("Users").child(auth.getCurrentUser().getUid()).setValue(info);
                                            }
                                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure.
                                            Log.i("User Profile", e.toString());

                                        }
                                    });
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
                                    OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
                                    provider.addCustomParameter("tenant", "850aa78d-94e1-4bc6-9cf3-8c11b530701c");
                                    auth
                                        .startActivityForSignInWithProvider(/* activity= */ SplashActivity.this, provider.build())
                                        .addOnSuccessListener(
                                                new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        if(authResult.getAdditionalUserInfo().isNewUser()) {
                                                            final HashMap<String, String> info = new HashMap<>();
                                                            info.put("Name", auth.getCurrentUser().getDisplayName());
                                                            info.put("Score", "0");
                                                            db.child(auth.getCurrentUser().getEmail()).setValue(info);
                                                        }
                                                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle failure.
                                                        Log.i("User Profile", e.toString());

                                                    }
                                                });
                                }
                            }
                        });
            } else {
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
                provider.addCustomParameter("tenant", "850aa78d-94e1-4bc6-9cf3-8c11b530701c");
                auth
                    .startActivityForSignInWithProvider(/* activity= */ SplashActivity.this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    if(authResult.getAdditionalUserInfo().isNewUser()) {
                                        final HashMap<String, String> info = new HashMap<>();
                                        info.put("Name", auth.getCurrentUser().getDisplayName());
                                        info.put("Score", "0");
                                        db.child(auth.getCurrentUser().getEmail()).setValue(info);
                                    }
                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.i("User Profile", e.toString());

                                }
                            });
            }


            setContentView(R.layout.activity_splash);
        }

    }

//    private int checkFirstRun() {
//
//        PREFS_NAME = "MyPrefsFile";
//        PREF_VERSION_CODE_KEY = "version_code";
//        DOESNT_EXIST = -1;
//
//        // Get current version code
//        currentVersionCode = BuildConfig.VERSION_CODE;
//
//        // Get saved version code
//        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
//
//        // Check for first run or upgrade
//        if (currentVersionCode == savedVersionCode) {
//
//            // This is just a normal run
//            return 0;
//
//        } else if (savedVersionCode == DOESNT_EXIST) {
//
//            // TODO This is a new install (or the user cleared the shared preferences)
//
//            return 1;
//
//        } else if (currentVersionCode > savedVersionCode) {
//
//            // TODO This is an upgrade
//            return 2;
//        }
//
//
//
//        return -1;
//    }
}
