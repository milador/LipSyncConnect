package com.mmc.lipsyncconnect.Activates;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mmc.lipsyncconnect.Fragments.CalibrationFragment;
import com.mmc.lipsyncconnect.Fragments.DeadzoneFragment;
import com.mmc.lipsyncconnect.Fragments.DebugFragment;
import com.mmc.lipsyncconnect.Fragments.FactoryResetFragment;
import com.mmc.lipsyncconnect.Fragments.DeadzoneFragment;
import com.mmc.lipsyncconnect.Fragments.GamingMappingFragment;
import com.mmc.lipsyncconnect.Fragments.MacroBluetoothConfigFragment;
import com.mmc.lipsyncconnect.Fragments.MacroFragment;
import com.mmc.lipsyncconnect.Fragments.MacroMappingFragment;
import com.mmc.lipsyncconnect.Fragments.MacroSensitivityFragment;
import com.mmc.lipsyncconnect.Fragments.MouseMappingFragment;
import com.mmc.lipsyncconnect.Fragments.PressureThresholdFragment;
import com.mmc.lipsyncconnect.Fragments.VersionFragment;
import com.mmc.lipsyncconnect.Fragments.WirelessBluetoothConfigFragment;
import com.mmc.lipsyncconnect.Fragments.WirelessFragment;
import com.mmc.lipsyncconnect.Fragments.WirelessMappingFragment;
import com.mmc.lipsyncconnect.Fragments.WirelessSensitivityFragment;
import com.mmc.lipsyncconnect.R;
import com.mmc.lipsyncconnect.Arduino.Arduino;
import com.mmc.lipsyncconnect.Arduino.ArduinoListener;
import com.mmc.lipsyncconnect.Fragments.GamingButtonModeFragment;
import com.mmc.lipsyncconnect.Fragments.GamingFragment;
import com.mmc.lipsyncconnect.Fragments.GamingSensitivityFragment;
import com.mmc.lipsyncconnect.Fragments.InitializationFragment;
import com.mmc.lipsyncconnect.Fragments.MainFragment;
import com.mmc.lipsyncconnect.Fragments.MouseFragment;
import com.mmc.lipsyncconnect.Fragments.MouseSensitivityFragment;

import java.io.UnsupportedEncodingException;

/******************************************************************************
 Copyright (c) 2020. MakersMakingChange.com (info@makersmakingchange.com)
Developed by : Milad Hajihassan (milador)
 ******************************************************************************/

