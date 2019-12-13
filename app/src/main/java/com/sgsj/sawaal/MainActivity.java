package com.sgsj.sawaal;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private CircularProgressButton btnLoginOutlook;
    public String PREF_ACCESS_TOKEN = "access_token";
    public  String PREFS_NAME = "MyPrefsFile";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //Get Firebase auth instance

        // set the view now
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLoginOutlook = findViewById(R.id.btn_login_outlook);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.GONE);

        btnLoginOutlook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
//                provider.addCustomParameter("login_hint", "user@iitg.ac.in");
                provider.addCustomParameter("tenant", "850aa78d-94e1-4bc6-9cf3-8c11b530701c");
//                Task<AuthResult> pendingResultTask = auth.getPendingAuthResult();
//                if (pendingResultTask != null) {
//                    // There's something already here! Finish the sign-in for your user.
//                    pendingResultTask
//                            .addOnSuccessListener(
//                                    new OnSuccessListener<AuthResult>() {
//                                        @Override
//                                        public void onSuccess(AuthResult authResult) {
//                                            // User is signed in.
//                                            // IdP data available in
//                                            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//
////                                            prefs.edit().putString(PREF_ACCESS_TOKEN, authResult.getCredential().getIdToken()).apply();
//                                            Log.e("Hello", authResult.toString());
//                                            Log.i("User Profile 1", authResult.getAdditionalUserInfo().getProfile().toString());
//                                            Log.i("User Profile Token", authResult.getCredential().toString());
//                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                            // The OAuth access token can also be retrieved:
//                                            // authResult.getCredential().getAccessToken()
//                                            // The OAuth ID token can also be retrieved:
//                                            // authResult.getCredential().getIdToken()
//                                        }
//                                    })
//                            .addOnFailureListener(
//                                    new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            // Handle failure.
//                                        }
//                                    });
//                } else {
                    auth
                            .startActivityForSignInWithProvider(/* activity= */ MainActivity.this, provider.build())
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // User is signed in.
                                            // IdP data available in
//                                            authResult.getCredential().zza();
                                            Log.e("Hello", authResult.getCredential().zza(). toString());
                                            Log.i("User Profile 2", authResult.getAdditionalUserInfo().getProfile().toString());
                                            Log.i("auth email", auth.getCurrentUser().getEmail());
                                            Log.e("Hey there", auth.getCurrentUser().getProviderData().get(0).toString());
                                            Log.e("Ro dunga", ((OAuthCredential) authResult.getCredential()).getAccessToken());
                                            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                            prefs.edit().putString(PREF_ACCESS_TOKEN, ((OAuthCredential) authResult.getCredential()).getAccessToken()).apply();
//                                            Log.e("User Profile Token", credential.getIdToken());
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            // authResult.getAdditionalUserInfo().getProfile().
                                            // The OAuth access token can also be retrieved:
                                            // authResult.getCredential().getAccessToken().
                                            // The OAuth ID token can also be retrieved:
                                            // authResult.getCredential().getIdToken().
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




//            }
        });
    }
}
