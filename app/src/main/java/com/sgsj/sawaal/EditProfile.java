package com.sgsj.sawaal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class EditProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText newname, newphone;
    private TextView score, uploads, hacks, email, branch;
    private CircularProgressButton donebtn;
    private DatabaseReference db;
    String name, phone;
    private boolean complete1, complete2, complete3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Profile");
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        donebtn = findViewById(R.id.donebtn);
        newname = findViewById(R.id.profilenameedit);
        newphone = findViewById(R.id.profilemobedit);
        score = findViewById(R.id.profilescore);
        uploads = findViewById(R.id.profileuploads);
        hacks = findViewById(R.id.profilehacks);
        email = findViewById(R.id.profileemail);
        branch = findViewById(R.id.profilebranch);

//        Fade fade = new Fade();
//        View decor = getWindow().getDecorView();
//        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
//        fade.excludeTarget(android.R.id.statusBarBackground, true);
//        fade.excludeTarget(android.R.id.navigationBarBackground, true);
//        fade.excludeTarget(R.id.ll, true);
//
//        getWindow().setEnterTransition(fade);
//        getWindow().setExitTransition(fade);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            phone = extras.getString("phone");
            newname.setText(extras.getString("name"));
            newphone.setText(extras.getString("phone"));
            score.setText(extras.getString("score"));
            uploads.setText(extras.getString("uploads"));
            hacks.setText(extras.getString("hacks"));
            email.setText(extras.getString("email"));
            branch.setText(extras.getString("branch"));
            //The key argument here must match that used in the other activity
        }


    }

    public void done(View v) {
        if(newname.getText().length() == 0) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(newphone.getText().length() != 10) {
            Toast.makeText(this, "Phone no. is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name==newname.getText().toString()) {complete1 = true; complete2 = true;}
        if(phone==newphone.getText().toString()) complete3 = true;

        if(!complete1 || !complete3) donebtn.startAnimation();

        if(!complete1) {
            FirebaseUser user = auth.getCurrentUser();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newname.getText().toString()).build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.e("TAG", "Username updated");
                        complete1 = true;
                        if (complete1 && complete2 && complete3) {
//                            Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
//                            startActivity(intent);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("newmob", newphone.getText().toString());
                            returnIntent.putExtra("newname", newname.getText().toString());
                            setResult(1,returnIntent);
                            finish();
                        }
                    } else {
                    }
                }
            });
        }

        if(!complete2) {
            db.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").setValue(newname.getText().toString(), new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    Log.e("TAG", "Username set in databse");
                    complete2 = true;
                    if (complete1 && complete2 && complete3) {
//                        Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
//                        startActivity(intent);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("newmob", newphone.getText().toString());
                        returnIntent.putExtra("newname", newname.getText().toString());
                        setResult(1,returnIntent);
                        finish();
                    }
                }
            });
        }

        if(!complete3) {
            db.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mobile_No").setValue(newphone.getText().toString(), new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    Log.e("TAG", "Mob set in databse");
                    complete3 = true;
                    if (complete1 && complete2 && complete3) {
//                        Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
//                        startActivity(intent);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("newmob", newphone.getText().toString());
                        returnIntent.putExtra("newname", newname.getText().toString());
                        setResult(1,returnIntent);
                        finish();
                    }
                }
            });
        }
    }
}
