package com.mmc.lipsyncconnect.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mmc.lipsyncconnect.Dialogs.CalibrationDialog;
import com.mmc.lipsyncconnect.R;

public class CalibrationFragment extends Fragment {
    private Button calibrationButton;
    private TextView calibrationChangeTextView;
    private TextView calibrationStatusTextView;
    private ViewGroup calibrationFragmentLayout;
    private ImageView calibrationImageView;
    private CalibrationDialog calibrationDialog;
    private final static String CALIBRATION_FRAGMENT_TAG = CalibrationFragment.class.getSimpleName();

    TextView calibrationProgressDialogTitle;
    ImageView calibrationProgressDialogImage;


    private CalibrationFragment.OnCalibrationFragmentListener mListener;
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                new CalibrationFragment.AsyncCalibration().execute(command);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public CalibrationFragment() {
        // Required empty public constructor
    }

    public static CalibrationFragment newInstance() {
        CalibrationFragment fragment = new CalibrationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calibration_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calibrationFragmentLayout = view.findViewById(R.id.calibrationFragmentLayout);
        calibrationChangeTextView = (TextView) view.findViewById(R.id.calibrationChangeTextView);
        calibrationStatusTextView = (TextView) view.findViewById(R.id.calibrationStatusTextView);
        calibrationImageView = (ImageView) view.findViewById(R.id.calibrationImageView);
        calibrationButton = (Button) view.findViewById(R.id.calibrationButton);
        calibrationButton.setOnTouchListener(mButtonTouchListener);

        calibrationDialog = new CalibrationDialog(getActivity());
        //calibrationProgressDialog.setContentView(R.layout.calibration_dialog);
        //calibrationProgressDialog.setCancelable(false);
        //calibrationProgressDialog.setCanceledOnTouchOutside(false);

        //calibrationProgressDialogTitle = (TextView) calibrationProgressDialog.findViewById(R.id.calibrationDialogTitle);
        //calibrationProgressDialogImage = (ImageView) calibrationProgressDialog.findViewById(R.id.calibrationDialogImage);

        setActionBarTitle(R.string.calibration_fragment_title);
    }

