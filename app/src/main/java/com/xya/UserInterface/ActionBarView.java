package com.xya.UserInterface;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.xya.LocalApplication.VariableApplication;

/**
 * Created by ubuntu on 14-12-24.
 */
public class ActionBarView {

    private static ActionBar actionBar;


    //设置基本的statusbar与toolbar
    public static void initActionbar(ActionBarActivity activity, ImageView statusBar, Toolbar toolbar) {

//        默认值private static int primaryColor = 0xff03a9f4;
        int primaryColor = ((VariableApplication) activity.getApplication()).getPrimaryColor();

        // set darker status bar if device is kitkat
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            statusBar.setImageDrawable(new ColorDrawable(darkenColor(primaryColor)));
        else
            statusBar.setImageDrawable(new ColorDrawable(primaryColor));
        toolbar.setBackgroundColor(primaryColor);
        // INIT ACTION BAR
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }




    //改变颜色深度
    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.85f; // value component
        return Color.HSVToColor(hsv);
    }


}
