package com.xya.MainFragment.StatisticFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xya.MainActivity.R;

/**
 * StatisticFragment包下的4个fragment是在统计fragment里面被使用到的
 * 这是缩略词Fragment
 */
public class AbbreFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_statics_fragment_abbre, null);
        return view;
    }
}
