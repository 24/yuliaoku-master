package com.xya.MainActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.LocalApplication.VariableApplication;
import com.xya.UserInterface.ActionBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ubuntu on 14-12-24.
 */
public class SearchActivity extends ActionBarActivity {


    private ImageView statusBar;
    private Toolbar mToolbar;
    private LinearLayout expandedToolbar;
    private VariableApplication variableApplication;
    private EditText autoComplete;
    private ListView autoComplete_list;
    private LocalDateBaseManager db;

//查询种类
    private int kind = R.id.action_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dictionary_search);
        findview();
        initEvent();
    }
    public void findview() {
        statusBar = (ImageView) findViewById(R.id.statusBar);
        //设置搜索activity的toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        expandedToolbar = (LinearLayout) findViewById(R.id.expanded_toolbar);
        autoComplete_list = (ListView) findViewById(R.id.autoComplete_list);
        autoComplete = (EditText) findViewById(R.id.autoComplete);
    }
    public void initEvent(){

        //获取数据库对象
        db = new LocalDateBaseManager(this);
        db.openDatabase();

        mToolbar.setTitle(getResources().getString(R.string.action_search));
        autoComplete.setTextColor(Color.WHITE);
        //设置整体的actionbar
        ActionBarView.initActionbar(this, statusBar, mToolbar);
        variableApplication = (VariableApplication) this.getApplication();
        //  Log.d("variableApplication.getPrimaryColor()", String.valueOf(variableApplication.getPrimaryColor()));
        expandedToolbar.setBackgroundColor((variableApplication.getPrimaryColor()));

        initListener();
    }
    public void initListener() {
        autoComplete_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                注意啦，此处获取对应单词的方式，这个TextView是listView里面的哦！！
                TextView itemText = (TextView) view.findViewById(R.id.textView);
                Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
                Bundle extra = new Bundle();
                extra.putString("key", itemText.getText().toString());
                extra.putInt("kind", kind);
                intent.putExtras(extra);
                startActivity(intent, extra);
            }
        });
//        监听用户输入，查询相关单词
        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                for(CorpusVO vo: corpusVOs){
//                    Log.d("Corpus",vo.getWord());
//                }
                setListView(autoComplete_list, autoComplete.getText().toString().trim());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //监听回车事件
        autoComplete.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && !TextUtils.isEmpty(autoComplete.getText().toString().trim())) {
                    Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
                    Bundle extra = new Bundle();
                    extra.putString("key", autoComplete.getText().toString());
                    extra.putInt("kind", kind);
                    intent.putExtras(extra);
                    startActivity(intent, extra);
                }
                return false;
            }
        });
    }
    //listview的具体实现
//    参数：key  指用户输入
    public void setListView(ListView listView, String key) {
//        查询数据库
        List<String> words = db.getWordByWord(key, kind);
        ArrayList<HashMap<String, String>> auto = new ArrayList<>();

        auto.clear();

        if (words == null) {
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);

            Log.d("Searchactivity", "测试listview是否得到数据");
            for (String corpusVO : words) {
                HashMap<String, String> map = new HashMap<>();
                map.put("imageView", "");
                map.put("word", corpusVO);
                auto.add(map);
            }
//            使用SimpleAdapter
            SimpleAdapter mSchdule = new SimpleAdapter(
                    this,
                    auto,
                    R.layout.layout_dictionary_search_list,
                    new String[]{"word"},
                    new int[]{R.id.textView}
            );
            listView.setAdapter(mSchdule);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            mToolbar.setTitle(getResources().getString(R.string.action_approximately));
            kind = R.id.action_search;
            autoComplete.setText("");
            return true;
        }
        if (id == R.id.action_words) {
            mToolbar.setTitle(getResources().getString(R.string.action_words));
            kind = R.id.action_words;
            autoComplete.setText("");
            return true;
        } else if (id == R.id.action_abbreviation) {
            mToolbar.setTitle(getResources().getString(R.string.action_abbreviation));
            kind = R.id.action_abbreviation;
            autoComplete.setText("");
            return true;
        } else if (id == R.id.action_build) {
            mToolbar.setTitle(getResources().getString(R.string.action_build));
            kind = R.id.action_build;
            autoComplete.setText("");
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
