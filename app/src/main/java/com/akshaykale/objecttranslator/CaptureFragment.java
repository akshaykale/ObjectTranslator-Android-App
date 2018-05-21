package com.akshaykale.objecttranslator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.akshaykale.objecttranslator.models.MWord;
import com.akshaykale.objecttranslator.utils.firebase.FireDataManager;
import com.akshaykale.objecttranslator.utils.translate.TranslateCallback;
import com.akshaykale.objecttranslator.utils.translate.TranslateTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.CameraKitEventListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class CaptureFragment extends Fragment implements CameraKitEventListener {

    private static final int PICK_IMAGE = 122;
    private static final int MY_PERMISSIONS_WRITE_RXTERNAL_STORAGE = 12;
    public final String TAG = getClass().getSimpleName();

    private boolean FLASH = false; //off


    @BindView(R.id.camera)
    CameraView cameraView;

    @BindView(R.id.bt_capture_image)
    ImageView btCapture;

    @BindView(R.id.bt_camera_switch)
    RelativeLayout btCameraSwitch;
    private long captureStartTime;

    public static CaptureFragment newInstance() {
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return new CaptureFragment();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b, container, false);
        ButterKnife.bind(this, rootView);

        cameraView.setMethod(CameraKit.Constants.METHOD_STILL);
        cameraView.setCropOutput(true);
        cameraView.setZoom(CameraKit.Constants.ZOOM_PINCH);
        cameraView.addCameraKitListener(this);

        requestWriteStoragePermission();

        new FireDataManager().getKey();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @OnClick(R.id.bt_capture_image)
    public void onCaptureClicked(View view) {
        captureStartTime = System.currentTimeMillis();
        /*cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                long callbackTime = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                CaptureResultHolder.dispose();
                CaptureResultHolder.setImage(bitmap);
                CaptureResultHolder.setNativeCaptureSize(
                        //captureModeRadioGroup.getCheckedRadioButtonId() == R.restaurant_id.modeCaptureStandard ?
                        //        camera.getCaptureSize() : camera.getPreviewSize()
                        cameraView.getPreviewSize()
                );
                CaptureResultHolder.setTimeToCallback(callbackTime - startTime);
                launchPreviewActivity();
            }
        });*/
        cameraView.captureImage();
    }

    @OnClick(R.id.bt_camera_switch)
    public void onCameraSwitchClicked(View view) {
        cameraView.toggleFacing();
    }

    @OnClick(R.id.bt_open_gallery)
    public void openImagePicker() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void requestWriteStoragePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_RXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_RXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Permission required.");
                    alertDialog.setMessage("To save captured Images to gallery, app needs permission to access device storage");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    requestWriteStoragePermission();
                                }
                            });
                    alertDialog.show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                // start picker to get image for cropping and then use the image in cropping activity
                CaptureResultHolder.dispose();
                CaptureResultHolder.setImage(getBitmapFromUri(selectedImageUri));
                launchPreviewActivity();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                CaptureResultHolder.dispose();
                CaptureResultHolder.setImage(getBitmapFromUri(resultUri));
                launchPreviewActivity();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void launchPreviewActivity() {
        Intent intent = new Intent(getActivity(), PreviewActivity.class);
        startActivity(intent);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onEvent(CameraKitEvent cameraKitEvent) {

    }

    @Override
    public void onError(CameraKitError cameraKitError) {

    }

    @Override
    public void onImage(CameraKitImage cameraKitImage) {
        byte[] jpeg = cameraKitImage.getJpeg();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
        long callbackTime = System.currentTimeMillis();

        CaptureResultHolder.dispose();
        CaptureResultHolder.setImage(bitmap);
        CaptureResultHolder.setNativeCaptureSize(cameraView.getCaptureSize());
        CaptureResultHolder.setTimeToCallback(callbackTime - captureStartTime);
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {

    }
}