package com.sgsj.sawaal;

import android.net.Uri;
import android.widget.ImageView;

public class Data1 {
    public String profilename;
    public String profilescore;
    public ImageView profileimg;
    public Uri profileimgurl;
    public String uid;

    Data1(String profilename, String profilescore, ImageView profileimg, Uri profileimgurl, String uid) {
        this.profilename = profilename;
        this.profilescore = profilescore;
        this.profileimg = profileimg;
        this.profileimgurl = profileimgurl;
        this.uid = uid;
    }
}
