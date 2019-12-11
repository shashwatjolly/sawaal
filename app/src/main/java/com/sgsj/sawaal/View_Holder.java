package com.sgsj.sawaal;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class View_Holder extends RecyclerView.ViewHolder {
    CardView cv;
    TextView papername, papercoursecode, paperkayear, papertype, paperkaprof, paperuploadedby;
    Button downloadbtn, hackbtn;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView)  itemView.findViewById(R.id.cardView);
//        papername = itemView.findViewById(R.id.papername);
        papercoursecode = itemView.findViewById(R.id.papercoursecode);
        paperkayear = itemView.findViewById(R.id.paperyear);
        papertype = itemView.findViewById(R.id.papertype);
        paperkaprof = itemView.findViewById(R.id.paperprof);
        paperuploadedby = itemView.findViewById(R.id.paperuploadedby);
        downloadbtn = itemView.findViewById(R.id.paperdownloadbtn);
        hackbtn = itemView.findViewById(R.id.paperhackbtn);

    }


}
