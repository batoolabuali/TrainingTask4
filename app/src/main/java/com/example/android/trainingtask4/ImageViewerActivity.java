package com.example.android.trainingtask4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

public class ImageViewerActivity extends AppCompatActivity {

    LinearLayout parent;
    ImageView imageView;
    Bitmap img;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        parent = (LinearLayout) findViewById(R.id.parent);
        imagePath = getIntent().getStringExtra("ImagePath");
        imageView = (ImageView) findViewById(R.id.fullimage_view);
        img = getImageFromPath(imagePath);

        correctRotation();
        imageView.setImageBitmap(img);

    }


    public Bitmap getImageFromPath(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return bitmap;
    }

    //    Solve rotation problem on some devices
    public void correctRotation() {
        try {
            int rotationInDegrees = 0;
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationInDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationInDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationInDegrees = 270;
                    break;
            }
            if (rotationInDegrees != 0) {
                Matrix matrix = new Matrix();
                matrix.preRotate(rotationInDegrees);
                img = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
