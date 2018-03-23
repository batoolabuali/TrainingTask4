package com.example.android.trainingtask4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@SuppressWarnings("unused")
public class ConversationActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int RESULT_VEDIO_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private String mCurrentVideoPath;
    final ArrayList<Message> messages = new ArrayList<Message>();
    MessageAdapter messageAdapter ;

    //    ImageView mImageView = new ImageView(this);
//    //        get LinearLayout
//    final LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
//    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT);
//    parentLayout.addView(mImageview,lp);
//private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //hide the Soft Keyboard after touch any view another editText
        setupUI(findViewById(R.id.parentLayout));

        // Find the toolbar view inside the activity layout
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //      Camera Button
        final ImageView camBtn = (ImageView) findViewById(R.id.cam_btn);
        final ImageView sendButton = (ImageView) findViewById(R.id.send_btn);
        final EditText editText = (EditText) findViewById(R.id.msg_text);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                camBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                camBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editText.getText().toString() == "")
                camBtn.setVisibility(View.VISIBLE);
            }
        });

//       mImageView = findViewById(R.id.mImageView);

//        send message
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = editText.getText().toString();
                if (text != null) {
                    if (text.equalsIgnoreCase("")) {
                        Toast.makeText(ConversationActivity.this, "Make Sure to enter a text!", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    Calendar rightNow = Calendar.getInstance();
//                    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
//                    int minute = rightNow.get(Calendar.MINUTE);
//                    Date currentTime = Calendar.getInstance().getTime();
//                    String ts = tsLong.toString() + " seconds ago, ";
//                    String ts = currentTime.toString();
//                    String ts = String.valueOf(currentHour) + ":" + String.valueOf(minute);
//                    Message message = new Message(text, ts);
//                    message.setmMessageDate(DateUtils.formatDateTime(message.getCreatedAt());
                    Long time = System.currentTimeMillis();
                    String ts = formattingTime(time);
                    Message message = new Message(text, 0 , 0 );
                    messages.add(message);


//                    clear editText
                    editText.setText("");
                    editText.getText().clear();
                    hideSoftKeyboard(ConversationActivity.this);

                }
            }
        });



        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Shoe Dialoge
                final Dialog dialog = new Dialog(ConversationActivity.this, R.style.AppTheme_DarkDialog);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.show();

                dialog.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                Toast toast = Toast.makeText(ConversationActivity.this, "Error, Can't Create Image File", Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(ConversationActivity.this,
                                        getPackageName() + ".fileprovider",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }

                        }
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.take_video).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(ConversationActivity.this, "Wait", Toast.LENGTH_SHORT).show();
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File videoFile = null;
                            try {
                                videoFile = createVideoFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                Toast toast = Toast.makeText(ConversationActivity.this, "Error, Can't Create Video File", Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                            if (videoFile != null) {
                                Uri videoURI = FileProvider.getUriForFile(ConversationActivity.this,
                                        getPackageName() + ".fileprovider",
                                        videoFile);
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                            }


                        }
                        dialog.dismiss();
                    }
                });

            }
        });
        ListView list = (ListView) findViewById(R.id.list);
        messageAdapter = new MessageAdapter(ConversationActivity.this, messages);
        list.setAdapter(messageAdapter);
//        hideSoftKeyboard(this);

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        Tried getExternalStoragePublicDirectory()
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String VideoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File video = File.createTempFile(
                VideoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentVideoPath = video.getAbsolutePath();
//        this.videoUri = Uri.fromFile( video );
//        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
//        mediaScanIntent.setData( this.videoUri );
//        this.sendBroadcast( mediaScanIntent );
        return video;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                //add the function to perform here
                for (Message msg : messages) {
                    if (msg.getMessageStatus() == Message.STATE_WAITING)
                    msg.setMessageStatus(Message.STATE_SEND);
                }
                messageAdapter.notifyDataSetChanged();
                break;
//                return (true);
            case R.id.done_all:
                //add the function to perform here
                for (Message msg : messages) {
                    if (msg.getMessageStatus() == Message.STATE_SEND)
                        msg.setMessageStatus(Message.STATE_READ);
                }
                messageAdapter.notifyDataSetChanged();
                break;
//                return (true);
            case R.id.copy_msg:
                //add the function to perform here
//                return (true);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

            case REQUEST_IMAGE_CAPTURE:
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

//            setPic(mImageView, mCurrentPhotoPath);
//            galleryAddPic();
//            Calendar rightNow = Calendar.getInstance();
//            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
//            int minute = rightNow.get(Calendar.MINUTE);
//            String ts = String.valueOf(currentHour) + ":" + String.valueOf(minute);
//            Message message = new Message("", ts, mCurrentPhotoPath);
//            messages.add(message);
            Long time = System.currentTimeMillis();
            String ts = formattingTime(time);
            Message message = new Message(mCurrentPhotoPath, 1 , 0 );
            messages.add(message);
        }

            case REQUEST_VIDEO_CAPTURE:
                if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//                    Calendar rightNow = Calendar.getInstance();
//                    int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
//                    int minute = rightNow.get(Calendar.MINUTE);
//                    String ts = String.valueOf(currentHour) + ":" + String.valueOf(minute);
//                    Message message = new Message("", ts, "",  mCurrentVideoPath);
//                    messages.add(message);
//                    Uri videoUri = data.getData();
//                    mVideoView.setVideoURI(videoUri);
                    Long time = System.currentTimeMillis();
                    String ts = formattingTime(time);
                    Message message = new Message(mCurrentVideoPath, 2 , 0 );
                    messages.add(message);
                }
        }
    }


    // formatting time message chat
    public static String formattingTime(long time) {
        long now = System.currentTimeMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.FORMAT_SHOW_TIME);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        String timeString = ago + "  " + format.format(time) + " ";
        return timeString;
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }



    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ConversationActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
