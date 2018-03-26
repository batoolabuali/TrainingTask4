package com.example.android.trainingtask4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageViewerActivity extends AppCompatActivity {

    LinearLayout parent;
    private String imagePath;
    ImageView imageView;
    int parentWidth, parentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        parent = (LinearLayout) findViewById(R.id.parent);
        parentWidth = parent.getWidth();
        parentHeight = parent.getHeight();

        imagePath = getIntent().getStringExtra("ImagePath");

        imageView=(ImageView) findViewById(R.id.fullimage_view);

        imageView.setImageBitmap(getImageFromPath(imagePath));

    }



    public Bitmap getImageFromPath(String path){

//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/parentWidth, photoH/parentHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);


//        Bitmap bitmap = BitmapFactory.decodeFile(path);

        return bitmap;
    }

}
