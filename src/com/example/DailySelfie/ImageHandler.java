package com.example.DailySelfie;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by wojder on 20.11.14.
 */
public class ImageHandler implements Camera.PictureCallback {


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();

        if(!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(MainActivity.DEBUG_TAG, "Can't create dir to save image");



        }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return new File(sdDir, "Camera API");
    }
}
