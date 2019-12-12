package com.sgsj.sawaal;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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


