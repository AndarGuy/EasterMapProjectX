package com.example.mikhail.help;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;

public class MainListener {

    private static final String TAG = "MainListener";

    boolean isFabMenuOpen = false;

    View.OnClickListener onClickName = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Do something for onClickName
        }
    };

    View.OnClickListener onClickProfileImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Do something for onClickProfileImage
        }
    };

    public View.OnClickListener onMiniFabClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = view.getId();

                switch (id) {
                    case R.id.fabPlace:

                        break;
                    case R.id.fabEvent:

                        break;
                    case R.id.fabText:

                        break;

                }
            }
        };
    }

    public View.OnClickListener onFabClick(final FloatingActionButton fab, final FloatingActionButton fabPlace, final FloatingActionButton fabEvent, final FloatingActionButton fabText, final FrameLayout fabBackGround) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFabMenuOpen) fabMenuOpen(fab, fabPlace, fabEvent, fabText, fabBackGround);
                else fabMenuClose(fab, fabPlace, fabEvent, fabText, fabBackGround);

            }
        };
    }

    private void changeFabMenuOpen() {
        isFabMenuOpen = !isFabMenuOpen;
    }

    private void fabMenuClose(final FloatingActionButton fab, final FloatingActionButton fabPlace, final FloatingActionButton fabEvent, final FloatingActionButton fabText, final FrameLayout fabBackGround) {
        fab.animate().rotation(0F);
        fabPlace.animate().translationY(0F);
        fabEvent.animate().translationY(0F);
        fabText.animate().translationY(0F);
        fabBackGround.animate().alpha(0F);
        fabBackGround.setClickable(false);

        changeFabMenuOpen();
    }

    private void fabMenuOpen(final FloatingActionButton fab, final FloatingActionButton fabPlace, final FloatingActionButton fabEvent, final FloatingActionButton fabText, final FrameLayout fabBackGround) {
        fab.animate().rotation(45F);
        fabPlace.animate().translationY(-550F);
        fabEvent.animate().translationY(-375F);
        fabText.animate().translationY(-200F);
        fabBackGround.animate().alpha(0.5F);
        fabBackGround.setClickable(false);
        fabBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenuClose(fab, fabPlace, fabEvent, fabText, fabBackGround);
            }
        });

        changeFabMenuOpen();
    }


    View.OnClickListener onClickItemDrawerMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int id = view.getId();

            switch (id) {
                case R.id.findsLayout:
                    break;
                case R.id.shopLayout:
                    break;
                case R.id.accountManageLayout:
                    break;
                case R.id.settingsLayout:
                    break;
                case R.id.bugReportLayout:
                    break;
            }
        }
    };

}
