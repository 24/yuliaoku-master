package com.xya.MainActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.xya.LocalApplication.VariableApplication;
import com.xya.MainFragment.CloudFragment;
import com.xya.MainFragment.DictionaryFragment;
import com.xya.MainFragment.NoteFragment;
import com.xya.MainFragment.StatisticsFragment;
import com.xya.MainFragment.UserFragment;
import com.xya.MainFragment.WordFragment;
import com.xya.MaterialNavigation.MaterialAccount;
import com.xya.MaterialNavigation.MaterialAccountListener;
import com.xya.MaterialNavigation.MaterialNavigationDrawerActivity;
import com.xya.MaterialNavigation.MaterialSection;
import cn.bmob.v3.Bmob;


public class MainActivity extends MaterialNavigationDrawerActivity {
//    SharedPreferences用于存储用户数据的文件名称
    private static String USER_INFO = "user_info";
//    账户
    MaterialAccount account;
//    7个MaterialSection，每一个都有一个targetFragment
    MaterialSection homePage, dictionary, userCenter, cloudSynch, userWord, userNote, settingsSection;
    private int primaryColor;
    private String pathOfhead;
    // after 5 second (async task loading photo from website) change user photo
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
                account.setPhoto(new BitmapDrawable(BitmapFactory.decodeFile(pathOfhead)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyAccountDataChanged();
                        //Toast.makeText(getApplicationContext(), "Loaded 'from web' user image", Toast.LENGTH_SHORT).show();
                    }
                });
                //Log.w("PHOTO","user account photo setted");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    //用户名点击事件
    @Override
    public void onUsernameClick() {
        if (account.getTitle().equals("点击登录")) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivityForResult(intent, 100);
        } else {
            SnackbarManager.show(Snackbar.with(this)
                    .text("已登陆"));
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {


        //初始化Bmob
        Bmob.initialize(this, "6366708f127f0b1182ae2c127415c2b3");
        primaryColor = ((VariableApplication) this.getApplication()).getPrimaryColor();
        //添加账户
        account = new MaterialAccount("点击登录", "", new ColorDrawable(Color.parseColor("#ff03a9f4")), this.getResources().getDrawable(R.drawable.mat1));
        this.addAccount(account);
        this.setAccountListener(new MaterialAccountListener() {
            @Override
            public void onAccountOpening(MaterialAccount account) {

                SharedPreferences preferences = getApplicationContext().getSharedPreferences(USER_INFO, 0);
                String username = preferences.getString("username", "");
                if (TextUtils.isEmpty(username)) {
                    return;
                } else
                    SnackbarManager.show(Snackbar
                            .with(MainActivity.this)
                            .text("Photo"));

            }

            @Override
            public void onChangeAccount(MaterialAccount newAccount) {
            }
        });
        //创建sections
        homePage = this.newSection("辞典", new DictionaryFragment()).setSectionColor(primaryColor);
        dictionary = this.newSection("统计", new StatisticsFragment()).setSectionColor(primaryColor);
        userCenter = this.newSection("用户中心",
                this.getResources().getDrawable(R.drawable.ic_face_unlock_grey600_24dp),
                new UserFragment()
        ).setNotifications(10).setSectionColor(primaryColor);
        cloudSynch = this.newSection("云端同步",
                this.getResources().getDrawable(R.drawable.ic_cloud_queue_grey600_24dp),
                new CloudFragment()
        ).setNotifications(150).setSectionColor(primaryColor);
        userNote = this.newSection("我的笔记", new NoteFragment()).setSectionColor(primaryColor);
        userWord = this.newSection("单词本", new WordFragment()).setSectionColor(primaryColor);


        Intent i = new Intent(this, SettingsActivity.class);
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_settings_black_24dp);
        drawable.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        settingsSection = this.newSection("系统设置", drawable, i);

        //添加section到drawer
        this.addSection(homePage);
        this.addSection(dictionary);
        this.addDivisor();
        this.addSection(userCenter);
        this.addSection(cloudSynch);
        this.addDivisor();
        this.addSection(userNote);
        this.addSection(userWord);
        this.addDivisor();
        this.addBottomSection(settingsSection);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences user_info = getApplication().getSharedPreferences(USER_INFO, 0);

        if (data != null) {
            user_info.edit().putString("username", data.getStringExtra("username")).apply();
            user_info.edit().putString("email", data.getStringExtra("email")).apply();
            //更改个人信息UI
            account.setTitle(data.getStringExtra("username"));
            account.setSubTitle(data.getStringExtra("email"));
        }
        if (11 == requestCode) {
            this.recreate();
        }

        if (20 == requestCode) {
            //保存个人信息到sharePreferences
            user_info.edit().putString("path", data.getStringExtra("path")).apply();
            //网络下载设置图标
            t.start();
        } else if (22 == requestCode) {
            pathOfhead = data.getStringExtra("path");
            account.setBackground(new BitmapDrawable((pathOfhead)));
        } else {
            //设为默认图标
        }

    }
}
