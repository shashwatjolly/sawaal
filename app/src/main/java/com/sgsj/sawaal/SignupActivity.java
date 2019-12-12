package com.sgsj.sawaal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sgsj.sawaal.UploadFragment.PICK_PDF_CODE;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputroll,inputnum,inputUname;
    private DatabaseReference mDatabase;
    private StorageReference mStorageReference;
    private Button btnSignIn, btnResetPassword, btnbranch, browsedp;
    private CircularProgressButton btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String college,branch="",email,password,username,roll_no,mobile_num;
    private TextView dpuri, signin;
    private CircleImageView dp;
    private int score;
    private Uri dpurival;


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
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            final HashMap<String, String> info = new HashMap<>();
                            info.put("Username", username);
                            info.put("Email", email);
                            info.put("Password", password);
                            info.put("Branch", branch);
                            info.put("Roll_No", roll_no);
                            info.put("Mobile_No", mobile_num);
//                            info.put("Score",score);

                            mDatabase = FirebaseDatabase.getInstance().getReference();

                            mStorageReference = FirebaseStorage.getInstance().getReference();
                            final Uri filePath = dpurival;
                            final String file_name = user.getUid();
                            StorageReference sRef = mStorageReference.child("ProfilePics/" + file_name);

                            //adding the file to reference
                            sRef.putFile(filePath)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            StorageReference filepath = mStorageReference.child("ProfilePics/").child(file_name);
                                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(final Uri uri) {

                                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.i("Success", "Yes");
                                                            } else {
                                                                Log.i("Success", "No");
                                                            }
                                                        }
                                                    });
                                                    Toast.makeText(SignupActivity.this, "Verification email has been sent to your email. Verification link will expire in 15 minutes and account will be deleted if not verified.", Toast.LENGTH_LONG).show();

                                                    final String safe = auth.getCurrentUser().getUid();
                                                    mDatabase.child("Users").child(safe).child("Score").setValue("0", new DatabaseReference.CompletionListener() {
                                                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                                                            Log.e("TAG", "Mob set in databse");
                                                            btnSignUp.revertAnimation();
                                                            mDatabase.child("Users").child(safe).child("Username").setValue(username);
                                                            mDatabase.child("Users").child(safe).child("Email").setValue(email);
                                                            mDatabase.child("Users").child(safe).child("Password").setValue(password);
                                                            mDatabase.child("Users").child(safe).child("Branch").setValue(branch);
                                                            mDatabase.child("Users").child(safe).child("Roll_No").setValue(roll_no);
                                                            mDatabase.child("Users").child(safe).child("Mobile_No").setValue(mobile_num);
                                                            mDatabase.child("Users").child(safe).child("ProfilePic").setValue(uri.toString());
                                                            Log.e("SET DP URL", "HMM");

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

                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).setPhotoUri(uri).build();
                                                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                auth.signOut();
//                                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                                finish();
                                                                Log.d("TAG", "User display name updated");
                                                                //                                        Toast.makeText(ProfileActivity.this, ".", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Log.d("TAG", "Error in updating user display name");
                                                            }
                                                            //                                        Toast.makeText(ProfileActivity.this, "Error while updating display name.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
//                            progressDialog.dismiss();

                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            //displaying the upload progress

                                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            if((int)progress>0) btnSignUp.setProgress((int)progress);

//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                        }
                                    });

                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this, "Could not create new account", Toast.LENGTH_LONG).show();
                            btnSignUp.revertAnimation();
                            return;
                        }
                    }
                });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        startService(new Intent(this, OnClearFromRecentService.class));

        progressBar = (ProgressBar) findViewById(R.id.regprogressBar);
        progressBar.setVisibility(View.GONE);
        btnbranch = findViewById(R.id.branchbtnopt);
        browsedp = findViewById(R.id.btn_browsedp);
        dpuri = findViewById(R.id.dpuri);
        dp = findViewById(R.id.dp);
        dp.setVisibility(View.GONE);
        signin = findViewById(R.id.link_signin);
        getSupportActionBar().setTitle("Register");

        ///////////////////////////// SPINNER FOR COLLEGE //////////////////////////////////////////////////////
