package com.mmc.lipsyncconnect.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.mmc.lipsyncconnect.R;

import java.lang.ref.WeakReference;

/******************************************************************************
 Copyright (c) 2020. MakersMakingChange.com (info@makersmakingchange.com)
 Developed by : Milad Hajihassan (milador)
 ******************************************************************************/

public class MacroFragment extends Fragment {
    // Log
    private final static String MACRO_FRAGMENT_TAG = MacroFragment.class.getSimpleName();
    private final static String MACRO_BLUETOOTH_CONFIG_FRAGMENT_TAG = MacroBluetoothConfigFragment.class.getSimpleName();
    private final static String MACRO_CALIBRATION_FRAGMENT_TAG =CalibrationFragment.class.getSimpleName();
    private final static String MACRO_DEADZONE_FRAGMENT_TAG =DeadzoneFragment.class.getSimpleName();
    private final static String MACRO_DEBUG_FRAGMENT_TAG =DebugFragment.class.getSimpleName();
    private final static String MACRO_FACTORY_RESET_FRAGMENT_TAG =FactoryResetFragment.class.getSimpleName();
    private final static String MACRO_INITIALIZATION_FRAGMENT_TAG =InitializationFragment.class.getSimpleName();
    private final static String MACRO_MAPPING_FRAGMENT_TAG =MacroMappingFragment.class.getSimpleName();
    private final static String MACRO_PRESSURE_THRESHOLD_FRAGMENT_TAG =PressureThresholdFragment.class.getSimpleName();
    private final static String MACRO_SENSITIVITY_FRAGMENT_TAG = MacroSensitivityFragment.class.getSimpleName();
    private final static String MACRO_VERSION_FRAGMENT_TAG =VersionFragment.class.getSimpleName();

    private static final int kModule_BluetoothConfig = 0;
    private static final int kModule_Calibration = 1;
    private static final int kModule_Deadzone = 2;
    private static final int kModule_Debug = 3;
    private static final int kModule_FactoryReset = 4;
    private static final int kModule_Initialization = 5;
    private static final int kModule_Mapping = 6;
    private static final int kModule_PressureThreshold = 7;
    private static final int kModule_Sensitivity = 8;
    private static final int kModule_Version = 9;
    private static final int kNumModules = 10;

    private TextView macroChangeTextView;
    private TextView macroStatusTextView;
    private ViewGroup macroFragmentLayout;
    // UI
    private MacroFragment.ControllerAdapter mControllerAdapter;


    private WeakReference<MacroBluetoothConfigFragment> mWeakMacroBluetoothConfigFragment = null;
    private WeakReference<CalibrationFragment> mWeakCalibrationFragment = null;
    private WeakReference<DeadzoneFragment> mWeakDeadzoneFragment = null;
    private WeakReference<DebugFragment> mWeakDebugFragment = null;
    private WeakReference<FactoryResetFragment> mWeakFactoryResetFragment = null;
    private WeakReference<InitializationFragment> mWeakInitializationFragment = null;
    private WeakReference<MacroMappingFragment> mWeakMacroMappingFragment = null;
    private WeakReference<PressureThresholdFragment> mWeakPressureThresholdFragment = null;
    private WeakReference<MacroSensitivityFragment> mWeakMacroSensitivityFragment = null;
    private WeakReference<VersionFragment> mWeakVersionFragment = null;

    private MacroFragment.OnMacroFragmentListener mListener;

    // region Fragment Lifecycle
    public static MacroFragment newInstance(@Nullable String singlePeripheralIdentifier) {
        MacroFragment fragment = new MacroFragment();
        return fragment;
    }

