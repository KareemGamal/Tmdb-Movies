package com.example.kemooo.myapplication;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by KeMoOo on 9/23/2016.
 */

public class Main extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TabHost tab = getTabHost();
        tab.addTab(tab.newTabSpec("main").setIndicator("Top Rated").setContent(new Intent(this, Top_Rated.class)));
        tab.addTab(tab.newTabSpec("main").setIndicator("Popular").setContent(new Intent(this, popular.class)));
        tab.addTab(tab.newTabSpec("main").setIndicator("Coming Soon").setContent(new Intent(this, coming_soon.class)));
        tab.addTab(tab.newTabSpec("main").setIndicator("Now Playing").setContent(new Intent(this, Now_playing.class)));

        TextView x = (TextView) tab.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        x.setTextSize(10);
        x.setTextColor(Color.GREEN);


        TextView y = (TextView) tab.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        y.setTextSize(10);
        y.setTextColor(Color.GREEN);

        TextView z = (TextView) tab.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        z.setTextSize(10);
        z.setTextColor(Color.GREEN);


        TextView a = (TextView) tab.getTabWidget().getChildAt(3).findViewById(android.R.id.title);
        a.setTextSize(10);
        a.setTextColor(Color.GREEN);

        tab.setCurrentTab(0);






    }
}
