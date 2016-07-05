package com.xya.MainActivity;

import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.ErrorSolution.SnackbarSolution;
import com.xya.LocalApplication.VariableApplication;
import com.xya.StaticClass.StaticConstant;
import com.xya.UserInterface.ActionBarView;
import com.xya.UserInterface.ImageUtils;
import com.xya.ValueObject.NotesVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class NoteActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private String TAG = this.getClass().getSimpleName();

    //UI
    private MaterialEditText note;
    private TextView date;
    private ImageView mImageView;
    private ImageView statusBar;
    private Toolbar mToolbar;
    private ToggleButton desktop, words, saveAs;
    private ObservableScrollView scrollView;
    private View capture ;
    private Bitmap bitmap;

    //data
    private LocalDateBaseManager db;
    private int primaryColor;
    private String path;
    private String thumb;
    private Handler FileCopy = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String sourcePath = msg.obj.toString();
            File sourceFile = new File(sourcePath);
            path = StaticConstant.notePath + "/" + new Date().getTime();
            File aimFile = new File(path);
            try {
                if (!aimFile.exists()) {
                    aimFile.createNewFile();
                }
                FileInputStream inputStream = new FileInputStream(sourceFile);
                FileOutputStream outputStream = new FileOutputStream(aimFile);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                BitmapFactory.decodeStream(inputStream, null, options).compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
//                byte[] buffer = new byte[1024];
//                int rd = 0;
//                while ((rd = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, rd);
//                }
                outputStream.flush();
                inputStream.close();
                outputStream.close();
                mImageView.setImageDrawable(Drawable.createFromPath(path));

                //thumb
                thumb = StaticConstant.thumbPath + "/"+new Date().getTime();
                FileInputStream i = new FileInputStream(aimFile);
                FileOutputStream o = new FileOutputStream(new File(thumb));
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inSampleSize = 4;
                BitmapFactory.decodeStream(i, null, op).compress(Bitmap.CompressFormat.JPEG, 70, o);
//                byte[] b = new byte[1024];
//                int r = 0;
//                while ((r = i.read(b)) != -1) {
//                    o.write(b, 0, r);
//                }
                o.flush();
                i.close();
                o.close();
            } catch (IOException e) {
            }

        }
    };
    private int mParallaxImageHeight;

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dictionary_note_show);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        //init file
        File file = new File(StaticConstant.notePath);
        if (!file.exists())
            file.mkdirs();
        File file1 = new File(StaticConstant.thumbPath);
        if (!file1.exists())
            file1.mkdirs();
        File file2 = new File(StaticConstant.cutPath);
        if (!file2.exists())
            file2.mkdirs();
        // init data
        db = new LocalDateBaseManager(this);
        db.openDatabase();
        primaryColor = ((VariableApplication) getApplication()).getPrimaryColor();
        Intent intent = this.getIntent();
        final String kind = intent.getStringExtra("kind");
        final String key = intent.getStringExtra("key");


        //ToggleButton


        mImageView = (ImageView) findViewById(R.id.image);
        statusBar = (ImageView) findViewById(R.id.statusBar);
        //设置搜索activity的toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolBarBackgroundAlpha(mToolbar, 0f, primaryColor);
        setStatusBarBackgroundAlpha(statusBar, 0.2f, ActionBarView.darkenColor(primaryColor));

        //
        desktop = (ToggleButton) findViewById(R.id.desktop);
        words = (ToggleButton) findViewById(R.id.words);
        saveAs = (ToggleButton) findViewById(R.id.saveAs);
        note = (MaterialEditText) findViewById(R.id.note);
        date = (TextView) findViewById(R.id.time);
        capture = findViewById(R.id.cut);




        scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnackbarSolution solution = new SnackbarSolution(NoteActivity.this);
                NotesVO vo = new NotesVO();
                vo.setKey(key);
                vo.setKind(kind);

                if (TextUtils.isEmpty(note.getText().toString())) {
                    solution.SnackbarSolution(9);
                    return;
                }
                vo.setThumb(thumb);
                vo.setNote(note.getText().toString());
                vo.setDate(date.getText().toString());
                vo.setPath(path);
                db.insertNotes(vo);

                if (desktop.isChecked()) {
                    ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    c.setText(note.getText().toString());
                }
                if (words.isChecked()) {
                    //添加到单词本
                }
                String cut = "";
                if (saveAs.isChecked()) {
                    FileOutputStream out = null;
                    try {
                        cut = StaticConstant.cutPath + "/" + new Date().toLocaleString() + ".png";
                        out = new FileOutputStream(new File(cut));
                        createViewBitmap(capture).compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finish();
                //Log.d(TAG, path);
            }
        });


        //设置整体的actionbar
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(key);

    }

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_photos) {
            ImageUtils.openCameraImage(this);
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("imageUriFromCamera,",""+ImageUtils.imageUriFromCamera);
        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (ImageUtils.imageUriFromCamera != null) {
                    path = StaticConstant.notePath + "/" + new Date().hashCode();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.obj = getRealFilePath(NoteActivity.this, ImageUtils.imageUriFromCamera);
                            FileCopy.sendMessage(message);
                        }
                    }).start();
                }
                break;
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        setToolBarBackgroundAlpha(mToolbar, alpha, primaryColor);
        setStatusBarBackgroundAlpha(statusBar, alpha + 0.2f, ActionBarView.darkenColor(primaryColor));
        note.setAlpha(1 - alpha);
        date.setAlpha(1 - alpha);
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void setToolBarBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    private void setStatusBarBackgroundAlpha(ImageView statusBar, float alpha, int primaryColor) {
        statusBar.setBackgroundColor(primaryColor);
        statusBar.setAlpha(alpha);
    }


}
