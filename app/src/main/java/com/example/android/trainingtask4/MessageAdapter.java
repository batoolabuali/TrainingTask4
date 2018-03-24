package com.example.android.trainingtask4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MessageAdapter extends ArrayAdapter<Message> {
    Bitmap thumb;
    private static boolean messageFromMe = true;
    int parentHeight;
    int parentWidth;
//    Button playBtn;

    public MessageAdapter(@NonNull Context context, @NonNull ArrayList<Message> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.message_fromme_item, parent, false);
        }

        final Message currentMessage = getItem(position);
        TextView textViewMessage = (TextView) listItemView.findViewById(R.id.message_text);
        textViewMessage.setText(currentMessage.getMessageText());
        TextView messageTime = (TextView) listItemView.findViewById(R.id.message_time);
        messageTime.setText(formattingTime(currentMessage.getmMessageTime()));
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.img);
        parentHeight = parent.getHeight();
        parentWidth = parent.getWidth();
//        final VideoView videoView = (VideoView) listItemView.findViewById(R.id.mVideoView);
//        playBtn = (Button) listItemView.findViewById(R.id.play_button);
//        Log.v("Path == ", currentMessage.getImagePath());
        if (currentMessage.hasImage()) {
            imageView.setVisibility(View.VISIBLE);
            setPic(imageView, currentMessage.getImagePath());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImage(getContext(),currentMessage.getImagePath());
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }
//String p = currentMessage.getVideoPath();
//        Log.v("Path====", p);
        if (currentMessage.hasVideo()) {
            imageView.setVisibility(View.VISIBLE);
            thumb = ThumbnailUtils.createVideoThumbnail(currentMessage.getVideoPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(thumb, parent.getWidth()/2, parent.getHeight()/2,  ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            imageView.setImageBitmap(thumbBitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openVideo(getContext(), currentMessage.getVideoPath());

                }
            });

//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever .setDataSource(currentMessage.getVideoPath());
//            Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000); //unit in microsecond
//            imageView.setImageBitmap(bmFrame);

//            videoView.setVisibility(View.VISIBLE);
//            setVideo(videoView, currentMessage.getVideoPath());
//            videoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    openVideo(getContext(), currentMessage.getVideoPath());
//                }
//            });

//            playBtn.setVisibility(View.VISIBLE);
//            playBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    videoView.start();
//                }
//            });
            } else {
//            videoView.setVisibility(View.GONE);
        }

        ImageView imageViewMessageStatus = (ImageView) listItemView.findViewById(R.id.msg_status_img);

        switch (currentMessage.getMessageStatus()) {
            case Message.STATE_WAITING:
                imageViewMessageStatus.setImageResource(R.drawable.msg_status_gray_waiting);
                break;

            case Message.STATE_SEND:
                imageViewMessageStatus.setImageResource(R.drawable.msg_status_server_receive);
                break;

            case Message.STATE_READ:
                imageViewMessageStatus.setImageResource(R.drawable.msg_status_client_read);
                break;
        }
        return listItemView;
    }

    private void setPic(ImageView imageView, String photoPath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

     int targetW = parentWidth/2;
        int targetH = parentHeight/2;

        // Determine how much to scale down the image
        int scaleFactor = Math.min( photoW / targetW, photoH / targetH );

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }


    private void setVideo(VideoView videoView, String videoPath) {
//        videoView.setVideoPath(videoPath);
//        videoView.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory()+videoPath));

        //Create MediaController
        videoView.setVideoURI(Uri.parse(videoPath));
//        videoView.requestFocus();
//        videoView.start();
//            playBtn.setVisibility(View.VISIBLE);
//            playBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    videoView.start();
//                }
//            });

//        videoView.start();
    }

    private static void openImage(Context context, String path){
        Intent intent =new Intent(context,ImageViewerActivity.class);
        intent.putExtra("ImagePath", path);
        context.startActivity(intent);
    }

    private static void openVideo(Context context, String path){
        Intent intent =new Intent(context,VideoViewerActivity.class);
        intent.putExtra("videoPath", path);
        context.startActivity(intent);
    }

    // formatting time message chat
    public static String formattingTime(long time) {

        long now = System.currentTimeMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.FORMAT_SHOW_TIME);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        String timeString = ago + "  " + format.format(time) + " ";

        return timeString;
    }



//    private static void openVideo(Context context, String path){
//        Intent intent =new Intent(context,VideoViewerActivity.class);
//        intent.putExtra("videoPath", path);
//        context.startActivity(intent);
//    }
}
