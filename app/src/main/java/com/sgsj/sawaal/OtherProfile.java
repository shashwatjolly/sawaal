package com.sgsj.sawaal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import androidx.annotation.ColorInt;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.ColorUtils;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
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

import static java.lang.Integer.parseInt;


public class OtherProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView name, score, email, rollno;
    private CircleImageView profileimg;
    private LinearLayout ll;
    private String uid, profileemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        getSupportActionBar().setTitle("Profile");
        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.profilename);
        score = findViewById(R.id.profilescore);
        email = findViewById(R.id.profileemail);
        profileimg = findViewById(R.id.profileimg);
        rollno = findViewById(R.id.profilerollno);
        ll = findViewById(R.id.llprofile);


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
            uid = extras.getString(("uid"));
            profileemail = extras.getString("email");
            if(profileemail.equals("NULL")) {
                Log.e("Email", "NULL");
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Email"); //Path in database where to check
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profileemail = dataSnapshot.getValue().toString();
                        email.setText(profileemail);
                        extractrollno();
                        Log.e("Other profile mein", profileemail);
//            String done = extras.getString("done");
//            int pos = extras.getInt("pos");
                        ColorGenerator generator = ColorGenerator.MATERIAL;
                        final int color = generator.getColor(profileemail);
                        String initials = "";
                        for (String s : name.getText().toString().split(" ")) {
                            initials+=s.charAt(0);
                        }
                        TextDrawable profileimgdrawable = TextDrawable.builder().beginConfig().width(480).height(480).fontSize(160).endConfig().buildRound(initials, color);
                        profileimg.setImageDrawable(profileimgdrawable);
                        int colorbg = ColorUtils.blendARGB(color, Color.BLACK, 0.4f);
                        ll.setBackgroundColor(colorbg);
                        name.setTextColor(getContrastColor(colorbg));

                    }
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }
            else {
                email.setText(profileemail);
                extractrollno();
                Log.e("Other profile", profileemail);
//            String done = extras.getString("done");
//            int pos = extras.getInt("pos");
                ColorGenerator generator = ColorGenerator.MATERIAL;
                final int color = generator.getColor(profileemail);
                String initials = "";
                for (String s : name.getText().toString().split(" ")) {
                    initials+=s.charAt(0);
                }
                TextDrawable profileimgdrawable = TextDrawable.builder().beginConfig().width(480).height(480).fontSize(160).endConfig().buildRound(initials, color);
                profileimg.setImageDrawable(profileimgdrawable);
                int colorbg = ColorUtils.blendARGB(color, Color.BLACK, 0.4f);
                ll.setBackgroundColor(colorbg);
                name.setTextColor(getContrastColor(colorbg));
            }

//            profileimg.setImageDrawable(v.profileimg.getDrawable());
//            ImageView i = getParent().findViewById(Integer.parseInt(id));
//            profileimg.setImageDrawable(i.getDrawable());
//            if(done.equals("yes")) {
//                profileimg.setImageDrawable(v.profileimg.getDrawable());
//                Bitmap b = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
//                Palette.from(b).generate(new Palette.PaletteAsyncListener() {
//                    public void onGenerated(Palette p) {
//                        int color = p.getLightVibrantColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorAccent)));
//                        Palette.Swatch psVibrant = p.getVibrantSwatch();
////                        color = psVibrant.getPopulation();
//                        Palette.Swatch psMuted = p.getMutedSwatch();
////                        int color = getResources().getColor(R.color.colorPrimary);
////                        if(psVibrant!=null) {
////                            color = psVibrant.getRgb();
////                        }
////                        else if(psMuted!=null) {
////                            color = psMuted.getRgb();
////                        }
//                        ll.setBackgroundColor(color);
//                        name.setTextColor(getContrastColor(color));
//                        profileimg.setBorderColor(getContrastColor(color));
//                    }
//                });
//            }
//            else {
//                Picasso.with(getApplicationContext()).load(Uri.parse(profileimgurl)).placeholder(R.drawable.ic_launcher_background).into(profileimg, new com.squareup.picasso.Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Bitmap b = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
//                        Palette.from(b).generate(new Palette.PaletteAsyncListener() {
//                            public void onGenerated(Palette p) {
//                                int color = p.getLightVibrantColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorAccent)));
//                                Palette.Swatch psVibrant = p.getVibrantSwatch();
////                        color = psVibrant.getPopulation();
//                                Palette.Swatch psMuted = p.getMutedSwatch();
////                        int color = getResources().getColor(R.color.colorPrimary);
////                        if(psVibrant!=null) {
////                            color = psVibrant.getRgb();
////                        }
////                        else if(psMuted!=null) {
////                            color = psMuted.getRgb();
////                        }
//                                ll.setBackgroundColor(color);
//                                name.setTextColor(getContrastColor(color));
//                                profileimg.setBorderColor(getContrastColor(color));
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
//
//            }
            //The key argument here must match that used in the other activity
        }

    }

    public void extractrollno()
    {
        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
        Query query = testRef.orderByChild("Email").equalTo(email.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        Log.e("check",issue.getKey().toString());
                        rollno.setText(issue.child("Rollno").getValue().toString());
                    }

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1)
        {
            String newmob = data.getStringExtra("newmob");
            String newname = data.getStringExtra("newname");
            name.setText(newname);
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
