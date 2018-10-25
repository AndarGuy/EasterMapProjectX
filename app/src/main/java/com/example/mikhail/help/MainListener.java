package com.example.mikhail.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.mikhail.help.add.AddPlaceActivity;

public class MainListener {

    public MainListener(FloatingActionButton fab, FloatingActionButton fabPlace, FloatingActionButton fabEvent, FloatingActionButton fabText, FrameLayout fabBackGround, FrameLayout fabPlaceSide, FrameLayout fabEventSide, FrameLayout fabTextSide) {
        this.fab = fab;
        this.fabPlace = fabPlace;
        this.fabEvent = fabEvent;
        this.fabText = fabText;
        this.fabBackGround = fabBackGround;
        this.fabPlaceSide = fabPlaceSide;
        this.fabEventSide = fabEventSide;
        this.fabTextSide = fabTextSide;
    }

    private static final String TAG = "MainListener";

    FloatingActionButton fab, fabPlace, fabEvent, fabText;
    FrameLayout fabBackGround, fabPlaceSide, fabEventSide, fabTextSide;

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

    public View.OnClickListener onMiniFabClick(final Activity activity, final Context context, final MapHandler map) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = view.getId();

                try {
                    switch (id) {
                        case R.id.fabPlace:
                            String arg[][] = {{"Latitude", String.valueOf(map.location.getLatitude())}, {"Longitude", String.valueOf(map.location.getLongitude())}};
                            fabMenuClose();
                            openActivity(activity, new AddPlaceActivity(), context, arg);
                            break;
                        case R.id.fabEvent:

                            break;
                        case R.id.fabText:

                            break;

                    }
                } catch (NullPointerException e) {
                    Toast.makeText(context, R.string.can_not_get_position, Toast.LENGTH_SHORT).show();
                    fabMenuClose();
                }
            }
        };
    }

    public void openActivity(Activity from, Activity open, Context context, String arg[][]) {
        Intent intent = new Intent(context, open.getClass());
        if (arg != null) for (int i = 0; i < arg.length; i++) intent.putExtra(arg[i][0], arg[i][1]);
        from.startActivity(intent);
    }

    public View.OnClickListener onFabClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFabMenuOpen) fabMenuOpen();
                else fabMenuClose();

            }
        };
    }

    private void changeFabMenuOpen() {
        isFabMenuOpen = !isFabMenuOpen;
    }

    public void fabMenuClose() {
        if (isFabMenuOpen) {
            fab.animate().rotation(0F);
            fabPlaceSide.animate().translationY(0F);
            fabEventSide.animate().translationY(0F);
            fabTextSide.animate().translationY(0F);
            fabPlace.animate().translationY(0F);
            fabEvent.animate().translationY(0F);
            fabText.animate().translationY(0F);
            fabBackGround.animate().alpha(0F);
            fabPlaceSide.animate().alpha(0F);
            fabEventSide.animate().alpha(0F);
            fabTextSide.animate().alpha(0F);
            fabBackGround.setClickable(false);
            changeFabMenuOpen();
        }
    }

    public void fabMenuOpen() {
        if (!isFabMenuOpen) {
            fab.animate().rotation(45F);
            fabPlaceSide.animate().translationY(-550F);
            fabPlace.animate().translationY(-550F);
            fabEventSide.animate().translationY(-375F);
            fabEvent.animate().translationY(-375F);
            fabTextSide.animate().translationY(-200F);
            fabText.animate().translationY(-200F);
            fabPlaceSide.animate().alpha(1F);
            fabEventSide.animate().alpha(1F);
            fabTextSide.animate().alpha(1F);
            fabBackGround.animate().alpha(0.5F);
            fabBackGround.setClickable(false);
            fabBackGround.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabMenuClose();
                }
            });
            changeFabMenuOpen();
        }
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
