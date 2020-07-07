package com.mmc.lipsyncconnect.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mmc.lipsyncconnect.Adapters.MappingAdapter;
import com.mmc.lipsyncconnect.R;

/******************************************************************************
 Copyright (c) 2020. MakersMakingChange.com (info@makersmakingchange.com)
 Developed by : Milad Hajihassan (milador)
 ******************************************************************************/

public class MouseMappingFragment extends Fragment {

    private MainFragment mMainFragment;

    private Spinner mappingShortPuffSpinner;
    private Spinner mappingShortSipSpinner;
    private Spinner mappingLongPuffSpinner;
    private Spinner mappingLongSipSpinner;
    private Spinner mappingVeryLongPuffSpinner;
    private Spinner mappingVeryLongSipSpinner;
    private boolean isUserInteracting;
    private MappingAdapter mMappingAdapter;

    private Button mappingSetButton;
    private TextView mappingChangeTextView;
    private TextView mappingStatusTextView;
    private ViewGroup mappingFragmentLayout;
    private final static String MAPPING_TAG = MouseMappingFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();


    private String[] mappingSpinnerMainTitles;
    private String[] mappingSpinnerSecondaryTitles;
    private TypedArray mappingSpinnerImageArray;

    private int[] mappingSpinnerImages = {
            R.drawable.mapping_button,
            R.drawable.mapping_button,
            R.drawable.mapping_button,
            R.drawable.mapping_button,
            R.drawable.mapping_button,
            R.drawable.mapping_button,
            R.drawable.mapping_button,
            R.drawable.mapping_button};

    private MouseMappingFragment.OnMouseMappingFragmentListener mListener;

