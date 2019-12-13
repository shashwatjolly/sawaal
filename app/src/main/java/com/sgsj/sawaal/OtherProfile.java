package com.sgsj.sawaal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.annotation.ColorInt;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.palette.graphics.Palette;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sgsj.sawaal.Recycler_View_Adapter1.v;

public class OtherProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView name, score, uploads, hacks, email, phone, branch;
    private EditText newname, newemail, newphone;
    private CircularProgressButton donebtn;
    private Button editbtn;
    private DatabaseReference db;
    private CircleImageView profileimg;
    private boolean complete1, complete2, complete3;
    private LinearLayout ll;
    private String profileimgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        getSupportActionBar().setTitle("Profile");
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.profilename);
        score = findViewById(R.id.profilescore);
        uploads = findViewById(R.id.profileuploads);
        hacks = findViewById(R.id.profilehacks);
        email = findViewById(R.id.profileemail);
        branch = findViewById(R.id.profilebranch);
        donebtn = findViewById(R.id.donebtn);
        profileimg = findViewById(R.id.profileimg);
        ll = findViewById(R.id.llprofile);


        extractbranch();
        email.setText(auth.getCurrentUser().getEmail());

//        Dali.create(this).load(R.drawable.ic_launcher_background).blurRadius(12).into(profileimg);
//        BlurKit.init(this);
//        BlurKit.getInstance().blur(profileimg, 12);
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.temp);
//        Palette.from(b).generate(new Palette.PaletteAsyncListener() {
//            public void onGenerated(Palette p) {
//                Palette.Swatch psVibrant = p.getVibrantSwatch();
//                Palette.Swatch psMuted = p.getMutedSwatch();
//                int color = getResources().getColor(R.color.colorPrimary);
//                if(psVibrant!=null) {
//                    color = psVibrant.getRgb();
//                }
//                else if(psMuted!=null) {
//                    color = psMuted.getRgb();
//                }
//                ll.setBackgroundColor(color);
//                name.setTextColor(getContrastColor(color));
//                profileimg.setBorderColor(getContrastColor(color));
//            }
//        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name.setText(extras.getString("name"));
            score.setText(extras.getString("score"));
            profileimgurl = extras.getString("url");
            String done = extras.getString("done");
            int pos = extras.getInt("pos");
//            profileimg.setImageDrawable(v.profileimg.getDrawable());
//            ImageView i = getParent().findViewById(Integer.parseInt(id));
//            profileimg.setImageDrawable(i.getDrawable());
            Log.e(profileimgurl, "HMMDP");
            if(done.equals("yes")) {
                profileimg.setImageDrawable(v.profileimg.getDrawable());
                Bitmap b = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
                Palette.from(b).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette p) {
                        int color = p.getLightVibrantColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorAccent)));
                        Palette.Swatch psVibrant = p.getVibrantSwatch();
//                        color = psVibrant.getPopulation();
                        Palette.Swatch psMuted = p.getMutedSwatch();
//                        int color = getResources().getColor(R.color.colorPrimary);
//                        if(psVibrant!=null) {
//                            color = psVibrant.getRgb();
//                        }
//                        else if(psMuted!=null) {
//                            color = psMuted.getRgb();
//                        }
                        ll.setBackgroundColor(color);
                        name.setTextColor(getContrastColor(color));
                        profileimg.setBorderColor(getContrastColor(color));
                    }
                });
            }
            else {
                Picasso.with(getApplicationContext()).load(Uri.parse(profileimgurl)).placeholder(R.drawable.ic_launcher_background).into(profileimg, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap b = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
                        Palette.from(b).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                int color = p.getLightVibrantColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorAccent)));
                                Palette.Swatch psVibrant = p.getVibrantSwatch();
//                        color = psVibrant.getPopulation();
                                Palette.Swatch psMuted = p.getMutedSwatch();
//                        int color = getResources().getColor(R.color.colorPrimary);
//                        if(psVibrant!=null) {
//                            color = psVibrant.getRgb();
//                        }
//                        else if(psMuted!=null) {
//                            color = psMuted.getRgb();
//                        }
                                ll.setBackgroundColor(color);
                                name.setTextColor(getContrastColor(color));
                                profileimg.setBorderColor(getContrastColor(color));
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

            }
            //The key argument here must match that used in the other activity
        }


