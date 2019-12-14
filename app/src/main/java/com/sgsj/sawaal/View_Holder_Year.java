package com.sgsj.sawaal;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class View_Holder_Year extends RecyclerView.ViewHolder {
    TextView year;

    View_Holder_Year(View itemView) {
        super(itemView);
        year = itemView.findViewById(R.id.year);
    }
} 