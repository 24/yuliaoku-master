package com.xya.StaticClass;

import android.os.Environment;

/**
 * Created by ubuntu on 15-1-26.
 */
public class StaticConstant {

    public static String abolutePath = Environment.getExternalStorageDirectory().getPath() + "/yuliaoku/";
    public static String cachePath = Environment.getExternalStorageDirectory().getPath() + "/yuliaoku/.cache";
    public static String notePath = Environment.getExternalStorageDirectory().getPath() + "/yuliaoku/.note";
    public static String thumbPath = Environment.getExternalStorageDirectory().getPath() + "/yuliaoku/.thumb";
    public static String cutPath = Environment.getExternalStorageDirectory().getPath() + "/yuliaoku/cut";

}
