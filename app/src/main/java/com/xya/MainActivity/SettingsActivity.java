package com.xya.MainActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alertdialogpro.AlertDialogPro;
import com.xya.LocalApplication.VariableApplication;
import com.xya.UserInterface.ActionBarView;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends ActionBarActivity {

    //定义常亮判断不同事件
    public static int USER_CONSTANT = 0;
    public static int DATA_CONSTANT = 1;
    public static int ABOUT_CONSTANT = 2;
    public static int THEME_CONSTANT = 3;
    public static int UPDATE_CONSTANT = 4;
    public static int FONT_CONSTANT = 5;
    public static int SHARE_CONSTANT = 6;
    public static int AUTO_CONSTANT = 7;
    public static int OPINION_CONSTANT = 8;

    private int primaryColor;
    //是否改变主题
    private boolean change = false;
    //color集合
    private int[] colors = {
            R.color.system,
            R.color.aqua, R.color.blueviolet, R.color.coral,
            R.color.darkblue, R.color.darkcyan, R.color.ghostwhite,
            R.color.indianred, R.color.lightcyan, R.color.lightpink,
            R.color.orchid, R.color.slategray,
            R.color.tan, R.color.violet, R.color.yellow, 0,
    };

    private ToggleButton dataToggle, updateToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        //设置ToolBar
        ImageView statusBar = (ImageView) findViewById(R.id.statusBar);
        //设置搜索activity的toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.action_settings));
        //设置整体的actionbar
        ActionBarView.initActionbar(this, statusBar, mToolbar);

        primaryColor = ((VariableApplication) getApplication()).getPrimaryColor();

        //设置内容
        //设置用户管理点击事件
        View account = findViewById(R.id.set_account);
        account.setOnClickListener(new OnClick(USER_CONSTANT));

        //2G/3G联网控制
        dataToggle = (ToggleButton) findViewById(R.id.set_data_toggle);
        dataToggle.setOnClickListener(new OnClick(DATA_CONSTANT));

        //更换主题
        View theme = findViewById(R.id.set_theme);
        theme.setOnClickListener(new OnClick(THEME_CONSTANT));

        //字体大小
        View font = findViewById(R.id.set_font);
        font.setOnClickListener(new OnClick(FONT_CONSTANT));

        //分享
        View share = findViewById(R.id.set_share);
        share.setOnClickListener(new OnClick(SHARE_CONSTANT));

        //更新
        View update = findViewById(R.id.set_update);
        update.setOnClickListener(new OnClick(UPDATE_CONSTANT));

        //WIFI自动更新
        updateToggle = (ToggleButton) findViewById(R.id.set_auto_toggle);
        updateToggle.setOnClickListener(new OnClick(AUTO_CONSTANT));

        //意见反馈
        View opinion = findViewById(R.id.set_opinion);
        opinion.setOnClickListener(new OnClick(OPINION_CONSTANT));

        //关于我们
        View about = findViewById(R.id.set_about);
        about.setOnClickListener(new OnClick(ABOUT_CONSTANT));

        //初始化全局
        init();
    }

    public void init() {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("user_info", 0);
        ((TextView) findViewById(R.id.username)).setText(preferences.getString("username", "用户管理"));

        dataToggle.setChecked(((VariableApplication) this.getApplication()).isMobileData());
        setToggleBackground(dataToggle, dataToggle.isChecked());
        updateToggle.setChecked(((VariableApplication) this.getApplication()).isWifiUpdate());
        setToggleBackground(updateToggle, updateToggle.isChecked());

    }

    private void setToggleBackground(ToggleButton toggle, boolean checked) {
        if (checked)
            toggle.getBackground().setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        else
            toggle.getBackground().setColorFilter(0x000000, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            if (change)
                setResult(11);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 23) {
            int primaryColor = data.getIntExtra("primaryColor", 0);
            ((VariableApplication) SettingsActivity.this.getApplication()).setPrimaryColor((primaryColor));
            SettingsActivity.this.recreate();
        }

    }

    private class OnClick implements View.OnClickListener {

        private int kind;

        private OnClick(int kind) {
            this.kind = kind;
        }

        @Override
        public void onClick(View v) {
            switch (kind) {
                //3G/2G是否允许联网
                case 1:
                    ((VariableApplication) SettingsActivity.this.getApplication()).setMobileData(!((VariableApplication) SettingsActivity.this.getApplication()).isMobileData());
                    setToggleBackground(dataToggle, dataToggle.isChecked());
                    break;
                //关于
                case 2:
                    new AlertDialogPro.Builder(SettingsActivity.this)
                            .setTitle("关于")
                            .setMessage(getResources().getString(R.string.changelog))
                            .setNegativeButton("确定", null)
                            .create().show();
                    break;
                //更换主题
                case 3:
                    //随机生成dot图标
                    int[] draw = {
                            R.drawable.color_system,
                            R.drawable.color_aqua, R.drawable.color_blueviolet, R.drawable.color_coral,
                            R.drawable.color_darkblue, R.drawable.color_darkcyan, R.drawable.color_ghostwhite,
                            R.drawable.color_indianred, R.drawable.color_lightcyan, R.drawable.color_lightpink,
                            R.drawable.color_orchid, R.drawable.color_slategray,
                            R.drawable.color_tan, R.drawable.color_violet, R.drawable.color_yellow, 0,
                    };

                    String[] color = {
                            "默认", "浅绿色", "蓝紫罗兰", "珊瑚", "暗蓝色", "暗青色", "幽灵白", "印度红", "淡青色", "浅粉红", "兰花紫", "灰石色", "茶色", "紫罗兰", "纯黄", "自定义"
                    };
                    //生成动态数组，加入数据
                    ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
                    for (int i = 0; i < draw.length; i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("color", color[i]);
                        map.put("image", draw[i]);
                        arrayList.add(map);
                    }

                    new AlertDialogPro.Builder(SettingsActivity.this)
                            .setTitle("选择主题")
                            .setAdapter(new SimpleAdapter(
                                    SettingsActivity.this,
                                    arrayList,
                                    R.layout.layout_settings_item,
                                    new String[]{"color", "image"},
                                    new int[]{R.id.color, R.id.image}
                            ), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    change = true;
                                    if (which == (colors.length - 1)) {
                                        startActivityForResult(new Intent(SettingsActivity.this, ColorActivity.class), 23);
                                    } else {
                                        ((VariableApplication) SettingsActivity.this.getApplication()).setPrimaryColor(getResources().getColor(colors[which]));
                                        SettingsActivity.this.recreate();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();
                    break;
                //在线检测是否最新
                case 4:
                    /*code*/
                    break;
                //字体大小
                case 5:
                    new AlertDialogPro.Builder(SettingsActivity.this)
                            .setTitle("选择字体")
                            .setItems(new String[]{"大", "中(默认)", "小"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setNegativeButton("取消", null)
                            .create().show();
                    //wifi自动更新
                case 7:
                    ((VariableApplication) SettingsActivity.this.getApplication()).setWifiUpdate(!((VariableApplication) SettingsActivity.this.getApplication()).isWifiUpdate());
                    // ((VariableApplication) SettingsActivity.this.getApplication()).setMobileData(!((VariableApplication) SettingsActivity.this.getApplication()).isMobileData());
                    setToggleBackground(updateToggle, updateToggle.isChecked());
                    break;
            }
        }
    }
}

