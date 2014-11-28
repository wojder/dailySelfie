package com.example.DailySelfie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    //constants
    protected final static String DEBUG_TAG = "MainActivity";
    static final int REQUEST_IMAGE_PHOTO = 1;
    private static final String JPG_PREFIX = "IMG_";
    private static final String JPG_SUFFIX = ".jpg";
    private static final String IMAGE_VISIBILITY_KEY = "imageviewvisibility";
    private static String IMAGE_DIRECTORY_NAME = "Selfie";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    

    //fields
    private Uri fileUri;
    private SelfieAdapter selfieAdapter;
    private Bitmap selfieBitmap;
    private ImageView selfieView;
    private AlbumStorageDirFactory albumStorageDirFactory = null;
    String selectedImagePath;
    private Bitmap bitmap;
    private Intent data;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mName, currentSelfiePath;
    private SharedPreferences sharedPref;
    private SelfieAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        this.selfieView = (ImageView) this.findViewById(R.id.photoView);

        init();
    }

    private void init() {

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int size = sharedPref.getInt("size", 0);

        if (size >0) {

            for (int i =0; i < size; i++) {

                String path = sharedPref.getString(i + "", "");
                String name  = sharedPref.getString(i + "_Name", "");

                int targetWid = 100;
                int targetHeight = 100;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int selfieWid = options.outWidth;
                int selfieHeight = options.outHeight;

                int scaleFactor = 1;
                if ((targetWid > 0 || targetHeight > 0 )) {

                    scaleFactor = Math.min(selfieWid/targetWid, selfieHeight/targetHeight);

                }

                options.inJustDecodeBounds = false;
                options.inSampleSize = scaleFactor;
                options.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                SelfieItem item = new SelfieItem(bitmap, path, name);
                mAdapter.add(item);

            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);

        return true;
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String selfieFileName = JPG_PREFIX + timestamp + "_";
        File albumSelfie = getAlbumDir();
        File fileSelfie = File.createTempFile(selfieFileName, JPG_SUFFIX, albumSelfie);
        selectedImagePath = fileSelfie.getAbsolutePath();
        mName = timestamp;
        return fileSelfie;
    }
    private File setUpSelfieFile() throws IOException {

        File f = createImageFile();
        selectedImagePath = f.getAbsolutePath();

        return f;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.camera_icon:

                dispatchTakePictureIntent();

                return true;
            default:
                return false;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = setUpSelfieFile();
            selectedImagePath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            selectedImagePath = null;
        }

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //check the result is correct capture image
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleSmallSelfie(data);
        }

        super.onActivityResult(requestCode, resultCode, data );
    }

    private void handleSmallSelfie(Intent intent) {

        if (selectedImagePath != null) {

            decodeFile();
            galleryAddPic();
            selectedImagePath =null;
        }

    }
//getPath method

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

//decodeFile method

    public void decodeFile() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);

        int selfieWid = options.outWidth;
        int selfieHeight = options.outHeight;

        int scaleFactor = 1;
        int targetWid = 100;
        int targetHeight = 100;
        if ((targetWid > 0) || (targetHeight > 0)) {
            scaleFactor = Math.min(selfieWid/targetWid, selfieHeight/targetHeight);
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        SelfieItem i = new SelfieItem(bitmap, selectedImagePath, mName);
        mAdapter.add(i);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("size", mAdapter.getCount());
        editor.putString((mAdapter.getCount() - 1) + "", selectedImagePath);
        editor.putString((mAdapter.getCount() - 1) + "_Name", mName);
        editor.commit();

}
    private File getAlbumDir() {

        File selfieDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return selfieDir;
    }

    private void galleryAddPic(){

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(selectedImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(IMAGE_VISIBILITY_KEY, (selfieBitmap !=null));
        outState.putParcelable("file_uri", fileUri);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fileUri = savedInstanceState.getParcelable("file_uri");
    }
}
