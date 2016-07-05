package com.xya.MainActivity;
/*
* 写在前面
* 看到导入的包的个数可以看出这个类有多大
* 内容太复杂:数据处理+试图更新+数据访问
* */

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xya.DatabaseUtil.CorpusDataManager;
import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.ErrorSolution.BmobSolution;
import com.xya.ErrorSolution.SnackbarSolution;
import com.xya.LocalApplication.VariableApplication;
import com.xya.UserInterface.ActionBarView;
import com.xya.UserInterface.MaterialWidget.Widget.PaperButton;
import com.xya.UserInterface.ResultItem;
import com.xya.ValueObject.AbbreviationVO;
import com.xya.ValueObject.BuildVO;
import com.xya.ValueObject.CorpusVO;
import com.xya.ValueObject.WordsVO;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class ResultActivity extends ActionBarActivity {


    private static boolean flag = true;
    private final int[] icon = {
            R.id.iv1,
            R.id.iv2,
            R.id.iv3,
            R.id.iv4,
            R.id.iv5
    };
    private final int[] drawable = {
            R.drawable.ic_dns_grey600_24dp,
            R.drawable.ic_voicemail_grey600_24dp,
            R.drawable.ic_translate_grey600_24dp,
            R.drawable.ic_more_grey600_24dp,
            R.drawable.ic_today_grey600_24dp
    };
    private final int[] layout = {
            R.id.linearlayout0,
            R.id.linearlayout1,
            R.id.linearlayout2,
            R.id.linearlayout3,
            R.id.linearlayout4,
            R.id.linearlayout5
    };
    //UI
    private List<LinearLayout> linearLayout = new ArrayList<>();
    private Toolbar mToolbar;
    private TextView mTitleView, connectNetwork;
    private View view, empty;
    private MaterialEditText definition, word, pronunciation, date;
    private int primaryColor;
    private ObservableScrollView scrollView;
    //DATA
    private ArrayList<HashMap<String, String>> current;
    private LocalDateBaseManager dateBaseManager;
    private int mFlexibleSpaceHeight;
    private String key;
    private CorpusDataManager corpusDataManager;
    private int kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateBaseManager = new LocalDateBaseManager(this);
        dateBaseManager.openDatabase();
        //从上个Activity获取到的数据
        Bundle bundle = this.getIntent().getExtras();
        key = bundle.getString("key");
        kind = bundle.getInt("kind");      //此出与读数据库的kind不一致，注意
        setContentView(R.layout.layout_dictionary_result);

        primaryColor = ((VariableApplication) getApplication()).getPrimaryColor();
        ((TextView) findViewById(R.id.textView4)).setTextColor(primaryColor);


        ImageView statusBar = (ImageView) findViewById(R.id.statusBar);
        //设置搜索activity的toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        View expandedToolbar = findViewById(R.id.expanded_toolbar);
        //设置整体的actionbar
        ActionBarView.initActionbar(this, statusBar, mToolbar);
        expandedToolbar.setBackgroundColor(primaryColor);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(key);
        setTitle(null);

        //actionbar logo位置图标
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));

        //试图的缩放等
        scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean b, boolean b2) {
                updateFlexibleSpaceText(scrollY);
            }

            public void onDownMotionEvent() {
            }

            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
            }
        });

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_height);
        ViewTreeObserver vto = mTitleView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTitleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateFlexibleSpaceText(scrollView.getCurrentScrollY());
            }
        });
        view = findViewById(R.id.include);
        empty = findViewById(R.id.empty);
        init();

        corpusDataManager = new CorpusDataManager(primaryColor);

        //又见大量findviewbyid，呵呵
        if (kind == R.id.action_search) {
            int type = dateBaseManager.getType(key);
            if (type != 0)
                kind = type;
            else {
                empty.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
            }
        }
        //Toast.makeText(this, "" + kind, Toast.LENGTH_SHORT).show();
        if (kind == R.id.action_words)
            isWord();
        else if (kind == R.id.action_abbreviation)
            isAbbrevation();
        else if (kind == R.id.action_build)
            isBuild();
    }

    /*
    * 视图的初始化
    * */
    private void init() {

        for (int i = 0; i < drawable.length; i++) {
            //我也不想这样
            Drawable draw = (getResources().getDrawable(drawable[i]));
            draw.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
            findViewById(icon[i]).setBackground(draw);
        }

        connectNetwork = (TextView) findViewById(R.id.connectNetwork);
        connectNetwork.setTextColor(primaryColor);

        word = (MaterialEditText) findViewById(R.id.word);
        pronunciation = (MaterialEditText) findViewById(R.id.pronunciation);
        definition = (MaterialEditText) findViewById(R.id.definition);
        date = (MaterialEditText) findViewById(R.id.date);
        date.setText(DateFormat.getDateInstance().format(new Date()));

        for (int l : layout) {
            linearLayout.add((LinearLayout) findViewById(l));
        }

        //addto实现
        final LinearLayout add = (LinearLayout) findViewById(R.id.add);
        MaterialEditText addTo = (MaterialEditText) findViewById(R.id.addto);
        addTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add.getVisibility() == View.VISIBLE)
                    add.setVisibility(View.GONE);
                else
                    add.setVisibility(View.VISIBLE);
            }
        });
        PaperButton addToNotice = (PaperButton) findViewById(R.id.addToNotice);
        PaperButton addToWord = (PaperButton) findViewById(R.id.addToWord);
        addToNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultActivity.this, NoteActivity.class);
                intent.putExtra("key", key);
                String St = "";
                switch (kind) {
                    case R.id.action_build:
                        St = getResources().getString(R.string.action_build);
                        break;
                    case R.id.action_abbreviation:
                        St = getResources().getString(R.string.action_abbreviation);
                        break;
                    case R.id.action_words:
                        St = getResources().getString(R.string.action_corpus);
                        break;
                }
                intent.putExtra("kind", St);
                startActivity(intent);
                add.setVisibility(View.GONE);
            }
        });
        addToWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordsVO vo = new WordsVO();
                String St = "";
                switch (kind) {
                    case R.id.action_build:
                        St = getResources().getString(R.string.action_build);
                        break;
                    case R.id.action_abbreviation:
                        St = getResources().getString(R.string.action_abbreviation);
                        break;
                    case R.id.action_words:
                        St = getResources().getString(R.string.action_corpus);
                        break;
                }
                vo.setKind(St);
                vo.setDate(new Date().toLocaleString());
                vo.setKey((word.getText().toString()));
                if (kind == R.id.action_build)
                    vo.setMeaning(pronunciation.getText().toString());
                else
                    vo.setMeaning(definition.getText().toString());
                dateBaseManager.insertWords(vo);
                SnackbarSolution solution = new SnackbarSolution(ResultActivity.this);
                solution.SnackbarSolution(10);
                add.setVisibility(View.GONE);
            }
        });


    }

     /*
    * 这一部分是当有数据Abbrevation时，更新UI内容
    * 内容有点多有点杂
    * Abbrevation start here
    *********************************************************************
    * */


    /*
    *缩略语 */
    private void isAbbrevation() {
        List<AbbreviationVO> abbreviations = dateBaseManager.getAbbreviationDetailByWord(key);
        //如果无法找到改数据,处理方法
        if (abbreviations.size() < 1) {
            final BmobQuery<AbbreviationVO> query = new BmobQuery<>();
            empty.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            connectNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query.addWhereEqualTo("abbreviation", key);
                    query.findObjects(ResultActivity.this, new FindListener<AbbreviationVO>() {
                        @Override
                        public void onSuccess(List<AbbreviationVO> abbreviationVOs) {
                            if (abbreviationVOs.size() < 1) {
                                BmobSolution solution = new BmobSolution(ResultActivity.this, 9022, getApplication());
                                solution.ErrorManager();
                            } else {
                                updateAbbrevationUI(abbreviationVOs);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            BmobSolution solution = new BmobSolution(ResultActivity.this, i, getApplication());
                            solution.ErrorManager();
                        }
                    });
                }
            });
        } else {
            updateAbbrevationUI(abbreviations);
        }
    }

    private void updateAbbrevationUI(List<AbbreviationVO> abbreviations) {
        empty.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);

        //把数据添加到页面中
        word.setText(abbreviations.get(0).getAbbreviation());
        word.setFloatingLabelText("缩略词");
        word.setSingleLine(false);
        if (!TextUtils.isEmpty(abbreviations.get(0).getAbbreviation())) {
            pronunciation.setText(abbreviations.get(0).getPronuciation());
            pronunciation.setSingleLine(false);
        } else
            linearLayout.get(1).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(abbreviations.get(0).getMeaning_zh())) {
            definition.setText(abbreviations.get(0).getMeaning_zh());
            definition.setSingleLine(false);
        } else
            linearLayout.get(2).setVisibility(View.GONE);
    }
