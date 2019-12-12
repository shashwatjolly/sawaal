package com.sgsj.sawaal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static android.content.Context.DOWNLOAD_SERVICE;


public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> {

    public static Recycler_View_Adapter recycler;
    JSONObject data = new JSONObject();
    AlertDialog hackerdialog;
    CircularProgressButton browse;
    final HashMap<String,String> mapper = new HashMap<>();
    final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Hacks");
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();


    private FirebaseAuth auth = FirebaseAuth.getInstance();;
    Resources resources;
    TextView displaytextbrowser;
    boolean dismissed;
    boolean doneupload;
    String newpdfurl;

    String storer;


    List<Data> list = Collections.emptyList();
    Context context;
    public Uri origpdf;
    public String faulter="";
//    public String key="";
    public Recycler_View_Adapter(List<Data> list, Context context) {            final HashMap<String,String> mapper = new HashMap<>();

        this.list = list;
        this.context = context;
    }
    final static int PICK_PDF_CODE = 2342;

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        holder.papervotes.setText("Total Votes: " + list.get(position).votes);
        holder.paperkaprof.setText("Prof: " + list.get(position).prof);
        holder.papertype.setText(list.get(position).typeofpaper);
        holder.paperkayear.setText(list.get(position).year);
        holder.papercoursecode.setText(list.get(position).course_code);
        origpdf=list.get(position).fileurl;
        recycler = this;

        faulter=list.get(position).usermail;

        String currentUser = auth.getCurrentUser().getEmail();
        String toRef = "Uploads/"+list.get(position).key;
        DatabaseReference uploads = FirebaseDatabase.getInstance().getReference(toRef);

        final boolean hasUpvoted=true,hasDownvoted=false;


        if(hasUpvoted){
            holder.upvotebtn.setChecked(true);
            holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.upvoteon));
        }
        else{
            holder.upvotebtn.setChecked(false);
            holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.roundedbtnbg));
        }

        if(hasDownvoted){
            holder.downvotebtn.setChecked(true);
            holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.downvoteon));
        }
        else{
            holder.downvotebtn.setChecked(false);
            holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.roundedbtnbg));
        }




        holder.upvotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOn = holder.upvotebtn.isChecked();

                if(isOn){
                    holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.upvoteon));
                }
                else{
                    holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.roundedbtnbg));
                }
            }

        });

        holder.downvotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOn = holder.downvotebtn.isChecked();

                if(isOn){
                    holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.downvoteon));
                }
                else{
                    holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.roundedbtnbg));
                }
            }

        });


        holder.downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(list.get(position).fileurl, list.get(position).filename, v);
            }

        });
    }


    public void uploadFile(final Uri filePath, String newpdfname) {
        //checking if file is available
        browse.startAnimation();
        displaytextbrowser.setText(newpdfname);
        ((AlertDialog) hackerdialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        if (filePath != null) {
            //displaying progress dialog while image is uploading

            Date datetemp = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String date = df.format(datetemp);
            final String file_name =  newpdfname+"_"+date+"_"+System.currentTimeMillis();

            //getting the storage reference
            StorageReference sRef = mStorageReference.child("Hacks/" + file_name);

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            browse.revertAnimation();
                            Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.tickicon);
                            browse.doneLoadingAnimation(Color.parseColor("#FFC107"), bmp);
                            ((AlertDialog) hackerdialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            final String uploadId = mDatabaseReference.push().getKey();
                            StorageReference filepath = mStorageReference.child("Hacks/").child(file_name);
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newpdfurl = uri.toString();
                                    storer=newpdfurl;
                                    doneupload = true;
                                    browse.revertAnimation();
                                    if(dismissed) {
                                        StorageReference newpdfref = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
                                        newpdfref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                Log.d("TAG", "onSuccess: deleted file");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                                Log.d("TAG", "onFailure: did not delete file");
                                            }
                                        });
                                        Log.e("TAG", "Hata diya");
                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            browse.revertAnimation();
//                            Toast.makeText(, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            if((int)progress>0) browse.setProgress((int)progress);
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }


    }

    public void downloadFile(Uri u, String filename, final View v) {
        DownloadManager.Request request = new DownloadManager.Request(u);
        String down_name = filename;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, down_name+".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        DownloadManager manager = (DownloadManager) v.getContext().getSystemService(DOWNLOAD_SERVICE);
        final long enq = manager.enqueue(request);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("TAG", "onReceive: Aaya hai");
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    Log.e("TAG", "onReceive: Aaya hai");
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enq);
                    DownloadManager temp = (DownloadManager) v.getContext().getSystemService(DOWNLOAD_SERVICE);
                    Cursor c = temp.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            //TODO : Use this local uri and launch intent to open file
//                            File file = new File(uriString);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW);
                            intent1.setDataAndType(Uri.parse(uriString), "application/pdf");
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            v.getContext().startActivity(intent1);

                        }
                    }
                }
            }
        };
        v.getContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Data data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Data data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }



}
