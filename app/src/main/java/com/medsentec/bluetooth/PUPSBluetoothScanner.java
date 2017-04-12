package com.medsentec.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * A class to wrap the implementations of the bluetooth scanner functions.
 * Any type of bluetooth scan function should be here.
 * Created by Justin Ho on 2/27/17.
 */

public class PUPSBluetoothScanner {

    private static final String TAG = PUPSBluetoothScanner.class.getSimpleName();
    //  Current scan period is 15 seconds
    private static final long SCAN_PERIOD = 15000;
    public static final int SCAN_FINISHED = 0;

    private Handler bluetoothHandler;
    private boolean isScanning;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback bluetoothScanCallback;
    private Set<BluetoothDevice> deviceSet;

    /**
     * Creates a new instance of {@link PUPSBluetoothScanner}.
     */
    private PUPSBluetoothScanner() {
        this.deviceSet = new HashSet<>();
        this.isScanning = false;
    }

    /**
     * Creates a new instance of {@link PUPSBluetoothScanner}
     * @param bluetoothHandler the {@link Handler} associated with this Scanner.
     * @param bluetoothLeScanner the {@link BluetoothLeScanner} used by this scanner.
     * @param bluetoothScanCallback the {@link ScanCallback} to use when receiving results from a scan.
     */
    public PUPSBluetoothScanner(Handler bluetoothHandler, BluetoothLeScanner bluetoothLeScanner, ScanCallback bluetoothScanCallback) {
        this();
        this.bluetoothHandler = bluetoothHandler;
        this.bluetoothLeScanner = bluetoothLeScanner;
        this.bluetoothScanCallback = bluetoothScanCallback;
    }

    /**
     * Starts a Bluetooth Low Energy scan for devices
     */
    public void startBLEScan() {
        //  Creates a job that will call run after the scan period is up
        //  postDelayed will run on the UI Thread
        bluetoothHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  Stops scanning and re-enables the start button
                if (isScanning) {
                    Log.d(TAG, "Bluetooth Low Energy scan period expired...");
                    stopBLEScan();
                    bluetoothScanCallback.onScanResult(SCAN_FINISHED, null);
                }
            }
        }, SCAN_PERIOD);
        //  Starts the Bluetooth Low Energy scan
        if (!isScanning) {
            resetBluetoothDeviceSet();
            Log.d(TAG, "Starting Bluetooth Low Energy scan...");
            isScanning = true;
            bluetoothLeScanner.startScan(bluetoothScanCallback);
        }
    }

    /**
     * Stops Bluetooth Low Energy scans for devices
     */
    public void stopBLEScan() {
        Log.d(TAG, "Stopping Bluetooth Low Energy scan...");
        isScanning = false;
        bluetoothLeScanner.stopScan(bluetoothScanCallback);
    }

    /**
     * Gets the set of available Bluetooth Low Energy Devices that this device can connect to.
     * @return the set of available Bluetooth Low Energy Devices that this device can connect to.
     */
    public Set<BluetoothDevice> getDeviceSet() {
        return deviceSet;
    }

    /**
     * Resets the set of available bluetooth low energy devices.
     */
    public void resetBluetoothDeviceSet() {
        deviceSet.clear();
    }
}
