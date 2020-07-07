package com.mmc.lipsyncconnect.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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

/******************************************************************************
 Copyright (c) 2020. MakersMakingChange.com (info@makersmakingchange.com)
 Developed by : Milad Hajihassan (milador)
 ******************************************************************************/

public class WirelessCommunicationModeFragment extends Fragment {

    private MainFragment mMainFragment;

    private Button wirelessCommunicationModeUsbButton;
    private Button wirelessCommunicationModeBtButton;
    private TextView wirelessCommunicationModeChangeTextView;
    private TextView wirelessCommunicationModeStatusTextView;
    private ViewGroup wirelessCommunicationModeFragmentLayout;
    private final static String COMMUNICATION_MODE_TAG = WirelessCommunicationModeFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();


    private WirelessCommunicationModeFragment.OnWirelessCommunicationModeFragmentListener mListener;

    View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String command = (String) view.getTag();
            switch(view.getId()){
                case R.id.wirelessCommunicationModeUsbButton:
                    view.setPressed(false);
                    new WirelessCommunicationModeFragment.AsyncSendCheck().execute(command);
                    break;
                case R.id.wirelessCommunicationModeBtButton:
                    view.setPressed(false);
                    new WirelessCommunicationModeFragment.AsyncSendCheck().execute(command);
                    break;
            }
        }
    };
    /*
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
    */

    public WirelessCommunicationModeFragment() {
        // Required empty public constructor
    }

    public static WirelessCommunicationModeFragment newInstance() {
        WirelessCommunicationModeFragment fragment = new WirelessCommunicationModeFragment();
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
        View view = inflater.inflate(R.layout.wireless_communication_mode_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wirelessCommunicationModeFragmentLayout = view.findViewById(R.id.wirelessCommunicationModeFragmentLayout);
        wirelessCommunicationModeChangeTextView = (TextView) view.findViewById(R.id.wirelessCommunicationModeChangeTextView);
        wirelessCommunicationModeStatusTextView = (TextView) view.findViewById(R.id.wirelessCommunicationModeStatusTextView);
        wirelessCommunicationModeUsbButton = (Button) view.findViewById(R.id.wirelessCommunicationModeUsbButton);
        wirelessCommunicationModeBtButton = (Button) view.findViewById(R.id.wirelessCommunicationModeBtButton);
        //wirelessCommunicationModeUsbButton.setOnTouchListener(mButtonTouchListener);
        //wirelessCommunicationModeBtButton.setOnTouchListener(mButtonTouchListener);

        wirelessCommunicationModeUsbButton.setOnClickListener(mButtonClickListener);
        wirelessCommunicationModeBtButton.setOnClickListener(mButtonClickListener);

        setActionBarTitle(R.string.wireless_communication_mode_fragment_title);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WirelessCommunicationModeFragment.OnWirelessCommunicationModeFragmentListener) {
            mListener = (WirelessCommunicationModeFragment.OnWirelessCommunicationModeFragmentListener) context;
        } else if (getTargetFragment() instanceof WirelessCommunicationModeFragment.OnWirelessCommunicationModeFragmentListener) {
            mListener = (WirelessCommunicationModeFragment.OnWirelessCommunicationModeFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnWirelessCommunicationModeFragmentListener");
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

        ViewTreeObserver observer = wirelessCommunicationModeFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                wirelessCommunicationModeFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (mListener.onIsArduinoAttached()) {
            wirelessCommunicationModeStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            wirelessCommunicationModeStatusTextView.setText(getString(R.string.default_status_text));
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
            new WirelessCommunicationModeFragment.AsyncSendCheck().execute(getString(R.string.communication_mode_send_command));
        } else {
        }
    }

    public void setWirelessCommunicationModeChangeText(String text) {
        wirelessCommunicationModeChangeTextView.setText(text);
    }

    public void setWirelessCommunicationModeStatusText(String text) {
        wirelessCommunicationModeStatusTextView.setText(text);
    }

    public interface OnWirelessCommunicationModeFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setWirelessCommunicationModeChangeText(String text);
        void setWirelessCommunicationModeStatusText(String text);
    }
}
