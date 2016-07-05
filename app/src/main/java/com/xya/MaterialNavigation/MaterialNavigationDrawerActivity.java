package com.xya.MaterialNavigation;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xya.MainActivity.R;
import com.xya.MainActivity.SearchActivity;
import com.xya.MainActivity.SettingsActivity;
import com.xya.MainActivity.SignInActivity;
import com.xya.MainFragment.NoteFragment;
import com.xya.MainFragment.WordFragment;
import com.xya.UserInterface.ActionBarView;

import java.util.LinkedList;
import java.util.List;

/**
 * Activity that implements ActionBarActivity with a Navigation Drawer with Material Design style
 *
 * @author created by neokree
 */
@SuppressLint("InflateParams")
public abstract class MaterialNavigationDrawerActivity<Fragment> extends ActionBarActivity implements MaterialSectionListener {

    public static final int BOTTOM_SECTION_START = 100;
    public static final int USER_CHANGE_TRANSITION = 500;
    private static int indexFragment = 0;
    private DrawerLayout layout;
    //    private ActionBar actionBar;
    private ActionBarDrawerToggle pulsante;
    private ImageView statusBar;
    private Toolbar toolbar;
    private RelativeLayout drawer;
    private ImageView userphoto;
    private ImageView usercover;
    private TextView username;
    private TextView usermail;
//    通过sections.addView
    private LinearLayout sections;
    private LinearLayout bottomSections;
    private List<MaterialSection> sectionList;
    private List<MaterialSection> bottomSectionList;
    private List<MaterialAccount> accountManager;
    private MaterialSection currentSection;
    private MaterialAccount currentAccount;
    private CharSequence title;
    private float density;
    private int primaryColor;
    private MaterialAccountListener accountListener;
    private View.OnClickListener currentAccountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // enter into account properties

            if (accountListener != null) {
                accountListener.onAccountOpening(currentAccount);
            }

            // close drawer
            layout.closeDrawer(drawer);

        }
    };

    public abstract void onUsernameClick();

    //    findViewById
    public void findview() {
        statusBar = (ImageView) findViewById(R.id.statusBar);
        userphoto = (ImageView) this.findViewById(R.id.user_photo);
        usercover = (ImageView) this.findViewById(R.id.user_cover);

        username = (TextView) this.findViewById(R.id.user_nome);
        usermail = (TextView) this.findViewById(R.id.user_email);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawer = (RelativeLayout) this.findViewById(R.id.drawer);

        sections = (LinearLayout) this.findViewById(R.id.sections);
        bottomSections = (LinearLayout) this.findViewById(R.id.bottom_sections);

        layout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
    }

    public void initListener() {
        statusBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.openDrawer(drawer);
            }
        });
        userphoto.setOnClickListener(currentAccountListener);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUsernameClick();
            }
        });
/**
 * ActionBarDrawerToggle 是 DrawerLayout.DrawerListener 实现。
 * 和 NavigationDrawer 搭配使用，推荐用这个方法，符合Android design规范
 * */
        pulsante = new ActionBarDrawerToggle(this, layout, toolbar, R.string.nothing, R.string.nothing) {
//        pulsante = new ActionBarDrawerToggle(this, layout, R.string.nothing, R.string.nothing) {

            public void onDrawerClosed(View view) {
//                更新
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

        };
//DrawerLayout设置监听器
        layout.setDrawerListener(pulsante);
    }

    public void initEvent(Bundle savedInstanceState) {
        // init lists
        sectionList = new LinkedList<>();
        bottomSectionList = new LinkedList<>();
        accountManager = new LinkedList<>();

        initListener();
        //get density
//        密度，density值表示每英寸有多少个显示点，与分辨率是两个不同的概念。
        density = this.getResources().getDisplayMetrics().density;
//原作者写的ActionBarView
        ActionBarView.initActionbar(this, statusBar, toolbar);
//init，抽象方法，会由父类实现
        init(savedInstanceState);

        if (sectionList.size() == 0) {
            throw new RuntimeException("You must add at least one Section to top list.");
        }

        title = sectionList.get(indexFragment).getTitle();


        // init account views
        if (accountManager.size() > 0) {
            currentAccount = accountManager.get(0);
            notifyAccountDataChanged();
        }

        // init section
        MaterialSection section = sectionList.get(0);
        currentSection = section;
        section.select();
        setFragment((Fragment) section.getTargetFragment(), section.getTitle(), null);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_navigation_drawer);
