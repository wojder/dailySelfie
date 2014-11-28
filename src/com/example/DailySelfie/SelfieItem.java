package com.example.DailySelfie;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by wojder on 21.11.14.
 */
public class SelfieItem {
        ImageView mImageView;
        String mText, mName;
        Bitmap mBitmap;


    public SelfieItem(Bitmap bm, String text, String name) {

        this.mBitmap = bm;
        this.mText = text;
        this.mName = name;
        }

        ImageView getImageView() {
        return mImageView;
        }

        Bitmap getBitmap(){
        return mBitmap;
        }

        String getText() {
        return mText;
        }

        String getName() {
        return mName;
        }

}
