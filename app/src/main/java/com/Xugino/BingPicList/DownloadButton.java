package com.Xugino.BingPicList;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import android.net.Uri;

public class DownloadButton extends AppCompatButton {
    private Uri uri = null;
    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    private String time = "";
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DownloadButton(Context context) {
        super(context);
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
