package com.example.android.trainingtask4;


public class Message {

    public static final int STATE_WAITING = 0;
    public static final int STATE_SEND = 1;
    public static final int STATE_READ = 2;
    public static final int MESSAGE_FROM_ME = 0;
    public static final int MESSAGE_FROM_FRIEND = 1;
    public static final int MESSAGE_TEXT = 0;
    public static final int MESSAGE_IMAGE = 1;
    public static final int MESSAGE_VIDEO = 2;
    private static final int NO_VIDEO_PROVIDED = -1;
    private static final int NO_IMAGE_PROVIDED = -1;
    private int messageStatus;
    private String mMessageText;
    private String mImagePath;
    private String mVideoPath;
    private Long mMessageTime;
    private int mAudioResourceId;
    private int mMessageFrom = MESSAGE_FROM_ME;
    private int hasVideo = NO_VIDEO_PROVIDED;
    private int hasimage = NO_IMAGE_PROVIDED;


    public Message(String content, int type, int audioResourceId) {
        messageStatus = STATE_WAITING;
        mAudioResourceId = audioResourceId;
        if (type == MESSAGE_TEXT) {
            mMessageText = content;
        } else if (type == MESSAGE_IMAGE) {
            mImagePath = content;
            hasimage = 1;
        } else if (type == MESSAGE_VIDEO) {
            mVideoPath = content;
            hasVideo = 1;
        }

        setmMessageTime(System.currentTimeMillis());
    }

    public String getMessageText() {
        return mMessageText;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public boolean hasVideo() {
        return hasVideo != NO_VIDEO_PROVIDED;
    }

    public boolean hasImage() {
        return hasimage != NO_IMAGE_PROVIDED;
    }


    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }


    public Long getmMessageTime() {
        return mMessageTime;
    }

    public void setmMessageTime(Long mMessageTime) {
        this.mMessageTime = mMessageTime;
    }

    public int getmMessageFrom() {
        return mMessageFrom;
    }

    public void setmMessageFrom(int mMessageFrom) {
        this.mMessageFrom = mMessageFrom;
    }

    public int getmAudioResourceId() {
        return mAudioResourceId;
    }

    public void setMessageAudio(int id) {
        mAudioResourceId = id;
    }
}
