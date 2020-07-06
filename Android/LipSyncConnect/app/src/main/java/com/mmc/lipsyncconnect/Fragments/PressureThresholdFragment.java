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
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mmc.lipsyncconnect.R;

public class PressureThresholdFragment extends Fragment {

    private MainFragment mMainFragment;

    private Button pressureThresholdSetButton;
    private Button pressureThresholdIncButton;
    private Button pressureThresholdDecButton;
    private TextView pressureThresholdValueTextView;
    private TextView pressureThresholdPuffValueTextView;
    private TextView pressureThresholdNominalValueTextView;
    private TextView pressureThresholdSipValueTextView;
    private TextView pressureThresholdChangeTextView;
    private TextView pressureThresholdStatusTextView;
    private SeekBar pressureThresholdSeekBar;
    private ViewGroup pressureThresholdFragmentLayout;

    private final static String PRESSURE_THRESHOLD_FRAGMENT_TAG = PressureThresholdFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();


    private final static int THRESHOLD_STEP = 1;

    private PressureThresholdFragment.OnPressureThresholdFragmentListener mListener;
    OnSeekBarChangeListener mSeekBarChangeListener= new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final String stringValue = String.valueOf(progress)+"%";
            pressureThresholdValueTextView.setText(stringValue);
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
            if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.pressureThresholdSetButton) {
                view.setPressed(false);
                String thresholdPercent = String.valueOf(pressureThresholdSeekBar.getProgress());
                new AsyncSendCheck().execute(command+":"+thresholdPercent);
                view.performClick();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.pressureThresholdDecButton) {
                view.setPressed(false);
                onDecPressureThreshold(THRESHOLD_STEP);
                view.performClick();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.pressureThresholdIncButton) {
                view.setPressed(false);
                onIncPressureThreshold(THRESHOLD_STEP);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public PressureThresholdFragment() {
        // Required empty public constructor
    }

    public static PressureThresholdFragment newInstance() {
        PressureThresholdFragment fragment = new PressureThresholdFragment();
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
        View view = inflater.inflate(R.layout.pressure_threshold_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pressureThresholdFragmentLayout = view.findViewById(R.id.pressureThresholdFragmentLayout);
        pressureThresholdChangeTextView = (TextView) view.findViewById(R.id.pressureThresholdChangeTextView);
        pressureThresholdStatusTextView = (TextView) view.findViewById(R.id.pressureThresholdStatusTextView);
        pressureThresholdSeekBar = (SeekBar) view.findViewById(R.id.pressureThresholdSeekBar);
        pressureThresholdValueTextView = (TextView) view.findViewById(R.id.pressureThresholdValueTextView);
        pressureThresholdPuffValueTextView = (TextView) view.findViewById(R.id.pressureThresholdPuffValueTextView);
        pressureThresholdNominalValueTextView = (TextView) view.findViewById(R.id.pressureThresholdNominalValueTextView);
        pressureThresholdSipValueTextView = (TextView) view.findViewById(R.id.pressureThresholdSipValueTextView);
        pressureThresholdSetButton = (Button) view.findViewById(R.id.pressureThresholdSetButton);
        pressureThresholdDecButton = (Button) view.findViewById(R.id.pressureThresholdDecButton);
        pressureThresholdIncButton = (Button) view.findViewById(R.id.pressureThresholdIncButton);
        pressureThresholdSetButton.setOnTouchListener(mButtonTouchListener);
        pressureThresholdDecButton.setOnTouchListener(mButtonTouchListener);
        pressureThresholdIncButton.setOnTouchListener(mButtonTouchListener);
        pressureThresholdSeekBar.setProgress(1);
        pressureThresholdSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        setActionBarTitle(R.string.pressure_threshold_fragment_title);
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

    private class AsyncSendCheck extends AsyncTask<String, Void, String>{

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
        if (context instanceof PressureThresholdFragment.OnPressureThresholdFragmentListener) {
            mListener = (PressureThresholdFragment.OnPressureThresholdFragmentListener) context;
        } else if (getTargetFragment() instanceof PressureThresholdFragment.OnPressureThresholdFragmentListener) {
            mListener = (PressureThresholdFragment.OnPressureThresholdFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPressureThresholdFragmentListener");
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

        ViewTreeObserver observer = pressureThresholdFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pressureThresholdFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        if (mListener.onIsArduinoAttached()) {
            pressureThresholdStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            pressureThresholdStatusTextView.setText(getString(R.string.default_status_text));
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
            new AsyncSendCheck().execute(getString(R.string.pressure_threshold_send_command));
        } else {
        }
    }

    public void onIncPressureThreshold(int step) {
        int value = pressureThresholdSeekBar.getProgress();
        if (value==pressureThresholdSeekBar.getMax()) {
            value=pressureThresholdSeekBar.getMax();
        } else {
            value=value+step;
        }
        pressureThresholdValueTextView.setText(String.valueOf(value)+"%");
        pressureThresholdSeekBar.setProgress(value);
    }

    public void onDecPressureThreshold(int step) {
        int value = pressureThresholdSeekBar.getProgress();
        if (value ==1) {
            value=1;
        } else {
            value=value-step;
        }
        pressureThresholdValueTextView.setText(String.valueOf(value)+"%");
        pressureThresholdSeekBar.setProgress(value);
    }

    public void setPressureThresholdSeekBar(int value) {
        pressureThresholdSeekBar.setProgress(value);
    }

    public void setPressureThresholdValueText(int threshold,double nominal) {
        final double puffTreshold = (nominal + ((threshold * 5.0)/100.0));
        final double sipTreshold = (nominal - ((threshold * 5.0)/100.0));
        pressureThresholdNominalValueTextView.setText(String.format("%.2f", nominal));
        pressureThresholdPuffValueTextView.setText(String.format("%.2f", puffTreshold));
        pressureThresholdSipValueTextView.setText(String.format("%.2f", sipTreshold));
    }

    public void setPressureThresholdChangeText(String text) {
        pressureThresholdChangeTextView.setText(text);
    }

    public void setPressureThresholdStatusText(String text) {
        pressureThresholdStatusTextView.setText(text);
    }

    public interface OnPressureThresholdFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setPressureThresholdValueText(int threshold,double nominal);
        void setPressureThresholdSeekBar(int value);
        void setPressureThresholdChangeText(String text);
        void setPressureThresholdStatusText(String text);
    }
}
