package com.xya.MainActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xya.ErrorSolution.BmobSolution;
import com.xya.ErrorSolution.SnackbarSolution;
import com.xya.JniMethod.ImageBlur;
import com.xya.UserInterface.ImageUtils;
import com.xya.ValueObject.AccountVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends ActionBarActivity {


    private static ActionBar actionBar;
    private boolean flag = true;
    private Uri icon;       // for head
    private View include;
    private MaterialEditText username, password, new_username, new_password, new_email, repeat_password;
    private TextView signuptv, return_back;
    private Button signinbtn, more, submit;
    private ImageView statusBar;
    private Toolbar mToolbar;
    private ImageView image;
    private CircleImageView head;
    private RelativeLayout all;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //延迟加载blur
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            image.buildDrawingCache();
            Bitmap bmp = image.getDrawingCache();
            blur(bmp, all);

//            image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    image.getViewTreeObserver().removeOnPreDrawListener(this);
//
//                    return true;
//                }
//            });
        }
    };
    private LinearLayout content;
    //在线城中对对片进行blur
    private Handler blurHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {

//            Toast.makeText(SignInActivity.this,"XXXXXXXXXXXXXXXX",Toast.LENGTH_SHORT).show();
            Bitmap bitmap = (Bitmap) msg.obj;
            all.setBackground(new BitmapDrawable(getResources(), bitmap));
            super.handleMessage(msg);
        }
    };

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
        setContentView(R.layout.layout_user_signin);

        statusBar = (ImageView) findViewById(R.id.statusBar);
        //设置搜索activity的toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置整体的actionbar
        // ActionBarBlur.initActionbar(this, statusBar, mToolbar);
        image = (ImageView) findViewById(R.id.image);

        all = (RelativeLayout) findViewById(R.id.all);
        //初始化toolbar与状态栏
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_return_24dp));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("返回");
        //  applyBlur();
        //在图片加载完成后进行blur处理
        Thread thread = new Thread(runnable);
        thread.start();

        //把注册界面
        include = findViewById(R.id.include);
        //跟多登陆选项
        more = (Button) findViewById(R.id.more);
        //获取登陆的Linearlayout
        content = (LinearLayout) findViewById(R.id.content);


        signup();
        signin();

    }

    public void signin() {
        //把注册界面隐藏
        include.setVisibility(View.GONE);
        //对输入处理
        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);
        signinbtn = (Button) findViewById(R.id.signin);
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameSt = username.getText().toString().trim();
                String passwordSt = password.getText().toString().trim();
                SnackbarSolution solution = new SnackbarSolution(SignInActivity.this);

                if (TextUtils.isEmpty(usernameSt)) {
                    solution.SnackbarSolution(1);
                    return;
                }

                if (TextUtils.isEmpty(passwordSt)) {
                    solution.SnackbarSolution(2);
                    return;
                }
                //联网确认用户名密码是否存在;
                BmobQuery<AccountVO> query1 = new BmobQuery<>();
                BmobQuery<AccountVO> query2 = new BmobQuery<>();
                //判断用户名是不是邮箱
                Boolean isEmail = usernameSt.contains("@");
                if (isEmail)
                    query1.addWhereEqualTo("email", usernameSt);
                else
                    query1.addWhereEqualTo("username", usernameSt);
                query2.addWhereEqualTo("password", passwordSt);
                List<BmobQuery<AccountVO>> queries = new ArrayList<>();
                queries.add(query1);
                queries.add(query2);
                BmobQuery<AccountVO> query = new BmobQuery<>();
                query.and(queries);
                query.findObjects(SignInActivity.this, new FindListener<AccountVO>() {
                    @Override
                    public void onSuccess(List<AccountVO> accountVOs) {
                        if (accountVOs.size() == 1) {
                            //用户的处理
                            Intent intent = new Intent();
                            intent.putExtra("username", accountVOs.get(0).getUsername());
                            intent.putExtra("email", accountVOs.get(0).getEmail());


                            intent.putExtra("path", accountVOs.get(0).getAccountBkg().getFileUrl(SignInActivity.this));
                            setResult(20, intent);
                            finish();

                        } else {
                            SnackbarSolution solution = new SnackbarSolution(SignInActivity.this);
                            solution.SnackbarSolution(3);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        BmobSolution solution = new BmobSolution(SignInActivity.this, i, getApplication());
                        solution.ErrorManager();
                    }
                });
            }
        });

        signuptv = (TextView) findViewById(R.id.signup);
        signuptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示注册页面
                more.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                include.setVisibility(View.VISIBLE);
            }
        });


    }

    public void signup() {
        //注册
        return_back = (TextView) findViewById(R.id.return_back);
        return_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.setVisibility(View.VISIBLE);
                content.setVisibility(View.VISIBLE);
                include.setVisibility(View.GONE);
            }
        });

        //新注册用户信息
        new_username = (MaterialEditText) findViewById(R.id.new_username);
        new_password = (MaterialEditText) findViewById(R.id.new_password);
        new_email = (MaterialEditText) findViewById(R.id.new_email);
        repeat_password = (MaterialEditText) findViewById(R.id.repeat_password);
        submit = (Button) findViewById(R.id.submit);
        head = (CircleImageView) findViewById(R.id.title);
        //头像
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (include.getVisibility() == View.GONE)
                    return;

                new AlertDialogPro.Builder(SignInActivity.this)
                        .setTitle("选择图片")
                        .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (0 == which) {
                                    ImageUtils.openCameraImage(SignInActivity.this);
                                } else {
                                    ImageUtils.openLocalImage(SignInActivity.this);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = new_username.getText().toString().trim();
                final String email = new_email.getText().toString().trim();
                final String password = new_password.getText().toString().trim();
                String re_password = repeat_password.getText().toString().trim();
                SnackbarSolution solution = new SnackbarSolution(SignInActivity.this);
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
                    solution.SnackbarSolution(1);
                    return;
                }
                if (!email.contains("@") || !email.contains(".")) {
                    solution.SnackbarSolution(5);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    solution.SnackbarSolution(2);
                    return;
                }

                if (password.length() < 8) {
                    solution.SnackbarSolution(6);
                    return;
                }

                if (!password.equals(re_password)) {
                    solution.SnackbarSolution(4);
                    return;
                }

                //判断数据库是否存在相同的数据
                BmobQuery<AccountVO> query1 = new BmobQuery<>();
                BmobQuery<AccountVO> query2 = new BmobQuery<>();
                query1.addWhereEqualTo("username", username);
                query2.addWhereEqualTo("email", email);
                List<BmobQuery<AccountVO>> queries = new ArrayList<>();
                queries.add(query1);
                queries.add(query2);
                BmobQuery<AccountVO> query = new BmobQuery<>();
                query.or(queries);
                query.findObjects(SignInActivity.this, new FindListener<AccountVO>() {
                    @Override
                    public void onSuccess(List<AccountVO> accountVOs) {
                        if (accountVOs.size() > 0) {
                            SnackbarSolution solution = new SnackbarSolution(SignInActivity.this);
                            solution.SnackbarSolution(7);
                            flag = false;
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        BmobSolution solution = new BmobSolution(SignInActivity.this, i, getApplication());
                        solution.ErrorManager();
                        flag = false;
                    }
                });

                //防止用户取消后会继续执行下面的方法
                if (!flag) {
                    flag = true;
                    return;
                }

                if (icon == null) {
                    new AlertDialogPro.Builder(SignInActivity.this)
                            .setTitle("注意")
                            .setMessage("你未选择上传头像，确认使用默认头像么?")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AccountVO account = new AccountVO();
                                    account.setUsername(username);
                                    account.setEmail(email);
                                    account.setPassword(password);
                                    account.save(SignInActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            //return data
                                            Intent intent = new Intent();
                                            intent.putExtra("username", username);
                                            intent.putExtra("email", email);
                                            SignInActivity.this.setResult(21, intent);
                                            SignInActivity.this.finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            BmobSolution solution = new BmobSolution(SignInActivity.this, i, getApplication());
                                            solution.ErrorManager();
                                        }
                                    });
                                }
                            })
                            .show();
                } else {
                    //上传用户信息和头像
                    //定义BmobFile对象
                    //Toast.makeText(SignInActivity.this,getRealFilePath(SignInActivity.this,icon),Toast.LENGTH_SHORT).show();
                    final BmobFile file = new BmobFile(new File(getRealFilePath(SignInActivity.this, icon)));
                    file.upload(SignInActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            AccountVO vo = new AccountVO();
                            vo.setUsername(username);
                            vo.setEmail(email);
                            vo.setPassword(password);
                            vo.setAccountBkg(file);
                            vo.save(SignInActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    // Toast.makeText(SignInActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                    //register successfully
                                    Intent intent = new Intent();
                                    intent.putExtra("username", username);
                                    intent.putExtra("email", email);
                                    //intent.setData(icon);
                                    intent.putExtra("path", getRealFilePath(SignInActivity.this, icon));
                                    SignInActivity.this.setResult(22, intent);
                                    SignInActivity.this.finish();

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(SignInActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                                    BmobSolution solution = new BmobSolution(SignInActivity.this, i, getApplication());
                                    solution.ErrorManager();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(SignInActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                            BmobSolution solution = new BmobSolution(SignInActivity.this, i, getApplication());
                            solution.ErrorManager();
                        }
                    });
                }
            }
        });
    }

    //响应图片返回参数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                if (ImageUtils.imageUriFromCamera != null) {
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                }
                break;
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if (data != null && data.getData() != null) {
                    ImageUtils.cropImage(this, data.getData());
                }
                break;
            case ImageUtils.CROP_IMAGE:
                if (ImageUtils.cropImageUri != null) {
                    icon = ImageUtils.cropImageUri;
                    head.setImageURI(ImageUtils.cropImageUri);
                }
                break;
        }

    }

    private void blur(Bitmap bkg, View view) {

        float scaleFactor = 1;
        float radius = 20;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = doBlurJniBitMap(overlay, (int) radius, true);

        Message msg = new Message();
        msg.obj = overlay;
        blurHander.sendMessage(msg);
    }

    public Bitmap doBlurJniBitMap(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }
        //Jni BitMap
        ImageBlur.blurBitMap(bitmap, radius);
        return (bitmap);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
