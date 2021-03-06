package com.example.android.trainingtask4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import java.util.Locale;


@SuppressWarnings("unused")
public class ConversationActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    final static ArrayList<Message> messages = new ArrayList<Message>();
    private static final int RESULT_VEDIO_CAPTURE = 1;
    MessageAdapter messageAdapter;
    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;
    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int i) {
                    if (i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (i == AudioManager.AUDIOFOCUS_GAIN) {
                        mMediaPlayer.start();

                    } else if (i == AudioManager.AUDIOFOCUS_LOSS) {
                        releaseMediaPlayer();
                    }
                }
            };
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };
    private String mCurrentPhotoPath;
    private String mCurrentVideoPath;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
                if (editText.getText().toString().equals(""))
                    camBtn.setVisibility(View.VISIBLE);
            }
        });

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
                    Long time = System.currentTimeMillis();
                    String ts = formattingTime(time);
                    Message message = new Message(text, 0, R.raw.send_tone);
                    messages.add(message);
                    releaseMediaPlayer();
                    int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                        mMediaPlayer = MediaPlayer.create(ConversationActivity.this, message.getmAudioResourceId());
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    }
                    messageAdapter.notifyDataSetChanged();

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
        list.setDividerHeight(0);
        list.setDivider(null);
    }

    //Create File which stored locally for the app
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
        return video;
    }

    //    here we add the send, done, read buttons on the ActionBar
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
                for (Message msg : messages) {
                    if (msg.getMessageStatus() == Message.STATE_WAITING)
                        msg.setMessageStatus(Message.STATE_SEND);
                }
                messageAdapter.notifyDataSetChanged();
                break;
            case R.id.done_all:
                for (Message msg : messages) {
                    if (msg.getMessageStatus() == Message.STATE_SEND)
                        msg.setMessageStatus(Message.STATE_READ);
                }
                messageAdapter.notifyDataSetChanged();
                break;
            case R.id.copy_msg:
                if (!messages.isEmpty()) {
                    Message lastMessage = messages.get(messages.size() - 1);
                    lastMessage.setmMessageFrom(1);
                    lastMessage.setMessageAudio(R.raw.recieve_tone);
                    messages.add(lastMessage);
                    releaseMediaPlayer();
                    int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                        mMediaPlayer = MediaPlayer.create(ConversationActivity.this, lastMessage.getmAudioResourceId());
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    }
                    messageAdapter.notifyDataSetChanged();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//What to do after we take a photo
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Long time = System.currentTimeMillis();
                    String ts = formattingTime(time);
                    Message message = new Message(mCurrentPhotoPath, 1, R.raw.send_tone);
                    messages.add(message);
                    releaseMediaPlayer();
                    int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        mMediaPlayer = MediaPlayer.create(ConversationActivity.this, message.getmAudioResourceId());
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    }
                    messageAdapter.notifyDataSetChanged();
                }

                //What to do after we record a video
            case REQUEST_VIDEO_CAPTURE:
                if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
                    Long time = System.currentTimeMillis();
                    String ts = formattingTime(time);
                    Message message = new Message(mCurrentVideoPath, 2, R.raw.send_tone);
                    messages.add(message);
                    releaseMediaPlayer();
                    int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                        mMediaPlayer = MediaPlayer.create(ConversationActivity.this, message.getmAudioResourceId());
                        mMediaPlayer.start();
                        mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    }
                    messageAdapter.notifyDataSetChanged();
                }
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

}
