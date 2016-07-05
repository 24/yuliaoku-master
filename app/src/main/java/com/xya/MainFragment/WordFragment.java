package com.xya.MainFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.LocalApplication.VariableApplication;
import com.xya.MainActivity.ItemActivity;
import com.xya.MainActivity.R;
import com.xya.UserInterface.XListView;
import com.xya.ValueObject.WordsVO;

import java.util.List;

/**
 * 单词本
 */
public class WordFragment extends Fragment {
    private int primaryColor;
    private boolean current = true;
    private LocalDateBaseManager db;
    private List<WordsVO> words;

    private XListView listView;
    private LinearLayout searchBar;
    // 只是用来模拟异步获取数据
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_word_fragment, null);

        db = new LocalDateBaseManager(getActivity());
        db.openDatabase();
        primaryColor = ((VariableApplication) getActivity().getApplication()).getPrimaryColor();
        words = db.getWords();

        listView = (XListView) view.findViewById(R.id.listview);
        searchBar = (LinearLayout) view.findViewById(R.id.search_bar);
        searchBar.setBackgroundColor(primaryColor);
        searchBar.setVisibility(View.GONE);

        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(true);

        handler = new Handler();
        final ListViewAdapter adapter = new ListViewAdapter(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra("data", words.get(position - 1));
                startActivity(intent);
            }
        });
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    // 模拟加载数据，1s之后停止加载
                    @Override
                    public void run() {
                        if (words.size() - adapter.getCount() > 5)
                            adapter.setCount(5);
                        else if (words.size() == adapter.getCount())
                            listView.setPullLoadEnable(false);
                        else
                            adapter.setCount(words.size() - adapter.getCount());
                        adapter.notifyDataSetChanged();
                        onLoad();
                    }
                }, 1000);
            }
        });
        listView.setAdapter(adapter);
        return view;
    }

    private void onLoad() {
        listView.stopRefresh();
        listView.stopLoadMore();
        listView.setRefreshTime("刚刚");
    }

    public void SortView() {
    }

    public void SearchView() {
        if (current)
            searchBar.setVisibility(View.VISIBLE);
        else
            searchBar.setVisibility(View.GONE);
        current = !current;
    }

    private class ListViewAdapter extends BaseAdapter {

        protected static final String TAG = "ListViewAdapter";

        private Activity context;
        private int size;
        private Drawable drawable;

        public ListViewAdapter(Activity context) {
            drawable = getResources().getDrawable(R.drawable.ic_more_vert_grey600_24dp);
            drawable.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
            this.context = context;
            if (words.size() > 15)
                size = 15;
            else
                size = words.size();
        }

        @Override
        public int getCount() {
            return size;
        }

        public void setCount(int count) {
            if (size + count > words.size())
                size = words.size();
            this.size += count;
        }


        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view = context.getLayoutInflater().inflate(R.layout.layout_word_fragment_item, parent, false);

            TextView key = ((TextView) view.findViewById(R.id.key));
            key.setTextColor(primaryColor);
            key.setText(words.get(position).getKey());
            ((TextView) view.findViewById(R.id.date)).setText("最后修改: " + words.get(position).getDate());
            ((TextView) view.findViewById(R.id.meaning)).setText(words.get(position).getMeaning());
            ImageButton menuButton = (ImageButton) view.findViewById(R.id.menu);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu menu = new PopupMenu(getActivity(), v);
                    menu.getMenuInflater().inflate(R.menu.menu_words_menu, menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.action_share) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/*");
                                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                                intent.putExtra(Intent.EXTRA_TEXT, words.get(position).getKey() + "\n" + words.get(position).getMeaning());
                                //  intent.putExtra(Intent.EXTRA_STREAM, R.drawable.bamboo);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Intent.createChooser(intent, null));
                            } else if (id == R.id.action_delete) {
                                db.delWord(words.get(position).getId());
                                words.remove(position);
                                size--;
                                notifyDataSetChanged();

                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });
            menuButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                        v.setBackground(drawable);
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                        v.setBackgroundResource(R.drawable.ic_more_vert_grey600_24dp);
                    return false;
                }
            });
            return view;
        }
    }

}
