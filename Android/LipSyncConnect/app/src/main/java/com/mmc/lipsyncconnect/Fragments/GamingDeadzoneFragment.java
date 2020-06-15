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

public class GamingDeadzoneFragment extends Fragment {

    private MainFragment mMainFragment;

    private Button gamingDeadzoneSetButton;
    private Button gamingDeadzoneIncButton;
    private Button gamingDeadzoneDecButton;
    private TextView gamingDeadzoneValueTextView;
    private TextView gamingDeadzoneChangeTextView;
    private TextView gamingDeadzoneStatusTextView;
    private SeekBar gamingDeadzoneSeekBar;
    private ViewGroup gamingDeadzoneFragmentLayout;
    private final static String GAMING_DEADZONE_FRAGMENT_TAG = GamingDeadzoneFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();


    private final static int DEADZONE_STEP = 1;

    private GamingDeadzoneFragment.OnGamingDeadzoneFragmentListener mListener;
    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final String stringValue = String.valueOf(progress);
            gamingDeadzoneValueTextView.setText(stringValue);
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
            if (event.getAction() == MotionEvent.ACTION_UP && id== R.id.gamingDeadzoneSetButton) {
                view.setPressed(false);
                String deadzone = String.valueOf(gamingDeadzoneSeekBar.getProgress());
                new AsyncSendCheck().execute(command+":"+deadzone);
                view.performClick();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.gamingDeadzoneDecButton) {
                view.setPressed(false);
                onDecPressureThreshold(DEADZONE_STEP);
                view.performClick();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP && id==R.id.gamingDeadzoneIncButton) {
                view.setPressed(false);
                onIncPressureThreshold(DEADZONE_STEP);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public GamingDeadzoneFragment() {
        // Required empty public constructor
    }

    public static GamingDeadzoneFragment newInstance() {
        GamingDeadzoneFragment fragment = new GamingDeadzoneFragment();
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
        View view = inflater.inflate(R.layout.gaming_deadzone_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gamingDeadzoneFragmentLayout = view.findViewById(R.id.gamingDeadzoneFragmentLayout);
        gamingDeadzoneChangeTextView = (TextView) view.findViewById(R.id.gamingDeadzoneChangeTextView);
        gamingDeadzoneStatusTextView = (TextView) view.findViewById(R.id.gamingDeadzoneStatusTextView);
        gamingDeadzoneSeekBar = (SeekBar) view.findViewById(R.id.gamingDeadzoneSeekBar);
        gamingDeadzoneValueTextView = (TextView) view.findViewById(R.id.gamingDeadzoneValueTextView);
        gamingDeadzoneSetButton = (Button) view.findViewById(R.id.gamingDeadzoneSetButton);
        gamingDeadzoneDecButton = (Button) view.findViewById(R.id.gamingDeadzoneDecButton);
        gamingDeadzoneIncButton = (Button) view.findViewById(R.id.gamingDeadzoneIncButton);
        gamingDeadzoneSetButton.setOnTouchListener(mButtonTouchListener);
        gamingDeadzoneDecButton.setOnTouchListener(mButtonTouchListener);
        gamingDeadzoneIncButton.setOnTouchListener(mButtonTouchListener);
        gamingDeadzoneSeekBar.setProgress(1);
        gamingDeadzoneSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        setActionBarTitle(R.string.gaming_deadzone_fragment_title);
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
        if (context instanceof GamingDeadzoneFragment.OnGamingDeadzoneFragmentListener) {
            mListener = (GamingDeadzoneFragment.OnGamingDeadzoneFragmentListener) context;
        } else if (getTargetFragment() instanceof GamingDeadzoneFragment.OnGamingDeadzoneFragmentListener) {
            mListener = (GamingDeadzoneFragment.OnGamingDeadzoneFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGamingDeadzoneFragmentListener");
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

        ViewTreeObserver observer = gamingDeadzoneFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gamingDeadzoneFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        if (mListener.onIsArduinoAttached()) {
            gamingDeadzoneStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            gamingDeadzoneStatusTextView.setText(getString(R.string.default_status_text));
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMainFragment = new MainFragment();
            fragmentTransaction.replace(R.id.contentFragmentLayout, mMainFragment,MAIN_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }

        if (mListener.onIsArduinoOpened()) {
            new AsyncSendCheck().execute(getString(R.string.deadzone_send_command));
        } else {
        }
    }

    public void onIncPressureThreshold(int step) {
        int value = gamingDeadzoneSeekBar.getProgress();
        if (value==gamingDeadzoneSeekBar.getMax()) {
            value=gamingDeadzoneSeekBar.getMax();
        } else {
            value=value+step;
        }
        gamingDeadzoneValueTextView.setText(String.valueOf(value));
        gamingDeadzoneSeekBar.setProgress(value);
    }

    public void onDecPressureThreshold(int step) {
        int value = gamingDeadzoneSeekBar.getProgress();
        if (value ==1) {
            value=1;
        } else {
            value=value-step;
        }
        gamingDeadzoneValueTextView.setText(String.valueOf(value));
        gamingDeadzoneSeekBar.setProgress(value);
    }

    public void setGamingDeadzoneSeekBar(int value) {
        gamingDeadzoneSeekBar.setProgress(value);
    }


    public void setGamingDeadzoneChangeText(String text) {
        gamingDeadzoneChangeTextView.setText(text);
    }

    public void setGamingDeadzoneStatusText(String text) {
        gamingDeadzoneStatusTextView.setText(text);
    }

    public interface OnGamingDeadzoneFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setGamingDeadzoneSeekBar(int value);
        void setGamingDeadzoneChangeText(String text);
        void setGamingDeadzoneStatusText(String text);
    }
}
