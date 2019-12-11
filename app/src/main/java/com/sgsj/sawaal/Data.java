package com.sgsj.sawaal;

import android.net.Uri;

public class Data {
        public String course_code;
        public String username;
        public String usermail;
        public String year;
        public String typeofpaper;
        public String filename;
        public String prof;
        public String key;
        public Uri fileurl;

        Data(String course_code, String username,String usermail, String year, String typeofpaper , String filename , String prof, Uri fileurl,String key) {
            this.course_code = course_code;
            this.username = username;
            this.year = year;
            this.usermail=usermail;
            this.typeofpaper = typeofpaper;
            this.filename = filename;
            this.prof=prof;
            this.fileurl=fileurl;
            this.key=key;
        }
}
