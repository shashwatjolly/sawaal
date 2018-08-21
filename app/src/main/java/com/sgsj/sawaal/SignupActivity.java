package com.sgsj.sawaal;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.sql.Types.NULL;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputroll,inputnum,inputUname;
    private DatabaseReference mDatabase;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String college,branch,email,password,username,roll_no,mobile_num,score;


    private void Call(){
                        auth.getCurrentUser().reload()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                if(auth.getCurrentUser().isEmailVerified())
                                {
                                    Log.e("MACHAYA","MACHAYA");
                                    PerformSignup();
                                }
                                else
                                {
                                    Log.e("HAGGA","HAGGA");
                                }
                            }
                        });

    }

    private void PerformSignup(){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "Verification email has been sent to your email. Verification link will expire in 15 minutes and account will be deleted if not verified." + task.isSuccessful(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.i("Success", "Yes");
                                    }
                                    else{
                                        Log.i("Success", "No");}
                                }
                            });

//                            HashMap<String,String> info = new HashMap<>();
//                            info.put("Username",username);
//                            info.put("Email",email);
//                            info.put("Password",password);
//                            info.put("College",college);
//                            info.put("Branch",branch);
//                            info.put("Roll_No",roll_no);
//                            info.put("Mobile_No",mobile_num);
//                            info.put("Score",score);
//
//
//// ...
//                            mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(info);

                            startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                            finish();

                    }
                });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
//        startService(new Intent(this, OnClearFromRecentService.class));
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressBar = (ProgressBar) findViewById(R.id.regprogressBar);
        progressBar.setVisibility(View.GONE);

        ///////////////////////////// SPINNER FOR COLLEGE //////////////////////////////////////////////////////
        final Spinner spinner = (Spinner) findViewById(R.id.collegespin);
        String[] colleges = new String[]{
                "College",
                "IIT Guwahati",
                "IIT Roorkee",
                "IIT Bombay",
        };

        final List<String> colList = new ArrayList<>(Arrays.asList(colleges));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,colList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                college="";
                if(position==0){
//                     Notify the selected item text
                }
                else
                    college=selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ///////////////////////////// SPINNER FOR BRANCH ///////////////////////////////////////////////////////////////
        final Spinner spinner1 = (Spinner) findViewById(R.id.branchspin);

        // Initializing a String Array
        String[] branches = new String[]{
                "Branch",
                "Computer Science and Engineering",
                "Mathematics and Computing",
                "Mechanical Engineering",
        };

        final List<String> branchList = new ArrayList<>(Arrays.asList(branches));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,branchList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner1.setAdapter(spinnerArrayAdapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                branch="";
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position==0){
//                     Notify the selected item text
//                    Toast.makeText
//                            (getApplicationContext(), "Select your branch", Toast.LENGTH_SHORT)
//                            .show();
                }
                else
                    branch=selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////////////


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

//        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.btn_reg);
        inputEmail = (EditText) findViewById(R.id.regemail);
        inputPassword = (EditText) findViewById(R.id.regpassword);
        progressBar = (ProgressBar) findViewById(R.id.regprogressBar);

        inputroll = (EditText) findViewById(R.id.regroll);
        inputnum = (EditText) findViewById(R.id.regmob);
        inputUname = (EditText) findViewById(R.id.reguname);



//        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
//            }
//        });

//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                mobile_num = inputnum.getText().toString().trim();
                roll_no = inputroll.getText().toString().trim();
                score = "0";
                username = inputUname.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                PerformSignup();
//                auth.signInAnonymously()
//                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("TAG", "signInAnonymously:success");
//                                    if (auth.getCurrentUser().isEmailVerified() == false) {
//                                        auth.getCurrentUser().updateEmail(email);
//                                        auth.getCurrentUser().sendEmailVerification();
//                                        Log.i("TAG", "mail sent.....................................");
//                                        Call();
//                                    }
//                                    //updateUI(user);
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.i("TAG", "signInAnonymously:failure", task.getException());
//                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        auth.getCurrentUser().reload()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        if(auth.getCurrentUser().isEmailVerified() == false) {
                                            final FirebaseUser currentUser = auth.getCurrentUser();
                                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("TAG","Deleted user");
                                                        Toast.makeText(getApplicationContext(), "Verification link expired. Please signup again.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(SignupActivity.this, SignupActivity.class));
                                                        finish();
                                                    } else {
                                                        Log.w("TAG","Could not delete user");
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            HashMap<String,String> info = new HashMap<>();
                                            info.put("Username",username);
                                            info.put("Email",email);
                                            info.put("Password",password);
                                            info.put("College",college);
                                            info.put("Branch",branch);
                                            info.put("Roll_No",roll_no);
                                            info.put("Mobile_No",mobile_num);
                                            info.put("Score",score);
                                            mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(info);
                                        }
                                    }
                                });

                    }
                }, 900000);




            }
        });
    }

    public static class OnClearFromRecentService extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("ClearFromRecentService", "Service Started");
            return START_NOT_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            final Context ct = this;
            Log.d("ClearFromRecentService", "Service Destroyed");
            ((ActivityManager)ct.getSystemService(ACTIVITY_SERVICE))
                    .clearApplicationUserData();
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            Log.e("ClearFromRecentService", "END");
            FirebaseAuth auth1;
            auth1 = FirebaseAuth.getInstance();
            final Context ct = this;
            if(auth1.getCurrentUser() != null) {
                if (auth1.getCurrentUser().isEmailVerified() == false) {
//                    Intent newIntent = new Intent(ct, MainActivity.class);
//                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(newIntent);
//                    auth1 = FirebaseAuth.getInstance();
                    final FirebaseUser currentUser = auth1.getCurrentUser();
                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d("TAG", "Deleted user");
                                Toast.makeText(ct, "Verification link expired. Please signup again.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ct, MainActivity.class));
                            } else {
                                Log.w("TAG", "Could not delete user");
                            }
                        }
                    });
                }
            }
            stopSelf();
        }
    }


}