    protected void setActionBarTitle(int titleStringId) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(titleStringId);
                TextView titleText = new TextView(getActivity());
                RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                titleText.setLayoutParams(layoutparams);
                titleText.setText(titleStringId);
                titleText.setTextColor(Color.WHITE);
                titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);
                titleText.setTextSize(20);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(titleText);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private class AsyncSendCheck extends AsyncTask<String, Void, String>
    {

        boolean enableSending = true;
        private ProgressDialog progressDialog;
        private int minProgressTime = Integer.parseInt(getString(R.string.progress_min_time));
        long startTime;
        long endTime;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(),R.style.progressDialogStyle);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            startTime = System.currentTimeMillis();
        }
        @Override
        protected String doInBackground(String... command) {
            String result="";
            try{
                while (enableSending){
                    if(mListener.onIsArduinoSending()){
                        enableSending=true;
                    } else {
                        mListener.onSendCommand(command[0]);
                        enableSending=false;
                        result = "success";
                    }
                }
            }
            catch(Exception e){
                result = "error";
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            endTime = System.currentTimeMillis();
            long timeDifference = endTime - startTime;
            if (timeDifference >= minProgressTime && result.equals("success")){
                progressDialog.dismiss();
            } else {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, minProgressTime - timeDifference);
            }
        }
    }

    private class AsyncCalibration extends AsyncTask<String, Void, String>
    {

        boolean enableSending = true;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            calibrationDialog.show();
        }
        @Override
        protected String doInBackground(String... command) {
            String result="";
            try{
                while (enableSending){
                    if(mListener.onIsArduinoSending()){
                        enableSending=true;
                    } else {
                        mListener.onSendCommand(command[0]);
                        enableSending=false;
                        result = "success";
                    }
                }
            }
            catch(Exception e){
                result = "error";
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CalibrationFragment.OnCalibrationFragmentListener) {
            mListener = (CalibrationFragment.OnCalibrationFragmentListener) context;
        } else if (getTargetFragment() instanceof CalibrationFragment.OnCalibrationFragmentListener) {
            mListener = (CalibrationFragment.OnCalibrationFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCalibrationFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        String exitCommandString = getString(R.string.exit_send_command);
        new CalibrationFragment.AsyncSendCheck().execute(exitCommandString);
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewTreeObserver observer = calibrationFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                calibrationFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //mListener.onSendCommand(getString(R.string.calibration_send_command));
        //onSendCheck(getString(R.string.calibration_send_command));
        if (mListener.onIsArduinoAttached()) {
            calibrationStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            calibrationStatusTextView.setText(getString(R.string.default_status_text));
        }
        if (mListener.onIsArduinoOpened()) {
            new AsyncSendCheck().execute(getString(R.string.calibration_send_command));
        } else {
        }
    }


    public void setCalibrationChangeText(String text) {
        calibrationChangeTextView.setText(text);
    }

    public void setCalibrationImage(String stepNumberString) {
        int stepNumber = 0;

        try {
            stepNumber = Integer.parseInt(stepNumberString);
        } catch(NumberFormatException nfe) {
            // Handle parse error.
        }
        switch(stepNumber) {
            case 0:
                calibrationImageView.setImageResource(R.drawable.calibration_step0);
                //calibrationDialogTitle.setText("Step 0");
                //calibrationDialogImage.setImageResource(R.drawable.calibration_step0);
                calibrationDialog.setTitle(R.string.calibration_zero_set_res_title);
                calibrationDialog.setMessage(R.string.calibration_zero_set_res_message);
                calibrationDialog.setImage(R.drawable.calibration_step0);
                break;
            case 1:
                //calibrationImageView.setImageResource(R.drawable.calibration_step1);
                //calibrationDialogTitle.setText("Step 1");
                //calibrationDialogImage.setImageResource(R.drawable.calibration_step1);
                calibrationDialog.setTitle(R.string.calibration_one_set_res_title);
                calibrationDialog.setMessage(R.string.calibration_one_set_res_message);
                calibrationDialog.setImage(R.drawable.calibration_step1);
                break;
            case 2:
                calibrationImageView.setImageResource(R.drawable.calibration_step2);
                //calibrationDialogTitle.setText("Step 2");
                //calibrationDialogImage.setImageResource(R.drawable.calibration_step2);
                calibrationDialog.setTitle(R.string.calibration_two_set_res_title);
                calibrationDialog.setMessage(R.string.calibration_two_set_res_message);
                calibrationDialog.setImage(R.drawable.calibration_step2);
                break;
            case 3:
                calibrationImageView.setImageResource(R.drawable.calibration_step3);
                //calibrationDialogTitle.setText("Step 3");
                //calibrationDialogImage.setImageResource(R.drawable.calibration_step3);
                calibrationDialog.setTitle(R.string.calibration_three_set_res_title);
                calibrationDialog.setMessage(R.string.calibration_three_set_res_message);
                calibrationDialog.setImage(R.drawable.calibration_step3);
                break;
            case 4:
                calibrationImageView.setImageResource(R.drawable.calibration_step4);
                //calibrationDialogTitle.setText("Step 4");
                //calibrationDialogImage.setImageResource(R.drawable.calibration_step4);
                calibrationDialog.setTitle(R.string.calibration_four_set_res_title);
                calibrationDialog.setMessage(R.string.calibration_four_set_res_message);
                calibrationDialog.setImage(R.drawable.calibration_step4);
                break;
            case 5:
                calibrationImageView.setImageResource(R.drawable.calibration_default);
                calibrationDialog.setTitle(R.string.calibration_default_title);
                calibrationDialog.setMessage(R.string.calibration_default_message);
                calibrationDialog.setImage(R.drawable.calibration_default);
                calibrationDialog.dismiss();
                break;
            default:
                calibrationImageView.setImageResource(R.drawable.calibration_default);
                calibrationDialog.setTitle(R.string.calibration_default_title);
                calibrationDialog.setMessage(R.string.calibration_default_message);
                calibrationDialog.setImage(R.drawable.calibration_default);
                calibrationDialog.dismiss();
                break;
        }
    }

    public void setCalibrationStatusText(String text) {
        calibrationStatusTextView.setText(text);
    }

    public interface OnCalibrationFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setCalibrationChangeText(String text);
        void setCalibrationImage(int number);
        void setCalibrationStatusText(String text);
    }
}
