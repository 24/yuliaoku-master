package com.xya.MainActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.LocalApplication.VariableApplication;
import com.xya.ValueObject.AbbreviationVO;
import com.xya.ValueObject.BuildVO;
import com.xya.ValueObject.CorpusVO;
import com.xya.ValueObject.NotesVO;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ubuntu on 15-3-5.
 * for notes item
 * Happy New Year!
 */
public class ItemActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {


    public final static int[] layout = new int[]{
            R.layout.layout_note_result_pager01,
            R.layout.layout_note_result_pager02
    };


    private int primaryColor;
    private NotesVO note;
    private LocalDateBaseManager db;

    private View content;
    private TextView key;
    private ViewPager pager;
    private ImageView[] tips;
    private List<ImageView> image;
    private List<View> view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_note_result);

        //init data
        db = new LocalDateBaseManager(this);
        db.openDatabase();
        Intent intent = getIntent();
        note = (NotesVO) intent.getSerializableExtra("data");
        Toast.makeText(this, "note = " + note.getKey(), Toast.LENGTH_SHORT).show();
        primaryColor = ((VariableApplication) getApplication()).getPrimaryColor();
        view = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater().from(this);
        for (int i = 0; i < layout.length; i++) {
            view.add(inflater.inflate(layout[i], null));

        }
//            view.get(0).findViewById(R.id.image).setBackground(Drawable.createFromPath(note.getPath()));
//        view.get(0).findViewById(R.id.image).setBackground(Drawable.createFromPath(note.getPath()));
//        if (TextUtils.isEmpty(note.getPath()))
//            ((ImageView) inflater.inflate(layout[i], null).findViewById(R.id.image))
//                    .setImageBitmap(BitmapFactory.decodeFile(note.getPath()));
//        Log.d("path", note.getPath());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView statusBar = (ImageView) findViewById(R.id.statusBar);

        //ActionBarView.initActionbar(this,statusBar,toolbar);
        statusBar.setBackgroundColor(primaryColor);
        statusBar.setAlpha(0.2f);
        content = findViewById(R.id.note);
        //ImageView image = (ImageView) findViewById(R.id.image);

        //init ui
        //pager01
        MaterialEditText _0 = (MaterialEditText) view.get(0).findViewById(R.id.key);
        _0.setText(note.getKey());
        _0.setFloatingLabelText(note.getKind());

        MaterialEditText _1 = (MaterialEditText) view.get(1).findViewById(R.id.meaning);
        _1.setText(getMeaning(note.getKind(), note.getKey()));

        ((MaterialEditText) findViewById(R.id.mynote)).setText(note.getNote());

        //        key = (TextView) findViewById(R.id.key);
        //        key.setText(note.getKey());
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        pager = (ViewPager) findViewById(R.id.viewPager);
        if (TextUtils.isEmpty(note.getPath()))
            pager.setBackground(getResources().getDrawable(R.drawable.background));
        else
            pager.setBackground(Drawable.createFromPath(note.getPath()));
        if (!TextUtils.isEmpty(note.getPath())) {
            Bitmap bitmap = BitmapFactory.decodeFile(note.getPath());
            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch vibrant = palette.getLightVibrantSwatch();
                    if (vibrant != null)
                        content.setBackgroundColor(vibrant.getRgb());
                }
            });
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                    if (swatch != null) {
                        content.setBackgroundColor(swatch.getRgb());
                    }
                }
            });
        }
        //findViewById(R.id.note).setBackgroundColor(primaryColor);
        tips = new ImageView[layout.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new WindowManager.LayoutParams(10, 10));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(7, 7);
            lp.setMargins(7, 0, 7, 0);
            imageView.setLayoutParams(lp);
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            group.addView(imageView);
        }
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return layout.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(view.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(view.get(position));
                return view.get(position);
            }
        });
        pager.setOnPageChangeListener(this);


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

    }

    private String getMeaning(String kind, String key) {
        List<String> meaning = new ArrayList<>();
        if (kind.equals(getResources().getString(R.string.action_build))) {
            List<BuildVO> builds = db.getBuildDetailByWord(key);
            for (BuildVO vo : builds)
                meaning.add(vo.getMeaning());
        } else if (kind.equals(getResources().getString(R.string.action_abbreviation))) {
            List<AbbreviationVO> abbreviation = db.getAbbreviationDetailByWord(key);
            for (AbbreviationVO vo : abbreviation)
                meaning.add(vo.getMeaning_zh());
        } else if (kind.equals(getResources().getString(R.string.action_corpus))) {
            List<CorpusVO> corpus = db.getCorpusDetailByWord(key);
            for (CorpusVO vo : corpus)
                meaning.add(vo.getDefinition());
        }

        String temp = "";
        for (String s : meaning) {
            temp += (s + "\n");
        }

        return temp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, note.getNote());
            //  intent.putExtra(Intent.EXTRA_STREAM, R.drawable.bamboo);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, null));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < tips.length; i++) {
            if (i == position) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
