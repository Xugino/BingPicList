package com.Xugino.BingPicList;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

class LoadingDialog extends Dialog {

    LoadingDialog(Context context){
        super(context);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        ImageView imageView=this.findViewById(R.id.loading_dialog_pic);
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_anim);
        imageView.startAnimation(hyperspaceJumpAnimation);
    }

}
