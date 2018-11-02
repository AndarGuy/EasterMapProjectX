package com.example.mikhail.help.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.widget.TextView;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class Utilities {

    public static final String
            SPC_CHARACTERS = " ",
            OTH_CHARACTERS = "_",
            NUM_CHARACTERS = "1234567890",
            ENG_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
            RUS_CHARACTERS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    private static final String TAG = "Utilities";

    public static final String
            DESCRIPTION_CHARACTERS = SPC_CHARACTERS + RUS_CHARACTERS + OTH_CHARACTERS + NUM_CHARACTERS + ENG_CHARACTERS,
            LOGIN_CHARACTERS = ENG_CHARACTERS + NUM_CHARACTERS + OTH_CHARACTERS,
            PASSWORD_CHARACTERS = ENG_CHARACTERS + NUM_CHARACTERS;

    public static int getPxFromDp(int dp, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    public static String getSHA3(String string) throws Exception {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(string.getBytes());
        return Hex.toHexString(digest);
    }

    public static boolean isAvailableCharacters(String s, String res) {
        for (Character c : s.toCharArray()) {
            if (!res.contains(c.toString())) {
                return true;
            }
        }
        return false;
    }

    public static void setColoredText(TextView textView, String text, Integer color) {
        textView.setTextColor(color);
        textView.setText(text);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