//设置窗体全屏
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//获取控件
        findview();
        initEvent(savedInstanceState);
    }

    //这里 根据不同的actionbar 标题（因为每次replace fragment的时候都会修改title）来设置menu文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater menuInflater = getMenuInflater();

        //判断actionbar,这种处理，败笔！
        if (currentSection.getTitle().equals("辞典")) {
            menuInflater.inflate(R.menu.menu_dictionary, menu);
        } else if (currentSection.getTitle().equals("统计")) {
            menuInflater.inflate(R.menu.menu_statistics, menu);
        } else if (currentSection.getTitle().equals("用户中心")) {
            menuInflater.inflate(R.menu.menu_usercenter, menu);
        } else if (currentSection.getTitle().equals("云端同步")) {
            menuInflater.inflate(R.menu.menu_cloud, menu);
        } else if (currentSection.getTitle().equals("我的笔记")) {
            menuInflater.inflate(R.menu.menu_note, menu);
        } else if (currentSection.getTitle().equals("单词本")) {
            menuInflater.inflate(R.menu.menu_note, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, 0);
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_searchs) {
            if (currentSection.getTitle().equals("我的笔记"))
                ((NoteFragment) currentSection.getTargetFragment()).SearchView();
            else if (currentSection.getTitle().equals("单词本"))
                ((WordFragment) currentSection.getTargetFragment()).SearchView();
        } else if (id == R.id.action_sort) {
            if (currentSection.getTitle().equals("我的笔记"))
                ((NoteFragment) currentSection.getTargetFragment()).SortView();
            else if (currentSection.getTitle().equals("单词本"))
                ((WordFragment) currentSection.getTargetFragment()).SortView();
        }

// 本来是return super.onOptionsItemSelected(item);添加pulsante.onOptionsItemSelected(item)||之后
// ，点击actionBar的左侧那个图标就可以打开菜单啦！
        return pulsante.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------
    //该方法会自动和actionBar关联, 将开关的图片显示在了action上，如果不设置，也可以有抽屉的效果，不过是默认的图标  ，是一个返回的箭头
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        pulsante.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        pulsante.onConfigurationChanged(newConfig);

    }
    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        this.getSupportActionBar().setTitle(title);
    }

    private void setFragment(Fragment fragment, String title, Fragment oldFragment) {
        setTitle(title);

        // change for default Fragment / support Fragment
        if (fragment instanceof android.app.Fragment) {
            if (oldFragment instanceof android.support.v4.app.Fragment)
                throw new RuntimeException("You should use only one type of Fragment");

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (oldFragment != null && fragment != oldFragment)
                ft.remove((android.app.Fragment) oldFragment);
//替换掉原来的oldFragment
            ft.replace(R.id.frame_container, (android.app.Fragment) fragment).commit();
        } else if (fragment instanceof android.support.v4.app.Fragment) {
            if (oldFragment instanceof android.app.Fragment)
                throw new RuntimeException("You should use only one type of Fragment");

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (oldFragment != null && oldFragment != fragment)
                ft.remove((android.support.v4.app.Fragment) oldFragment);

            ft.replace(R.id.frame_container, (android.support.v4.app.Fragment) fragment).commit();
        } else
            throw new RuntimeException("Fragment must be android.app.Fragment or android.support.v4.app.Fragment");
//选择之后关闭抽屉drawer是一个view,这个view就是整个菜单view
        layout.closeDrawer(drawer);
    }

    private MaterialAccount findAccountNumber(int number) {
        for (MaterialAccount account : accountManager)
            if (account.getAccountNumber() == number)
                return account;
        return null;
    }

    private void setUserEmail(String email) {
        this.usermail.setText(email);
    }

    private void setUsername(String username) {
        this.username.setText(username);
    }

    private void setFirstAccountPhoto(Bitmap photo) {
        userphoto.setImageBitmap(photo);
    }

    private void setDrawerBackground(Bitmap background) {
        usercover.setImageBitmap(background);
    }

    protected int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    @Override
    public void onClick(MaterialSection section) {
        invalidateOptionsMenu();

        if (section.getTarget() == MaterialSection.TARGET_FRAGMENT) {
            if (section.getTitle().equals("用户中心")) {
                SharedPreferences user = getSharedPreferences("user_info", 0);
                if (TextUtils.isEmpty(user.getString("username", ""))) {
                    Intent intent = new Intent(MaterialNavigationDrawerActivity.this, SignInActivity.class);
                    startActivityForResult(intent, 100);
                }
            }
//            此处切换Fragment
            setFragment((Fragment) section.getTargetFragment(), section.getTitle(), (Fragment) currentSection.getTargetFragment());

            // setting toolbar color if is setted
            if (section.hasSectionColor()) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                    this.statusBar.setImageDrawable(new ColorDrawable(darkenColor(section.getSectionColor())));
                else
                    this.statusBar.setImageDrawable(new ColorDrawable(section.getSectionColor()));
                this.getToolbar().setBackgroundColor(section.getSectionColor());
            } else {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                    this.statusBar.setImageDrawable(new ColorDrawable(darkenColor(primaryColor)));
                else
                    this.statusBar.setImageDrawable(new ColorDrawable(primaryColor));
                this.getToolbar().setBackgroundColor(primaryColor);
            }
        } else {
            this.startActivityForResult(section.getTargetIntent(), 11);
        }

        int position = section.getPosition();

        for (MaterialSection mySection : sectionList) {
            if (position != mySection.getPosition())
                mySection.unSelect();
        }
        for (MaterialSection mySection : bottomSectionList) {
            if (position != mySection.getPosition())
                mySection.unSelect();
        }

        currentSection = section;
    }

    public void setAccountListener(MaterialAccountListener listener) {
        this.accountListener = listener;
    }


