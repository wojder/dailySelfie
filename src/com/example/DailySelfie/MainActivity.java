package com.example.DailySelfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected final static String DEBUG_TAG = "MainActivity";
    private static String IMAGE_DIRECTORY_NAME = "Selfie";
    private ImageView mImageView;
    private String mCurrentPhotoPath;
    private Bitmap imageBitmap;
    private Camera camera;
    private int cameraId = 0;
    private Uri fileUri;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//
//            Toast.makeText(this, "You do not have camera", Toast.LENGTH_SHORT).show();
//
//        } else {
//
//            cameraId = findFrontFacingCamera();
//
//            if(cameraId < 0){
//                Toast.makeText(this, "No front camera availible", Toast.LENGTH_SHORT).show();
//            } else {
//
//                camera = Camera.open(cameraId);
//
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         dispatchTakePictureIntent();

        return true;
    }

//    private int findFrontFacingCamera(){
//
//        int cameraId = -1;
//
//        int numberOfCameras = Camera.getNumberOfCameras();
//
//        for (int i = 0; i < numberOfCameras; i++) {
//
//            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
//
//            Camera.getCameraInfo(i, info);
//
//            if(info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT){
//
//                Log.d(DEBUG_TAG, "Camera found");
//
//                cameraId = i;
//                break;
//
//            }
//        }
//        return cameraId;
//    }
////
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(takePictureIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check the result is correct capture image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            //if is ok, capture image
            if (resultCode == RESULT_OK){
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // if user canceled capturing, return toast message
                Toast.makeText(getApplicationContext(), "User cancelled image", Toast.LENGTH_SHORT).show();
                //if something wrong with camera, return toast message
            } else {
                Toast.makeText(getApplicationContext(), "Failed to capure image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void previewCapturedImage() {


    }

    private File createImageFile() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timestamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;

    }
//create Uri to store images
    public Uri getOutputMediaFileUri(int type){
        
        return Uri.fromFile(getOutputMediaFile(type));
        
    }
    //
    public static File getOutputMediaFile(int type) {
        //External file location
        File mediaStorageDir = new File(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        //create if dir does not exist and creates
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {

                Log.d(IMAGE_DIRECTORY_NAME, "Failed to create" + IMAGE_DIRECTORY_NAME + " directory");

            }

        }

        String timeStamp = new SimpleDateFormat("yymmddhhmmss", Locale.getDefault()).format(new Date());

        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE) {

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG" + timeStamp + ".jpg");
        } else {return null;}

    return mediaFile;
    }

}