    View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String command = (String) view.getTag();
            switch(view.getId()){
                case R.id.mouseMappingSetButton:
                    view.setPressed(false);
                    onMouseMappingDialog(command+":"+getMouseMappingSpinnerSelections());
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
                onMouseMappingDialog(command+":"+getMouseMappingSpinnerSelections());
                view.performClick();
                return true;
            }
            return false;
        }
    };
    */
    View.OnTouchListener mSpinnerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mappingShortPuffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isUserInteracting) {
                        Toast.makeText(getContext(), mappingSpinnerMainTitles[i], Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mappingShortSipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isUserInteracting) {
                        Toast.makeText(getContext(), mappingSpinnerMainTitles[i], Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mappingLongPuffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isUserInteracting) {
                        Toast.makeText(getContext(), mappingSpinnerMainTitles[i], Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mappingLongSipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isUserInteracting) {
                        Toast.makeText(getContext(), mappingSpinnerMainTitles[i], Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mappingVeryLongPuffSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isUserInteracting) {
                        Toast.makeText(getContext(), mappingSpinnerMainTitles[i], Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mappingVeryLongSipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isUserInteracting) {
                        Toast.makeText(getContext(), mappingSpinnerMainTitles[i], Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            return false;
        }
    };


    public MouseMappingFragment() {
        // Required empty public constructor
    }

    public static MouseMappingFragment newInstance() {
        MouseMappingFragment fragment = new MouseMappingFragment();
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
        View view = inflater.inflate(R.layout.mouse_mapping_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mappingFragmentLayout = view.findViewById(R.id.mouseMappingFragmentLayout);
        mappingChangeTextView = (TextView) view.findViewById(R.id.mouseMappingChangeTextView);
        mappingStatusTextView = (TextView) view.findViewById(R.id.mouseMappingStatusTextView);
        mappingSetButton = (Button) view.findViewById(R.id.mouseMappingSetButton);

        mappingShortPuffSpinner = (Spinner) view.findViewById(R.id.mouseMappingFragmentShortPuffSpinner);
        mappingShortSipSpinner = (Spinner) view.findViewById(R.id.mouseMappingFragmentShortSipSpinner);
        mappingLongPuffSpinner = (Spinner) view.findViewById(R.id.mouseMappingFragmentLongPuffSpinner);
        mappingLongSipSpinner = (Spinner) view.findViewById(R.id.mouseMappingFragmentLongSipSpinner);
        mappingVeryLongPuffSpinner = (Spinner) view.findViewById(R.id.mouseMappingFragmentVeryLongPuffSpinner);
        mappingVeryLongSipSpinner = (Spinner) view.findViewById(R.id.mouseMappingFragmentVeryLongSipSpinner);

        mappingSpinnerMainTitles = getResources().getStringArray(R.array.mouse_mapping_fragment_button_main_titles);
        mappingSpinnerSecondaryTitles = getResources().getStringArray(R.array.mouse_mapping_fragment_button_secondary_titles);

        mappingSpinnerImageArray = getResources().obtainTypedArray(R.array.mouse_mapping_fragment_button_images);
        for (int i=0; i<mappingSpinnerImageArray.length(); i++)
        {
            mappingSpinnerImages[i]= mappingSpinnerImageArray.getResourceId(i, -1);
        }

        mMappingAdapter = new MappingAdapter(getContext(),mappingSpinnerMainTitles,mappingSpinnerSecondaryTitles,mappingSpinnerImages);

        mappingShortPuffSpinner.setAdapter(mMappingAdapter);
        mappingShortSipSpinner.setAdapter(mMappingAdapter);

        mappingLongPuffSpinner.setAdapter(mMappingAdapter);
        mappingLongSipSpinner.setAdapter(mMappingAdapter);

        mappingVeryLongPuffSpinner.setAdapter(mMappingAdapter);
        mappingVeryLongSipSpinner.setAdapter(mMappingAdapter);

        mappingShortPuffSpinner.setOnTouchListener(mSpinnerTouchListener);
        mappingShortSipSpinner.setOnTouchListener(mSpinnerTouchListener);

        mappingLongPuffSpinner.setOnTouchListener(mSpinnerTouchListener);
        mappingLongSipSpinner.setOnTouchListener(mSpinnerTouchListener);

        mappingVeryLongPuffSpinner.setOnTouchListener(mSpinnerTouchListener);
        mappingVeryLongSipSpinner.setOnTouchListener(mSpinnerTouchListener);

        //setGamingMappingSpinnerSelections("121111");
        //mappingSetButton.setOnTouchListener(mButtonTouchListener);

        mappingSetButton.setOnClickListener(mButtonClickListener);
        setActionBarTitle(R.string.mouse_mapping_fragment_title);
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

    private void onMouseMappingDialog (String command) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Action Mapping?")
                .setMessage("Are you sure, you want to update action mapping?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncSendCheck().execute(command);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
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
        if (context instanceof MouseMappingFragment.OnMouseMappingFragmentListener) {
            mListener = (MouseMappingFragment.OnMouseMappingFragmentListener) context;
        } else if (getTargetFragment() instanceof MouseMappingFragment.OnMouseMappingFragmentListener) {
            mListener = (MouseMappingFragment.OnMouseMappingFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMouseMappingFragmentListener");
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

        ViewTreeObserver observer = mappingFragmentLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mappingFragmentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        if (mListener.onIsArduinoAttached()) {
            mappingStatusTextView.setText(getString(R.string.attached_status_text));
        } else {
            mappingStatusTextView.setText(getString(R.string.default_status_text));
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
            new AsyncSendCheck().execute(getString(R.string.mapping_send_command));
        } else {
        }
    }

    public static String intArrayToString(int[] array) {
        StringBuilder builder = new StringBuilder();
        for(int i : array) {
            builder.append(i);
        }
        return builder.toString();
    }

    private String getMouseMappingSpinnerSelections() {
        int[] spinnerSelection = {0, 0, 0, 0, 0, 0};
        String result = "000000";
        try {
            spinnerSelection[0] = mappingShortPuffSpinner.getSelectedItemPosition();
            spinnerSelection[1] = mappingShortSipSpinner.getSelectedItemPosition();
            spinnerSelection[2] = mappingLongPuffSpinner.getSelectedItemPosition();
            spinnerSelection[3] = mappingLongSipSpinner.getSelectedItemPosition();
            spinnerSelection[4] = mappingVeryLongPuffSpinner.getSelectedItemPosition();
            spinnerSelection[5] = mappingVeryLongSipSpinner.getSelectedItemPosition();
            result = intArrayToString(spinnerSelection);
        } catch (Exception e) {

        }
        return result;
    }

    public void setMouseMappingSpinnerSelections(String text) {
        int[] spinnerSelection = {0, 0, 0, 0, 0, 0};
        try {
            if(text.length()==6) {
                for (int i=0; i<text.length(); i++)
                {
                    spinnerSelection[i]= Integer.parseInt(text.substring(i, i+1));
                }

            }
            mappingShortPuffSpinner.setSelection(spinnerSelection[0]);
            mappingShortSipSpinner.setSelection(spinnerSelection[1]);
            mappingLongPuffSpinner.setSelection(spinnerSelection[2]);
            mappingLongSipSpinner.setSelection(spinnerSelection[3]);
            mappingVeryLongPuffSpinner.setSelection(spinnerSelection[4]);
            mappingVeryLongSipSpinner.setSelection(spinnerSelection[5]);
        } catch (Exception e) {

        }
    }

    public void setMouseMappingChangeText(String text) {
        mappingChangeTextView.setText(text);
    }

    public void setMouseMappingStatusText(String text) {
        mappingStatusTextView.setText(text);
    }

    public interface OnMouseMappingFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setMouseMappingSpinnerSelections(String text);
        void setMouseMappingChangeText(String text);
        void setMouseMappingStatusText(String text);
    }
}
