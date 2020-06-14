package com.mmc.lipsyncconnect.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mmc.lipsyncconnect.R;

import static java.lang.Math.max;

public class DebugFragment extends Fragment {
    private Button debugOnButton;
    private Button debugOffButton;
    private Button debugShareButton;
    private ScrollView debugScrollView;
    private TextView debugChangeTextView;
    private TextView debugDataTextView;
    private TextView debugStatusTextView;
    private ViewGroup debugFragmentLayout;

    private final static String DEBUG_TAG = DebugFragment.class.getSimpleName();

    private DebugFragment.OnDebugFragmentListener mListener;
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            final int id= view.getId();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                if (id==R.id.debugShareButton) {
                    new AsyncShare();
                } else {
                    new AsyncSendCheck().execute(command);
                }
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public DebugFragment() {
        // Required empty public constructor
    }

    public static DebugFragment newInstance() {
        DebugFragment fragment = new DebugFragment();
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
        View view = inflater.inflate(R.layout.debug_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        debugFragmentLayout = view.findViewById(R.id.debugFragmentLayout);
        debugChangeTextView = (TextView) view.findViewById(R.id.debugChangeTextView);
        debugStatusTextView = (TextView) view.findViewById(R.id.debugStatusTextView);
        debugDataTextView = (TextView) view.findViewById(R.id.debugDataTextView);
        debugOnButton = (Button) view.findViewById(R.id.debugOnButton);
        debugOffButton = (Button) view.findViewById(R.id.debugOffButton);
        debugShareButton = (Button) view.findViewById(R.id.debugShareButton);
        debugScrollView = (ScrollView) view.findViewById(R.id.debugScrollView);
        debugOnButton.setOnTouchListener(mButtonTouchListener);
        debugOffButton.setOnTouchListener(mButtonTouchListener);
        debugShareButton.setOnTouchListener(mButtonTouchListener);

        setActionBarTitle(R.string.debug_fragment_title);
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

    /*
    private void onShare() {
        String debugData = debugDataTextView.getText().toString();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "LipSync Logs");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, debugData);
        startActivity(Intent.createChooser(sharingIntent, "Share log data via"));
    }*/

    private class AsyncShare extends AsyncTask<Void, Void, String>
    {
        boolean enableSending = true;
        private ProgressDialog progressDialog;
        private int minProgressTime = Integer.parseInt(getString(R.string.progress_min_time));
        long startTime;
        long endTime;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            startTime = System.currentTimeMillis();
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result="";
            try{
                while (enableSending){
                    if(mListener.onIsArduinoSending()){
                        enableSending=true;
                    } else {
                        mListener.onSendCommand(getString(R.string.calibration_send_command));
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
                        String debugData = debugDataTextView.getText().toString();
                        debugData = debugChangeTextView.getText() + "\n" + debugData;
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "LipSync Logs");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, debugData);
                        startActivity(Intent.createChooser(sharingIntent, "Share log data via"));
                    }
                }, minProgressTime - timeDifference);
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
            progressDialog = new ProgressDialog(getActivity());
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DebugFragment.OnDebugFragmentListener) {
            mListener = (DebugFragment.OnDebugFragmentListener) context;
        } else if (getTargetFragment() instanceof DebugFragment.OnDebugFragmentListener) {
            mListener = (DebugFragment.OnDebugFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDebugFragmentListener");
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
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewTreeObserver observer = debugFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                debugFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (mListener.onIsArduinoAttached()) {
            debugStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            debugStatusTextView.setText(getString(R.string.default_status_text));
        }
        if (mListener.onIsArduinoOpened()) {
            new AsyncSendCheck().execute(getString(R.string.debug_send_command));
        } else {
        }
    }

    public void setDebugDataText(String text) {
        if (debugDataTextView.getText().toString().equals("")) {
            debugDataTextView.setText(debugDataTextView.getLineCount()+":"+text+"\n");
        } else {
            debugDataTextView.append(debugDataTextView.getLineCount()+":"+text+"\n");
        }
        debugScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //replace this line to scroll up or down
                debugScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100L);
    }

    public void setDebugChangeText(String text) {
        debugChangeTextView.setText(text);
    }

    public void setDebugStatusText(String text) {
        debugStatusTextView.setText(text);
    }

    public interface OnDebugFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setDebugDataText(String text);
        void setDebugChangeText(String text);
        void setDebugStatusText(String text);
    }
}
