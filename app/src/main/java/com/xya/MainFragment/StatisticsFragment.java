package com.xya.MainFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xya.LocalApplication.VariableApplication;
import com.xya.MainActivity.R;
import com.xya.MainFragment.StatisticFragment.AbbreFragment;
import com.xya.MainFragment.StatisticFragment.BuildFragment;
import com.xya.MainFragment.StatisticFragment.CorpusFragment;
import com.xya.MainFragment.StatisticFragment.TotalFragment;
import com.xya.UserInterface.ActionBarView;
import com.xya.UserInterface.MaterialWidget.Widget.PagerSlidingTabStrip;


/**
 *统计fragment
 */

public class StatisticsFragment extends Fragment {
    private int primaryColor;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_statics_fragment, null);

        //初始化主题
        primaryColor = ((VariableApplication) getActivity().getApplication()).getPrimaryColor();

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(new StatisticsAdapter(getActivity().getSupportFragmentManager()));
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        initTabsValue();
        return view;
    }

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        // 底部游标颜色
        mPagerSlidingTabStrip.setIndicatorColor(ActionBarView.darkenColor(primaryColor));
        // tab的分割线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        mPagerSlidingTabStrip.setBackgroundColor(primaryColor);
        // tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        // 游标高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColor(getResources().getColor(R.color.tab_color));
        mPagerSlidingTabStrip.setTextSize((int) getResources().getDimension(R.dimen.fontSize));
    }

    /* ***************FragmentPagerAdapter***************** */
    public class StatisticsAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"总体", "单词", "缩略语", "构词法"};
        private final Fragment[] fragments = {new TotalFragment(), new CorpusFragment(), new AbbreFragment(), new BuildFragment()};

        public StatisticsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

    }
}
