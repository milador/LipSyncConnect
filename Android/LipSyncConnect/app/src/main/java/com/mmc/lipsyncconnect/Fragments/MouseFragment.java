package com.mmc.lipsyncconnect.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

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

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mmc.lipsyncconnect.R;

import java.lang.ref.WeakReference;


public class MouseFragment extends Fragment {

    private MainFragment mMainFragment;
    // Log
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private final static String MOUSE_FRAGMENT_TAG = MouseFragment.class.getSimpleName();
    private final static String MOUSE_SENSITIVITY_FRAGMENT_TAG = MouseSensitivityFragment.class.getSimpleName();
    private final static String MOUSE_MAPPING_FRAGMENT_TAG = MouseMappingFragment.class.getSimpleName();
    private final static String MOUSE_INITIALIZATION_FRAGMENT_TAG = InitializationFragment.class.getSimpleName();
    private final static String MOUSE_CALIBRATION_FRAGMENT_TAG =CalibrationFragment.class.getSimpleName();
    private final static String MOUSE_PRESSURE_THRESHOLD_FRAGMENT_TAG =PressureThresholdFragment.class.getSimpleName();
    private final static String MOUSE_DEBUG_FRAGMENT_TAG =DebugFragment.class.getSimpleName();
    private final static String MOUSE_FACTORY_RESET_FRAGMENT_TAG =FactoryResetFragment.class.getSimpleName();
    private final static String MOUSE_VERSION_FRAGMENT_TAG =VersionFragment.class.getSimpleName();

    private static final int kModule_Sensitivity = 0;
    private static final int kModule_Mapping = 1;
    private static final int kModule_Initialization = 2;
    private static final int kModule_Calibration = 3;
    private static final int kModule_PressureThreshold = 4;
    private static final int kModule_Debug = 5;
    private static final int kModule_FactoryReset = 6;
    private static final int kModule_Version = 7;
    private static final int kNumModules = 8;

    private TextView mouseChangeTextView;
    private TextView mouseStatusTextView;
    private ViewGroup mouseFragmentLayout;
    // UI
    private MouseFragment.ControllerAdapter mControllerAdapter;


    private WeakReference<MouseSensitivityFragment> mWeakMouseSensitivityFragment = null;
    private WeakReference<MouseMappingFragment> mWeakMouseMappingFragment = null;
    private WeakReference<InitializationFragment> mWeakInitializationFragment = null;
    private WeakReference<CalibrationFragment> mWeakCalibrationFragment = null;
    private WeakReference<PressureThresholdFragment> mWeakPressureThresholdFragment = null;
    private WeakReference<DebugFragment> mWeakDebugFragment = null;
    private WeakReference<VersionFragment> mWeakVersionFragment = null;
    private WeakReference<FactoryResetFragment> mWeakFactoryResetFragment = null;

    private MouseFragment.OnMouseFragmentListener mListener;

    // region Fragment Lifecycle
    public static MouseFragment newInstance(@Nullable String singlePeripheralIdentifier) {
        MouseFragment fragment = new MouseFragment();
        return fragment;
    }

    public MouseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        // Retain this fragment across configuration changes
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mouse_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mouseFragmentLayout = view.findViewById(R.id.mouseFragmentLayout);
        mouseChangeTextView = (TextView) view.findViewById(R.id.mouseChangeTextView);
        mouseStatusTextView = (TextView) view.findViewById(R.id.mouseStatusTextView);
        // Update ActionBar
        setActionBarTitle(R.string.mouse_fragment_title);