/*
* abbrevation end here
* */

    /*
    * 这一部分是当有数据Build时，更新UI内容
    * 内容有点多有点杂
    * build start here
    *********************************************************************
    * */

    /*
* 构词法*/
    private void isBuild() {
        List<BuildVO> builds = dateBaseManager.getBuildDetailByWord(key);
        //如果无法找到改数据,处理方法
        if (builds.size() < 1) {
            final BmobQuery<BuildVO> query = new BmobQuery<>();
            empty.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            connectNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query.addWhereEqualTo("wordpart", key);
                    query.findObjects(ResultActivity.this, new FindListener<BuildVO>() {
                        @Override
                        public void onSuccess(List<BuildVO> buildVOs) {
                            if (buildVOs.size() < 1) {
                                BmobSolution solution = new BmobSolution(ResultActivity.this, 9022, getApplication());
                                solution.ErrorManager();
                            }
                            //Toast.makeText(ResultActivity.this,""+corpusVOs.size(),Toast.LENGTH_LONG).show();
                            else {
                                updateBuildUI(buildVOs);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            BmobSolution solution = new BmobSolution(ResultActivity.this, i, getApplication());
                            solution.ErrorManager();
                        }
                    });
                }
            });
        } else {
            updateBuildUI(builds);
        }
    }


    public void updateBuildUI(List<BuildVO> builds) {
        empty.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);

        word.setText(builds.get(0).getWordpart());
        word.setFloatingLabelText("构词法");

        if (!TextUtils.isEmpty(builds.get(0).getMeaning())) {
            pronunciation.setText(builds.get(0).getMeaning());
            pronunciation.setSingleLine(false);
            pronunciation.setFloatingLabelText("英文释义");
        } else
            linearLayout.get(1).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(builds.get(0).getJson())) {
            definition.setText(builds.get(0).getJson());
            definition.setFloatingLabelText("实例");
            definition.setSingleLine(false);
        } else
            linearLayout.get(2).setVisibility(View.GONE);

    }

    /*
    * build end here
    *********************************************************************
    * */


    /*
    * 这一部分是当有Corpus数据时，更新UI内容
    * 内容有点多有点杂
    * corpus start here
    *********************************************************************
    * */
    /*
    * 单词*/
    public void isWord() {
        //从数据库取值放到layout
        List<CorpusVO> corpus = dateBaseManager.getCorpusDetailByWord(key);
        //如果无法找到改数据,处理方法
        if (corpus.size() < 1) {

            final BmobQuery<CorpusVO> query = new BmobQuery<>();
            empty.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            connectNetwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query.addWhereEqualTo("word", key);
                    query.findObjects(ResultActivity.this, new FindListener<CorpusVO>() {
                        @Override
                        public void onSuccess(List<CorpusVO> corpusVOs) {
                            if (corpusVOs.size() < 1) {
                                BmobSolution solution = new BmobSolution(ResultActivity.this, 9022, getApplication());
                                solution.ErrorManager();
                            }
                            //Toast.makeText(ResultActivity.this,""+corpusVOs.size(),Toast.LENGTH_LONG).show();
                            else {
                                updateCorpusUI(corpusVOs);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            BmobSolution solution = new BmobSolution(ResultActivity.this, i, getApplication());
                            solution.ErrorManager();
                        }
                    });
                }
            });
        } else
            updateCorpusUI(corpus);
    }

    public void updateCorpusUI(List<CorpusVO> corpus) {
        empty.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        //对数据进行整合处理
        current = corpusDataManager.Integration(corpus);
        //数据处理完成后，对数据的有效性等检查，改变UI
        //这是对多义词的处理
        if (current.size() > 1) {
            LinearLayout polysemyLayout = (LinearLayout) findViewById(R.id.polysemyLayout);
            polysemyLayout.setBackgroundColor(primaryColor);
            final ListView polysemyList = (ListView) findViewById(R.id.polysemyList);
            polysemyList.setAdapter(new ArrayAdapter<>(
                    this,
                    R.layout.layout_dictionary_result_item_item,
                    getData()
            ));
            setListViewHeightBasedOnChildren(polysemyList);
            polysemyLayout.setVisibility(View.VISIBLE);
            final ImageView expand = (ImageView) findViewById(R.id.polysemy);
            polysemyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag) {
                        expand.setBackground(getResources().getDrawable(R.drawable.ic_expand_less_white_24dp));
                        polysemyList.setVisibility(View.VISIBLE);
                    } else {
                        expand.setBackground(getResources().getDrawable(R.drawable.ic_expand_more_white_24dp));
                        polysemyList.setVisibility(View.GONE);
                    }
                    flag = !flag;
                }
            });
            polysemyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    polysemyList.setVisibility(View.GONE);
                    flag = !flag;
                    expand.setBackground(getResources().getDrawable(R.drawable.ic_expand_more_white_24dp));
                    enclosure(current.get(position));
                }
            });
        }
        //针对具体每个意思进行处理
        enclosure(current.get(0));
    }

    //对具体的意思封装
    public void enclosure(HashMap<String, String> currentHash) {
        //这么多if else我也是醉了
        word.setText(currentHash.get("word"));
        if (!TextUtils.isEmpty(currentHash.get("pronunciation")))
            pronunciation.setText(currentHash.get("pronunciation").replaceAll("UBUNTU|SSS|OOO|<|>", ""));
        else
            linearLayout.get(1).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(currentHash.get("definition")))
            definition.setText(currentHash.get("definition"));
        else
            linearLayout.get(2).setVisibility(View.GONE);


        linearLayout.get(3).removeAllViews();
        for (int i = 0; ; i++) {
            if (TextUtils.isEmpty(currentHash.get("quotation" + i)))
                break;
            ResultItem item = new ResultItem(this);
            item.setQuoatation(
                    corpusDataManager.getQuotation(currentHash.get("quotation" + i)),
                    corpusDataManager.simplifyQuotation(currentHash.get("quotation" + i), currentHash.get("word"))
            );
            item.setTranslation(
                    currentHash.get("translation" + i),
                    corpusDataManager.simplifyTranslation(currentHash.get("translation" + i),
                            currentHash.get("word"), currentHash.get("quotation" + i))
            );
            linearLayout.get(3).addView(item);
        }
    }

    private List<String> getData() {
        List<String> o = new ArrayList<>();
        for (HashMap<String, String> obj : current) {
            o.add(obj.get("definition"));
            //Log.d("arise", obj.get("definition"));
        }
        return o;
    }


    /*
    * corpus end here
    *********************************************************************
    * */


    private void updateFlexibleSpaceText(final int scrollY) {
        //     ViewHelper.setTranslationY(statusBar, -scrollY);
        int adjustedScrollY = scrollY;
        if (scrollY < 0) {
            adjustedScrollY = 0;
        } else if (mFlexibleSpaceHeight / 2 < scrollY) {
            adjustedScrollY = mFlexibleSpaceHeight / 2;
        }
        float maxScale = (float) (mFlexibleSpaceHeight / 2 - mToolbar.getHeight()) / mToolbar.getHeight();
        float scale = maxScale * ((float) mFlexibleSpaceHeight / 2 - adjustedScrollY) / (mFlexibleSpaceHeight / 2);

        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
//        ViewHelper.setScaleX(mTitleView, 1 );
//        ViewHelper.setScaleY(mTitleView, 1);
        //ViewHelper.setTranslationY(mTitleView, ViewHelper.getTranslationY(statusBar) + statusBar.getHeight() - mTitleView.getHeight() * (1 + scale));
        int maxTitleTranslationY = mTitleView.getHeight() + (mFlexibleSpaceHeight / 2) - (int) (mTitleView.getHeight() * (1 + scale));
        int titleTranslationY = (int) (maxTitleTranslationY * ((float) (mFlexibleSpaceHeight / 2) - adjustedScrollY) / (mFlexibleSpaceHeight / 2));
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }


    /*
    * 动态设置ListView组建的高度
    * 因为scrollerview中再放一个listview会到时listview只能显示一个item,并且滑动有问题
    * */
    public void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}

//actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
