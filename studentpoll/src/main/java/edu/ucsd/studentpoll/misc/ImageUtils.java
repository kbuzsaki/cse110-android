package edu.ucsd.studentpoll.misc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.google.common.collect.ImmutableMap;

/**
 * Created by kbuzsaki on 5/31/15.
 */
public class ImageUtils {

    public static final int MAX_SIZE = 256;

    private ImageUtils() {

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap resizeIfNecessary(Bitmap bitmap) {
        if(bitmap.getWidth() > MAX_SIZE || bitmap.getHeight() > MAX_SIZE) {
            Rect size = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            size = getScaledDown(size);
            return resizeBitmap(bitmap, size.width(), size.height());
        }
        else {
            return bitmap;
        }
    }

    private static Rect getScaledDown(Rect rect) {
        if(rect.width() > rect.height()) {
            float ratio = ((float)rect.height()) / ((float)rect.width());
            return new Rect(0, 0, MAX_SIZE, (int)(ratio * MAX_SIZE));
        }
        else if(rect.height() > rect.width()) {
            float ratio = ((float)rect.width()) / ((float)rect.height());
            return new Rect(0, 0, (int)(ratio *MAX_SIZE), MAX_SIZE);
        }
        else {
            return new Rect(0, 0, MAX_SIZE, MAX_SIZE);
        }
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }
}
