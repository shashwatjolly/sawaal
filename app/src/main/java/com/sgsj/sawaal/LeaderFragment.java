package com.sgsj.sawaal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sgsj.sawaal.FindFragment.paperdetails;
import static com.sgsj.sawaal.HomeActivity.isathome;
import static java.lang.Math.min;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LeaderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Data1> newdata;
    private List<Data1> data;
    Recycler_View_Adapter1 adapter;
    Integer lastKnownScore = Integer.MAX_VALUE;
    int tot=0;
    boolean ekbaaraaya = false;

    boolean isLoading = false;

    public LeaderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderFragment newInstance(String param1, String param2) {
        LeaderFragment fragment = new LeaderFragment();
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
    }

    private Toolbar mTopToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        mTopToolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
//        mTopToolbar.setTitle("Leaderboard");
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Leaderboard");
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_leader, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);//every item of the RecyclerView has a fix size
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newdata = new ArrayList<>();


        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecyclerViewData();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);

                // Fetching data from server
//                loadRecyclerViewData();
            }
        });

        return rootView;


    }

    private void loadRecyclerViewData()
    {
        // Showing refresh animation before making http call
        newdata.clear();
        isLoading=false;
        lastKnownScore = Integer.MAX_VALUE;
        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
        Query query = testRef.orderByChild("Score").limitToLast(tot);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
//                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        if(issue.getKey().toString().equals("Admins"))
                            continue;
                        Log.e("lp",issue.getKey());
                        Log.e("leader",issue.child("Username").getValue().toString());
                        Uri u = Uri.parse(issue.child("ProfilePic").getValue().toString());
                        ImageView iv = new ImageView(getContext());
                        iv.setImageResource(R.drawable.ic_launcher_background);
                        lastKnownScore = min(lastKnownScore, Integer.valueOf(issue.child("Score").getValue().toString()));
                        newdata.add(new Data1(issue.child("Username").getValue().toString(),issue.child("Score").getValue().toString(), iv, u, issue.getKey()));

                    }
                    Collections.reverse(newdata);
                    if(getActivity()!=null) {
                        Log.e("TAG", "onDataChange: hmm");
                        int size = data.size();
                        data.clear();
                        adapter.notifyItemRangeRemoved(0, size);
                        mSwipeRefreshLayout.setRefreshing(false);
                        data.addAll(newdata);
                        adapter.notifyDataSetChanged();
                    }

                }
                else
                {
//                                uploadFile(file_uri);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        data = new ArrayList<>();
        final RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview1);
        final ProgressBar pb = getView().findViewById(R.id.progressbarleader);
        pb.setVisibility(View.VISIBLE);
//        Recycler_View_Adapter1 adapter = new Recycler_View_Adapter1(data, getActivity().getApplication());
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
        tot=10;
        Query query = testRef.orderByChild("Score").limitToLast(10);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
//                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        if(issue.getKey().toString().equals("Admins"))
                            continue;
                        Log.e("lp",issue.getKey());
                        Log.e("leader",issue.child("Username").getValue().toString());
                        Uri u = Uri.parse(issue.child("ProfilePic").getValue().toString());
                        ImageView iv = new ImageView(getActivity());
                        iv.setImageResource(R.drawable.ic_launcher_background);
                        lastKnownScore = min(lastKnownScore, Integer.valueOf(issue.child("Score").getValue().toString()));
                        data.add(new Data1(issue.child("Username").getValue().toString(),issue.child("Score").getValue().toString(), iv,  u, issue.getKey()));

                    }
                    Collections.reverse(data);
                    if(getActivity()!=null) {
                        adapter = new Recycler_View_Adapter1(data, getActivity().getApplication());
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        pb.setVisibility(View.GONE);
                        initScrollListener();
                    }

                }
                else
                {
//                                uploadFile(file_uri);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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


    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                Log.e("Scrolling", "wooo");
                Log.e("Scrolling", ((Integer) linearLayoutManager.findLastCompletelyVisibleItemPosition()).toString());
                Log.e("Scrolling", ((Integer) data.size()).toString());
                if(isLoading) Log.e("Scrolling", "true");
                else Log.e("Scrolling", "no");

                if (!isLoading) {
                    Log.e("Aaya", "hai");
                    if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == data.size() - 1) {
                        //bottom of list!
                        loadMore();
                        Log.e("loading", "noo");
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        Log.e("load", "more");
        data.add(null);
        adapter.notifyDataSetChanged();
        Handler handler = new Handler();
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final List<Data1> temp = new ArrayList<>();
                final DatabaseReference testRef = FirebaseDatabase.getInstance().getReference().child("Users"); //Path in database where to check
                tot+=10;
                Query query = testRef.orderByChild("Score").endAt(lastKnownScore-1).limitToLast(10);
                Log.e("Last known", lastKnownScore.toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.e("exists", "yes");
                            // dataSnapshot is the "issue" node with all children with id 0
        //                                Toast.makeText(getContext(),"Paper already present. You can check if it is valid and report it otherwise.", Toast.LENGTH_LONG).show();
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                // do something with the individual "issues"
                                if(issue.getKey().toString().equals("Admins"))
                                    continue;
                                Log.e("lp",issue.getKey());
                                Log.e("leader",issue.child("Username").getValue().toString());
                                Uri u = Uri.parse(issue.child("ProfilePic").getValue().toString());
                                ImageView iv = new ImageView(getContext());
                                iv.setImageResource(R.drawable.ic_launcher_background);
                                lastKnownScore = Integer.valueOf(issue.child("Score").getValue().toString());

                                temp.add(new Data1(issue.child("Username").getValue().toString(),issue.child("Score").getValue().toString(), iv,  u, issue.getKey()));
        //                        data.add(new Data1(issue.child("Username").getValue().toString(),issue.child("Score").getValue().toString(), iv,  u, issue.getKey()));

                            }
                            data.remove(data.size() - 1);
                            int scrollPosition = data.size();
                            adapter.notifyItemRemoved(scrollPosition);
                            Collections.reverse(temp);
                            data.addAll(temp);
                            adapter.notifyDataSetChanged();
                            isLoading = false;
                            ekbaaraaya = true;
                        }
                        else
                        {
                            Log.e("exists", "no");
                            if(ekbaaraaya) Toast.makeText(getContext(), "Reached end of list", Toast.LENGTH_SHORT).show();
//                            isLoading = false;
                            if(data.get(data.size()-1) == null) {
                                data.remove(data.size() - 1);
                                int scrollPosition = data.size();
                                adapter.notifyItemRemoved(scrollPosition);
                            }

        //                                uploadFile(file_uri);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


    }
}
