package com.xya.MainFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.LocalApplication.VariableApplication;
import com.xya.MainActivity.R;
import com.xya.UserInterface.CloudItem;

import java.util.Date;

/**
 * 云端同步
 */
public class CloudFragment extends Fragment {


    static final private int RELEASE_To_REFRESH = 0;
    static final private int PULL_To_REFRESH = 1;
    static final private int REFRESHING = 2;
    static final private int DONE = 3;
    private int headerState = DONE;
    private ScrollView sc;
    private LinearLayout header;
    private ImageView arrowImg;
    private ProgressBarCircularIndeterminate headProgress;
    private TextView lastUpdateTxt;
    //////////////////////////////////////////////////////////////
    private TextView tipsTxt;
    private ProgressBarCircularIndeterminate loading;
    private View containers;
    private RotateAnimation tipsAnimation;
    private RotateAnimation reverseAnimation;
    /////////////////////////////////////////////////////////////////
    private int primaryColor;
    private int headerHeight;    //头高度
    private int lastHeaderPadding; //最后一次调用
    private boolean isBack; //从Release 转到 pull
    private SharedPreferences preferences;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            containers.setVisibility(View.VISIBLE);
//            loading.setVisibility(View.GONE);
//
//
//            super.handleMessage(msg);
//        }
//    };

