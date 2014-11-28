package com.example.DailySelfie;

import android.os.Environment;

import java.io.File;

/**
 * Created by wojder on 24.11.14.
 */
public class BaseAlbumDirFactory extends AlbumStorageDirFactory {

    private static final String CAMERA_DIR = "/Selfies/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + CAMERA_DIR
                + albumName
        );
    }
}
