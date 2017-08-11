package com.Xugino.BingPicList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EmailDialog extends Dialog {

    private MainActivity.EmailInterface emailInterface;
    Button btn1,btn2;
    EditText title,content;

    public EmailDialog(Context context, MainActivity.EmailInterface emailInterface){
        super(context);
        setContentView(R.layout.dialog_email);
        this.emailInterface=emailInterface;
    }
    public void setDisplay(){
        btn1 = this.findViewById(R.id.dialog_email_btn1);
        btn2 = this.findViewById(R.id.dialog_email_btn2);
        title = this.findViewById(R.id.dialog_email_title);
        content = this.findViewById(R.id.dialog_email_content);
        setCancelable(false);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putCharSequence("title","[BingPic反馈]"+title.getText().toString());
                bundle.putCharSequence("content",content.getText().toString());
                emailInterface.sendEmail(bundle);
                dismiss();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        show();
    }
}