// Method used for customize layout
//添加到sectionList并且 给LinearLayout sections添加一个view;
    public void addSection(MaterialSection section) {
        section.setPosition(sectionList.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
        sectionList.add(section);
//        LinearLayout.addView添加一个子视图，千万看清楚 LinearLayout sections不是MaterialSection section
        sections.addView(section.getView(), params);
    }

    public void addBottomSection(MaterialSection section) {
        section.setPosition(BOTTOM_SECTION_START + bottomSectionList.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
        bottomSectionList.add(section);
        bottomSections.addView(section.getView(), params);
    }

    public void addDivisor() {
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#e0e0e0"));
        // height 1 px
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(0, (int) (8 * density), 0, (int) (8 * density));

        sections.addView(view, params);
    }

    public void addAccount(MaterialAccount account) {
        if (accountManager.size() == 3)
            throw new RuntimeException("Currently are supported max 3 accounts");

        account.setAccountNumber(accountManager.size());
        accountManager.add(account);
    }


    /**
     * Reload Application data from Account Information
     */
    public void notifyAccountDataChanged() {
        switch (accountManager.size()) {
            case 1:
                this.setFirstAccountPhoto(currentAccount.getCircularPhoto());
                this.setDrawerBackground(currentAccount.getBackground());
                this.setUsername(currentAccount.getTitle());
                this.setUserEmail(currentAccount.getSubTitle());
            default:
        }
    }

    // create sections

    public MaterialSection newSection(String title, Drawable icon, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this, true, MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Drawable icon, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this, true, MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this, true, MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this, true, MaterialSection.TARGET_ACTIVITY);
//        设置菜单触摸选择的监听器
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);
        return section;
    }

    public MaterialSection newSection(String title, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this, false, MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);
        return section;
    }


    public MaterialSection newSection(String title, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this, false, MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }


    // abstract methods

    public abstract void init(Bundle savedInstanceState);

    // get methods

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MaterialSection getCurrentSection() {
        return currentSection;
    }

    public MaterialAccount getCurrentAccount() {
        return currentAccount;
    }

    public MaterialAccount getAccountAtCurrentPosition(int position) {

        if (position < 0 || position >= accountManager.size())
            throw new RuntimeException("Account Index Overflow");

        return findAccountNumber(position);
    }


}