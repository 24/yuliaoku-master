package com.xya.MainFragment.StatisticFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xya.MainActivity.R;

/**
 * ÓïÁÏ¿â£¬È«¼¯
 */
public class CorpusFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_statics_fragment_word, null);
        return view;
    }
}
