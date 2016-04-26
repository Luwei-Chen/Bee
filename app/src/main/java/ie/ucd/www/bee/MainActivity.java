package ie.ucd.www.bee;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "Alice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void capture(View view) {
        EditText email = (EditText) findViewById(R.id.user_email);
        EditText password = (EditText) findViewById(R.id.user_password);
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (validateUserInfo(userEmail, userPassword)) {
            final Intent captureIntent = new Intent(this, CapturePhotoActivity.class);
            captureIntent.putExtra("userEmail", userEmail);
            new AlertDialog.Builder(this).setTitle("Caution").setMessage("You need to take 10 photos.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(captureIntent);
                        }
                    }).create().show();
        } else {
            new AlertDialog.Builder(this).setTitle("Oops..").setMessage("Email or password error!")
                    .setPositiveButton("Return", null).create().show();
        }
    }

    // Validate the user information
    // Further feature
    public boolean validateUserInfo(String email, String password) {
        if (email.equals("") || password.equals("")) {
            return false;
        } else {
            return true;
        }
    }
}
