package com.sgsj.sawaal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import static com.sgsj.sawaal.HomeActivity.isathome;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static android.app.Activity.RESULT_OK;

import static com.sgsj.sawaal.HomeActivity.uploadname;
import static com.sgsj.sawaal.HomeActivity.uploadurl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth auth;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        auth=FirebaseAuth.getInstance();
    }

    private Toolbar mTopToolbar;
    private CircularProgressButton uploadpdf;
    private Button browsepdf,btntype;
    private EditText inputcode, inputprof, inputyear;
    private TextView inputdisplaytext;
    private String code, college, prof, year, date, type="Quiz 1", scorestr ,fullname;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private Uri file_uri;
    private Integer score;
    private boolean isthere;
    final static int PICK_PDF_CODE = 2342;
//    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        mTopToolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
//        mTopToolbar.setTitle("Upload Paper");
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Upload Paper");
        return inflater.inflate(R.layout.fragment_upload, container, false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upload_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(getContext(), "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isathome=false;
        btntype = getView().findViewById(R.id.paperbtn);
        uploadpdf = getView().findViewById(R.id.btn_upload);
        browsepdf = (Button) getView().findViewById(R.id.btn_browse);
        inputcode = (EditText) getView().findViewById(R.id.papercourse);
        inputprof = (EditText) getView().findViewById(R.id.paperprof);
        inputyear = (EditText) getView().findViewById(R.id.paperyear);
        inputdisplaytext = (TextView)  getView().findViewById(R.id.displaytext);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        progressBar = (ProgressBar) getView().findViewById(R.id.paperprogressBar);
        progressBar.setVisibility(View.GONE);

        if(uploadurl!=null)
            file_uri = uploadurl;

        if(uploadname!="")
            inputdisplaytext.setText(uploadname);

        final TextInputLayout coursecodein = getView().findViewById(R.id.coursecodein1);
        coursecodein.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    coursecodein.setError("Course code cannot be empty");
                }

                if (s.length() > 0) {
                    coursecodein.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final TextInputLayout yearin = getView().findViewById(R.id.yearin);
        yearin.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    yearin.setError("Year cannot be empty");
                }

                if (s.length() > 0) {
                    yearin.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        uploadpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = inputcode.getText().toString().replaceAll("\\s","").toUpperCase();
                Toast.makeText(getContext(), code, Toast.LENGTH_SHORT).show();
                prof = inputprof.getText().toString();
                Toast.makeText(getContext(), code, Toast.LENGTH_SHORT).show();
                year = inputyear.getText().toString();
                Toast.makeText(getContext(), code, Toast.LENGTH_SHORT).show();
                Date datetemp = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                date = df.format(datetemp);

                if(code.length()==0) {
                    coursecodein.setError("Course code cannot be empty");
                }
                if(year.length()==0) {
                    yearin.setError("Course code cannot be empty");
                }

                if(code!="" && year!="" && type!="" && file_uri!=null) {uploadpdf.startAnimation(); checkPaperandUpload();}
                else if(code!="" && year!="" && type!="" && file_uri==null) {
                    Toast.makeText(getContext(), "Please select a file", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btntype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Paper Type");

// add a list
                final String[] branches = {"Quiz 1", "Midsem", "Quiz 2", "Endsem" , "Other"};
                builder.setItems(branches, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btntype.setText(branches[which]);
                        type = (String) btntype.getText();
                    }
                });

// create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        browsepdf.setOnClickListener(new View.OnClickListener() {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                }

                //creating an intent for file chooser
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);

            }

        });



    }

