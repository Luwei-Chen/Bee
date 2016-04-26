package ie.ucd.www.bee.model;

import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoCaptureModel {
    // photos is for keeping the img file
    // photosDir is to passing the img file to another Activity by Intent
    private ArrayList<File> photos = new ArrayList<File>();
    private ArrayList<String> photosDir = new ArrayList<String>();

    // Make a photo file
    public File createPhotoFile(String userEmail, Location location) throws IOException {
        // Create a individual filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String picFileName = userEmail + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                picFileName,
                ".jpg",
                storageDir
        );

        // In case the exif is null
        String imageFullName = image.getAbsolutePath();
        ExifInterface exif = new ExifInterface(imageFullName);
        if ((exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE) == null) && (location != null)) {
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, Double.toString(location.getAltitude()));
        }
        if ((exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) == null) && (location != null)) {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(location.getLatitude()));
        }
        return image;
    }

    public int photoTakedNum() {
        return photos.size();
    }

    public File getPhoto(int i) {
        return photos.get(i);
    }

    public List<String> getPhotosDir() {
        return photosDir;
    }

    public void addPhoto(File photo) {
        photos.add(photo);
        photosDir.add("file:" + photo.getAbsolutePath());
    }

    // Add the location infomation
    public File addLocationInfo(File photo) {
        return photo;
    }
}
