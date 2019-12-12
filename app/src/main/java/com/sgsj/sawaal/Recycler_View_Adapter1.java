package com.sgsj.sawaal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class Recycler_View_Adapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Data1> list = Collections.emptyList();
    Context context;
    private String done = "no";
    public static View_Holder1 v;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public Recycler_View_Adapter1(List<Data1> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        if(viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_profile, parent, false);
            View_Holder1 holder = new View_Holder1(v);
            return holder;
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            Log.e("Loading", "Displayed");
            return new LoadingViewHolder(view);
        }

    }

    private long lastClickTime = 0;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        if(holder instanceof View_Holder1) {
            populateItemRows((View_Holder1) holder, position);

        }
        else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }

        //animate(holder);

    }

    private void populateItemRows(final View_Holder1 holder, final int position) {

        holder.profileimg.setTag("no");
        holder.profilename.setText(list.get(position).profilename);
        holder.profilescore.setText(list.get(position).profilescore);
        Log.e(list.get(position).profileimgurl.toString(), "Hmm");
        Picasso.with(getApplicationContext()).load(list.get(position).profileimgurl).placeholder(R.drawable.ic_launcher_background).into(holder.profileimg, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.profileimg.setTag("yes");
            }

            @Override
            public void onError() {

            }
        });

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                Toast.makeText(view.getContext(), "clicked on " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), OtherProfile.class);
                Pair<View, String> p1 = Pair.create((View) holder.profileimg, "profileimg");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), p1);
                intent.putExtra("name", holder.profilename.getText());
                intent.putExtra("score", holder.profilescore.getText());
                intent.putExtra("url", list.get(position).profileimgurl.toString());
                intent.putExtra("done", holder.profileimg.getTag().toString());
                Log.e("done", holder.profileimg.getTag().toString());
                v = holder;
                view.getContext().startActivity(intent, options.toBundle());
            }
        });

    }

//    public void downloadFile(Uri u, String filename, final View v) {
//        DownloadManager.Request request = new DownloadManager.Request(u);
//        String down_name = filename;
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, down_name+".pdf");
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
//        DownloadManager manager = (DownloadManager) v.getContext().getSystemService(DOWNLOAD_SERVICE);
//        final long enq = manager.enqueue(request);
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
//
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Log.e("TAG", "onReceive: Aaya hai");
//                String action = intent.getAction();
//                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                    Log.e("TAG", "onReceive: Aaya hai");
//                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//                    DownloadManager.Query query = new DownloadManager.Query();
//                    query.setFilterById(enq);
//                    DownloadManager temp = (DownloadManager) v.getContext().getSystemService(DOWNLOAD_SERVICE);
//                    Cursor c = temp.query(query);
//                    if (c.moveToFirst()) {
//                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
//                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                            // Use this local uri and launch intent to open file
////                            File file = new File(uriString);
//                            Intent intent1 = new Intent(Intent.ACTION_VIEW);
//                            intent1.setDataAndType(Uri.parse(uriString), "application/pdf");
//                            intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            v.getContext().startActivity(intent1);
//
//                        }
//                    }
//                }
//            }
//        };
//        v.getContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//
//    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list==null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Data1 data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Data1 data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

}