    public MacroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.macro_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        macroFragmentLayout = view.findViewById(R.id.macroFragmentLayout);
        macroChangeTextView = (TextView) view.findViewById(R.id.macroChangeTextView);
        macroStatusTextView = (TextView) view.findViewById(R.id.macroStatusTextView);
        // Update ActionBar
        setActionBarTitle(R.string.macro_fragment_title);
        // UI
        final Context context = getContext();
        if (context != null) {

            // Recycler view
            RecyclerView recyclerView = view.findViewById(R.id.macroFragmentRecyclerView);
            DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            Drawable lineSeparatorDrawable = ContextCompat.getDrawable(context, R.drawable.simpledivideritemdecoration);
            assert lineSeparatorDrawable != null;
            itemDecoration.setDrawable(lineSeparatorDrawable);
            recyclerView.addItemDecoration(itemDecoration);

            RecyclerView.LayoutManager peripheralsLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(peripheralsLayoutManager);

            // Disable update animation
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

            // Adapter
            WeakReference<MacroFragment> weakThis = new WeakReference<>(this);
            mControllerAdapter = new MacroFragment.ControllerAdapter(context, new MacroFragment.ControllerAdapter.Listener() {

                @Override
                public void onModuleSelected(int moduleId) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        if (fragmentManager != null) {
                            Fragment fragment = null;
                            String fragmentTag = null;
                            switch (moduleId) {
                                case kModule_BluetoothConfig:
                                    MacroBluetoothConfigFragment macroBluetoothConfigFragment = MacroBluetoothConfigFragment.newInstance();
                                    fragment = macroBluetoothConfigFragment;
                                    fragmentTag = MACRO_BLUETOOTH_CONFIG_FRAGMENT_TAG;
                                    mWeakMacroBluetoothConfigFragment= new WeakReference<>(macroBluetoothConfigFragment);
                                    break;
                                case kModule_Calibration:
                                    CalibrationFragment calibrationFragment = CalibrationFragment.newInstance();
                                    fragment = calibrationFragment;
                                    fragmentTag = MACRO_CALIBRATION_FRAGMENT_TAG;
                                    mWeakCalibrationFragment = new WeakReference<>(calibrationFragment);
                                    break;
                                case kModule_Deadzone:
                                    DeadzoneFragment deadzoneFragmentFragment = DeadzoneFragment.newInstance();
                                    fragment = deadzoneFragmentFragment;
                                    fragmentTag = MACRO_DEADZONE_FRAGMENT_TAG;
                                    mWeakDeadzoneFragment = new WeakReference<>(deadzoneFragmentFragment);
                                    break;
                                case kModule_Debug:
                                    DebugFragment debugFragment = DebugFragment.newInstance();
                                    fragment = debugFragment;
                                    fragmentTag = MACRO_DEBUG_FRAGMENT_TAG;
                                    mWeakDebugFragment = new WeakReference<>(debugFragment);
                                    break;
                                case kModule_FactoryReset:
                                    FactoryResetFragment factoryResetFragment = FactoryResetFragment.newInstance();
                                    fragment = factoryResetFragment;
                                    fragmentTag = MACRO_FACTORY_RESET_FRAGMENT_TAG;
                                    mWeakFactoryResetFragment = new WeakReference<>(factoryResetFragment);
                                    break;
                                case kModule_Initialization:
                                    InitializationFragment initializationFragment = InitializationFragment.newInstance();
                                    fragment = initializationFragment;
                                    fragmentTag = MACRO_INITIALIZATION_FRAGMENT_TAG;
                                    mWeakInitializationFragment= new WeakReference<>(initializationFragment);
                                    break;
                                case kModule_Mapping:
                                    MacroMappingFragment macroMappingFragment = MacroMappingFragment.newInstance();
                                    fragment = macroMappingFragment;
                                    fragmentTag = MACRO_MAPPING_FRAGMENT_TAG;
                                    mWeakMacroMappingFragment= new WeakReference<>(macroMappingFragment);
                                    break;
                                case kModule_PressureThreshold:
                                    PressureThresholdFragment pressureThresholdFragment = PressureThresholdFragment.newInstance();
                                    fragment = pressureThresholdFragment;
                                    fragmentTag = MACRO_PRESSURE_THRESHOLD_FRAGMENT_TAG;
                                    mWeakPressureThresholdFragment = new WeakReference<>(pressureThresholdFragment);
                                    break;
                                case kModule_Sensitivity:
                                    MacroSensitivityFragment macroSensitivityFragment = MacroSensitivityFragment.newInstance();
                                    fragment = macroSensitivityFragment;
                                    fragmentTag = MACRO_SENSITIVITY_FRAGMENT_TAG;
                                    fragmentTag = getString(R.string.macro_sensitivity_fragment_tag);
                                    mWeakMacroSensitivityFragment = new WeakReference<>(macroSensitivityFragment);
                                    break;
                                case kModule_Version:
                                    VersionFragment versionFragment = VersionFragment.newInstance();
                                    fragment = versionFragment;
                                    fragmentTag = MACRO_VERSION_FRAGMENT_TAG;
                                    mWeakVersionFragment = new WeakReference<>(versionFragment);
                                    break;
                            }

                            if (fragment != null) {
                                fragment.setTargetFragment(MacroFragment.this, 0);
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                                        .replace(R.id.contentFragmentLayout, fragment, fragmentTag);
                                fragmentTransaction.addToBackStack(fragmentTag);
                                fragmentTransaction.commitAllowingStateLoss();      // Allowing state loss to avoid detected crashes
                            }
                        }
                    }
                }
            });
            recyclerView.setAdapter(mControllerAdapter);

        }
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
        if (context instanceof MacroFragment.OnMacroFragmentListener) {
            mListener = (MacroFragment.OnMacroFragmentListener) context;
        } else if (getTargetFragment() instanceof MacroFragment.OnMacroFragmentListener) {
            mListener = (MacroFragment.OnMacroFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMacroFragmentListener");
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
    public void onStop() {
        //Log.d(MACRO_FRAGMENT_TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onResume() {
        //Log.d(MACRO_FRAGMENT_TAG, "onResume");
        super.onResume();
        final Context context = getContext();
        ViewTreeObserver observer = macroFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                macroFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (mListener.onIsArduinoAttached()) {
            macroStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            macroStatusTextView.setText(getString(R.string.default_status_text));
        }
        if (mListener.onIsArduinoOpened()) {
            new MacroFragment.AsyncSendCheck().execute(getString(R.string.model_send_command));
        } else {

        }
    }

    @Override
    public void onPause() {
        //Log.d(MACRO_FRAGMENT_TAG, "onPause");
        super.onPause();
        final Context context = getContext();

    }

    @Override
    public void onDestroy() {
        //Log.d(MACRO_FRAGMENT_TAG, "onDestroy");
        final Context context = getContext();
        super.onDestroy();
    }


    // endregion



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentActivity activity = getActivity();
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // region Adapter
    private static class ControllerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // Config
        private static final int[] kModuleTitleKeys = {
                R.string.macro_bluetooth_config_fragment_title,
                R.string.calibration_fragment_title,
                R.string.deadzone_fragment_title,
                R.string.debug_fragment_title,
                R.string.factory_reset_fragment_title,
                R.string.initialization_fragment_title,
                R.string.macro_mapping_fragment_title,
                R.string.pressure_threshold_fragment_title,
                R.string.macro_sensitivity_fragment_title,
                R.string.version_fragment_title};

        // Constants
        private static final int kCellType_ModuleCell = 10;

        private static final int kModuleCellsStartPosition = 0;

        // Interface
        interface Listener {
            void onModuleSelected(int moduleId);
        }

        // Data Structures
        private class SectionViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;

            SectionViewHolder(View view) {
                super(view);
                titleTextView = view.findViewById(R.id.titleTextView);
            }
        }



        private class ModuleViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;
            ViewGroup mainViewGroup;

            ModuleViewHolder(View view) {
                super(view);
                nameTextView = view.findViewById(R.id.nameTextView);
                mainViewGroup = view.findViewById(R.id.mainViewGroup);
            }
        }

        // Data
        private Context mContext;
        private MacroFragment.ControllerAdapter.Listener mListener;

        ControllerAdapter(@NonNull Context context, @NonNull MacroFragment.ControllerAdapter.Listener listener) {
            mContext = context.getApplicationContext();
            mListener = listener;
        }

        @Override
        public int getItemViewType(int position) {
            super.getItemViewType(position);
            return kCellType_ModuleCell;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_common_textview_item, parent, false);
            return new MacroFragment.ControllerAdapter.ModuleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final int viewType = getItemViewType(position);
            switch (viewType) {
                case kCellType_ModuleCell:
                    MacroFragment.ControllerAdapter.ModuleViewHolder moduleViewHolder = (MacroFragment.ControllerAdapter.ModuleViewHolder) holder;
                    final int moduleId = position - kModuleCellsStartPosition;
                    moduleViewHolder.nameTextView.setText(kModuleTitleKeys[position - kModuleCellsStartPosition]);
                    moduleViewHolder.mainViewGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null) {
                                mListener.onModuleSelected(moduleId);
                            }
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return kNumModules;
        }
    }

    public void setMacroChangeText(String text) {
        macroChangeTextView.setText(text);
    }

    public void setMacroStatusText(String text) {
        macroStatusTextView.setText(text);
    }

    public interface OnMacroFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setMacroChangeText(String text);
        void setMacroStatusText(String text);
    }

}

