package com.example.mikhail.help;

import android.view.View;

public class MainListeners {

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

    View.OnClickListener onClickItemDrawerMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int id = view.getId();

            switch (id) {
                case R.id.finds_layout:
                    break;
                case R.id.shop_layout:
                    break;
                case R.id.account_manage_layout:
                    break;
                case R.id.settings_layout:
                    break;
                case R.id.bug_report_layout:
                    break;
            }
        }
    };

}
