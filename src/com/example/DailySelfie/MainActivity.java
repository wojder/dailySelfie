package com.example.DailySelfie;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ListActivity {
    /**
     * Called when the activity is first created.
     */
    //constants
    static final int REQUEST_IMAGE_PHOTO = 1;
    protected final static String DEBUG_TAG = "MainActivity";
    private static final String JPG_PREFIX = "IMG_";
    private static final String JPG_SUFFIX = ".jpg";
    private static final String IMAGE_VISIBILITY_KEY = "imageviewvisibility";
    private static final long INITIAL_DELAY = 2*60*1000L;

    //fields
    String mName, selectedImagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri fileUri;
    private SelfieAdapter selfieAdapter;
    private Bitmap selfieBitmap;
    private ImageView selfieView;
    private SharedPreferences sharedPref;
    private SelfieAdapter mAdapter;
    private final int targetHeight = 100;
    private final int targetWid = 100;
    private int scaleFactor = 1;
    private int height;
    private int outWidth;
    private ListView selfieListView;
    private AlarmManager alarmManager;
    private PendingIntent notificationPendingIntent;
    private Intent notificationReciver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAlarm();

        this.selfieView = (ImageView) this.findViewById(R.id.photoView);
        selfieListView = getListView();
        mAdapter = new SelfieAdapter(getApplicationContext());

        init();

        setListAdapter(mAdapter);

        selfieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent selfieIntent = new Intent();
                    selfieIntent.setAction(Intent.ACTION_VIEW);
                    SelfieItem selfie = (SelfieItem) mAdapter.getItem(position);
                    selfieIntent.setDataAndType(Uri.parse("file://"  + selfie.getText()), "image/*");
                    startActivity(selfieIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        selfieListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final CharSequence[] options = {"Open", "Delete", "Cancel"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.options_title)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (options[which].equals("Open")) {

                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    SelfieItem selfieItem = (SelfieItem) mAdapter.getItem(position);

                                    intent.setDataAndType(Uri.parse("file://" + selfieItem.getText()), "image/*");
                                    startActivity(intent);

                                } else if (options[which].equals("Delete")) {

                                    builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            mAdapter.delete(position);
                                            int size = mAdapter.getCount();

                                            sharedPref = getPreferences(Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.clear();
                                            editor.putInt("size", mAdapter.getCount());

                                            for (int i = 0; i < size; i++) {

                                                SelfieItem selfieItem = (SelfieItem) mAdapter.getItem(i);
                                                editor.putString(i + "", selfieItem.getText());
                                                editor.putString(i + "_Name", selfieItem.getName());

                                            }

                                            editor.commit();

                                        }})
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                } else if (options[position].equals("Cancel")){
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
                return false;
            }
        });
    }

    private void createAlarm() {

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        notificationReciver = new Intent(MainActivity.this, AlarmReceiverNotification.class);
        notificationPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, notificationReciver, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + INITIAL_DELAY, INITIAL_DELAY, notificationPendingIntent);
    }

    private void init() {

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        int size = sharedPref.getInt("size", 0);

        optymalizerChecker(size);

    }

    private void optymalizerChecker(int size) {
        if (size > 0) {

            for (int i =0; i < size; i++) {

                String path = sharedPref.getString(i + "", "");
                String name  = sharedPref.getString(i + "_Name", "");

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);

                decodeHelper(options);

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
        File fileSelfie = File.createTempFile(selfieFileName,JPG_SUFFIX,albumSelfie);
        selectedImagePath = fileSelfie.getAbsolutePath();
        mName = timestamp;
        return fileSelfie;
    }
    private File setUpSelfieFile() throws IOException {

        File setUpselfie = createImageFile();
        selectedImagePath = setUpselfie.getAbsolutePath();

        return setUpselfie;
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
        File setUpselfie = null;

        try {
            setUpselfie = setUpSelfieFile();
            selectedImagePath = setUpselfie.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(setUpselfie));

        } catch (IOException e) {
            e.printStackTrace();
            setUpselfie = null;
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


//decodeFile method

    public void decodeFile() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);

        decodeHelper(options);

        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
        SelfieItem selfieItem = new SelfieItem(bitmap, selectedImagePath, mName);
        mAdapter.add(selfieItem);

        editorHelper();

}

    private void editorHelper() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("size", mAdapter.getCount());
        editor.putString((mAdapter.getCount() - 1) + "", selectedImagePath);
        editor.putString((mAdapter.getCount() - 1) + "_Name", mName);
        editor.commit();
    }

    private void decodeHelper(BitmapFactory.Options options) {
        outWidth = options.outWidth;
        height = options.outHeight;


        if ((targetWid > 0) || (targetHeight > 0)) {
            scaleFactor = Math.min(outWidth / targetWid, height / targetHeight);
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;
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
