package ca.bcit.heimdallr.ending_violence;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity_v2 extends AppCompatActivity {
    private Camera myCamera;
    private CameraPreview myPreview;
    private Button capture;
    private Context cameraCtx;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private Uri fileUri; // file url to store image/video

    private ImageView imgPreview;
    private VideoView videoPreview;
    private Button btnCapturePicture, btnRecordVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_activity_v2);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        videoPreview = (VideoView) findViewById(R.id.videoPreview);
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);

        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                recordVideo();
            }
        });

    }


    private void previewCapturedImage() {
        try {
            // hide video preview
            videoPreview.setVisibility(View.GONE);

            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory

            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent,CAMERA_CAPTURE_VIDEO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            // code to check capture image response
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * Previewing recorded video
     */
    private void previewVideo() {
        try {
            // hide image preview
            imgPreview.setVisibility(View.GONE);

            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}
