package com.sgsj.sawaal;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar ;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sgsj.sawaal.Recycler_View_Adapter.PICK_PDF_CODE;
import static com.sgsj.sawaal.Recycler_View_Adapter.recycler;


public class HomeActivity extends AppCompatActivity {

    public static Uri newpdfuri;
    public static String newpdfname;
    //    private TextView mTextMessage;
    public static boolean isathome = true;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    public static Uri uploadurl=null;
    public static String uploadname="";
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private CircleImageView profileimg;
    private TextView profilename;
    private long lastClickTime = 0;
    MenuItem resend;
    public static Drawable d = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "1086466113456");
        installation.put("UserEmail", auth.getCurrentUser().getEmail());
        installation.saveInBackground();
        setContentView(R.layout.activity_home);
        Log.e("Name ", ""+auth.getCurrentUser().getDisplayName());
        Log.e("Name ", ""+auth.getCurrentUser().getEmail());
        toolbar = findViewById(R.id.toolbar);
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        nv = (NavigationView)findViewById(R.id.nv);
        t = new ActionBarDrawerToggle(this, dl, toolbar, R.string.open, R.string.close);
        setSupportActionBar(toolbar);
        dl.addDrawerListener(t);
        t.syncState();

        resend = nv.getMenu().findItem(R.id.resendverification);
        if(auth.getCurrentUser().isEmailVerified()) {
                resend.setVisible(false);
        }

        View header = nv.getHeaderView(0);
        profilename = header.findViewById(R.id.profilenamenv);
        final TextView profileemail = header.findViewById(R.id.profileemailnv);
        profilename.setText(auth.getCurrentUser().getDisplayName().toUpperCase());
        profileemail.setText(auth.getCurrentUser().getEmail());

        profileimg = header.findViewById(R.id.profile_imagenv);
        Log.e("Hmm", auth.getCurrentUser().getPhotoUrl().toString());
//        Picasso.with(getApplicationContext()).load(auth.getCurrentUser().getPhotoUrl()).into(profileimg);

        Picasso.with(getApplicationContext()).load(auth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.ic_launcher_background).into(profileimg, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                d = profileimg.getDrawable();
            }

            @Override
            public void onError() {

            }
        });

        final Activity ac = this;

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                Pair<View, String> p1 = Pair.create((View)profileimg, "profileimg");
//                Pair<View, String> p3 = Pair.create((View)cv, "card");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, p1);
                ActivityCompat.startActivityForResult(ac, intent, 2, options.toBundle());
            }
        });


        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        String fileName="";
        if(Intent.ACTION_SEND.equals(receivedAction)){
            Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            uploadurl = receivedUri;


            String result = null;
            if (receivedUri.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(receivedUri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
            if (result == null) {
                result = receivedUri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            uploadname = result;
            Log.e("TAG", "onCreate: "+result);
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadFragment()).commit();
                nv.setCheckedItem(R.id.upload);
            }
        }
        else {
            if(savedInstanceState == null) {
                Log.e("TAG", "onCreate: usual");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new FindFragment()).commit();
                nv.setCheckedItem(R.id.find);
            }
        }

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                dl.closeDrawer(GravityCompat.START);
                switch(id)
                {
                    case R.id.find:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new FindFragment()).commit();
                        Toast.makeText(HomeActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.upload:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadFragment()).commit();
                        Toast.makeText(HomeActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.leader:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new LeaderFragment()).commit();
                        Toast.makeText(HomeActivity.this, "My Cart",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.resendverification:
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("Success", "Yes");
                                } else {
                                    Log.i("Success", "No");
                                }
                            }
                        });
                        Toast.makeText(HomeActivity.this, "Verification email has been sent to your email. Verification link will expire in 15 minutes and account will be deleted if not verified.", Toast.LENGTH_LONG).show();
                        return true;

                case R.id.about:
                    Intent intent = new Intent(HomeActivity.this, About.class);
                    startActivity(intent);
                    return true;
                    default:
                        return true;
                }

            }
        });


        //        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        getSupportActionBar().hide();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Find Paper");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Find Paper");
//        loadFragment(new FindFragment());
        //        mTextMessage = (TextView) findViewById(R.id.message);
        //        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment fragment;
//            Log.e("DEBUG", "4");
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    Log.e("DEBUG", "1");
//                    fragment = new FindFragment();
//                    loadFragment(fragment);
//                    return true;
//                case R.id.navigation_dashboard:
//                    Log.e("DEBUG", "2");
//                    fragment = new UploadFragment();
//                    loadFragment(fragment);
//                    return true;
//                case R.id.navigation_notifications:
//                    Log.e("DEBUG", "3");
//                    fragment = new LeaderFragment();
//                    loadFragment(fragment);
//                    return true;
//            }
//            return false;
//        }
//    };

//
//    private void loadFragment(Fragment fragment) {
//        // load fragment
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

//    public void onBackPressed() {
//        BottomNavigationView mBottomNavigationView = findViewById(R.id.navigation);
//        if (isathome) {
////            super.onBackPressed();
//            finish();
//
//        } else {
//            if (mBottomNavigationView.getSelectedItemId() != R.id.navigation_home)
//                mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
//            else {
//                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//                    getSupportFragmentManager().popBackStack();
//                    isathome=true;
//                    Log.e("TER", "RW");
//                } else {
////                    super.onBackPressed();
//                    finish();
//                }
//            }
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (t.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file

        if(resultCode==2) {
            String newname = data.getStringExtra("newname");
            profilename.setText(newname.toUpperCase());
        }
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                Toast.makeText(this, displayName, Toast.LENGTH_SHORT).show();
//                inputdisplaytext.setText(displayName);
//                file_uri = data.getData();
                Log.e("file",data.getData().toString());
                newpdfname=displayName;
                newpdfuri=data.getData();
                recycler.uploadFile(newpdfuri, newpdfname);

            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth.getCurrentUser().isEmailVerified()) {
            resend.setVisible(false);
        } else {
            auth.getCurrentUser().reload()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                Log.e("MACHAYA", "MACHAYA");
                                resend.setVisible(false);

                            } else {
                                Log.e("HAGGA", "HAGGA");
                            }
                        }
                    });
        }
    }
}



