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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mmc.lipsyncconnect.R;

public class MacroBluetoothConfigFragment extends Fragment {

    private MainFragment mMainFragment;

    private Button macroBluetoothConfigButton;
    private TextView macroBluetoothConfigChangeTextView;
    private TextView macroBluetoothConfigStatusTextView;
    private ViewGroup macroBluetoothConfigFragmentLayout;
    private final static String MACRO_BLUETOOTH_CONFIG_FRAGMENT_TAG = MacroBluetoothConfigFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();

    private MacroBluetoothConfigFragment.OnMacroBluetoothConfigFragmentListener mListener;
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                new MacroBluetoothConfigFragment.AsyncSendCheck().execute(command);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public MacroBluetoothConfigFragment() {
        // Required empty public constructor
    }

    public static MacroBluetoothConfigFragment newInstance() {
        MacroBluetoothConfigFragment fragment = new MacroBluetoothConfigFragment();
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
        View view = inflater.inflate(R.layout.wireless_bluetooth_config_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        macroBluetoothConfigFragmentLayout = view.findViewById(R.id.macroBluetoothConfigFragmentLayout);
        macroBluetoothConfigChangeTextView = (TextView) view.findViewById(R.id.macroBluetoothConfigChangeTextView);
        macroBluetoothConfigStatusTextView = (TextView) view.findViewById(R.id.macroBluetoothConfigStatusTextView);
        macroBluetoothConfigButton = (Button) view.findViewById(R.id.macroBluetoothConfigButton);
        macroBluetoothConfigButton.setOnTouchListener(mButtonTouchListener);
        setActionBarTitle(R.string.wireless_bluetooth_config_fragment_title);
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
        if (context instanceof MacroBluetoothConfigFragment.OnMacroBluetoothConfigFragmentListener) {
            mListener = (MacroBluetoothConfigFragment.OnMacroBluetoothConfigFragmentListener) context;
        } else if (getTargetFragment() instanceof MacroBluetoothConfigFragment.OnMacroBluetoothConfigFragmentListener) {
            mListener = (MacroBluetoothConfigFragment.OnMacroBluetoothConfigFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMacroBluetoothConfigFragmentListener");
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

        ViewTreeObserver observer = macroBluetoothConfigFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                macroBluetoothConfigFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        if (mListener.onIsArduinoAttached()) {
            macroBluetoothConfigStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            macroBluetoothConfigStatusTextView.setText(getString(R.string.default_status_text));
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMainFragment = new MainFragment();
            fragmentTransaction.replace(R.id.contentFragmentLayout, mMainFragment,MAIN_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        if (mListener.onIsArduinoOpened()) {
            new MacroBluetoothConfigFragment.AsyncSendCheck().execute(getString(R.string.bluetooth_config_send_command));
        } else {
        }
    }


    public void setMacroBluetoothConfigChangeText(String text) {
        macroBluetoothConfigChangeTextView.setText(text);
    }

    public void setMacroBluetoothConfigStatusText(String text) {
        macroBluetoothConfigStatusTextView.setText(text);
    }

    public interface OnMacroBluetoothConfigFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setMacroBluetoothConfigChangeText(String text);
        void setMacroBluetoothConfigStatusText(String text);
    }
}
