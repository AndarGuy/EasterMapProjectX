package com.example.mikhail.help.utils;

import android.widget.TextView;

public class Utils {

    public void setColoredText(TextView textView, String text, Integer color) {
        textView.setTextColor(color);
        textView.setText(text);
    }

}