//        Fade fade = new Fade();
//        View decor = getWindow().getDecorView();
//        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
//        fade.excludeTarget(android.R.id.statusBarBackground, true);
//        fade.excludeTarget(android.R.id.navigationBarBackground, true);
//        fade.excludeTarget(R.id.ll, true);
//        getWindow().setExitTransition(fade);

    }

    public void extractscore()
    {
        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
        Query query = testRef.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.e("check",issue.getKey().toString());

                        score.setText(issue.child("Score").getValue().toString());
                        complete1 = true;
                        if(complete1 && complete2 && complete3) {
                            editbtn.setVisibility(View.VISIBLE);
                        }
                    }

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void extractmob()
    {
        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
        Query query = testRef.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.e("check",issue.getKey().toString());
                        phone.setText(issue.child("Mobile_No").getValue().toString());
                        complete2 = true;
                        if(complete1 && complete2 && complete3) {
                            editbtn.setVisibility(View.VISIBLE);
                        }
                    }

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void extractbranch()
    {
        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
        Query query = testRef.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.e("check1",issue.getKey().toString());

                        branch.setText(issue.child("Branch").getValue().toString());
                        complete3 = true;
                        if(complete1 && complete2 && complete3) {
                            editbtn.setVisibility(View.VISIBLE);
                        }
                    }

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void editprofile(View v) {
//        newname.setText(name.getText());
//        newphone.setText(phone.getText());
//        name.setVisibility(View.GONE);
//        phone.setVisibility(View.GONE);
//        editbtn.setVisibility(View.GONE);
//        newname.setVisibility(View.VISIBLE);
//        newphone.setVisibility(View.VISIBLE);
//        donebtn.setVisibility(View.VISIBLE);
//        complete1 = false;
//        complete2 = false;
//        complete3 = false;
        Intent i = new Intent(OtherProfile.this, EditProfile.class);
        i.putExtra("name", name.getText());
        i.putExtra("email", email.getText());
        i.putExtra("uploads", uploads.getText());
        i.putExtra("hacks", hacks.getText());
        i.putExtra("phone", phone.getText());
        i.putExtra("score", score.getText());
        i.putExtra("branch", branch.getText());
        Pair<View, String> p1 = Pair.create((View)editbtn, "profilebtn");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(OtherProfile.this, p1);
        ActivityCompat.startActivityForResult(this, i, 1, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1)
        {
            String newmob = data.getStringExtra("newmob");
            String newname = data.getStringExtra("newname");
            name.setText(newname);
            phone.setText(newmob);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("newname", newname);
            setResult(2,returnIntent);
        }
    }

    @ColorInt
    public static int getContrastColor(@ColorInt int color) {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return a < 0.5 ? Color.BLACK : Color.WHITE;
    }

//    public void done(View v) {
//        if(newname.getText().length() == 0) {
//            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(newphone.getText().length() != 10) {
//            Toast.makeText(this, "Phone no. is invalid", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if(name.getText()==newname.getText().toString()) {complete1 = true; complete2 = true;}
//        if(phone.getText()==newphone.getText().toString()) complete3 = true;
//
//        if(!complete1 || !complete3) donebtn.startAnimation();
//
//        if(!complete1) {
//            FirebaseUser user = auth.getCurrentUser();
//            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newname.getText().toString()).build();
//            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Log.e("TAG", "Username updated");
//                        complete1 = true;
//                        if (complete1 && complete2 && complete3) {
//                            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    } else {
//                    }
//                }
//            });
//        }
//
//        if(!complete2) {
//            db.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Username").setValue(newname.getText().toString(), new DatabaseReference.CompletionListener() {
//                public void onComplete(DatabaseError error, DatabaseReference ref) {
//                    Log.e("TAG", "Username set in databse");
//                    complete2 = true;
//                    if (complete1 && complete2 && complete3) {
//                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            });
//        }
//
//        if(!complete3) {
//            db.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mobile_No").setValue(newphone.getText().toString(), new DatabaseReference.CompletionListener() {
//                public void onComplete(DatabaseError error, DatabaseReference ref) {
//                    Log.e("TAG", "Mob set in databse");
//                    complete3 = true;
//                    if (complete1 && complete2 && complete3) {
//                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            });
//        }
//    }
}
