package com.Xugino.BingPicList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

public class UpdateManager {

    private Context mContext;
    private String saveFileName= Environment.DIRECTORY_DOWNLOADS+"BingPicList.apk";

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public void checkUpdateInfo(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        },1200);
    }

    private void installApk(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + saveFileName), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }
}
