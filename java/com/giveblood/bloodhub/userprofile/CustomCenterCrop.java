package com.giveblood.bloodhub.userprofile;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

/**
 * Created by YouCaf Iqbal on 7/4/2016.
 */
public class CustomCenterCrop extends CenterCrop {

    public CustomCenterCrop(BitmapPool bitmapPool) {
        super(bitmapPool);
    }

    public CustomCenterCrop(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if (toTransform.getHeight() > outHeight || toTransform.getWidth() > outWidth) {
            return super.transform(pool, toTransform, outWidth, outHeight);
        } else {
            return toTransform;
        }
    }
}