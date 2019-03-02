package com.example.mikhail.help.util;

import android.support.annotation.Nullable;
import android.text.style.CharacterStyle;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.List;

public class MyAutocompletePrediction implements AutocompletePrediction {

    CharSequence text, describe;

    public MyAutocompletePrediction(CharSequence text, CharSequence describe) {
        this.text = text;
        this.describe = describe;
    }

    @Override
    public CharSequence getFullText(@Nullable CharacterStyle characterStyle) {
        return text;
    }

    @Override
    public CharSequence getPrimaryText(@Nullable CharacterStyle characterStyle) {
        return text;
    }

    @Override
    public CharSequence getSecondaryText(@Nullable CharacterStyle characterStyle) {
        return describe;
    }

    @Nullable
    @Override
    public String getPlaceId() {
        return null;
    }

    @Nullable
    @Override
    public List<Integer> getPlaceTypes() {
        return null;
    }

    @Override
    public AutocompletePrediction freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