public class MainActivity extends AppCompatActivity
    implements
        ArduinoListener,
        MainFragment.OnMainFragmentListener,
        MouseFragment.OnMouseFragmentListener,
        GamingFragment.OnGamingFragmentListener,
        WirelessFragment.OnWirelessFragmentListener,
        MacroFragment.OnMacroFragmentListener,
        CalibrationFragment.OnCalibrationFragmentListener,
        DeadzoneFragment.OnDeadzoneFragmentListener,
        DebugFragment.OnDebugFragmentListener,
        FactoryResetFragment.OnFactoryResetFragmentListener,
        InitializationFragment.OnInitializationFragmentListener,
        PressureThresholdFragment.OnPressureThresholdFragmentListener,
        VersionFragment.OnVersionFragmentListener,
        MouseMappingFragment.OnMouseMappingFragmentListener,
        MouseSensitivityFragment.OnMouseSensitivityFragmentListener,
        GamingButtonModeFragment.OnGamingButtonModeFragmentListener,
        GamingMappingFragment.OnGamingMappingFragmentListener,
        GamingSensitivityFragment.OnGamingSensitivityFragmentListener,
        WirelessBluetoothConfigFragment.OnWirelessBluetoothConfigFragmentListener,
        WirelessMappingFragment.OnWirelessMappingFragmentListener,
        WirelessSensitivityFragment.OnWirelessSensitivityFragmentListener,
        MacroBluetoothConfigFragment.OnMacroBluetoothConfigFragmentListener,
        MacroMappingFragment.OnMacroMappingFragmentListener,
        MacroSensitivityFragment.OnMacroSensitivityFragmentListener {

    private Fragment currentFragment;
    private MainFragment mainFragment;

    private MouseFragment mouseFragment;
    private GamingFragment gamingFragment;
    private WirelessFragment wirelessFragment;
    private MacroFragment macroFragment;

    private MouseMappingFragment mouseMappingFragment;
    private MouseSensitivityFragment mouseSensitivityFragment;

    private GamingButtonModeFragment gamingButtonModeFragment;
    private GamingMappingFragment gamingMappingFragment;
    private GamingSensitivityFragment gamingSensitivityFragment;

    private WirelessBluetoothConfigFragment wirelessBluetoothConfigFragment;
    private WirelessMappingFragment wirelessMappingFragment;
    private WirelessSensitivityFragment wirelessSensitivityFragment;

    private MacroBluetoothConfigFragment macroBluetoothConfigFragment;
    private MacroMappingFragment macroMappingFragment;
    private MacroSensitivityFragment macroSensitivityFragment;

    private CalibrationFragment calibrationFragment;
    private DeadzoneFragment deadzoneFragment;
    private DebugFragment debugFragment;
    private FactoryResetFragment factoryResetFragment;
    private InitializationFragment initializationFragment;
    private PressureThresholdFragment pressureThresholdFragment;
    private VersionFragment versionFragment;

    private MainFragment mMainFragment;

    private final String MAIN_ACTIVITY_TAG = MainActivity.class.getSimpleName();
    private final String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private Arduino arduino;
    private int lipsyncModel = 0;
    private boolean arduinoIsOpened = false;
    private boolean arduinoIsAttached = false;
    private boolean arduinoIsSending = false;
    private TextView statusTextView;
    private TextView modelTextView;

    public String settingsCommand = "";
    public String sendCommand = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        arduino = new Arduino(this);
        arduino.addVendorId(1234);
        arduino.setBaudRate(115200);

        settingsCommand = getString(R.string.settings_send_command);

        final FragmentManager fragmentManager = getSupportFragmentManager();
                if (savedInstanceState == null) {
                mMainFragment = MainFragment.newInstance();
                // mMainFragment = pressureThresholdFragment.newInstance();
                fragmentManager.beginTransaction()
                .add(R.id.contentFragmentLayout, mMainFragment, MAIN_FRAGMENT_TAG)
                .addToBackStack(MAIN_FRAGMENT_TAG)
                .commit();
                }

                // Back navigation listener
                if (fragmentManager.getBackStackEntryCount() > 0) {        // Check if coming back
                    fragmentManager.popBackStack();
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
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            startTime = System.currentTimeMillis();
        }
        @Override
        protected String doInBackground(String... command) {
            String result="";
            try{
                while (enableSending){
                    if(onIsArduinoSending()){
                        enableSending=true;
                    } else {
                        onSendCommand(command[0]);
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
                setEnabledMainButton(true,lipsyncModel);
                progressDialog.dismiss();
            } else {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        setEnabledMainButton(true,lipsyncModel);
                        progressDialog.dismiss();
                    }
                }, minProgressTime - timeDifference);
            }
        }
    }

    private static int stringCharCounter(
            String someString, char searchedChar, int index) {
        if (index >= someString.length()) {
            return 0;
        }

        int count = someString.charAt(index) == searchedChar ? 1 : 0;
        return count + stringCharCounter(
                someString, searchedChar, index + 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduinoIsOpened=false;
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        //statusTextView.setText("Lipsync/Arduino attached!");
        onUpdateStatusText(getString(R.string.attached_status_text));
        arduinoIsAttached=true;
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        //statusTextView.setText("Arduino detached");
        mMainFragment.setEnabledAllMainButtons(false);
        onUpdateStatusText(getString(R.string.detached_status_text));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mMainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.contentFragmentLayout, mMainFragment,MAIN_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(MAIN_FRAGMENT_TAG);
        fragmentTransaction.commit();
        arduinoIsAttached=false;
    }


    @Override
    public void onArduinoMessage(byte[] bytes) {
        String commandString = new String(bytes);
        String successString = getString(R.string.settings_res_command);
        commandString = commandString.replaceAll("\\s","");
        //statusTextView.setText(receivedString);
        int commandParts = stringCharCounter(commandString, ':',0);
        if(commandString.contains(getString(R.string.settings_res_success_command)) && (commandParts ==1)){
            try {
                arduino.send(sendCommand.getBytes("UTF-8"));
                sendCommand="";
                onUpdateChangeText("Entering Settings");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if ((commandString.contains(getString(R.string.settings_res_fail_command)) || commandString.contains(getString(R.string.exit_res_success_command))) && (commandParts ==1)) {
            onUpdateChangeText("Exit Settings");
            sendCommand="";
            arduinoIsSending=false;
        } else if (commandParts ==2) {
            String[] commandList = commandString.split(":");
            String[] actionList = commandList[2].split(",");
            if (commandList[0].equals(getString(R.string.log_res_command))) {
                if(commandList[1].equals(getString(R.string.log_initialization_res_command))){
                    onUpdateDataText("xHNt:"+actionList[0]+",xLNt:"+actionList[1]+",yHNt:"+actionList[2]+",yLNt:"+actionList[3]);
                    sendCommand="";
                } else if (commandList[1].equals(getString(R.string.log_calibration_res_command))){
                    onUpdateDataText("xHMax:"+actionList[0]+",xLMax:"+actionList[1]+",yHMax:"+actionList[2]+",yLMax:"+actionList[3]);
                    sendCommand="";
                } else if (commandList[1].equals(getString(R.string.log_raw_res_command))){
                    onUpdateDataText("xH:"+actionList[0]+",xL:"+actionList[1]+",yH:"+actionList[2]+",yL:"+actionList[3]);
                    sendCommand="";
                }
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.model_res_command))) {
                if (commandList[2].contains(getString(R.string.model_mouse_res_command))) {
                    lipsyncModel=1;
                    onUpdateChangeText(getString(R.string.model_mouse_res_text));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.model_gaming_res_command))) {
                    lipsyncModel=2;
                    onUpdateChangeText(getString(R.string.model_gaming_res_text));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.model_wireless_res_command))) {
                    lipsyncModel=3;
                    onUpdateChangeText(getString(R.string.model_wireless_res_text));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.model_micro_res_command))) {
                    lipsyncModel=4;
                    onUpdateChangeText(getString(R.string.model_macro_res_text));
                    sendCommand="";
                }
            }
            //Version
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.version_res_command))) {
                onUpdateChangeText("Version:"+commandList[2]);
                sendCommand="";
            }
            //Sensitivity
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.sensitivity_res_command))) {
                onUpdateChangeText("Sensitivity:"+commandList[2]);
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.sensitivity_set_res_command))) {
                onUpdateChangeText("Sensitivity:"+commandList[2]);
                sendCommand="";
            }
            //Deadzone
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.deadzone_res_command))) {
                onUpdateChangeText("Deadzone:"+commandList[2]);
                onUpdateSeekBar(Integer.parseInt(commandList[2]));
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.deadzone_set_res_command))) {
                onUpdateChangeText("Deadzone:"+commandList[2]);
                sendCommand="";
            }
            //Button Mapping
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.mapping_res_command))) {
                onUpdateChangeText("Button Mapping:"+commandList[2]);
                onUpdateSpinner(commandList[2]);
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.mapping_set_res_command))) {
                onUpdateChangeText("Button Mapping:"+commandList[2]);
                sendCommand="";
            }
            //Bluetooth Config
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.bluetooth_config_res_command))) {
                if (commandList[2].contains(getString(R.string.bluetooth_config_mouse_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_mouse_set_res_text));
                } else if (commandList[2].contains(getString(R.string.bluetooth_config_keyboard_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_keyboard_set_res_text));
                } else if (commandList[2].contains(getString(R.string.bluetooth_config_joystick_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_joystick_set_res_text));
                } else if (commandList[2].contains(getString(R.string.bluetooth_config_comp_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_comp_set_res_text));
                }
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.bluetooth_config_set_res_command))) {
                if (commandList[2].contains(getString(R.string.bluetooth_config_mouse_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_mouse_set_res_text));
                } else if (commandList[2].contains(getString(R.string.bluetooth_config_keyboard_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_keyboard_set_res_text));
                } else if (commandList[2].contains(getString(R.string.bluetooth_config_joystick_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_joystick_set_res_text));
                } else if (commandList[2].contains(getString(R.string.bluetooth_config_comp_res_command))) {
                    onUpdateChangeText(getString(R.string.bluetooth_config_comp_set_res_text));
                }
                sendCommand="";
            }
            //Button Mode
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.button_mode_res_command))) {
                onUpdateChangeText("Button Mode:"+commandList[2]);
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.button_mode_set_res_command))) {
                onUpdateChangeText("Button Mode:"+commandList[2]);
                sendCommand="";
            }
            //Debug
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.debug_res_command))) {
                if (commandList[2].contains(getString(R.string.debug_off_res_command))) {
                    onUpdateChangeText("Debug mode:"+getString(R.string.debug_off_set_res_text));
                } else if (commandList[2].contains(getString(R.string.debug_on_res_command))) {
                    onUpdateChangeText("Debug mode:"+getString(R.string.debug_on_set_res_text));
                }
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.debug_set_res_command))) {
                if (commandList[2].contains(getString(R.string.debug_off_res_command))) {
                    onUpdateChangeText("Debug mode:"+getString(R.string.debug_off_set_res_text));
                } else if (commandList[2].contains(getString(R.string.debug_on_res_command))) {
                    onUpdateChangeText("Debug mode:"+getString(R.string.debug_on_set_res_text));
                }
                sendCommand="";
            }
            //Factory Reset
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.factory_reset_set_res_command))) {
                if (commandList[2].contains(getString(R.string.factory_reset_success_res_command))) {
                    onUpdateChangeText(getString(R.string.factory_reset_success_res_text));
                } else{
                }
                sendCommand="";
            }
            //Initialization
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.initialization_res_command))) {
                onUpdateChangeText("xH:"+actionList[0]+",xL:"+actionList[1]+",yH:"+actionList[2]+",yL:"+actionList[3]);
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.initialization_set_res_command))) {
                onUpdateChangeText("xH:"+actionList[0]+",xL:"+actionList[1]+",yH:"+actionList[2]+",yL:"+actionList[3]);
                sendCommand="";
            }
            //Calibration
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.calibration_res_command))) {
                onUpdateChangeText("xH:"+actionList[0]+",xL:"+actionList[1]+",yH:"+actionList[2]+",yL:"+actionList[3]);
                sendCommand="";
            }
            //Calibration
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.calibration_set_res_command))) {
                if (commandList[2].contains(getString(R.string.calibration_zero_set_res_command))) {
                    onUpdateChangeText(getString(R.string.calibration_zero_set_res_message));
                    onUpdateChangeImage(getString(R.string.calibration_zero_set_res_command));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.calibration_one_set_res_command))) {
                    onUpdateChangeText(getString(R.string.calibration_one_set_res_message));
                    onUpdateChangeImage(getString(R.string.calibration_one_set_res_command));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.calibration_two_set_res_command))) {
                    onUpdateChangeText(getString(R.string.calibration_two_set_res_message));
                    onUpdateChangeImage(getString(R.string.calibration_two_set_res_command));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.calibration_three_set_res_command))) {
                    onUpdateChangeText(getString(R.string.calibration_three_set_res_message));
                    onUpdateChangeImage(getString(R.string.calibration_three_set_res_command));
                    sendCommand="";
                } else if (commandList[2].contains(getString(R.string.calibration_four_set_res_command))) {
                    onUpdateChangeText(getString(R.string.calibration_four_set_res_message));
                    onUpdateChangeImage(getString(R.string.calibration_four_set_res_command));
                    sendCommand="";
                }
            }
            arduinoIsSending=false;
        }  else if (commandParts ==3) {
            String[] commandList = commandString.split(":");
            String[] actionList = commandList[3].split(",");
            //Calibration
            if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.calibration_set_res_command))) {
                if (commandList[2].contains(getString(R.string.calibration_five_set_res_command))) {
                    onUpdateChangeText("xH:"+actionList[0]+",xL:"+actionList[1]+",yH:"+actionList[2]+",yL:"+actionList[3]);
                    onUpdateChangeImage(getString(R.string.calibration_five_set_res_command));
                    sendCommand="";
                }
            }
            //Pressure Threshold
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.pressure_threshold_res_command))) {
                onUpdateChangeText("Threshold:"+commandList[2]+"% "+ "Nominal:"+commandList[3]+"V");
                onUpdateSeekBar(Integer.parseInt(commandList[2]));
                onUpdatePressureThresholdValueText(Integer.parseInt(commandList[2]), Double.parseDouble(commandList[3]));
                sendCommand="";
            }
            else if (commandList[0].equals(successString) && commandList[1].equals(getString(R.string.pressure_threshold_set_res_command))) {
                onUpdateChangeText("Threshold:"+commandList[2]+"% "+ "Nominal:"+commandList[3]+"V");
                onUpdatePressureThresholdValueText(Integer.parseInt(commandList[2]), Double.parseDouble(commandList[3]));
                sendCommand="";
            }
            arduinoIsSending=false;
        }
    }

    @Override
    public void onArduinoOpened() {
        arduinoIsOpened=true;
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
        if (currentFragment instanceof MainFragment) {
            mainFragment =  (MainFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
            new AsyncSendCheck().execute(getString(R.string.model_send_command));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //arduino.unsetArduinoListener();
        arduino.close();
        sendCommand="";
        arduinoIsOpened=false;
    }


    @Override
    public void onUsbPermissionDenied() {
        //statusTextView.setText("Permission denied...");
        onUpdateStatusText(getString(R.string.perm_denied_status_text));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }

    @Override
    public void onSendCommand(String command) {
        arduinoIsSending=true;
        sendCommand=settingsCommand;
        try {
            arduino.send(sendCommand.getBytes("UTF-8"));
            sendCommand=command;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onLipsyncModel() {
        return lipsyncModel;
    }

    @Override
    public boolean onIsArduinoAttached() {
        return arduinoIsAttached;
    }

    @Override
    public boolean onIsArduinoOpened() {
        return arduinoIsOpened;
    }

    @Override
    public boolean onIsArduinoSending() {
        return arduinoIsSending;
    }

    @Override
    public void setMacroSensitivityChangeText(String text) {

    }

    @Override
    public void setMacroSensitivityStatusText(String text) {

    }

    @Override
    public void setMacroMappingSpinnerSelections(String text) {

    }

    @Override
    public void setMacroMappingChangeText(String text) {

    }

    @Override
    public void setMacroMappingStatusText(String text) {

    }

    @Override
    public void setMacroBluetoothConfigChangeText(String text) {

    }

    @Override
    public void setMacroBluetoothConfigStatusText(String text) {

    }

    @Override
    public void setMacroChangeText(String text) {

    }

    @Override
    public void setMacroStatusText(String text) {

    }

    @Override
    public void setWirelessSensitivityChangeText(String text) {

    }

    @Override
    public void setWirelessSensitivityStatusText(String text) {

    }

    @Override
    public void setWirelessMappingSpinnerSelections(String text) {

    }

    @Override
    public void setWirelessMappingChangeText(String text) {

    }

    @Override
    public void setWirelessMappingStatusText(String text) {

    }

    @Override
    public void setWirelessBluetoothConfigChangeText(String text) {

    }

    @Override
    public void setWirelessBluetoothConfigStatusText(String text) {

    }

    @Override
    public void setWirelessChangeText(String text) {

    }

    @Override
    public void setWirelessStatusText(String text) {

    }

    @Override
    public void setMouseMappingSpinnerSelections(String text) {

    }

    @Override
    public void setMouseMappingChangeText(String text) {

    }

    @Override
    public void setMouseMappingStatusText(String text) {

    }

    @Override
    public void setGamingMappingSpinnerSelections(String text) {

    }

    @Override
    public void setGamingMappingChangeText(String text) {

    }

    @Override
    public void setGamingMappingStatusText(String text) {

    }


    @Override
    public void setEnabledMainButton(boolean bool, int button) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof MainFragment) {
                    mainFragment =  (MainFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mainFragment.setEnabledMainButton(bool,button);
                }
            }
        });
    }

    private void onUpdateDataText(String dataText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof DebugFragment) {
                    debugFragment =  (DebugFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    debugFragment.setDebugDataText(dataText);
                }
            }
        });
    }

    private void onUpdateChangeText(String changeText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof MainFragment) {
                    mainFragment =  (MainFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mainFragment.setMainChangeText(changeText);
                    //Log.v("Fragment", "MainFragment");
                } else if (currentFragment instanceof MouseFragment) {
                    mouseFragment =  (MouseFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseFragment.setMouseChangeText(changeText);
                    //Log.v("Fragment", "MouseFragment");
                } else if (currentFragment instanceof GamingFragment) {
                    gamingFragment =  (GamingFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingFragment.setGamingChangeText(changeText);
                    //Log.v("Fragment", "GamingFragment");
                } else if (currentFragment instanceof WirelessFragment) {
                    wirelessFragment =  (WirelessFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessFragment.setWirelessChangeText(changeText);
                    //Log.v("Fragment", "WirelessFragment");
                } else if (currentFragment instanceof MacroFragment) {
                    macroFragment =  (MacroFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroFragment.setMacroChangeText(changeText);
                    //Log.v("Fragment", "MacroFragment");
                } else if (currentFragment instanceof MouseSensitivityFragment) {
                    mouseSensitivityFragment =  (MouseSensitivityFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseSensitivityFragment.setMouseSensitivityChangeText(changeText);
                    //Log.v("Fragment", "MouseSensitivityFragment");
                } else if (currentFragment instanceof GamingSensitivityFragment) {
                    gamingSensitivityFragment =  (GamingSensitivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingSensitivityFragment.setGamingSensitivityChangeText(changeText);
                    //Log.v("Fragment", "GamingSensitivityFragment");
                } else if (currentFragment instanceof WirelessSensitivityFragment) {
                    wirelessSensitivityFragment =  (WirelessSensitivityFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessSensitivityFragment.setWirelessSensitivityChangeText(changeText);
                    //Log.v("Fragment", "WirelessSensitivityFragment");
                } else if (currentFragment instanceof MacroSensitivityFragment) {
                    macroSensitivityFragment =  (MacroSensitivityFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroSensitivityFragment.setMacroSensitivityChangeText(changeText);
                    //Log.v("Fragment", "MacroSensitivityFragment");
                } else if (currentFragment instanceof MouseMappingFragment) {
                    mouseMappingFragment =  (MouseMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseMappingFragment.setMouseMappingChangeText(changeText);
                    //Log.v("Fragment", "MouseMappingFragment");
                } else if (currentFragment instanceof GamingMappingFragment) {
                    gamingMappingFragment =  (GamingMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingMappingFragment.setGamingMappingChangeText(changeText);
                    //Log.v("Fragment", "GamingMappingFragment");
                } else if (currentFragment instanceof WirelessMappingFragment) {
                    wirelessMappingFragment =  (WirelessMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessMappingFragment.setWirelessMappingChangeText(changeText);
                    //Log.v("Fragment", "WirelessMappingFragment");
                } else if (currentFragment instanceof MacroMappingFragment) {
                    macroMappingFragment =  (MacroMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroMappingFragment.setMacroMappingChangeText(changeText);
                    //Log.v("Fragment", "MacroMappingFragment");
                } else if (currentFragment instanceof WirelessBluetoothConfigFragment) {
                    wirelessBluetoothConfigFragment =  (WirelessBluetoothConfigFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessBluetoothConfigFragment.setWirelessBluetoothConfigChangeText(changeText);
                    //Log.v("Fragment", "WirelessBluetoothConfigFragment");
                } else if (currentFragment instanceof MacroBluetoothConfigFragment) {
                    macroBluetoothConfigFragment =  (MacroBluetoothConfigFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroBluetoothConfigFragment.setMacroBluetoothConfigChangeText(changeText);
                    //Log.v("Fragment", "MacroBluetoothConfigFragment");
                } else if (currentFragment instanceof GamingButtonModeFragment) {
                    gamingButtonModeFragment =  (GamingButtonModeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingButtonModeFragment.setGamingButtonModeChangeText(changeText);
                    //Log.v("Fragment", "GamingButtonModeFragment");
                } else if (currentFragment instanceof DeadzoneFragment) {
                    deadzoneFragment =  (DeadzoneFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    deadzoneFragment.setDeadzoneChangeText(changeText);
                    //Log.v("Fragment", "DeadzoneFragment");
                } else if (currentFragment instanceof InitializationFragment) {
                    initializationFragment =  (InitializationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    initializationFragment.setInitializationChangeText(changeText);
                    //Log.v("Fragment", "InitializationFragment");
                } else if (currentFragment instanceof CalibrationFragment) {
                    calibrationFragment =  (CalibrationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    calibrationFragment.setCalibrationChangeText(changeText);
                    //Log.v("Fragment", "CalibrationFragment");
                } else if (currentFragment instanceof PressureThresholdFragment) {
                    pressureThresholdFragment =  (PressureThresholdFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    pressureThresholdFragment.setPressureThresholdChangeText(changeText);
                    //Log.v("Fragment", "PressureThresholdFragment");
                } else if (currentFragment instanceof DebugFragment) {
                    debugFragment =  (DebugFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    debugFragment.setDebugChangeText(changeText);
                    //Log.v("Fragment", "DebugFragment");
                } else if (currentFragment instanceof FactoryResetFragment) {
                    factoryResetFragment =  (FactoryResetFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    factoryResetFragment.setFactoryResetChangeText(changeText);
                    //Log.v("Fragment", "FactoryResetFragment");
                } else if (currentFragment instanceof VersionFragment) {
                    versionFragment =  (VersionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    versionFragment.setVersionChangeText(changeText);
                    //Log.v("Fragment", "VersionFragment");
                }
            }
        });
    }
    private void onUpdateChangeImage(String stepNumberString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof CalibrationFragment) {
                    calibrationFragment =  (CalibrationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    calibrationFragment.setCalibrationImage(stepNumberString);
                    //Log.v("Fragment", "CalibrationFragment");
                }
            }
        });
    }

    private void onUpdatePressureThresholdValueText(int threshold, double nominal) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof PressureThresholdFragment) {
                    pressureThresholdFragment =  (PressureThresholdFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    pressureThresholdFragment.setPressureThresholdValueText(threshold,nominal);
                    //Log.v("Fragment", "PressureThresholdFragment");
                }
            }
        });
    }

    private void onUpdateSpinner(String mapping) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof MouseMappingFragment) {
                    mouseMappingFragment =  (MouseMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseMappingFragment.setMouseMappingSpinnerSelections(mapping);
                    //Log.v("Fragment", "MouseMappingFragment");
                } else if (currentFragment instanceof GamingMappingFragment) {
                    gamingMappingFragment =  (GamingMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingMappingFragment.setGamingMappingSpinnerSelections(mapping);
                    //Log.v("Fragment", "GamingMappingFragment");
                } else if (currentFragment instanceof WirelessMappingFragment) {
                    wirelessMappingFragment =  (WirelessMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessMappingFragment.setWirelessMappingSpinnerSelections(mapping);
                    //Log.v("Fragment", "WirelessMappingFragment");
                }else if (currentFragment instanceof MacroMappingFragment) {
                    macroMappingFragment =  (MacroMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroMappingFragment.setMacroMappingSpinnerSelections(mapping);
                    //Log.v("Fragment", "MacroMappingFragment");
                }
            }
        });
    }

    private void onUpdateSeekBar(int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof DeadzoneFragment) {
                    deadzoneFragment =  (DeadzoneFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    deadzoneFragment.setDeadzoneSeekBar(value);
                    //Log.v("Fragment", "DeadzoneFragment");
                } else if (currentFragment instanceof PressureThresholdFragment) {
                    pressureThresholdFragment =  (PressureThresholdFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    pressureThresholdFragment.setPressureThresholdSeekBar(value);
                    //Log.v("Fragment", "PressureThresholdFragment");
                }
            }
        });
    }

    private void onUpdateStatusText(String statusText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                if (currentFragment instanceof MainFragment) {
                    mainFragment =  (MainFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mainFragment.setMainStatusText(statusText);
                    //Log.v("Fragment", "MainFragment");
                } else if (currentFragment instanceof MouseFragment) {
                    mouseFragment =  (MouseFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseFragment.setMouseStatusText(statusText);
                    //Log.v("Fragment", "MouseFragment");
                } else if (currentFragment instanceof GamingFragment) {
                    gamingFragment =  (GamingFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingFragment.setGamingStatusText(statusText);
                    //Log.v("Fragment", "GamingFragment");
                } else if (currentFragment instanceof WirelessFragment) {
                    wirelessFragment =  (WirelessFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessFragment.setWirelessStatusText(statusText);
                    //Log.v("Fragment", "WirelessFragment");
                }else if (currentFragment instanceof MacroFragment) {
                    macroFragment =  (MacroFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroFragment.setMacroStatusText(statusText);
                    //Log.v("Fragment", "MacroFragment");
                } else if (currentFragment instanceof MouseSensitivityFragment) {
                    mouseSensitivityFragment =  (MouseSensitivityFragment)getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseSensitivityFragment.setMouseSensitivityStatusText(statusText);
                    //Log.v("Fragment", "MouseSensitivityFragment");
                } else if (currentFragment instanceof GamingSensitivityFragment) {
                    gamingSensitivityFragment =  (GamingSensitivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingSensitivityFragment.setGamingSensitivityStatusText(statusText);
                    //Log.v("Fragment", "GamingSensitivityFragment");
                } else if (currentFragment instanceof WirelessSensitivityFragment) {
                    wirelessSensitivityFragment =  (WirelessSensitivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessSensitivityFragment.setWirelessSensitivityStatusText(statusText);
                    //Log.v("Fragment", "WirelessSensitivityFragment");
                } else if (currentFragment instanceof MacroSensitivityFragment) {
                    macroSensitivityFragment =  (MacroSensitivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroSensitivityFragment.setMacroSensitivityStatusText(statusText);
                    //Log.v("Fragment", "MacroSensitivityFragment");
                } else if (currentFragment instanceof MouseMappingFragment) {
                    mouseMappingFragment =  (MouseMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    mouseMappingFragment.setMouseMappingStatusText(statusText);
                    //Log.v("Fragment", "MouseMappingFragment");
                } else if (currentFragment instanceof GamingMappingFragment) {
                    gamingMappingFragment =  (GamingMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingMappingFragment.setGamingMappingStatusText(statusText);
                    //Log.v("Fragment", "GamingMappingFragment");
                } else if (currentFragment instanceof GamingMappingFragment) {
                    gamingMappingFragment =  (GamingMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingMappingFragment.setGamingMappingStatusText(statusText);
                    //Log.v("Fragment", "GamingMappingFragment");
                } else if (currentFragment instanceof MacroMappingFragment) {
                    macroMappingFragment =  (MacroMappingFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroMappingFragment.setMacroMappingStatusText(statusText);
                    //Log.v("Fragment", "MacroMappingFragment");
                }
                else if (currentFragment instanceof WirelessBluetoothConfigFragment) {
                    wirelessBluetoothConfigFragment =  (WirelessBluetoothConfigFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    wirelessBluetoothConfigFragment.setWirelessBluetoothConfigStatusText(statusText);
                    //Log.v("Fragment", "WirelessBluetoothConfigFragment");
                } else if (currentFragment instanceof MacroBluetoothConfigFragment) {
                    macroBluetoothConfigFragment =  (MacroBluetoothConfigFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    macroBluetoothConfigFragment.setMacroBluetoothConfigStatusText(statusText);
                    //Log.v("Fragment", "MacroBluetoothConfigFragment");
                } else if (currentFragment instanceof GamingButtonModeFragment) {
                    gamingButtonModeFragment =  (GamingButtonModeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    gamingButtonModeFragment.setGamingButtonModeStatusText(statusText);
                    //Log.v("Fragment", "GamingButtonModeFragment");
                } else if (currentFragment instanceof DeadzoneFragment) {
                    deadzoneFragment =  (DeadzoneFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    deadzoneFragment.setDeadzoneStatusText(statusText);
                    //Log.v("Fragment", "DeadzoneFragment");
                } else if (currentFragment instanceof InitializationFragment) {
                    initializationFragment =  (InitializationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    initializationFragment.setInitializationStatusText(statusText);
                    //Log.v("Fragment", "InitializationFragment");
                }  else if (currentFragment instanceof CalibrationFragment) {
                    calibrationFragment =  (CalibrationFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    calibrationFragment.setCalibrationStatusText(statusText);
                    //Log.v("Fragment", "CalibrationFragment");
                } else if (currentFragment instanceof PressureThresholdFragment) {
                    pressureThresholdFragment =  (PressureThresholdFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    pressureThresholdFragment.setPressureThresholdStatusText(statusText);
                    //Log.v("Fragment", "PressureThresholdFragment");
                } else if (currentFragment instanceof DebugFragment) {
                    debugFragment =  (DebugFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    debugFragment.setDebugStatusText(statusText);
                    //Log.v("Fragment", "DebugFragment");
                } else if (currentFragment instanceof FactoryResetFragment) {
                    factoryResetFragment =  (FactoryResetFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    factoryResetFragment.setFactoryResetStatusText(statusText);
                    //Log.v("Fragment", "FactoryResetFragment");
                } else if (currentFragment instanceof VersionFragment) {
                    versionFragment =  (VersionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentLayout);
                    versionFragment.setVersionStatusText(statusText);
                    //Log.v("Fragment", "VersionFragment");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do something here like
                int backStackEntryCount =getSupportFragmentManager().getBackStackEntryCount();
                if (backStackEntryCount > 0) {
                    getSupportFragmentManager().popBackStack();
                }
                return true;
        }
        return false;
    }

    @Override
    public void setFactoryResetChangeText(String text) {

    }

    @Override
    public void setFactoryResetStatusText(String text) {

    }

    @Override
    public void setVersionChangeText(String text) {

    }

    @Override
    public void setVersionStatusText(String text) {

    }

    @Override
    public void setDebugDataText(String text) {

    }

    @Override
    public void setDebugChangeText(String text) {

    }

    @Override
    public void setDebugStatusText(String text) {

    }

    @Override
    public void setDeadzoneSeekBar(int value) {

    }

    @Override
    public void setDeadzoneChangeText(String text) {

    }

    @Override
    public void setDeadzoneStatusText(String text) {

    }

    @Override
    public void setPressureThresholdValueText(int threshold, double nominal) {

    }


    @Override
    public void setPressureThresholdSeekBar(int value) {

    }

    @Override
    public void setPressureThresholdChangeText(String text) {

    }

    @Override
    public void setPressureThresholdStatusText(String text) {

    }

    @Override
    public void setCalibrationChangeText(String text) {

    }

    @Override
    public void setCalibrationImage(int number) {

    }

    @Override
    public void setCalibrationStatusText(String text) {

    }


    @Override
    public void setEnabledAllMainButtons(boolean bool) {

    }

    @Override
    public void setMainChangeText(String text) {

    }

    @Override
    public void setMainStatusText(String text) {

    }

    @Override
    public void setMouseChangeText(String text) {

    }

    @Override
    public void setMouseStatusText(String text) {

    }

    @Override
    public void setGamingChangeText(String text) {

    }

    @Override
    public void setGamingStatusText(String text) {

    }

    @Override
    public void setInitializationChangeText(String text) {

    }

    @Override
    public void setInitializationStatusText(String text) {

    }

    @Override
    public void setMouseSensitivityChangeText(String text) {

    }

    @Override
    public void setMouseSensitivityStatusText(String text) {

    }

    @Override
    public void setGamingSensitivityChangeText(String text) {

    }

    @Override
    public void setGamingSensitivityStatusText(String text) {

    }

    @Override
    public void setGamingButtonModeChangeText(String text) {

    }

    @Override
    public void setGamingButtonModeStatusText(String text) {

    }

}
