package com.sgsj.sawaal;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

public class Data {
        public String course_code;
        public String uploaderID;
        public String year;
        public String typeofpaper;
        public String filename;
        public String prof;
        public String key;
        public Integer votes;
        public Uri fileurl;

        public Boolean upvoted,downvoted;

        Data(String course_code,String uploaderID, String year, String typeofpaper , String filename , String prof, Uri fileurl,
             String key, Integer votes,Boolean upvoted, Boolean downvoted) {
            this.course_code = course_code;
            this.year = year;
            this.uploaderID=uploaderID;
            this.typeofpaper = typeofpaper;
            this.filename = filename;
            this.prof=prof;
            this.fileurl=fileurl;
            this.key=key;
            this.votes = votes;
            this.upvoted = upvoted;
            this.downvoted = downvoted;
        }
}
