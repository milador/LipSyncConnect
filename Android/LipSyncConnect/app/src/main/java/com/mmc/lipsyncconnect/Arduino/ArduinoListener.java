package com.mmc.lipsyncconnect.Arduino;

import android.hardware.usb.UsbDevice;

import java.io.UnsupportedEncodingException;

/**
 * Created by Omar on 21/05/2017.
 */

public interface ArduinoListener {
    void onArduinoAttached(UsbDevice device) throws UnsupportedEncodingException;
    void onArduinoDetached();
    void onArduinoMessage(byte[] bytes);
    void onArduinoOpened();
    void onUsbPermissionDenied();
}
