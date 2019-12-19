package com.sgsj.sawaal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
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
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.sgsj.sawaal.HomeActivity.d;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView name, score, email, rollno;
    private CircleImageView profileimg;
    private LinearLayout ll;
    private Button signout;
//    public String PREF_ACCESS_TOKEN = "access_token";
//    public  String PREFS_NAME = "MyPrefsFile";
//    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        auth = FirebaseAuth.getInstance();
        name = findViewById(R.id.profilename);
        score = findViewById(R.id.profilescore);
        email = findViewById(R.id.profileemail);
        rollno = findViewById(R.id.profilerollno);
        profileimg = findViewById(R.id.profileimg);
        ll = findViewById(R.id.llprofile);
        signout = findViewById(R.id.signout);

        name.setText(auth.getCurrentUser().getDisplayName());
        email.setText(auth.getCurrentUser().getEmail());

        extractscore();
        extractrollno();

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(auth.getCurrentUser().getEmail());
        String initials = "";
        for (String s : auth.getCurrentUser().getDisplayName().split(" ")) {
            initials+=s.charAt(0);
        }
        int len = initials.length();
        if(len>1) {
            initials = ""+initials.charAt(0)+initials.charAt(len-1);
        }
        TextDrawable profileimgdrawable = TextDrawable.builder().beginConfig().width(480).height(480).fontSize(160).endConfig().buildRound(initials, color);
        profileimg.setImageDrawable(profileimgdrawable);
        int colorbg = ColorUtils.blendARGB(color, Color.BLACK, 0.4f);
        ll.setBackgroundColor(colorbg);
        name.setTextColor(getContrastColor(colorbg));

        signout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   auth.signOut();
                   Toast.makeText(v.getContext(), "Signed out", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
               }
           });

        // Flow to get profile image from outlook

//        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String accessToken = prefs.getString(PREF_ACCESS_TOKEN, "NULL");
//        Log.e("Access token", accessToken);
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("https://graph.microsoft.com/v1.0/me/photo/$value")
//                .addHeader("Authorization", "Bearer "+accessToken)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                } else {
////                    Log.e("Type", response.body().getClass());
//                    InputStream i = response.body().byteStream();
////                    String b = response.body().string();
////                    BigInteger bigInt = new BigInteger(b, 2);
////                    byte[] binaryData = bigInt.toByteArray();
//                    final Bitmap image = BitmapFactory.decodeStream(i);
////                    Drawable image = new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length));
//                    Log.i("Response", response.body().string());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            profileimg.setImageBitmap(image);
//                            startPostponedEnterTransition();
//                        }
//                    });
////                    profileimg.setImageDrawable(image);
//                }
//            }
//        });


//        imageDownload(getApplicationContext(), auth.getCurrentUser().getPhotoUrl().toString());

//        Picasso p = new Picasso.Builder(getApplicationContext())
//                .memoryCache(new LruCache(24000))
//                .build();
//        if(d != null)  {
//            profileimg.setImageDrawable(d);
//            Log.e("Using", "Naya wala");
//            Bitmap b = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
//            Palette.from(b).generate(new Palette.PaletteAsyncListener() {
//                public void onGenerated(Palette p) {
//                    int color = p.getLightVibrantColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorAccent)));
//                    Palette.Swatch psVibrant = p.getVibrantSwatch();
////                        color = psVibrant.getPopulation();
//                    Palette.Swatch psMuted = p.getMutedSwatch();
////                        int color = getResources().getColor(R.color.colorPrimary);
////                        if(psVibrant!=null) {
////                            color = psVibrant.getRgb();
////                        }
////                        else if(psMuted!=null) {
////                            color = psMuted.getRgb();
////                        }
//                    ll.setBackgroundColor(color);
//                    name.setTextColor(getContrastColor(color));
//                    profileimg.setBorderColor(getContrastColor(color));
//                }
//            });
//        }
//        else {
//            Picasso.with(getApplicationContext()).load(auth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.ic_launcher_background).into(profileimg, new com.squareup.picasso.Callback() {
//                @Override
//                public void onSuccess() {
//                    Bitmap b = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
//                    Palette.from(b).generate(new Palette.PaletteAsyncListener() {
//                        public void onGenerated(Palette p) {
//                            int color = p.getLightVibrantColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorAccent)));
//                            Palette.Swatch psVibrant = p.getVibrantSwatch();
////                        color = psVibrant.getPopulation();
//                            Palette.Swatch psMuted = p.getMutedSwatch();
////                        int color = getResources().getColor(R.color.colorPrimary);
////                        if(psVibrant!=null) {
////                            color = psVibrant.getRgb();
////                        }
////                        else if(psMuted!=null) {
////                            color = psMuted.getRgb();
////                        }
//                            ll.setBackgroundColor(color);
//                            name.setTextColor(getContrastColor(color));
//                            profileimg.setBorderColor(getContrastColor(color));
//                        }
//                    });
//                }
//
//                @Override
//                public void onError() {
//
//                }
//            });
//        }
//        Dali.create(this).load(R.drawable.ic_launcher_background).blurRadius(12).into(profileimg);
//        BlurKit.init(this);
//        BlurKit.getInstance().blur(profileimg, 12);
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.temp);



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
                    }

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void extractrollno()
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
//            String newname = data.getStringExtra("newname");
//            name.setText(newname);
            Intent returnIntent = new Intent();
//            returnIntent.putExtra("newname", newname);
            setResult(2,returnIntent);
        }
    }

    @ColorInt
    public static int getContrastColor(@ColorInt int color) {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return a < 0.5 ? Color.BLACK : Color.WHITE;
    }

    //save image
    public static void imageDownload(Context ctx, String url){
        Picasso.with(ctx)
                .load(url)
                .into(getTarget(url));
    }

    //target to save
    private static Target getTarget(final String url){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Log.e("Target", target.toString());
        return target;
    }
}
