package happycoding.kenp.profilepage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button selectPhotoButton;
    private Button captureNewPhotoButton;
    private ImageView profileImage;
    private Uri imageUri;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        selectPhotoButton = (Button) findViewById(R.id.selectBtn);
        captureNewPhotoButton = (Button) findViewById(R.id.captureBtn);
        profileImage = (ImageView) findViewById(R.id.profileImage);

        selectPhotoButton.setOnClickListener(this);
        captureNewPhotoButton.setOnClickListener(this);

        readSavedProfileImage(getApplicationInfo().dataDir + "/data.txt");
        if (imagePath != null & !imagePath.equals("")) {
            profileImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.selectBtn) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        } else if (v.getId() == R.id.captureBtn) {
            String filePath = getApplicationInfo().dataDir + "/profileImage.jpg";
            File file = new File(filePath);
            imageUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                profileImage.setImageBitmap(bitmap);
                saveProfileImageUri(uri.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            profileImage.setImageBitmap(bitmap);
            saveProfileImageUri(getApplicationInfo().dataDir + "/profileImage.jpg");
        }
    }

    public void readSavedProfileImage(String filePath){
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                imagePath = "";
                return;
            }

            FileInputStream fIn = new FileInputStream(file);
            int c;
            String temp = "";
            while ((c = fIn.read()) != -1) {
                temp = temp + Character.toString((char)c);
            }
            imagePath = temp;
            fIn.close();
        } catch (Exception e) {
            Log.e("Read file: ", e.getMessage());
        }
    }

    public void saveProfileImageUri(String fileName) {
        try {
            FileOutputStream fOut = new FileOutputStream(new File(getApplicationInfo().dataDir + "/data.txt"));
            fOut.write(fileName.getBytes());
            fOut.close();
        } catch (Exception e) {
            Log.e("Write exception:", e.getMessage());
        }
    }

}
