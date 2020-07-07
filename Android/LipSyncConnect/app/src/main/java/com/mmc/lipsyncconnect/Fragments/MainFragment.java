package com.mmc.lipsyncconnect.Fragments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.mmc.lipsyncconnect.R;
import com.mmc.lipsyncconnect.ViewModels.MainViewModel;

/******************************************************************************
 Copyright (c) 2020. MakersMakingChange.com (info@makersmakingchange.com)
 Developed by : Milad Hajihassan (milador)
 ******************************************************************************/

public class MainFragment extends Fragment implements View.OnClickListener {

    private MainViewModel mViewModel;
    private MouseFragment mMouseFragment;
    private GamingFragment mGamingFragment;
    private WirelessFragment mWirelessFragment;
    private MacroFragment mMacroFragment;
    private HelpFragment mHelpFragment;

    private Button mainMouseButton;
    private Button mainGamingButton;
    private Button mainWirelessButton;
    private Button mainMacroButton;
    private Button mainHelpButton;
    private TextView mainChangeTextView;
    private TextView mainStatusTextView;
    private ViewGroup mainFragmentLayout;



    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private final static String MOUSE_FRAGMENT_TAG = MouseFragment.class.getSimpleName();
    private final static String GAMING_FRAGMENT_TAG = GamingFragment.class.getSimpleName();
    private final static String WIRELESS_FRAGMENT_TAG = WirelessFragment.class.getSimpleName();
    private final static String MACRO_FRAGMENT_TAG = MacroFragment.class.getSimpleName();
    private final static String HELP_FRAGMENT_TAG = HelpFragment.class.getSimpleName();

    private MainFragment.OnMainFragmentListener mListener;
    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainFragmentLayout = view.findViewById(R.id.mainFragmentLayout);
        mainChangeTextView = (TextView) view.findViewById(R.id.mainChangeTextView);
        mainStatusTextView = (TextView) view.findViewById(R.id.mainStatusTextView);
        mainMouseButton = (Button) view.findViewById(R.id.mainMouseButton);
        mainGamingButton = (Button) view.findViewById(R.id.mainGamingButton);
        mainWirelessButton = (Button) view.findViewById(R.id.mainWirelessButton);
        mainMacroButton = (Button) view.findViewById(R.id.mainMacroButton);
        mainHelpButton = (Button) view.findViewById(R.id.mainHelpButton);

        mainMouseButton.setOnClickListener((View.OnClickListener) this);
        mainGamingButton.setOnClickListener((View.OnClickListener) this);
        mainWirelessButton.setOnClickListener((View.OnClickListener) this);
        mainMacroButton.setOnClickListener((View.OnClickListener) this);
        mainHelpButton.setOnClickListener((View.OnClickListener) this);
        setActionBarTitle(R.string.main_fragment_title);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }
    protected void setActionBarTitle(int titleStringId) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(titleStringId);
                actionBar.setHomeButtonEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                TextView titleText = new TextView(getActivity());
                RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                titleText.setLayoutParams(layoutparams);
                titleText.setText(titleStringId);
                titleText.setTextColor(Color.WHITE);
                titleText.setGravity(Gravity.CENTER);
                titleText.setTypeface(titleText.getTypeface(), Typeface.BOLD);
                titleText.setTextSize(20);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(titleText);
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
                        setEnabledMainButton(true,mListener.onLipsyncModel());
                    }
                }, minProgressTime - timeDifference);
            }
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(v.getId()){
            case R.id.mainMouseButton:
                mMouseFragment = new MouseFragment();
                fragmentTransaction.replace(R.id.contentFragmentLayout, mMouseFragment,MOUSE_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(MOUSE_FRAGMENT_TAG);
                fragmentTransaction.commit();
                break;
            case R.id.mainGamingButton:
                //throw new RuntimeException("Test Crash");
                mGamingFragment = new GamingFragment();
                fragmentTransaction.replace(R.id.contentFragmentLayout, mGamingFragment,GAMING_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(GAMING_FRAGMENT_TAG);
                fragmentTransaction.commit();
                break;
            case R.id.mainWirelessButton:
                mWirelessFragment = new WirelessFragment();
                fragmentTransaction.replace(R.id.contentFragmentLayout, mWirelessFragment,GAMING_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(WIRELESS_FRAGMENT_TAG);
                fragmentTransaction.commit();
                break;
            case R.id.mainMacroButton:
                mMacroFragment = new MacroFragment();
                fragmentTransaction.replace(R.id.contentFragmentLayout, mMacroFragment,GAMING_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(MACRO_FRAGMENT_TAG);
                fragmentTransaction.commit();
                break;
            case R.id.mainHelpButton:
                mHelpFragment = new HelpFragment();
                fragmentTransaction.replace(R.id.contentFragmentLayout, mHelpFragment,HELP_FRAGMENT_TAG);
                fragmentTransaction.addToBackStack(HELP_FRAGMENT_TAG);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragment.OnMainFragmentListener) {
            mListener = (MainFragment.OnMainFragmentListener) context;
        } else if (getTargetFragment() instanceof MainFragment.OnMainFragmentListener) {
            mListener = (MainFragment.OnMainFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMainFragmentListener");
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
        ViewTreeObserver observer = mainFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //mListener.onSendCommand(getString(R.string.model_send_command));
        if (mListener.onIsArduinoAttached()) {

            mainStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            mainStatusTextView.setText(getString(R.string.default_status_text));
        }

        if (mListener.onIsArduinoOpened()) {
            //setEnabledAllMainButtons(true);
            new AsyncSendCheck().execute(getString(R.string.model_send_command));
        } else {
            //setEnabledAllMainButtons(false);
        }
    }

    public void setEnabledAllMainButtons(boolean bool) {
        mainMouseButton.setEnabled(bool);
        mainGamingButton.setEnabled(bool);
        mainWirelessButton.setEnabled(bool);
        mainMacroButton.setEnabled(bool);
        mainHelpButton.setEnabled(true);
    }

    public void setEnabledMainButton(boolean bool, int button) {
        if(button==1) {
            mainMouseButton.setEnabled(bool);
        } else if(button==2) {
            mainGamingButton.setEnabled(bool);
        } else if(button==3) {
            mainWirelessButton.setEnabled(bool);
        } else if(button==4) {
            mainMacroButton.setEnabled(bool);
        }

    }

    public void setMainChangeText(String text) {
        mainChangeTextView.setText(text);
    }

    public void setMainStatusText(String text) {
        mainStatusTextView.setText(text);
    }

    public interface OnMainFragmentListener {
        void onSendCommand(String command);
        int onLipsyncModel();
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setEnabledAllMainButtons(boolean bool);
        void setEnabledMainButton(boolean bool,int button);
        void setMainChangeText(String text);
        void setMainStatusText(String text);
    }
}
