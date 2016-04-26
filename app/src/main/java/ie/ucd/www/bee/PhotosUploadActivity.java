package ie.ucd.www.bee;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PhotosUploadActivity extends AppCompatActivity {
    private static final String UPLOAD_URL = "";
    final Intent mainIntent = new Intent(this, MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_upload);

        ArrayList<String> photosDir = getIntent().getStringArrayListExtra("photos_dir");

        // for test
        for (int i = 0; i < photosDir.size(); i++) {
            Log.v("Photo Dir", photosDir.get(i));
        }

        AsyncHttpClient client = new AsyncHttpClient();
        ArrayList<File> photos = new ArrayList<File>();

        for (int i = 0; i < photosDir.size(); i++) {
            File photo = new File(photosDir.get(i));
            photos.add(photo);
        }

        RequestParams upload = new RequestParams();
        upload.put("photos", photos);

        client.post(UPLOAD_URL, upload, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

            }
        })

    }
}
