package com.example.mikhail.help.tutorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mikhail.help.AuthorizationActivity;
import com.example.mikhail.help.R;
import com.example.mikhail.help.util.Utilities;

public class TutorialActivity extends FragmentActivity {

    static final int NUM_ITEMS = 5;
    private static final String IS_FIRST_RUNNING = "ISFIRSTRUNNING";

    TutorialAdapter mAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mAdapter = new TutorialAdapter(getSupportFragmentManager());

        mPager = findViewById(R.id.viewPager);
        mPager.setAdapter(mAdapter);

    }


    public static class TutorialAdapter extends FragmentPagerAdapter {
        TutorialAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return FirstPageFragment.newInstance();
                case 1:
                    return SecondPageFragment.newInstance();
                case 2:
                    return ThirdPageFragment.newInstance();
                case 3:
                    return FourthPageFragment.newInstance();
                case 4:
                    return AuthPageFragment.newInstance();
                default:
                    return null;
            }
        }
    }

    public static class FourthPageFragment extends Fragment {

        public static FourthPageFragment newInstance() {
            FourthPageFragment f = new FourthPageFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.tutorial_card, container, false);

            TextView header = v.findViewById(R.id.header);
            header.setText(R.string.have_a_friend);

            FrameLayout body = v.findViewById(R.id.body);

            ImageView tap = new ImageView(getContext());
            tap.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
            tap.setContentDescription(getString(R.string.have_a_friend));
            tap.setImageResource(R.drawable.party_chat);

            body.addView(tap);

            return v;
        }
    }

    public static class AuthPageFragment extends Fragment {

        public static AuthPageFragment newInstance() {
            AuthPageFragment f = new AuthPageFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.log_reg, container, false);

            Button enter = v.findViewById(R.id.cont);

            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    preferences.edit().putBoolean(IS_FIRST_RUNNING, false).apply();
                    Intent intent = new Intent(getContext(), AuthorizationActivity.class);
                    startActivity(intent);
                }
            });

            return v;
        }
    }

    public static class ThirdPageFragment extends Fragment {

        public static ThirdPageFragment newInstance() {
            ThirdPageFragment f = new ThirdPageFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.tutorial_card, container, false);

            TextView header = v.findViewById(R.id.header);
            header.setText(R.string.be_a_part_of_event);

            FrameLayout body = v.findViewById(R.id.body);

            ImageView tap = new ImageView(getContext());
            tap.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
            tap.setContentDescription(getString(R.string.be_a_part_of_event));
            tap.setImageResource(R.drawable.party_event);

            body.addView(tap);

            return v;
        }
    }


    public static class SecondPageFragment extends Fragment {

        public static SecondPageFragment newInstance() {
            SecondPageFragment f = new SecondPageFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.tutorial_card, container, false);

            TextView header = v.findViewById(R.id.header);
            header.setText(R.string.find_interesting_places);

            FrameLayout body = v.findViewById(R.id.body);

            ImageView tap = new ImageView(getContext());
            tap.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
            tap.setContentDescription(getString(R.string.find_interesting_places));
            tap.setImageResource(R.drawable.interesting_place);

            body.addView(tap);

            return v;
        }
    }

    public static class FirstPageFragment extends Fragment {

        ImageView tap;

        public static FirstPageFragment newInstance() {
            FirstPageFragment f = new FirstPageFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.tutorial_card, container, false);

            FrameLayout body = v.findViewById(R.id.body);

            ImageView tap = new ImageView(getContext());
            tap.setLayoutParams(new FrameLayout.LayoutParams(Utilities.getPxFromDp(120, getContext()), Utilities.getPxFromDp(120, getContext()), Gravity.CENTER | Gravity.END));
            tap.setContentDescription(getString(R.string.tap));
            tap.setImageResource(R.drawable.ic_tap);

            body.addView(tap);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            TranslateAnimation animation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, -width + Utilities.getPxFromDp(168, getContext()),
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0
            );

            animation.setDuration(2000);

            animation.setRepeatCount(Animation.INFINITE);

            tap.setAnimation(animation);
            tap.getAnimation().start();

            return v;
        }
    }
}