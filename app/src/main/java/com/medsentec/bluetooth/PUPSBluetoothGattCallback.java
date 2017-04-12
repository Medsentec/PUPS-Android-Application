package com.medsentec.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import static com.medsentec.bluetooth.PUPSGattService.ACTION_DATA_AVAILABLE;
import static com.medsentec.bluetooth.PUPSGattService.ACTION_GATT_CONNECTED;
import static com.medsentec.bluetooth.PUPSGattService.ACTION_GATT_DISCONNECTED;
import static com.medsentec.bluetooth.PUPSGattService.ACTION_GATT_SERVICES_DISCOVERED;

/**
 * A custom implementation of {@link BluetoothGattCallback} to handle the different cases of gatt
 * changes received by the application (Connection, Service, and Characteristic).
 * Created by Justin Ho on 2/4/17.
 */
public class PUPSBluetoothGattCallback extends BluetoothGattCallback {

    private static final String TAG = PUPSBluetoothGattCallback.class.getSimpleName();
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTED = 1;

    private com.medsentec.bluetooth.PUPSGattService PUPSGattService;
    private int connectionState;

    /**
     * Creates a new instance of {@link PUPSBluetoothGattCallback}.
     * @param PUPSGattService the {@link com.medsentec.bluetooth.PUPSGattService} to use to distribute the event.
     */
    public PUPSBluetoothGattCallback(com.medsentec.bluetooth.PUPSGattService PUPSGattService) {
        this.PUPSGattService = PUPSGattService;
        this.connectionState = STATE_DISCONNECTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        String intentAction;
        this.PUPSGattService.setBluetoothGatt(gatt);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            intentAction = ACTION_GATT_CONNECTED;
            connectionState = STATE_CONNECTED;
            this.PUPSGattService.broadcastUpdate(intentAction);
            Log.i(TAG, "Connected to GATT server.");
            Log.i(TAG, "Attempting to start service discovery:" + gatt.discoverServices());

        }
        else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            intentAction = ACTION_GATT_DISCONNECTED;
            connectionState = STATE_DISCONNECTED;
            Log.i(TAG, "Disconnected from GATT server.");
            this.PUPSGattService.broadcastUpdate(intentAction);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        this.PUPSGattService.setBluetoothGatt(gatt);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.PUPSGattService.broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
        }
        else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        this.PUPSGattService.setBluetoothGatt(gatt);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.PUPSGattService.broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.PUPSGattService.setBluetoothGatt(gatt);
        this.PUPSGattService.broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
    }

    /**
     * Gets the connection state of this device to the gatt server.
     * @return the connection state of this device to the gatt server.
     */
    public int getConnectionState() {
        return connectionState;
    }
}