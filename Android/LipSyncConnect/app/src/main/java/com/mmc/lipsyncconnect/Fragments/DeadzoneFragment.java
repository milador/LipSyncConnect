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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mmc.lipsyncconnect.R;

public class DeadzoneFragment extends Fragment {

    private MainFragment mMainFragment;

    private Button deadzoneSetButton;
    private Button deadzoneIncButton;
    private Button deadzoneDecButton;
    private TextView deadzoneValueTextView;
    private TextView deadzoneChangeTextView;
    private TextView deadzoneStatusTextView;
    private SeekBar deadzoneSeekBar;
    private ViewGroup deadzoneFragmentLayout;
    private final static String DEADZONE_FRAGMENT_TAG = DeadzoneFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();


    private final static int DEADZONE_STEP = 1;

    private DeadzoneFragment.OnDeadzoneFragmentListener mListener;
    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final String stringValue = String.valueOf(progress);
            deadzoneValueTextView.setText(stringValue);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            final int id= view.getId();
            if (event.getAction() == MotionEvent.ACTION_UP && id== R.id.deadzoneSetButton) {
                view.setPressed(false);
                String deadzone = String.valueOf(deadzoneSeekBar.getProgress());
                new AsyncSendCheck().execute(command+":"+deadzone);
                view.performClick();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.deadzoneDecButton) {
                view.setPressed(false);
                onDecPressureThreshold(DEADZONE_STEP);
                view.performClick();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.deadzoneIncButton) {
                view.setPressed(false);
                onIncPressureThreshold(DEADZONE_STEP);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public DeadzoneFragment() {
        // Required empty public constructor
    }

    public static DeadzoneFragment newInstance() {
        DeadzoneFragment fragment = new DeadzoneFragment();
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
        View view = inflater.inflate(R.layout.deadzone_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deadzoneFragmentLayout = view.findViewById(R.id.deadzoneFragmentLayout);
        deadzoneChangeTextView = (TextView) view.findViewById(R.id.deadzoneChangeTextView);
        deadzoneStatusTextView = (TextView) view.findViewById(R.id.deadzoneStatusTextView);
        deadzoneSeekBar = (SeekBar) view.findViewById(R.id.deadzoneSeekBar);
        deadzoneValueTextView = (TextView) view.findViewById(R.id.deadzoneValueTextView);
        deadzoneSetButton = (Button) view.findViewById(R.id.deadzoneSetButton);
        deadzoneDecButton = (Button) view.findViewById(R.id.deadzoneDecButton);
        deadzoneIncButton = (Button) view.findViewById(R.id.deadzoneIncButton);
        deadzoneSetButton.setOnTouchListener(mButtonTouchListener);
        deadzoneDecButton.setOnTouchListener(mButtonTouchListener);
        deadzoneIncButton.setOnTouchListener(mButtonTouchListener);
        deadzoneSeekBar.setProgress(1);
        deadzoneSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        setActionBarTitle(R.string.deadzone_fragment_title);
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

    private class AsyncSendCheck extends AsyncTask<String, Void, String> {

        boolean enableSending = true;
        private ProgressDialog progressDialog;
        private int minProgressTime = Integer.parseInt(getString(R.string.progress_min_time));
        long startTime;
        long endTime;
        @Override
        public void onPreExecute(){
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DeadzoneFragment.OnDeadzoneFragmentListener) {
            mListener = (DeadzoneFragment.OnDeadzoneFragmentListener) context;
        } else if (getTargetFragment() instanceof DeadzoneFragment.OnDeadzoneFragmentListener) {
            mListener = (DeadzoneFragment.OnDeadzoneFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDeadzoneFragmentListener");
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

        ViewTreeObserver observer = deadzoneFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                deadzoneFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        if (mListener.onIsArduinoAttached()) {
            deadzoneStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            deadzoneStatusTextView.setText(getString(R.string.default_status_text));
            /*
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMainFragment = new MainFragment();
            fragmentTransaction.replace(R.id.contentFragmentLayout, mMainFragment,MAIN_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
             */
        }

        if (mListener.onIsArduinoOpened()) {
            new AsyncSendCheck().execute(getString(R.string.deadzone_send_command));
        } else {
        }
    }

    public void onIncPressureThreshold(int step) {
        int value = deadzoneSeekBar.getProgress();
        if (value==deadzoneSeekBar.getMax()) {
            value=deadzoneSeekBar.getMax();
        } else {
            value=value+step;
        }
        deadzoneValueTextView.setText(String.valueOf(value));
        deadzoneSeekBar.setProgress(value);
    }

    public void onDecPressureThreshold(int step) {
        int value = deadzoneSeekBar.getProgress();
        if (value ==1) {
            value=1;
        } else {
            value=value-step;
        }
        deadzoneValueTextView.setText(String.valueOf(value));
        deadzoneSeekBar.setProgress(value);
    }

    public void setDeadzoneSeekBar(int value) {
        deadzoneSeekBar.setProgress(value);
    }


    public void setDeadzoneChangeText(String text) {
        deadzoneChangeTextView.setText(text);
    }

    public void setDeadzoneStatusText(String text) {
        deadzoneStatusTextView.setText(text);
    }

    public interface OnDeadzoneFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setDeadzoneSeekBar(int value);
        void setDeadzoneChangeText(String text);
        void setDeadzoneStatusText(String text);
    }
}
