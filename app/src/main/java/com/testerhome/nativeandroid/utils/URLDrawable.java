package com.testerhome.nativeandroid.utils;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by vclub on 15/11/4.
 */
public class URLDrawable extends BitmapDrawable {

    protected Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null){
            drawable.draw(canvas);
        }
    }
}
