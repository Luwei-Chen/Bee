package ie.ucd.www.bee;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ie.ucd.www.bee.model.PhotoCaptureModel;

public class CapturePhotoActivity extends AppCompatActivity {
    static final int REQUEST_PHOTO_CAPTURE = 1;
    private String userEmail = "";
    private String currentPhotoDir = "";
    private PhotoCaptureModel photosCaptured = new PhotoCaptureModel();
    private LocationManager locationManager = null;
    private boolean isGpsService = false;
    private boolean isNetworkService = false;
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 0;
    private final long MIN_TIME_CHANGE_FOR_UPDATEW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_photo);

        Intent userIntent = getIntent();
        userEmail = userIntent.getStringExtra("userEmail");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final Intent mainIntent = new Intent(this, MainActivity.class);
        final Intent uploadIntent = new Intent(this, PhotosUploadActivity.class);

        if (!indentifyLocationService()) {
            new AlertDialog.Builder(this).setTitle("Caution").setMessage("Sorry, Location service disabled.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(mainIntent);
                        }
                    }).create().show();
        }

        int photoCount = 0;
        while (photoCount < 3) {
            Location photoLocation = getLocation();
            dispatchTakePhotoIntent(photoLocation);
            storeInGallery();
            photoCount++;
        }

        if (photoCount == 3) {
            new AlertDialog.Builder(this).setTitle("Caution").setMessage("Photos taking complete. Upload?")
                    .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            uploadIntent.putStringArrayListExtra("photos_dir", (ArrayList<String>) photosCaptured.getPhotosDir());
                            startActivity(uploadIntent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(mainIntent);

                        }
                    }).create().show();
        }
    }

    // Start the camera device
    private void dispatchTakePhotoIntent(Location location) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File tempPhoto = null;
            try {
                tempPhoto = photosCaptured.createPhotoFile(userEmail, location);
            } catch (IOException e) {
                return;
            }
            if (tempPhoto != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPhoto));
                startActivityForResult(captureIntent, REQUEST_PHOTO_CAPTURE);
                this.photosCaptured.addPhoto(tempPhoto);
            }
        }
    }

    // Identify the location service status and set the locationManager
    public boolean indentifyLocationService() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGpsService = true;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isNetworkService = true;
        }
        return (isNetworkService || isGpsService);
    }

    // Get the location info
    public Location getLocation() {
        Location photoLocation = null;
        // Check the permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        if (isGpsService) {
            photoLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (isNetworkService) {
            photoLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return photoLocation;
    }

    public void storeInGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        for (int i = 0; i < photosCaptured.photoTakedNum(); i++) {
            File photoToStore = photosCaptured.getPhoto(i);
            Uri contentUri = Uri.fromFile(photoToStore);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
    }
}