//    public void extractname()
//    {
//        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
//        Query query = testRef.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
//                        // do something with the individual "issues"
//                        Log.e("check",issue.getKey().toString());
//
//                        fullname = issue.child("Username").getValue().toString();
//                        checkPaperandUpload();
//
//
//                    }
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
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
                        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                Toast.makeText(getContext(), displayName, Toast.LENGTH_SHORT).show();
                inputdisplaytext.setText(displayName);
                file_uri = data.getData();
            }else{
                Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri filePath) {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
//            progressDialog.show();
//            uploadpdf.startAnimation();


            final String uniqueId = mDatabaseReference.push().getKey();
            final DatabaseReference dref1 = mDatabaseReference.child(code+"_"+type).child(year).child(uniqueId);

            final String file_name =  inputdisplaytext.getText().toString()+"_"+date+"_"+System.currentTimeMillis();
            final HashMap<String,String> mapper = new HashMap<>();
            mapper.put("CourseCode",code);
            mapper.put("Year",year);
            mapper.put("Type",type);
            mapper.put("DateOfUpload",date);
            mapper.put("FileName",file_name);
            mapper.put("Prof",prof);
            mapper.put("uploaderID", FirebaseAuth.getInstance().getCurrentUser().getUid());

//            mapper.put("Course_Year",code+"_"+year);
//            mapper.put("Course_Type",code+"_"+type);
//            mapper.put("Course_Year_Type",code+"_"+year+"_"+type);

            //Upvotes and Downvotes
            mapper.put("upvoteCount","0");
            mapper.put("downvoteCount","0");
            final List<String> mails = new ArrayList<String>();
            mails.add("Dummy Mail");



            //getting the storage reference
            StorageReference sRef = mStorageReference.child("Uploads/" + file_name);

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
//                            progressDialog.dismiss();
                            Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.tickicon);
                            uploadpdf.doneLoadingAnimation(Color.parseColor("#FFC107"), bmp);

                            //displaying success toast
                            Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            //adding an upload to firebase database
                            final String uploadId = mDatabaseReference.push().getKey();

                            StorageReference filepath = mStorageReference.child("Uploads/").child(file_name);
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mapper.put("FileUrl",uri.toString());
                                    dref1.setValue(mapper);

                                    dref1.child("upvoters").setValue(mails);
                                    dref1.child("downvoters").setValue(mails);

                                    //Total Votes
                                    dref1.child("totalVotes").setValue(0);


                                    increaseScoreUpload();
                                    uploadpdf.revertAnimation();
                                    uploadpdf.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.roundedbtnbg));
//               Toast.makeText(getContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
//                                    Log.e("link", downloadUrl.toString());
//                                    downloadFile(downloadUrl, filename);
                                }
                            });


//                            final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                            testRef.child("Score").setValue(scorestr);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
//                            progressDialog.dismiss();
                            uploadpdf.revertAnimation();
                            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            if((int)progress>0) uploadpdf.setProgress((int)progress);
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }


    public void increaseScoreUpload() {
        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()); //Path in database where to check
        testRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    scorestr = dataSnapshot.child("Score").getValue().toString();
                    score = Integer.parseInt(scorestr);
//                            Toast.makeText(getContext(), filename, Toast.LENGTH_SHORT).show();
                    score = score + 50;
                    testRef.child("Score").setValue(score);
                }
                else{                                                                                       //If username not in database
                    Log.e("Fail", "Score Increase Upload");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Database should do nothing on Cancelling the query
            }
        });
    }

    public void checkPaperandUpload() {
        if(type!="Other") {
            final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Uploads").child(code+"_"+type).child(year); //Path in database where to check
            Query query = testRef.orderByChild("totalVotes").limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Upload Paper?");
                        final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.uploadcheckdialog, (ViewGroup) getView().findViewById(android.R.id.content), false);

                        TextView coursecode = viewInflated.findViewById(R.id.papercoursecode);
                        TextView type = viewInflated.findViewById(R.id.papertype);
                        TextView year = viewInflated.findViewById(R.id.paperyear);
                        TextView prof = viewInflated.findViewById(R.id.paperprof);
                        TextView votes = viewInflated.findViewById(R.id.papervotes);

                        for(DataSnapshot issue: dataSnapshot.getChildren()) {
                            String cc = issue.child("CourseCode").getValue().toString();
                            String ye = issue.child("Year").getValue().toString();
                            String ty = issue.child("Type").getValue().toString();
                            String pr = issue.child("Prof").getValue().toString();
                            final Uri furl = Uri.parse(issue.child("FileUrl").getValue().toString());
                            Integer totalvotes = Integer.parseInt(issue.child("totalVotes").getValue().toString());
                            coursecode.setText(cc);
                            type.setText(ty);
                            year.setText(ye);
                            prof.setText("Prof: " + pr);
                            votes.setText("Total Votes: " + totalvotes.toString());
                            FrameLayout outer = viewInflated.findViewById(R.id.cardView);
                            CardView cv = outer.findViewById(R.id.cardView);
                            cv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(furl, "application/pdf");
                                    v.getContext().startActivity(intent);
                                }

                            });

                        }

                        builder.setView(viewInflated);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                uploadFile(file_uri);

                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                uploadpdf.revertAnimation();
                            }
                        });

                        AlertDialog uploadcheckdialog = builder.create();
                        uploadcheckdialog.show();
                        uploadcheckdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                uploadpdf.revertAnimation();
                            }
                        });
                        // GIVE HACK OPTION
                    } else {
                        uploadFile(file_uri);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else uploadFile(file_uri);
    }
        // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