    private CloudItem corpus, build, abbreviation;

    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.layout_cloud_fragment, null);
        primaryColor = ((VariableApplication) getActivity().getApplication()).getPrimaryColor();
        loading = (ProgressBarCircularIndeterminate) view.findViewById(R.id.loading);
        containers = view.findViewById(R.id.content);
        containers.setVisibility(View.INVISIBLE);
        loading.setBackgroundColor(primaryColor);
        initScrollView(view);
        init(view);


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    handler.sendMessage(new Message());
//                }
//            }).start();
        corpus.setOnUpdateClickListener(new OnUpdateClickListener(1));
        build.setOnUpdateClickListener(new OnUpdateClickListener(2));
        abbreviation.setOnUpdateClickListener(new OnUpdateClickListener(3));
        return view;
    }

    private void getInformationFromSer() {
//        BmobQuery查询存储到share preferences
        preferences.edit().putInt("corpus", 123).apply();
        preferences.edit().putInt("build", 123).apply();
        preferences.edit().putInt("abbrev", 0).apply();
    }


    private void init(View view) {
        corpus = (CloudItem) view.findViewById(R.id.corpus);
        build = (CloudItem) view.findViewById(R.id.build);
        abbreviation = (CloudItem) view.findViewById(R.id.abbreviation);
        corpus.setPrimaryColor(primaryColor);
        build.setPrimaryColor(primaryColor);
        abbreviation.setPrimaryColor(primaryColor);

        /*
        * data
        * */
        preferences = getActivity().getApplication().getSharedPreferences("update_info", 0);
        if (preferences.getBoolean("first", true)) {
            preferences.edit().putBoolean("first", false).apply();
            containers.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            getInformationFromSer();
        } else {
            containers.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
        /*
        * 数据库
        * */
        LocalDateBaseManager DB = new LocalDateBaseManager(getActivity());
        DB.openDatabase();

        int corpus_local = DB.getCorpusAmount();
        int build_local = DB.getBuildAmount();
        int abbrev_local = DB.getAbbreviationAmount();
        int corpus_cloud = preferences.getInt("corpus", 0);
        int build_cloud = preferences.getInt("build", 0);
        int abbrev_cloud = preferences.getInt("abbrev", 0);

        corpus.setTotal(corpus_local, corpus_cloud);
        build.setTotal(build_local, build_cloud);
        abbreviation.setTotal(abbrev_local, abbrev_cloud);

    }

    private void initScrollView(View v) {
        LinearLayout globleLayout = (LinearLayout) v.findViewById(R.id.content);
        sc = (ScrollView) globleLayout.findViewById(R.id.scrollView);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        header = (LinearLayout) inflater.inflate(R.layout.layout_cloud_scrollview_header, null);
        header.findViewById(R.id.header).setBackgroundColor(primaryColor);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        lastHeaderPadding = (-1 * headerHeight); //最后一次调用Move Header的Padding
        header.setPadding(0, lastHeaderPadding, 0, 0);
        header.invalidate();
        globleLayout.addView(header, 0);
        headProgress = (ProgressBarCircularIndeterminate) v.findViewById(R.id.head_progressBar);
        headProgress.setRingWidth(2);
        arrowImg = (ImageView) v.findViewById(R.id.head_arrowImageView);
        arrowImg.setMinimumHeight(50);
        arrowImg.setMinimumWidth(50);
        tipsTxt = (TextView) v.findViewById(R.id.head_tipsTextView);
        lastUpdateTxt = (TextView) v.findViewById(R.id.head_lastUpdatedTextView);
        //箭头转动动画
        tipsAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        tipsAnimation.setInterpolator(new LinearInterpolator());
        tipsAnimation.setDuration(200);
        //动画持续时间
        tipsAnimation.setFillAfter(true);    //动画结束后保持动画 		//箭头反转动画
        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);        //为scrollview绑定事件
        sc.setOnTouchListener(new View.OnTouchListener() {
            private int beginY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //sc.getScrollY == 0 scrollview 滑动到头了
                        // lastHeaderPadding > (-1*headerHeight) 表示header还没完全隐藏起来时
                        // headerState != REFRESHING 当正在刷新时
                        if ((sc.getScrollY() == 0 || lastHeaderPadding > (-1 * headerHeight)) && headerState != REFRESHING) {
                            //拿到滑动的Y轴距离
                            int interval = (int) (event.getY() - beginY);                        //是向下滑动而不是向上滑动
                            if (interval > 0) {
                                interval = interval / 2;//下滑阻力
                                lastHeaderPadding = interval + (-1 * headerHeight);
                                header.setPadding(0, lastHeaderPadding, 0, 0);
                                if (lastHeaderPadding > 0) {
                                    //txView.setText("我要刷新咯");
                                    headerState = RELEASE_To_REFRESH;                                //是否已经更新了UI
                                    if (!isBack) {
                                        isBack = true; //到了Release状态，如果往回滑动到了pull则启动动画
                                        changeHeaderViewByState();
                                    }
                                } else {
                                    headerState = PULL_To_REFRESH;
                                    changeHeaderViewByState();                                //txView.setText("看到我了哦"); 								//sc.scrollTo(0, headerPadding);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:                    //加上下滑阻力与实际滑动距离的差（大概值）
                        beginY = (int) ((int) event.getY() + sc.getScrollY() * 1.5);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (headerState != REFRESHING) {
                            switch (headerState) {
                                case DONE:                            //什么也不干
                                    break;
                                case PULL_To_REFRESH:
                                    headerState = DONE;
                                    lastHeaderPadding = -1 * headerHeight;
                                    header.setPadding(0, lastHeaderPadding, 0, 0);
                                    changeHeaderViewByState();
                                    break;
                                case RELEASE_To_REFRESH:
                                    isBack = false; //准备开始刷新，此时将不会往回滑动
                                    headerState = REFRESHING;
                                    changeHeaderViewByState();
                                    onRefresh();
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }                //如果Header是完全被隐藏的则让ScrollView正常滑动，让事件继续否则的话就阻断事件
                if (lastHeaderPadding > (-1 * headerHeight) && headerState != REFRESHING) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void changeHeaderViewByState() {
        switch (headerState) {
            case PULL_To_REFRESH:            // 是由RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
                    arrowImg.startAnimation(reverseAnimation);
                    tipsTxt.setText("下拉刷新");
                }
                tipsTxt.setText("下拉刷新");
                break;
            case RELEASE_To_REFRESH:
                arrowImg.setVisibility(View.VISIBLE);
                headProgress.setVisibility(View.GONE);
                tipsTxt.setVisibility(View.VISIBLE);
                lastUpdateTxt.setVisibility(View.VISIBLE);
                arrowImg.clearAnimation();
                arrowImg.startAnimation(tipsAnimation);
                tipsTxt.setText("松开刷新");
                break;
            case REFRESHING:
                lastHeaderPadding = 0;
                header.setPadding(0, lastHeaderPadding, 0, 0);
                header.invalidate();
                headProgress.setVisibility(View.VISIBLE);
                arrowImg.clearAnimation();
                arrowImg.setVisibility(View.INVISIBLE);
                tipsTxt.setText("正在刷新...");
                lastUpdateTxt.setVisibility(View.VISIBLE);
                break;
            case DONE:
                lastHeaderPadding = -1 * headerHeight;
                header.setPadding(0, lastHeaderPadding, 0, 0);
                header.invalidate();
                headProgress.setVisibility(View.GONE);
                arrowImg.clearAnimation();
                arrowImg.setVisibility(View.VISIBLE);
                tipsTxt.setText("下拉刷新");
                lastUpdateTxt.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void onRefresh() {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                onRefreshComplete();
            }
        }.execute((Void[]) null);
    }

    public void onRefreshComplete() {
        lastUpdateTxt.setText("最近更新:" + new Date().toLocaleString());
        headerState = DONE;
        changeHeaderViewByState();
    }

    //由于OnCreate里面拿不到header的高度所以需要手动计算
    private void measureView(View childView) {
        ViewGroup.LayoutParams p = childView.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height = p.height;
        int childHeightSpec;
        if (height > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        childView.measure(childWidthSpec, childHeightSpec);
    }

    private class OnUpdateClickListener implements View.OnClickListener {
        private int item;

        private OnUpdateClickListener(int item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            switch (item) {
                case 1:
                    SnackbarManager.show(Snackbar.with(getActivity()).text("Corpus"));
                    break;
                case 2:
                    SnackbarManager.show(Snackbar.with(getActivity()).text("Build"));
                    break;
                case 3:
                    SnackbarManager.show(Snackbar.with(getActivity()).text("Abbrev"));
                    break;
            }
        }
    }


}
