package com.mmc.lipsyncconnect.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mmc.lipsyncconnect.R;

public class CalibrationDialog extends Dialog implements View.OnClickListener {
    Context mContext;
    TextView mTitle = null;
    TextView mMessage = null;
    ImageView mImage = null;
    View v = null;
    ProgressBar progressBar;

    public CalibrationDialog(Context context) {
        super(context);
        mContext = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.calibration_dialog);
        v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        mTitle = (TextView) findViewById(R.id.calibrationDialogTitle);
        mMessage = (TextView) findViewById(R.id.calibrationDialogMessage);
        mImage = (ImageView) findViewById(R.id.calibrationDialogImage);

        progressBar = (ProgressBar) findViewById(R.id.calibrationDialogProgress);

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mTitle.setText(mContext.getResources().getString(titleId));
    }

    public void setMessage(CharSequence  message) {
        mMessage.setText(message);
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void setMessage(int messageId) {
        mMessage.setText(mContext.getResources().getString(messageId));
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    public void setImage(int imageId) {
        mImage.setImageResource(imageId);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}