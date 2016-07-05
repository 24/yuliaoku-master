package com.xya.MainFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xya.DatabaseUtil.LocalDateBaseManager;
import com.xya.LocalApplication.VariableApplication;
import com.xya.MainActivity.ItemActivity;
import com.xya.MainActivity.R;
import com.xya.UserInterface.XListView;
import com.xya.UserInterface.swipe.adapter.BaseSwipeAdapter;
import com.xya.UserInterface.swipe.enums.DragEdge;
import com.xya.UserInterface.swipe.enums.ShowMode;
import com.xya.UserInterface.swipe.widget.ZSwipeItem;
import com.xya.ValueObject.NotesVO;

import java.util.List;

/**
 * 我的笔记fragment
 */

public class NoteFragment extends Fragment {

    private int primaryColor;
    private boolean current = true;
    private LocalDateBaseManager db;
    private List<NotesVO> notes;

    private XListView listView;
    private LinearLayout searchBar;
    // 只是用来模拟异步获取数据
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_note_fragment, null);

        db = new LocalDateBaseManager(getActivity());
        db.openDatabase();
        primaryColor = ((VariableApplication) getActivity().getApplication()).getPrimaryColor();
        notes = db.getNotes();

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
                intent.putExtra("data", notes.get(position - 1));

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
                        if (notes.size() - adapter.getCount() > 5)
                            adapter.setCount(5);
                        else if (notes.size() == adapter.getCount())
                            listView.setPullLoadEnable(false);
                        else
                            adapter.setCount(notes.size() - adapter.getCount());
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

    public void SearchView() {
        if (current)
            searchBar.setVisibility(View.VISIBLE);
        else
            searchBar.setVisibility(View.GONE);
        current = !current;
    }

    public void SortView() {
    }

    private class ListViewAdapter extends BaseSwipeAdapter {

        protected static final String TAG = "ListViewAdapter";

        private Activity context;
        private int size;

        public ListViewAdapter(Activity context) {
            this.context = context;
            if (notes.size() > 15)
                size = 15;
            else
                size = notes.size();
        }

        @Override
        public int getCount() {
            return size;
        }

        public void setCount(int count) {
            if (size + count > notes.size())
                size = notes.size();
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
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe_item;
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
            return context.getLayoutInflater().inflate(R.layout.layout_note_fragment_item,
                    parent, false);
        }

        @Override
        public void fillValues(final int position, View convertView) {

            final ZSwipeItem swipeItem = (ZSwipeItem) convertView.findViewById(R.id.swipe_item);
            ((TextView) convertView.findViewById(R.id.title)).setText(notes.get(position).getKey());
            ((TextView) convertView.findViewById(R.id.kind)).setText(notes.get(position).getKind());
            ((TextView) convertView.findViewById(R.id.time)).setText(notes.get(position).getDate());
            ((TextView) convertView.findViewById(R.id.notes)).setText(notes.get(position).getNote());
            ImageView imageView = ((ImageView) convertView.findViewById(R.id.thumb));
            if (!TextUtils.isEmpty(notes.get(position).getThumb()))
                imageView.setImageDrawable(Drawable.createFromPath(notes.get(position).getThumb()));
            else
                imageView.setBackgroundColor(primaryColor);

            swipeItem.setShowMode(ShowMode.LayDown);
            swipeItem.setDragEdge(DragEdge.Right);
            convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "position" + position, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "size" + size, Toast.LENGTH_SHORT).show();
                    size--;
                    db.delNotes(notes.get(position).getId());
                    notes.remove(position);
                    notifyDataSetChanged();
                    if (position == size)
                        Toast.makeText(getActivity(), "Last one !", Toast.LENGTH_SHORT).show();
                    else
                        swipeItem.close(true);
                }
            });
            convertView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                    // intent.putExtra(Intent.EXTRA_TEXT,"let me try");
                    intent.putExtra(Intent.EXTRA_STREAM, R.drawable.bamboo);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getTag()));
                }
            });
        }
    }

}
