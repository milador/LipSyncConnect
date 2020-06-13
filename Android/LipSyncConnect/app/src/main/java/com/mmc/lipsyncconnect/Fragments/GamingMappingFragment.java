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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mmc.lipsyncconnect.R;

public class GamingMappingFragment extends Fragment {
    private Button gamingMappingSetButton;
    private TextView gamingMappingChangeTextView;
    private TextView gamingMappingStatusTextView;
    private ViewGroup gamingMappingFragmentLayout;
    private final static String MAPPING_TAG = GamingMappingFragment.class.getSimpleName();

    private GamingMappingFragment.OnGamingMappingFragmentListener mListener;
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                new AsyncSendCheck().execute(command);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public GamingMappingFragment() {
        // Required empty public constructor
    }

    public static GamingMappingFragment newInstance() {
        GamingMappingFragment fragment = new GamingMappingFragment();
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
        View view = inflater.inflate(R.layout.gaming_mapping_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gamingMappingFragmentLayout = view.findViewById(R.id.gamingMappingFragmentLayout);
        gamingMappingChangeTextView = (TextView) view.findViewById(R.id.gamingMappingChangeTextView);
        gamingMappingStatusTextView = (TextView) view.findViewById(R.id.gamingMappingStatusTextView);
        gamingMappingSetButton = (Button) view.findViewById(R.id.gamingMappingSetButton);
        gamingMappingSetButton.setOnTouchListener(mButtonTouchListener);
        setActionBarTitle(R.string.gaming_mapping_fragment_title);
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
        if (context instanceof GamingMappingFragment.OnGamingMappingFragmentListener) {
            mListener = (GamingMappingFragment.OnGamingMappingFragmentListener) context;
        } else if (getTargetFragment() instanceof GamingMappingFragment.OnGamingMappingFragmentListener) {
            mListener = (GamingMappingFragment.OnGamingMappingFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGamingMappingFragmentListener");
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
    public void onResume() {
        super.onResume();

        ViewTreeObserver observer = gamingMappingFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gamingMappingFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (mListener.onIsArduinoAttached()) {
            gamingMappingStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            gamingMappingStatusTextView.setText(getString(R.string.default_status_text));
        }
        if (mListener.onIsArduinoOpened()) {
            new AsyncSendCheck().execute(getString(R.string.mapping_send_command));
        } else {
        }
    }

    public void setGamingMappingChangeText(String text) {
        gamingMappingChangeTextView.setText(text);
    }

    public void setGamingMappingStatusText(String text) {
        gamingMappingStatusTextView.setText(text);
    }

    public interface OnGamingMappingFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setGamingMappingChangeText(String text);
        void setGamingMappingStatusText(String text);
    }
}
