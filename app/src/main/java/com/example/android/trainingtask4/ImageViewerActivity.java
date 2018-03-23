package com.example.android.trainingtask4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewerActivity extends AppCompatActivity {

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imagePath = getIntent().getStringExtra("ImagePath");

        ImageView imageView=(ImageView) findViewById(R.id.fullimage_view);

        imageView.setImageBitmap(getImageFromPath(imagePath));

    }



    public static Bitmap getImageFromPath(String path){

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        return bitmap;
    }

}