        // UI
        final Context context = getContext();
        if (context != null) {

            // Recycler view
            RecyclerView recyclerView = view.findViewById(R.id.mouseFragmentRecyclerView);
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
            WeakReference<MouseFragment> weakThis = new WeakReference<>(this);
            mControllerAdapter = new MouseFragment.ControllerAdapter(context, new MouseFragment.ControllerAdapter.Listener() {

                @Override
                public void onModuleSelected(int moduleId) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        if (fragmentManager != null) {
                            Fragment fragment = null;
                            String fragmentTag = null;
                            switch (moduleId) {
                                case kModule_Sensitivity:
                                    MouseSensitivityFragment mouseSensitivityFragment = MouseSensitivityFragment.newInstance();
                                    fragment = mouseSensitivityFragment;
                                    //fragmentTag = getString(R.string.mouse_sensitivity_fragment_tag);
                                    fragmentTag = MOUSE_SENSITIVITY_FRAGMENT_TAG;
                                    mWeakMouseSensitivityFragment = new WeakReference<>(mouseSensitivityFragment);
                                    break;
                                case kModule_Mapping:
                                    MouseMappingFragment mouseMappingFragment = MouseMappingFragment.newInstance();
                                    fragment = mouseMappingFragment;
                                    fragmentTag = MOUSE_MAPPING_FRAGMENT_TAG;
                                    mWeakMouseMappingFragment= new WeakReference<>(mouseMappingFragment);
                                    break;
                                case kModule_Initialization:
                                    InitializationFragment initializationFragment = InitializationFragment.newInstance();
                                    fragment = initializationFragment;
                                    //fragmentTag = getString(R.string.mouse_initialization_fragment_tag);
                                    fragmentTag = MOUSE_INITIALIZATION_FRAGMENT_TAG;
                                    mWeakInitializationFragment= new WeakReference<>(initializationFragment);
                                    break;
                                case kModule_Calibration:
                                    CalibrationFragment calibrationFragment = CalibrationFragment.newInstance();
                                    fragment = calibrationFragment;
                                    fragmentTag = MOUSE_CALIBRATION_FRAGMENT_TAG;
                                    mWeakCalibrationFragment = new WeakReference<>(calibrationFragment);
                                    break;
                                case kModule_PressureThreshold:
                                    PressureThresholdFragment pressureThresholdFragment = PressureThresholdFragment.newInstance();
                                    fragment = pressureThresholdFragment;
                                    fragmentTag = MOUSE_PRESSURE_THRESHOLD_FRAGMENT_TAG;
                                    mWeakPressureThresholdFragment = new WeakReference<>(pressureThresholdFragment);
                                    break;
                                case kModule_Debug:
                                    DebugFragment debugFragment = DebugFragment.newInstance();
                                    fragment = debugFragment;
                                    fragmentTag = MOUSE_DEBUG_FRAGMENT_TAG;
                                    mWeakDebugFragment = new WeakReference<>(debugFragment);
                                    break;
                                case kModule_FactoryReset:
                                    FactoryResetFragment factoryResetFragment = FactoryResetFragment.newInstance();
                                    fragment = factoryResetFragment;
                                    fragmentTag = MOUSE_FACTORY_RESET_FRAGMENT_TAG;
                                    mWeakFactoryResetFragment = new WeakReference<>(factoryResetFragment);
                                    break;
                                case kModule_Version:
                                    VersionFragment versionFragment = VersionFragment.newInstance();
                                    fragment = versionFragment;
                                    fragmentTag = MOUSE_VERSION_FRAGMENT_TAG;
                                    mWeakVersionFragment = new WeakReference<>(versionFragment);
                                    break;
                            }

                            if (fragment != null) {
                                fragment.setTargetFragment(MouseFragment.this, 0);
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

    /*private void onSendCheck(String command) {
        boolean enableSending = true;
        while (enableSending){
            if(mListener.onIsArduinoSending()){
                enableSending=true;
            } else {
                mListener.onSendCommand(command);
                enableSending=false;
                break;
            }
        }
    }*/


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MouseFragment.OnMouseFragmentListener) {
            mListener = (MouseFragment.OnMouseFragmentListener) context;
        } else if (getTargetFragment() instanceof MouseFragment.OnMouseFragmentListener) {
            mListener = (MouseFragment.OnMouseFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMouseFragmentListener");
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
        //mListener.onSendCommand(getString(R.string.model_send_command));

    }

    @Override
    public void onStop() {
        //Log.d(MOUSE_FRAGMENT_TAG, "onStop");
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        final Context context = getContext();

        ViewTreeObserver observer = mouseFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mouseFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (mListener.onIsArduinoAttached()) {
            mouseStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            mouseStatusTextView.setText(getString(R.string.default_status_text));
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMainFragment = new MainFragment();
            fragmentTransaction.replace(R.id.contentFragmentLayout, mMainFragment,MAIN_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        if (mListener.onIsArduinoOpened()) {
            new AsyncSendCheck().execute(getString(R.string.model_send_command));
        } else {
        }
    }

    @Override
    public void onPause() {
        //Log.d(MOUSE_FRAGMENT_TAG, "onPause");
        super.onPause();
        final Context context = getContext();

    }

    @Override
    public void onDestroy() {
        Log.d(MOUSE_FRAGMENT_TAG, "onDestroy");
        final Context context = getContext();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                R.string.mouse_sensitivity_fragment_title,
                R.string.mouse_mapping_fragment_title,
                R.string.initialization_fragment_title,
                R.string.calibration_fragment_title,
                R.string.pressure_threshold_fragment_title,
                R.string.debug_fragment_title,
                R.string.factory_reset_fragment_title,
                R.string.version_fragment_title};
        // Constants
        private static final int kCellType_ModuleCell = 8;

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
        private MouseFragment.ControllerAdapter.Listener mListener;

        ControllerAdapter(@NonNull Context context, @NonNull MouseFragment.ControllerAdapter.Listener listener) {
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
            return new MouseFragment.ControllerAdapter.ModuleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final int viewType = getItemViewType(position);
            switch (viewType) {
                case kCellType_ModuleCell:
                    MouseFragment.ControllerAdapter.ModuleViewHolder moduleViewHolder = (MouseFragment.ControllerAdapter.ModuleViewHolder) holder;
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


    public void setMouseChangeText(String text) {
        mouseChangeTextView.setText(text);
    }

    public void setMouseStatusText(String text) {
        mouseStatusTextView.setText(text);
    }

    public interface OnMouseFragmentListener {

        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setMouseChangeText(String text);
        void setMouseStatusText(String text);
    }

}
