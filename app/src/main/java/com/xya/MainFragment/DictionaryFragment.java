package com.xya.MainFragment;


import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.xya.LocalApplication.VariableApplication;
import com.xya.MainActivity.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * 辞典
 */
public class DictionaryFragment extends Fragment {


    private final int[] imageView = new int[]{
            R.id.iv1,
            R.id.iv2,
            R.id.iv3,
            R.id.iv4,
            R.id.iv5
    };
    private final int[] drawable = new int[]{
            R.drawable.ic_today_grey600_24dp,
            R.drawable.ic_query_builder_grey600_24dp,
            R.drawable.ic_panorama_fisheye_grey600_24dp,
            R.drawable.ic_room_grey600_24dp,
            R.drawable.ic_subject_grey600_24dp
    };
    private List<String> say;
    private SharedPreferences whatever;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_dictionary_fragment, null);

        //setColorFilter
        for (int i = 0; i < drawable.length; i++) {
            Drawable dw = getResources().getDrawable(drawable[i]);
            dw.setColorFilter(((VariableApplication) this.getActivity().getApplication()).getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
            view.findViewById(imageView[i]).setBackground(dw);
        }
        //处理UI更新......
        getListText();
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        TextView week = (TextView) view.findViewById(R.id.week);
        TextView day = (TextView) view.findViewById(R.id.day);
        TextView month = (TextView) view.findViewById(R.id.month);
        TextView times = (TextView) view.findViewById(R.id.times);
        TextView sub_times = (TextView) view.findViewById(R.id.sub_times);
        TextView says = (TextView) view.findViewById(R.id.says);

        Date date = new Date();
        week.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(date));          //中文星期
        month.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(date));                   //中文月份
        day.setText(new SimpleDateFormat("DD", Locale.getDefault()).format(date));

        Long instance = (new Date()).getTime() - ((VariableApplication) this.getActivity().getApplication()).getDay();
        times.setText(String.valueOf(1 + instance / 86400000));
        times.setTextColor(((VariableApplication) this.getActivity().getApplication()).getPrimaryColor());
        sub_times.setText(String.valueOf(1 + instance / 86400000));                                    //计算使用时间

        says.setText(say.get((int) (instance / 86400000) % say.size()));
//关于随便写点什么的处理
        final MaterialEditText sense = (MaterialEditText) view.findViewById(R.id.sense);
        whatever = getActivity().getSharedPreferences("whatever", 0);
        sense.setText(whatever.getString("sense", ""));
        sense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                whatever.edit().putString("sense", sense.getText().toString()).apply();
            }
        });
    }
//从raw.says里面读取所有格言
    public void getListText() {
        try {
            say = new ArrayList<>();

            InputStream inputStream = getResources().openRawResource(R.raw.says);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String info = "";
            while ((info = bufferedReader.readLine()) != null) {
                say.add(info);
            }
        } catch (Exception ignored) {
        }
    }
}
