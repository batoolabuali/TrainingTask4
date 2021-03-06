package com.example.android.trainingtask4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MessageAdapter extends ArrayAdapter<Message> {
    Bitmap thumb;
    int parentHeight;
    int parentWidth;
    ImageButton playBtn;
    Message currentMessage;
    Bitmap resized;

    public MessageAdapter(@NonNull Context context, @NonNull ArrayList<Message> objects) {
        super(context, 0, objects);
    }

    // When clicking on image, open the full size in another activity
    private static void openImage(Context context, String path) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra("ImagePath", path);
        context.startActivity(intent);
    }

    // When clicking on play Button on video, play the full size video in another activity
    private static void openVideo(Context context, String path) {
        Intent intent = new Intent(context, VideoViewerActivity.class);
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        currentMessage = getItem(position);
        int from = currentMessage.getmMessageFrom();

        if (listItemView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //        اختبار المرسل، في حال كانت الرسالة ليست منك، نغير الخلفية للون الابيض و تموضع الرسالة على اليسار
            if (from == currentMessage.MESSAGE_FROM_ME) {
                listItemView = inflater.inflate(
                        R.layout.message_fromme_item, parent, false);
            } else if (from == currentMessage.MESSAGE_FROM_FRIEND) {

                listItemView = inflater.inflate(
                        R.layout.message_fromfriend_item, parent, false);
            }

        }

        TextView textViewMessage = (TextView) listItemView.findViewById(R.id.message_text);
        textViewMessage.setText(currentMessage.getMessageText());
        TextView messageTime = (TextView) listItemView.findViewById(R.id.message_time);
        messageTime.setText(formattingTime(currentMessage.getmMessageTime()));

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.img);
        parentHeight = parent.getHeight();
        parentWidth = parent.getWidth();
        playBtn = (ImageButton) listItemView.findViewById(R.id.play_button);
        if (currentMessage.hasImage()) {
            resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentMessage.getImagePath()), parentWidth / 2, parentHeight / 2);
            correctRotation();
            imageView.setImageBitmap(resized);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImage(getContext(), currentMessage.getImagePath());
                }
            });
        }

        if (currentMessage.hasVideo()) {
            playBtn.setVisibility(View.VISIBLE);
            thumb = ThumbnailUtils.createVideoThumbnail(currentMessage.getVideoPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(thumb, parentWidth / 2, parentHeight / 2, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            imageView.setImageBitmap(thumbBitmap);
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openVideo(getContext(), currentMessage.getVideoPath());

                }
            });
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

    //    Solve rotation problem on some devices
    public void correctRotation() {
        try {
            int rotationInDegrees = 0;
            ExifInterface exif = new ExifInterface(currentMessage.getImagePath());
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
                resized = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
