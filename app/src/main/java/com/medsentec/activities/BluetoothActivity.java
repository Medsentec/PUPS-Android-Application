package com.medsentec.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.medsentec.R;
import com.medsentec.bluetooth.PUPSBinder;
import com.medsentec.bluetooth.PUPSBluetoothScanner;
import com.medsentec.bluetooth.PUPSGattService;

import java.util.HashMap;
import java.util.Map;

import static com.medsentec.bluetooth.PUPSBluetoothScanner.SCAN_FINISHED;

/**
 * A custom activity for bluetooth related UI events.
 */
public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = BluetoothActivity.class.getSimpleName();

    private PUPSBluetoothScanner pupsBluetoothScanner;
    private Map<Button, BluetoothDevice> bluetoothDeviceMap;
    private BluetoothGatt currentConnectedDevice;
    private PUPSGattService bluetoothService;
    private ServiceConnection bluetoothServiceConnection;
    private Handler runnableHandler;

    /**
     * Creates a new instance of {@link BluetoothActivity}.
     */
    public BluetoothActivity() {
        bluetoothDeviceMap = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        runnableHandler = new Handler();
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        //  A callback which handles the results of a Bluetooth Low Energy scan
        ScanCallback bluetoothScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (callbackType != SCAN_FINISHED) {
                    super.onScanResult(callbackType, result);
                    addDeviceToList(result.getDevice());
                }
                else {
                    //  Scan finished, re-enable the start scan button
                    findViewById(R.id.startBLEScanButton).setEnabled(true);
                }
            }
        };
        pupsBluetoothScanner = new PUPSBluetoothScanner(runnableHandler, bluetoothLeScanner, bluetoothScanCallback);
        bluetoothServiceConnection = new PUPSBluetoothServiceConnection();
        Intent bluetoothServiceIntent = new Intent(this, PUPSGattService.class);
        bindService(bluetoothServiceIntent, bluetoothServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(bluetoothServiceConnection);
        resetBluetoothDeviceList();
    }

    /**
     * Starts a Bluetooth Low Energy scan for devices from the given view
     * @param view the view object that called this method.
     */
    public void startBLEScan(View view) {
        view.setEnabled(false);
        pupsBluetoothScanner.startBLEScan();
        findViewById(R.id.startBLEScanButton).setEnabled(false);
    }

    /**
     * Stops Bluetooth Low Energy scans from the given view
     * @param view the view object that called this method.
     */
    public void stopBLEScan(View view) {
        pupsBluetoothScanner.stopBLEScan();
        findViewById(R.id.startBLEScanButton).setEnabled(true);
    }

    /**
     * Adds the given device to the list of available Bluetooth Low Energy devices
     * @param device the device to add to the list
     */
    private void addDeviceToList(BluetoothDevice device) {
        if (!bluetoothDeviceMap.containsValue(device)) {
            Button newDevice = new Button(this);
            newDevice.setClickable(true);
            newDevice.setOnClickListener(new BluetoothDeviceOnClickListener());
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.deviceList);
            String deviceDisplayValue = device.getName();
            if (deviceDisplayValue == null) {
                deviceDisplayValue = device.getAddress();
            }
            newDevice.setText(deviceDisplayValue);
            viewGroup.addView(newDevice);
            bluetoothDeviceMap.put(newDevice, device);
            Log.d(TAG, "Device with name: " + deviceDisplayValue + " added to list of available devices");
        }
    }

    /**
     * Resets the bluetooth device list
     */
    private void resetBluetoothDeviceList() {
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.deviceList);
        viewGroup.removeAllViews();
        bluetoothDeviceMap.clear();
    }

    /**
     * Gets the context of this activity.
     * @return the context of this activity.
     */
    public Context getContext() {
        return this;
    }

    /**
     * Sets the current connected device to the given {@link BluetoothGatt}.
     * @param currentConnectedDevice the {@link BluetoothGatt} associated with the connected device.
     */
    public void setCurrentConnectedDevice(BluetoothGatt currentConnectedDevice) {
        this.currentConnectedDevice = currentConnectedDevice;
    }

    /**
     * Disconnects the current connected device.
     */
    private void disconnect() {
        if (this.currentConnectedDevice == null) {
            return;
        }
        this.currentConnectedDevice.close();
        this.currentConnectedDevice = null;
    }

    /**
     * Disconnects the currently connected device.
     * @param view the {@link View} this comes from.
     */
    public void disconnect(View view) {
        disconnect();
    }

    /**
     * Switches the activity to the {@link DeviceConnectedActivity}.
     * @param view the {@link View} this came from.
     */
    public void toBluetoothConnected(View view) {
        Log.d(TAG, "Switching activity to " + DeviceConnectedActivity.class.getSimpleName());
        Intent intent = new Intent(this, DeviceConnectedActivity.class);
        startActivity(intent);
    }


    /**
     * OnClickListener class to perform a function when a user clicks on a bluetooth device
     */
    private class BluetoothDeviceOnClickListener implements View.OnClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View view) {

            if (view.getParent() instanceof LinearLayout) {
                BluetoothDevice bluetoothDevice = bluetoothDeviceMap.get(view);
                String deviceDisplayValue = bluetoothDevice.getName();
                //  If device has no name, use the MAC address as a fallback
                if (deviceDisplayValue == null) {
                    deviceDisplayValue = bluetoothDevice.getAddress();
                }
                Log.i(TAG, "Connecting to device selected: " + deviceDisplayValue + "...");
                setCurrentConnectedDevice(bluetoothDevice.connectGatt(getContext(), false, bluetoothService.getPupsBluetoothGattCallback()));
                toBluetoothConnected(view);
                runnableHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothActivity.this.pupsBluetoothScanner.stopBLEScan();
                        findViewById(R.id.startBLEScanButton).setEnabled(true);
                        resetBluetoothDeviceList();
                    }
                }, 2000);
            }
        }
    }

    /**
     * A custom {@link ServiceConnection} associated with this activity.
     */
    private class PUPSBluetoothServiceConnection implements ServiceConnection {

        /**
         * Creates a new instance of {@link PUPSBluetoothServiceConnection}.
         */
        public PUPSBluetoothServiceConnection() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "***********************onServiceConnected***************************");
            PUPSBinder binder = (PUPSBinder) service;
            bluetoothService = binder.getService();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "*****************************onServiceDisconnected***********************");
            bluetoothService = null;
        }
    }

}
