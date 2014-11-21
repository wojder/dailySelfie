package com.example.DailySelfie;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wojder on 20.11.14.
 */
public class ImageHandler implements Camera.PictureCallback {

    private final Context context;

    public ImageHandler (Context context){

        this.context = context;

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();

        if(!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(MainActivity.DEBUG_TAG, "Can't create dir to save image");
            Toast.makeText(context, "Can't create dir to save image", Toast.LENGTH_SHORT).show();

            return;

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture"+date+".jpg";

        String fileName = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(fileName);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(context, "New Photo saved!"+photoFile, Toast.LENGTH_SHORT).show();
        } catch (Exception error) {
            Log.d(MainActivity.DEBUG_TAG, "File" + fileName + "not saved" + error.getMessage());
            Toast.makeText(context, "Photo could not be saved", Toast.LENGTH_SHORT).show();
        }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return new File(sdDir, "Camera API");
    }
}
