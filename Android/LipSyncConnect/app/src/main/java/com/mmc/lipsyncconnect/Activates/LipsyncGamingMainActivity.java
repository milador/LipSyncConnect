package com.mmc.lipsyncconnect.Activates;

import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mmc.lipsyncconnect.R;
import com.mmc.lipsyncconnect.Arduino.Arduino;
import com.mmc.lipsyncconnect.Arduino.ArduinoListener;

import java.io.UnsupportedEncodingException;

public class LipsyncGamingMainActivity extends AppCompatActivity implements ArduinoListener {
    private Arduino arduino;
    private TextView statusTextView;
    ListView settingsMenuList;
    String settingsList[] = {"India", "China", "australia", "Portugle"};

    public final String SETTINGS_COMMAND = "SETTINGS";
    public final String INC_SENSITIVITY_COMMAND = "JS,1:2";
    public final String DEC_SENSITIVITY_COMMAND = "JS,1:1";
    public final String BUTTON_MODE_ONE_COMMAND = "BM,1:1";
    public final String BUTTON_MODE_TWO_COMMAND = "BM,1:2";
    public String sendCommand = "";
    Button incSensButton, decSensButton, modeOneButton, modeTwoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lipsync_gaming);
        setTitle(getString(R.string.lipsync_gaming_activity_title));
        incSensButton = (Button) findViewById(R.id.buttonSensInc);
        decSensButton = (Button) findViewById(R.id.buttonSensDec);
        modeOneButton = (Button) findViewById(R.id.buttonModeOne);
        modeTwoButton = (Button) findViewById(R.id.buttonModeTwo);
        statusTextView = findViewById(R.id.lipsyncGamingStatusTextView);
        statusTextView.setMovementMethod(new ScrollingMovementMethod());

        setUiEnabled(false);
        arduino = new Arduino(this);
        arduino.addVendorId(1234);
        arduino.setBaudRate(115200);
        display("Joystick\n\n");
        display("Please plug an Arduino via OTG.\nOn some devices you will have to enable OTG Storage in the phone's settings.\n\n");

    }

    public void setTitle(String title){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }

    public void setUiEnabled(boolean bool) {
        incSensButton.setEnabled(bool);
        decSensButton.setEnabled(bool);
        modeOneButton.setEnabled(bool);
        modeTwoButton.setEnabled(bool);

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
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("Arduino attached!");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        display("Arduino detached");
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        String receivedString = new String(bytes);
        display("> "+receivedString);
        if(receivedString.contains("SUCCESS:SETTINGS")){
            try {
                arduino.send(sendCommand.getBytes("UTF-8"));
                sendCommand ="";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onArduinoOpened() {
        setUiEnabled(true);
        //String str = "Hello World !";
        //arduino.send(str.getBytes());
    }

    public void onSensitivityInc(View view) throws UnsupportedEncodingException {
        arduino.send(SETTINGS_COMMAND.getBytes("UTF-8"));
        sendCommand=INC_SENSITIVITY_COMMAND;
    }

    public void onSensitivityDec(View view) throws UnsupportedEncodingException {
        arduino.send(SETTINGS_COMMAND.getBytes("UTF-8"));
        sendCommand=DEC_SENSITIVITY_COMMAND;
    }

    public void onModeOne(View view) throws UnsupportedEncodingException {
        arduino.send(SETTINGS_COMMAND.getBytes("UTF-8"));
        sendCommand=BUTTON_MODE_ONE_COMMAND;
    }

    public void onModeTwo(View view) throws UnsupportedEncodingException {
        arduino.send(SETTINGS_COMMAND.getBytes("UTF-8"));
        sendCommand=BUTTON_MODE_TWO_COMMAND;
    }

    @Override
    public void onUsbPermissionDenied() {
        display("Permission denied... New attempt in 3 sec");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.append(message+"\n");
            }
        });
    }
}

