package com.sgsj.sawaal;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentTransaction;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


import static android.content.Context.DOWNLOAD_SERVICE;
import static com.sgsj.sawaal.HomeActivity.isathome;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static List<Data> paperdetails;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth auth;

    private OnFragmentInteractionListener mListener;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
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

    private Toolbar toolbar;
    private EditText inputcode, inputyear;
    private String code="", year="", type="", filename="";
    private CircularProgressButton findpdf;
    private Button btntype;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private ProgressBar progressBar;
    private Uri downloadUrl;
    private TextInputLayout coursecodein;
    public static ArrayList<HashMap<String, String> > datalist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        mTopToolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
//        mTopToolbar.setTitle("Find Paper");
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Find Paper");
        return inflater.inflate(R.layout.fragment_find, container, false);
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
        isathome=true;
//        toolbar = getView().findViewById(R.id.toolbar);
//        toolbar.setTitle("Find Paper");
        findpdf = getView().findViewById(R.id.btn_find);
        inputcode = (EditText) getView().findViewById(R.id.findcourse);
        inputyear = (EditText) getView().findViewById(R.id.findyear);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        progressBar = (ProgressBar) getView().findViewById(R.id.findprogressBar);
        coursecodein = getView().findViewById(R.id.coursecodein);
        progressBar.setVisibility(View.GONE);
        paperdetails = new ArrayList<>();

        btntype = getView().findViewById(R.id.typeofpaper);



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




        findpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = inputcode.getText().toString().replaceAll("\\s","").toUpperCase();
                year = inputyear.getText().toString();
                datalist = new ArrayList<HashMap<String, String> >();
                if(code.equals("")) {
                    coursecodein.setError("Course code cannot be empty");
                    Toast.makeText(getContext(), "Please enter all required information", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getContext(), "Course code cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(year.equals("") && !type.equals("")) {
                    findpdf.startAnimation();
                    final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Uploads"); //Path in database where to check
                    Query query = testRef.orderByChild("Course_Type").equalTo(code+"_"+type);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0
//                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                                paperdetails.clear();
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    // do something with the individual "issues"
                                    Log.e("check",issue.getKey().toString());

                                    String cc = issue.child("CourseCode").getValue().toString();
                                    String ue = issue.child("uploaderID").getValue().toString();
                                    String ye = issue.child("Year").getValue().toString();
                                    String ty = issue.child("Type").getValue().toString();
                                    String fn = issue.child("FileName").getValue().toString().substring(0,20);
                                    String pr = issue.child("Prof").getValue().toString();
                                    Uri furl = Uri.parse(issue.child("FileUrl").getValue().toString());

                                    Integer totalvotes = Integer.parseInt(issue.child("totalVotes").getValue().toString());

                                    boolean up,down;
                                    String currentUser = auth.getCurrentUser().getEmail();
                                    if(!issue.child("upvoters").exists()){
                                        up=false;
                                    }
                                    else{
                                        List<String> upvoters = (List<String>)issue.child("upvoters").getValue();
                                        if(upvoters.contains(currentUser)){
                                            up = true;
                                        }
                                        else{
                                            up = false;
                                        }
                                    }

                                    if(!issue.child("downvoters").exists()){
                                        down=false;
                                    }
                                    else{
                                        List<String> downvoters = (List<String>)issue.child("downvoters").getValue();
                                        if(downvoters.contains(currentUser)){
                                            down = true;
                                        }
                                        else{
                                            down = false;
                                        }
                                    }
                                    String key=issue.getKey().toString();
                                    paperdetails.add(new Data(cc,ue,ye,ty,fn,pr,furl,key,totalvotes,up,down));


                                }
                                openList();
                                findpdf.revertAnimation();

                            }
                            else
                            {
                                findpdf.revertAnimation();
                                Toast.makeText(getContext(),"No paper found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else if(!year.equals("") && type.equals("")) {
                    findpdf.startAnimation();
                    final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Uploads"); //Path in database where to check
                    Query query = testRef.orderByChild("Course_Year").equalTo(code+"_"+year);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0
//                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    // do something with the individual "issues"
                                    Log.e("check",issue.getKey().toString());

                                    String cc = issue.child("CourseCode").getValue().toString();
                                    String ye = issue.child("Year").getValue().toString();
                                    String ty = issue.child("Type").getValue().toString();
                                    String fn = issue.child("FileName").getValue().toString().substring(0,20);
                                    String pr = issue.child("Prof").getValue().toString();
                                    Uri furl = Uri.parse(issue.child("FileUrl").getValue().toString());
                                    String ue = issue.child("uploaderID").getValue().toString();


                                    String key=issue.getKey().toString();
                                    Integer totalvotes = Integer.parseInt(issue.child("totalVotes").getValue().toString());

                                    boolean up,down;
                                    String currentUser = auth.getCurrentUser().getEmail();
                                    if(!issue.child("upvoters").exists()){
                                        up=false;
                                    }
                                    else{
                                        List<String> upvoters = (List<String>)issue.child("upvoters").getValue();
                                        if(upvoters.contains(currentUser)){
                                            up = true;
                                        }
                                        else{
                                            up = false;
                                        }
                                    }

                                    if(!issue.child("downvoters").exists()){
                                        down=false;
                                    }
                                    else{
                                        List<String> downvoters = (List<String>)issue.child("downvoters").getValue();
                                        if(downvoters.contains(currentUser)){
                                            down = true;
                                        }
                                        else{
                                            down = false;
                                        }
                                    }
                                    paperdetails.add(new Data(cc,ue,ye,ty,fn,pr,furl,key,totalvotes,up,down));


                                }
                                openList();

                            }
                            else
                            {
                                findpdf.revertAnimation();
                                Toast.makeText(getContext(),"No paper found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else if(!year.equals("") && !type.equals("")) {
                    findpdf.startAnimation();
                    final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Uploads"); //Path in database where to check
                    Query query = testRef.orderByChild("Course_Year_Type").equalTo(code+"_"+year+"_"+type);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0
//                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    // do something with the individual "issues"
                                    Log.e("check",issue.getKey().toString());

                                    String cc = issue.child("CourseCode").getValue().toString();

                                    String ye = issue.child("Year").getValue().toString();
                                    String ty = issue.child("Type").getValue().toString();
                                    String fn = issue.child("FileName").getValue().toString().substring(0,20);
                                    String pr = issue.child("Prof").getValue().toString();
                                    String ue = issue.child("uploaderID").getValue().toString();

                                    Uri furl = Uri.parse(issue.child("FileUrl").getValue().toString());

                                    Integer totalvotes = Integer.parseInt(issue.child("totalVotes").getValue().toString());

                                    String key=issue.getKey().toString();

                                    boolean up,down;
                                    String currentUser = auth.getCurrentUser().getEmail();
                                    if(!issue.child("upvoters").exists()){
                                        up=false;
                                    }
                                    else{
                                        List<String> upvoters = (List<String>)issue.child("upvoters").getValue();
                                        if(upvoters.contains(currentUser)){
                                            up = true;
                                        }
                                        else{
                                            up = false;
                                        }
                                    }

                                    if(!issue.child("downvoters").exists()){
                                        down=false;
                                    }
                                    else{
                                        List<String> downvoters = (List<String>)issue.child("downvoters").getValue();
                                        if(downvoters.contains(currentUser)){
                                            down = true;
                                        }
                                        else{
                                            down = false;
                                        }
                                    }
                                    paperdetails.add(new Data(cc,ue,ye,ty,fn,pr,furl,key,totalvotes,up,down));


                                }
                                openList();

                            }
                            else
                            {
                                findpdf.revertAnimation();
                                Toast.makeText(getContext(),"No paper found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                else {
                    findpdf.startAnimation();
                    final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Uploads"); //Path in database where to check
                    Query query = testRef.orderByChild("CourseCode").equalTo(code);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // dataSnapshot is the "issue" node with all children with id 0
//                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    // do something with the individual "issues"
                                    Log.e("check",issue.getKey().toString());

                                    String cc = issue.child("CourseCode").getValue().toString();

                                    String ye = issue.child("Year").getValue().toString();
                                    String ty = issue.child("Type").getValue().toString();
                                    String fn = issue.child("FileName").getValue().toString().substring(0,20);
                                    String pr = issue.child("Prof").getValue().toString();
                                    Uri furl = Uri.parse(issue.child("FileUrl").getValue().toString());
                                    String ue = issue.child("uploaderID").getValue().toString();

                                    Integer totalvotes = Integer.parseInt(issue.child("totalVotes").getValue().toString());

                                    boolean up,down;
                                    String currentUser = auth.getCurrentUser().getEmail();
                                    if(!issue.child("upvoters").exists()){
                                        up=false;
                                    }
                                    else{
                                        List<String> upvoters = (List<String>)issue.child("upvoters").getValue();
                                        if(upvoters.contains(currentUser)){
                                            up = true;
                                        }
                                        else{
                                            up = false;
                                        }
                                    }

                                    if(!issue.child("downvoters").exists()){
                                        down=false;
                                    }
                                    else{
                                        List<String> downvoters = (List<String>)issue.child("downvoters").getValue();
                                        if(downvoters.contains(currentUser)){
                                            down = true;
                                        }
                                        else{
                                            down = false;
                                        }
                                    }
                                    String key=issue.getKey().toString();
                                    paperdetails.add(new Data(cc,ue,ye,ty,fn,pr,furl,key,totalvotes,up,down));


                                }
                                openList();

                            }
                            else
                            {
                                findpdf.revertAnimation();
                                Toast.makeText(getContext(),"No paper found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


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


    }

    public void openList(){
        Fragment newFragment = new ListFragment();
        newFragment.setEnterTransition(new Slide(Gravity.RIGHT));
        newFragment.setExitTransition(new Slide(Gravity.LEFT));
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack

        transaction.replace(R.id.frame_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    public void getfile() {
        StorageReference filepath = mStorageReference.child("Uploads/").child(filename);
        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
               downloadUrl = uri;
//               Toast.makeText(getContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
               Log.e("link", downloadUrl.toString());
               downloadFile(downloadUrl, filename);
           }
       });
    }

    public void downloadFile(Uri u, String filename) {
        DownloadManager.Request request = new DownloadManager.Request(u);
        String down_name = filename;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, down_name);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);

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
