package net.victium.xelg.notatry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TravelActivity extends AppCompatActivity {
    static final int NUM_ITEMS = 2;
    private String[] tabTitles = new String[]{"перемещение", "подмога"};

    ViewPager mTravelViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        mTravelViewPager = findViewById(R.id.vp_travel);
        mTravelViewPager.setAdapter(
                new TravelPagerAdapter(getSupportFragmentManager())
        );

        TabLayout tabLayout = findViewById(R.id.tl_travel_tabs);
        tabLayout.setupWithViewPager(mTravelViewPager);
    }

    public class TravelPagerAdapter extends FragmentPagerAdapter {
        public TravelPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    public static class PageFragment extends Fragment {
        int mNum;

        static PageFragment newInstance(int num) {
            PageFragment f = new PageFragment();

            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_travel_page, container, false);
            TextView textView = (TextView) v;
            textView.setText("Fragment #" + mNum);
            return v;
        }
    }
}
