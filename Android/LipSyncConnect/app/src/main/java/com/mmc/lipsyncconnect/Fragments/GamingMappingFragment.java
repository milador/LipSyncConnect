package com.mmc.lipsyncconnect.Fragments;

import android.annotation.SuppressLint;
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
import android.util.Log;
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

import java.lang.reflect.Array;

public class GamingMappingFragment extends Fragment {

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
    private final static String MAPPING_TAG = GamingMappingFragment.class.getSimpleName();
    private final static String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();


    //private String[] mappingSpinnerMainTitles = new String[]{"Button 1", "Button 2", "Button 3", "Button 4", "Button 5", "Button 6", "Button 7", "Button 8"};
    private String[] mappingSpinnerMainTitles;
    //private String[] mappingSpinnerSecondaryTitles = new String[]{"Left: X1. Right: View", "Left: X2. Right: Menu", "Left: LS. Right: RS", "Left: LB. Right: RB", "Left: A. Right: X", "Left: B. Right: Y", "Left: View. Right: X1", "Left: Menu. Right: X2"};
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

    private GamingMappingFragment.OnGamingMappingFragmentListener mListener;
    View.OnTouchListener mButtonTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                onGamingMappingDialog(command+":"+getGamingMappingSpinnerSelections());
                view.performClick();
                return true;
            }
            return false;
        }
    };

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


    public GamingMappingFragment() {
        // Required empty public constructor
    }

    public static GamingMappingFragment newInstance() {
        GamingMappingFragment fragment = new GamingMappingFragment();
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
        View view = inflater.inflate(R.layout.gaming_mapping_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mappingFragmentLayout = view.findViewById(R.id.gamingMappingFragmentLayout);
        mappingChangeTextView = (TextView) view.findViewById(R.id.gamingMappingChangeTextView);
        mappingStatusTextView = (TextView) view.findViewById(R.id.gamingMappingStatusTextView);
        mappingSetButton = (Button) view.findViewById(R.id.gamingMappingSetButton);

        mappingShortPuffSpinner = (Spinner) view.findViewById(R.id.gamingMappingFragmentShortPuffSpinner);
        mappingShortSipSpinner = (Spinner) view.findViewById(R.id.gamingMappingFragmentShortSipSpinner);
        mappingLongPuffSpinner = (Spinner) view.findViewById(R.id.gamingMappingFragmentLongPuffSpinner);
        mappingLongSipSpinner = (Spinner) view.findViewById(R.id.gamingMappingFragmentLongSipSpinner);
        mappingVeryLongPuffSpinner = (Spinner) view.findViewById(R.id.gamingMappingFragmentVeryLongPuffSpinner);
        mappingVeryLongSipSpinner = (Spinner) view.findViewById(R.id.gamingMappingFragmentVeryLongSipSpinner);

        mappingSpinnerMainTitles = getResources().getStringArray(R.array.gaming_mapping_fragment_button_main_titles);
        mappingSpinnerSecondaryTitles = getResources().getStringArray(R.array.gaming_mapping_fragment_button_secondary_titles);

        mappingSpinnerImageArray = getResources().obtainTypedArray(R.array.gaming_mapping_fragment_button_images);
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
        mappingSetButton.setOnTouchListener(mButtonTouchListener);
        setActionBarTitle(R.string.gaming_mapping_fragment_title);
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

    private void onGamingMappingDialog (String command) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update Button Mapping?")
                .setMessage("Are you sure, you want to update button mapping?")
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
        if (context instanceof GamingMappingFragment.OnGamingMappingFragmentListener) {
            mListener = (GamingMappingFragment.OnGamingMappingFragmentListener) context;
        } else if (getTargetFragment() instanceof GamingMappingFragment.OnGamingMappingFragmentListener) {
            mListener = (GamingMappingFragment.OnGamingMappingFragmentListener) getTargetFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnGamingMappingFragmentListener");
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

    private String getGamingMappingSpinnerSelections() {
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

    public void setGamingMappingSpinnerSelections(String text) {
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

    public void setGamingMappingChangeText(String text) {
        mappingChangeTextView.setText(text);
    }

    public void setGamingMappingStatusText(String text) {
        mappingStatusTextView.setText(text);
    }

    public interface OnGamingMappingFragmentListener {
        void onSendCommand(String command);
        boolean onIsArduinoAttached();
        boolean onIsArduinoOpened();
        boolean onIsArduinoSending();
        void setGamingMappingSpinnerSelections(String text);
        void setGamingMappingChangeText(String text);
        void setGamingMappingStatusText(String text);
    }
}
