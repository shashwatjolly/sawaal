package com.sgsj.sawaal;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class View_Holder1 extends RecyclerView.ViewHolder {
    CardView cv;
    TextView profilename, profilescore;
    ImageView profileimg;

    View_Holder1(final View itemView) {
        super(itemView);
        cv = (CardView)  itemView.findViewById(R.id.cardView1);
        profilename = itemView.findViewById(R.id.profilename);
        profilescore = itemView.findViewById(R.id.profilescore);
        profileimg = itemView.findViewById(R.id.profileimg);

    }
}


