package com.xya.LocalApplication;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by ubuntu on 14-12-19.
 */
public class VariableApplication extends Application {

    public static String TAG = "VariableApplication";

    //全局的设置
    private static boolean mobileData = true;      //是否允许2G/3G联网
    private static boolean wifiUpdate = true;       //是否允许wifi自动更新
    private static int primaryColor = 0xff03a9f4;        //默认基本色 light blue
    private static int secondaryColor = 0xfffafafa;      //默认第二基本色
    private static int fontSize = 12;      //默认字体大小
    private static Long day = 0L;     //day只能设置一次，初始时间，统计天数


    //定义sharepreferences保存全局变量
    private static String SETTING_INFO = "setting_info";
    private static SharedPreferences settingPreference;
    private static SharedPreferences.Editor settingEditor;

    //当系统第一次启动创建sharepreferences
    @Override
    public void onCreate() {
        settingPreference = getApplicationContext().getSharedPreferences(SETTING_INFO, 0);        //初始化sharepreferences
        //settingEditor = settingPreference.edit();

        if (settingPreference.getLong("day", 0) == 0) {
            //shezhi基本配置数据
            settingPreference.edit().putBoolean("mobileData", mobileData).apply();
            settingPreference.edit().putBoolean("wifiUpdate", wifiUpdate).apply();
            settingPreference.edit().putInt("primaryColor", primaryColor).apply();
            settingPreference.edit().putInt("secondaryColor", secondaryColor).apply();
            settingPreference.edit().putInt("fontSize", fontSize).apply();
            day = new Date().getTime();
            settingPreference.edit().putLong("day", day).apply();
        }

        //获取基本配置数据
        wifiUpdate = settingPreference.getBoolean("wifiUpdate", wifiUpdate);
        mobileData = settingPreference.getBoolean("mobileData", mobileData);
        primaryColor = settingPreference.getInt("primaryColor", primaryColor);
        secondaryColor = settingPreference.getInt("secondaryColor", secondaryColor);
        fontSize = settingPreference.getInt("fontSize", fontSize);
        day = settingPreference.getLong("day", day);

    }

    //修改基本数据的接口

    //返回全局设置变量
    public Long getDay() {
        return day;
    }

    public static void setDay(Long day) {
        VariableApplication.day = day;
        settingEditor = settingPreference.edit();
        settingEditor.putLong("day", day);
        settingEditor.apply();
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        VariableApplication.primaryColor = primaryColor;
        settingEditor = settingPreference.edit();
        settingEditor.putInt("primaryColor", primaryColor);
        settingEditor.apply();
    }

    public boolean isWifiUpdate() {
        return wifiUpdate;
    }

    public void setWifiUpdate(boolean wifiUpdate) {
        settingEditor = settingPreference.edit();
        VariableApplication.wifiUpdate = wifiUpdate;
        settingEditor.putBoolean("wifiUpdate", wifiUpdate);
        settingEditor.apply();
    }

    public boolean isMobileData() {
        return mobileData;
    }

    public void setMobileData(boolean mobileData) {
        settingEditor = settingPreference.edit();
        VariableApplication.mobileData = mobileData;
        settingEditor.putBoolean("mobileData", mobileData);
        settingEditor.apply();

    }

    public int getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        VariableApplication.secondaryColor = secondaryColor;
        settingEditor = settingPreference.edit();
        settingEditor.putInt("secondaryColor", secondaryColor);
        settingEditor.apply();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        VariableApplication.fontSize = fontSize;
        settingEditor = settingPreference.edit();
        settingEditor.putInt("fontSize", fontSize);
        settingEditor.apply();
    }
}
