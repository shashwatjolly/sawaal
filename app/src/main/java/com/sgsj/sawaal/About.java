package com.sgsj.sawaal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

public class About extends MaterialAboutActivity {

    @Override
    @NonNull
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("Sawaal")
//                .desc("Maggo and let Maggo")
                .icon(R.mipmap.ic_launcher_round)
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Version 1.0")
                .icon(R.drawable.ic_info_outline_black_24dp)
                .build());

//        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(c,
//                new IconicsDrawable(context)
//                        .icon(R.drawable.ic_info_outline_black_24dp)
//                        .color(ContextCompat.getColor(context, R.color.colorAccent))
//                        .sizeDp(18),
//                "Version",
//                false));


        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title("Made with ❤ by");
//        authorCardBuilder.titleColor(ContextCompat.getColor(c, R.color.colorAccent));

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Shashwat Jolly")
                .subText("facebook.com/shashwatjolly")
                .icon(R.drawable.ic_account_circle_black_24dp)
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Shaurya Gomber")
                .subText("facebook.com/shauryagomber")
                .icon(R.drawable.ic_account_circle_black_24dp)
                .build());


//        MaterialAboutCard.Builder otherCardBuilder = new MaterialAboutCard.Builder();
//        otherCardBuilder.title("Other");
//
//        otherCardBuilder.cardColor(Color.parseColor("#7986CB"));
//
//        otherCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//                .icon(new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_language_html5)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18))
//                .text("HTML Formatted Sub Text")
//                .subTextHtml("This is <b>HTML</b> formatted <i>text</i> <br /> This is very cool because it allows lines to get very long which can lead to all kinds of possibilities. <br /> And line breaks. <br /> Oh and by the way, this card has a custom defined background.")
//                .setIconGravity(MaterialAboutActionItem.GRAVITY_TOP)
//                .build()
//        );

        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build());
    }

    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

}
