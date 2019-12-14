package com.sgsj.sawaal;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class View_Holder extends RecyclerView.ViewHolder {
    CardView cv;
    TextView papername, papercoursecode, paperkayear, papertype, paperkaprof, papervotes;
    Button downloadbtn, hackbtn;
    ToggleButton upvotebtn,downvotebtn;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView)  itemView.findViewById(R.id.cardView);
        papercoursecode = itemView.findViewById(R.id.papercoursecode);
        paperkayear = itemView.findViewById(R.id.paperyear);
        papertype = itemView.findViewById(R.id.papertype);
        paperkaprof = itemView.findViewById(R.id.paperprof);
        papervotes = itemView.findViewById(R.id.papervotes);
        downloadbtn = itemView.findViewById(R.id.paperdownloadbtn);

        upvotebtn = itemView.findViewById(R.id.upvote);
        downvotebtn = itemView.findViewById(R.id.downvote);
    }
}

