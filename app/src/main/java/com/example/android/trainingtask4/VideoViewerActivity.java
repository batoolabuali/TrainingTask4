package com.example.android.trainingtask4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewerActivity extends AppCompatActivity {
    String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoPath = getIntent().getStringExtra("videoPath");
        MediaController mcontroller = new MediaController(this);
        mcontroller.setMediaPlayer(videoView);
        videoView.setMediaController(mcontroller);
        videoView.requestFocus();
        videoView.setVideoPath(videoPath);
        videoView.start();
    }
}
