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

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static android.content.Context.DOWNLOAD_SERVICE;


public class Recycler_View_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static Recycler_View_Adapter recycler;
    JSONObject data = new JSONObject();
    AlertDialog hackerdialog;
    CircularProgressButton browse;
    final HashMap<String,String> mapper = new HashMap<>();
    final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Hacks");
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private static int TYPE_YEAR = 1;
    private static int TYPE_PAPER = 2;


    private FirebaseAuth auth = FirebaseAuth.getInstance();;
    Resources resources;
    TextView displaytextbrowser;
    boolean dismissed;
    boolean doneupload;
    boolean hasUpvoted, hasDownvoted;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v;
        if(viewType==TYPE_PAPER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
            View_Holder holder = new View_Holder(v);
            return holder;
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_year, parent, false);
            View_Holder_Year holder = new View_Holder_Year(v);
            return holder;
        }

    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holderGen, final int position) {
        if(getItemViewType(position)==TYPE_PAPER) {
            final View_Holder holder = (View_Holder) holderGen;
            holder.papervotes.setText("" + list.get(position).votes);
            holder.paperkaprof.setText("Prof: " + list.get(position).prof);
            holder.papertype.setText(list.get(position).typeofpaper);
            holder.paperkayear.setText(list.get(position).year);
            holder.papercoursecode.setText(list.get(position).course_code);
            origpdf = list.get(position).fileurl;
            recycler = this;


            final String currentUser = auth.getCurrentUser().getEmail();
            String toRef = "Uploads/" + list.get(position).course_code + "_" + list.get(position).typeofpaper
                    + "/" + list.get(position).year + "/" + list.get(position).key;
            final DatabaseReference uploads = FirebaseDatabase.getInstance().getReference(toRef);


            hasUpvoted = list.get(position).upvoted;
            hasDownvoted = list.get(position).downvoted;

            if (hasUpvoted) {
                holder.upvotebtn.setChecked(true);
                holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_orange_24dp));
            } else {
                holder.upvotebtn.setChecked(false);
                holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_black_24dp));
            }

            if (hasDownvoted) {
                holder.downvotebtn.setChecked(true);
                holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_blue_24dp));
            } else {
                holder.downvotebtn.setChecked(false);
                holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_black_24dp));
            }

            holder.upvotebtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        performTransaction(uploads, list.get(position).uploaderID, currentUser, 1, 0, 1, 0, 0, 0);
                        list.get(position).votes++;

                        if (holder.downvotebtn.isChecked()) {
                            holder.downvotebtn.setChecked(false);
                            holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_black_24dp));
                        } else {
                            holder.papervotes.setText("" + (list.get(position).votes));

                        }

                        holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_orange_24dp));
                    } else {

                        performTransaction(uploads, list.get(position).uploaderID, currentUser, -1, 0, 0, 1, 0, 0);
                        list.get(position).votes--;
                        holder.papervotes.setText("" + (list.get(position).votes));

                        holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_black_24dp));
                    }
                }
            });

            holder.downvotebtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        performTransaction(uploads, list.get(position).uploaderID, currentUser, 0, 1, 0, 0, 1, 0);
                        list.get(position).votes--;


                        if (holder.upvotebtn.isChecked()) {
                            holder.upvotebtn.setChecked(false);
                            holder.upvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_black_24dp));
                        } else {
                            holder.papervotes.setText("" + (list.get(position).votes));
                        }

                        holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_blue_24dp));
                    } else {
                        performTransaction(uploads, list.get(position).uploaderID, currentUser, 0, -1, 0, 0, 0, 1);
                        list.get(position).votes++;
                        holder.papervotes.setText("" + (list.get(position).votes));
                        holder.downvotebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_black_24dp));
                    }
                }
            });


            holder.downloadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(list.get(position).fileurl, list.get(position).filename, v);
                }

            });

            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenOrigFile(v, list.get(position).fileurl);
                }

            });
        }
        else {
            final View_Holder_Year holder = (View_Holder_Year) holderGen;
            holder.year.setText(list.get(position).year);
        }
    }


    public void OpenOrigFile(View v,Uri paperURL)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(paperURL, "application/pdf");
        v.getContext().startActivity(intent);
    }

    public void performTransaction(DatabaseReference ref, final String uploaderID, final String mail, final Integer upvupd, final Integer downvupd,
                                   final Integer addupv, final Integer delupv, final Integer addown, final Integer deldown) {
        final DatabaseReference ref5 = ref;
        final DatabaseReference ref1 = ref5.child("upvoteCount");
        final DatabaseReference ref2 = ref5.child("downvoteCount");
        final DatabaseReference ref3 = ref5.child("upvoters");
        final DatabaseReference ref4 = ref5.child("downvoters");

        final DatabaseReference ref6 = ref5.child("totalVotes");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref1.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue() != null) {
                            String votes = mutableData.getValue().toString();
                            Integer vot = Integer.parseInt(votes);
                            vot = vot + upvupd;
                            mutableData.setValue(vot);
                        }
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ref2.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue() != null) {
                            String votes = mutableData.getValue().toString();
                            Integer vot = Integer.parseInt(votes);
                            vot = vot + downvupd;
                            mutableData.setValue(vot);
                        }
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ref3.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue()!=null) {
                            List<String> upvoters = (List<String>) mutableData.getValue();

                            if (addupv == 1) {
                                upvoters.add(mail);
                            } else if (delupv == 1) {
                                upvoters.remove(mail);
                            }

                            mutableData.setValue(upvoters);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        ref4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ref4.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue()!=null) {
                            List<String> downvoters = (List<String>) mutableData.getValue();
                            if (addown == 1) {
                                downvoters.add(mail);
                            } else if (deldown == 1) {
                                downvoters.remove(mail);
                            }
                            mutableData.setValue(downvoters);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        // Updating score of person whose paper is upvoted or downvoted
        final Integer scoreUpdate = 5 * (upvupd-downvupd);
        String toRef = "Users/" + uploaderID;
        final DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference(toRef).child("Score");
        scoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scoreRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue() != null) {
                            String scrstr = mutableData.getValue().toString();
                            Integer score = Integer.parseInt(scrstr);
                            score += scoreUpdate;
                            mutableData.setValue(score);
                        }
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        // Updating total votes of paper
        final Integer votesUpdate = (upvupd-downvupd);
        ref6.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref6.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.getValue() != null) {
                            String scrstr = mutableData.getValue().toString();
                            Integer score = Integer.parseInt(scrstr);
                            score += votesUpdate;
                            mutableData.setValue(score);
                        }
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
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
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(list.get(position).course_code)) {
            return TYPE_YEAR;

        } else {
            return TYPE_PAPER;
        }
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
