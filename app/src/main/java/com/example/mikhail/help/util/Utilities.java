package com.example.mikhail.help.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.mikhail.help.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class Utilities {

    private static final String TAG = "Utilities";

    private static final int[] mIconIds = {R.drawable.ic_gradient, R.drawable.ic_pillar, R.drawable.ic_video_vintage,
            R.drawable.ic_hills, R.drawable.ic_church, R.drawable.ic_building,
            R.drawable.ic_egg_easter};
    private static final String[] mIconTypes = {"GR", "MN", "PS", "MO", "CH", "EB", "EG"};

    private static final int
            INVALID_EMAIL = 2,
            LENGTH_ERROR = 1;
    private static final int OK = 0;
    private static final int
            MAX_PASSWORD_LENGTH = 32,
            MIN_PASSWORD_LENGTH = 6;

    public static int isEmailCorrect(String email) {
        if (!Utilities.isValidEmail(email)) {
            return INVALID_EMAIL;
        } else {
            return OK;
        }
    }

    public static int getIconId(String type) {
        return mIconIds[Arrays.asList(mIconTypes).indexOf(type)];
    }

    public static int getColorOfString(String s) {
        int r = s.hashCode() % 170 + 50, g = (s.hashCode() * 2) % 170 + 50, b = (s.hashCode() * 3) % 170 + 50;
        return Color.rgb(r, g, b);
    }

    public static Calendar parseDateFromString(String dateStr) {
        String[] dateAndTime = dateStr.split(" ");
        String[] date = dateAndTime[0].split("-");
        String[] time = dateAndTime[1].split(":");
        return new GregorianCalendar(
                Integer.valueOf(date[0]),
                Integer.valueOf(date[1]) - 1,
                Integer.valueOf(date[2]),
                Integer.valueOf(time[0]),
                Integer.valueOf(time[1]),
                Integer.valueOf(time[2]));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int isPasswordCorrect(String password) {
        if (password.length() > MAX_PASSWORD_LENGTH || password.length() < MIN_PASSWORD_LENGTH) {
            return LENGTH_ERROR;
        } else {
            return OK;
        }
    }

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

    public static Bitmap getPlaceImage(String id, String size, Context context){
        String imageFileName = "LOCATION_IMAGE_" + id + "_" + size;
        File storageDir = context.getObbDir();
        String path = storageDir.getAbsolutePath() + "/" + imageFileName;
        if (new File(path).exists()) return BitmapFactory.decodeFile(path);
        return null;
    }

    public static String getPlaceImagePath(String id, String size, Context context){
        String imageFileName = "LOCATION_IMAGE_" + id + "_" + size;
        File storageDir = context.getObbDir();
        String path = storageDir.getAbsolutePath() + "/" + imageFileName;
        if (new File(path).exists()) return path;
        return null;
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

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
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

    public static Bitmap getCircledBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap addBorderToRoundedBitmap(Bitmap srcBitmap, int cornerRadius, int borderWidth, int borderColor) {
        borderWidth = borderWidth * 2;

        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth() + borderWidth,
                srcBitmap.getHeight() + borderWidth,
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(dstBitmap);

        Paint paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        Rect rect = new Rect(
                borderWidth / 2,
                borderWidth / 2,
                dstBitmap.getWidth() - borderWidth / 2,
                dstBitmap.getHeight() - borderWidth / 2
        );

        RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        canvas.drawBitmap(srcBitmap, borderWidth / 2, borderWidth / 2, null);

        srcBitmap.recycle();

        return dstBitmap;
    }

    public static String formatDistance(Float distance, Context context) {
        if (distance < 1000) {
            return String.format("%s %s", Math.round(distance), context.getString(R.string.meters));
        } else {
            distance /= 1000;
            distance = BigDecimal.valueOf(distance).setScale(1, RoundingMode.UP).floatValue();
            return String.format("%s %s", distance, context.getString(R.string.kilometers));
        }
    }

    public static String formatName(String name) {
        StringBuilder formattedName = new StringBuilder();
        try {
            for (String s : name.split("_")) {
                if (s.length() > 1) {
                    formattedName.append(String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1));
                } else {
                    formattedName.append(s.toUpperCase());
                }
            }
            return formattedName.toString();
        } catch (Exception e) {
            Log.d(TAG, "onNickFormatting: formatting error! " + e.toString());
            return name;
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap != null) {
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
