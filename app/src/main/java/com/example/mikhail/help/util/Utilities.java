package com.example.mikhail.help.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class Utilities {

    private static final String TAG = "Utilities";

    public static int getPxFromDp(int dp, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    public static boolean isAvailableCharacters(String s, String res) {
        for (Character c : s.toCharArray()) {
            if (!res.contains(c.toString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void setColoredText(TextView textView, String text, Integer color) {
        textView.setTextColor(color);
        textView.setText(text);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        drawable = (DrawableCompat.wrap(drawable)).mutate();

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap resizeBitMapImage(String filePath, int targetWidth,
                                           int targetHeight) {
        Bitmap bitMapImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        double sampleSize = 0;
        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
                .abs(options.outWidth - targetWidth);

        if (options.outHeight * options.outWidth * 2 >= 1638) {
            sampleSize = scaleByHeight ? options.outHeight / targetHeight
                    : options.outWidth / targetWidth;
            sampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
        }

        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) sampleSize;
                bitMapImage = BitmapFactory.decodeFile(filePath, options);

                break;
            } catch (Exception ex) {
                try {
                    sampleSize = sampleSize * 2;
                } catch (Exception ex1) {

                }
            }
        }

        return bitMapImage;
    }

}
