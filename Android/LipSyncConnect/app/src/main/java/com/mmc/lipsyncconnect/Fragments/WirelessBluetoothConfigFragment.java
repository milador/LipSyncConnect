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

public class WirelessBluetoothConfigFragment extends Fragment {

    private MainFragment mMainFragment;

    private Button wirelessBluetoothConfigButton;
    private TextView wirelessBluetoothConfigChangeTextView;
    private TextView wirelessBluetoothConfigStatusTextView;
    private ViewGroup wirelessBluetoothConfigFragmentLayout;
    private final static String WIRELESS_BLUETOOTH_CONFIG_FRAGMENT_TAG = WirelessBluetoothConfigFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();

    private WirelessBluetoothConfigFragment.OnWirelessBluetoothConfigFragmentListener mListener;
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                new WirelessBluetoothConfigFragment.AsyncSendCheck().execute(command);
                view.performClick();
                return true;
            }
            return false;
        }
    };


    public WirelessBluetoothConfigFragment() {
        // Required empty public constructor
    }

    public static WirelessBluetoothConfigFragment newInstance() {
        WirelessBluetoothConfigFragment fragment = new WirelessBluetoothConfigFragment();
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
        wirelessBluetoothConfigFragmentLayout = view.findViewById(R.id.wirelessBluetoothConfigFragmentLayout);
        wirelessBluetoothConfigChangeTextView = (TextView) view.findViewById(R.id.wirelessBluetoothConfigChangeTextView);
        wirelessBluetoothConfigStatusTextView = (TextView) view.findViewById(R.id.wirelessBluetoothConfigStatusTextView);
        wirelessBluetoothConfigButton = (Button) view.findViewById(R.id.wirelessBluetoothConfigButton);
        wirelessBluetoothConfigButton.setOnTouchListener(mButtonTouchListener);
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
        if (context instanceof WirelessBluetoothConfigFragment.OnWirelessBluetoothConfigFragmentListener) {
            mListener = (WirelessBluetoothConfigFragment.OnWirelessBluetoothConfigFragmentListener) context;
        } else if (getTargetFragment() instanceof WirelessBluetoothConfigFragment.OnWirelessBluetoothConfigFragmentListener) {
            mListener = (WirelessBluetoothConfigFragment.OnWirelessBluetoothConfigFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnWirelessBluetoothConfigFragmentListener");
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

        ViewTreeObserver observer = wirelessBluetoothConfigFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                wirelessBluetoothConfigFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        if (mListener.onIsArduinoAttached()) {
            wirelessBluetoothConfigStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            wirelessBluetoothConfigStatusTextView.setText(getString(R.string.default_status_text));
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMainFragment = new MainFragment();
            fragmentTransaction.replace(R.id.contentFragmentLayout, mMainFragment,MAIN_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        if (mListener.onIsArduinoOpened()) {
            new WirelessBluetoothConfigFragment.AsyncSendCheck().execute(getString(R.string.bluetooth_config_send_command));
        } else {
        }
    }


    public void setWirelessBluetoothConfigChangeText(String text) {
        wirelessBluetoothConfigChangeTextView.setText(text);
    }

    public void setWirelessBluetoothConfigStatusText(String text) {
        wirelessBluetoothConfigStatusTextView.setText(text);
    }

    public interface OnWirelessBluetoothConfigFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setWirelessBluetoothConfigChangeText(String text);
        void setWirelessBluetoothConfigStatusText(String text);
    }
}
