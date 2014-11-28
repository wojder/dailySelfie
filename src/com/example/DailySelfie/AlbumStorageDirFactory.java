package com.example.DailySelfie;

import java.io.File;

/**
 * Created by wojder on 24.11.14.
 */
abstract class AlbumStorageDirFactory {

    public abstract File getAlbumStorageDir(String albumName);

}