//        final Spinner spinner = (Spinner) findViewById(R.id.collegespin);
//        String[] colleges = new String[]{
//                "College",
//                "IIT Guwahati",
//                "IIT Roorkee",
//                "IIT Bombay",
//        };
//
//        final List<String> colList = new ArrayList<>(Arrays.asList(colleges));
//        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
//                this,R.layout.spinner_item,colList){
//            @Override
//            public boolean isEnabled(int position){
//                if(position == 0)
//                {
//                    // Disable the first item from Spinner
//                    // First item will be use for hint
//                    return false;
//                }
//                else
//                {
//                    return true;
//                }
//            }
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//                if(position == 0){
//                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY);
//                }
//                else {
//                    tv.setTextColor(Color.WHITE);
//                }
//                return view;
//            }
//        };
//        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        spinner.setAdapter(spinnerArrayAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItemText = (String) parent.getItemAtPosition(position);
//                // If user change the default selection
//                // First item is disable and it is used for hint
//                college="";
//                if(position==0){
////                     Notify the selected item text
//                }
//                else
//                    college=selectedItemText;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        ///////////////////////////// SPINNER FOR BRANCH ///////////////////////////////////////////////////////////////
//        final Spinner spinner1 = (Spinner) findViewById(R.id.branchspin);
//
//        // Initializing a String Array
//        String[] branches = new String[]{
//                "Branch",
//                "Computer Science and Engineering",
//                "Mathematics and Computing",
//                "Mechanical Engineering",
//        };
//
//        final List<String> branchList = new ArrayList<>(Arrays.asList(branches));
//
//        // Initializing an ArrayAdapter
//        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
//                this,R.layout.support_simple_spinner_dropdown_item,branchList){
//            @Override
//            public boolean isEnabled(int position){
//                if(position == 0)
//                {
//                    // Disable the first item from Spinner
//                    // First item will be use for hint
//                    return false;
//                }
//                else
//                {
//                    return true;
//                }
//            }
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//                if(position == 0){
//                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY);
//                }
//                else {
//                    tv.setTextColor(Color.WHITE);
//                }
//                return view;
//            }
//        };
//        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        spinner1.setAdapter(spinnerArrayAdapter1);
//
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItemText = (String) parent.getItemAtPosition(position);
//                branch="";
//                // If user change the default selection
//                // First item is disable and it is used for hint
//                if(position==0){
////                     Notify the selected item text
////                    Toast.makeText
////                            (getApplicationContext(), "Select your branch", Toast.LENGTH_SHORT)
////                            .show();
//                }
//                else
//                    branch=selectedItemText;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        ////////////////////////////////////////////////////////////////////////////////////////////////////


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

//        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.btn_reg);
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

        signin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //perform your action here
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                mobile_num = inputnum.getText().toString().trim();
                roll_no = inputroll.getText().toString().trim();
                score =0;
                username = inputUname.getText().toString().trim();

                final TextInputLayout emailin = findViewById(R.id.emailin);
                emailin.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() < 1) {
                            emailin.setError("Email cannot be empty");
                        }

                        if (s.length() > 0) {
                            emailin.setErrorEnabled(false);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                final TextInputLayout unamein = findViewById(R.id.unamein);
                unamein.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() < 1) {
                            unamein.setError("Name cannot be empty");
                        }

                        if (s.length() > 0) {
                            unamein.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                final TextInputLayout rollin = findViewById(R.id.rollin);
                rollin.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() < 1) {
                            rollin.setError("Roll no. cannot be empty");
                        }

                        if (s.length() > 0) {
                            rollin.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                final TextInputLayout mobin = findViewById(R.id.mobin);
                mobin.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() < 1) {
                            mobin.setError("Mobile no. cannot be empty");
                        }

                        if (s.length() > 0) {
                            mobin.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                final TextInputLayout passin = findViewById(R.id.passin);
                passin.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() < 6) {
                            passin.setError("Password should be of length >= 6");
                        }

                        if (s.length() >= 6) {
                            passin.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    emailin.setError("Email cannot be empty");
                    return;
                }

                if (TextUtils.isEmpty(username)) {
//                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    unamein.setError("Username cannot be empty");
                    return;
                }

                if(branch.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Select branch!", Toast.LENGTH_SHORT).show();
//                    btnbranch.setError("Select branch");
                    return;
                }

                if (TextUtils.isEmpty(roll_no)) {
//                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    rollin.setError("Roll no. cannot be empty");
                    return;
                }

                if (TextUtils.isEmpty(mobile_num)) {
//                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    mobin.setError("Mobile no. cannot be empty");
                    return;
                }

//                if (TextUtils.isEmpty(password)) {
////                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
//                    inputPassword.setError("Password");
//                    return;
//                }

                if (password.length() < 6) {
//                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    passin.setError("Password should be of length >= 6");
                    return;
                }

                if(dpurival.toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Select profile picture!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                progressBar.setVisibility(View.VISIBLE);
                btnSignUp.startAnimation();


                PerformSignup();

            }
        });

        final Activity a = this;

        browsedp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for greater than lolipop versions we need the permissions asked on runtime
                //so if the permission is not available user will go to the screen to allow storage permission
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),
//                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    return;
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(a,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(a, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                }

                //creating an intent for file chooser
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_PDF_CODE);

            }

        });
    }



    public void showbranchopt(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a branch");

// add a list
        String[] branches = {"Computer Science and Engineering", "Mathematics and Computing", "Mechanical Engineering"};
        builder.setItems(branches, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        btnbranch.setText("Computer Science and Engineering");
                        branch = (String) btnbranch.getText();
                        break;
                    }
                    case 1: {
                        btnbranch.setText("Mathematics and Computing");
                        branch = (String) btnbranch.getText();
                        break;
                    }
                    case 2: {
                        btnbranch.setText("Mechanical Engineering");
                        branch = (String) btnbranch.getText();
                        break;
                    }
                }
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                dpurival = data.getData();
                dpuri.setVisibility(View.GONE);
                dp.setImageURI(dpurival);
                dp.setVisibility(View.VISIBLE);

            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

