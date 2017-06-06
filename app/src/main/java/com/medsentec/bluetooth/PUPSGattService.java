package com.medsentec.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;
import java.util.UUID;

/**
 * A custom service to broadcast updates when receiving updates from the connected GATT device.
 * Created by Justin Ho on 2/5/17.
 * BLE wasn't transferring data fast enough, current implementation is switching to utilize particle.io
 * and their cloud enabled chips
 */
@Deprecated
public class PUPSGattService extends Service {
    private final static String TAG = PUPSGattService.class.getSimpleName();

    private static final String PACKAGE = "com.medsentec.bluetooth";
    public static final String ACTION_GATT_CONNECTED = PACKAGE + ".ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = PACKAGE + ".ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = PACKAGE + ".ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = PACKAGE + ".ACTION_DATA_AVAILABLE";
    public static final String DATA_STRING_BUILT = PACKAGE + ".DATA_STRING_BUILT";
    public static final String EXTRA_DATA = PACKAGE + ".DATA_STRING_BUILT";
    //  Universal Asynchronous Receiver/Transmitter(UART) Service is the bi-directional communication
    // channel between Arduino and bluetooth low energy enabled devices
    public static final UUID UART_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    //  TX Characteristic is the transmitting channel from the UART Service, used to send data to the connected device
    //  currently this constant is not being used, so for now we'll keep this commented out.
    //  public static final UUID TX_CHARACTERISTIC_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    //  RX Characteristic is the receiving channel from the UART Service, used to receive data from the connected device
    public static final UUID RX_CHARACTERISTIC_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothGatt bluetoothGatt;
    private PUPSBluetoothGattCallback pupsBluetoothGattCallback;
    private IBinder bluetoothServiceBinder;

    /**
     * Creates a new instance of {@link PUPSGattService}.
     */
    public PUPSGattService() {
        pupsBluetoothGattCallback = new PUPSBluetoothGattCallback(this);
        bluetoothServiceBinder = new PUPSBinder(this);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bluetoothServiceBinder;
    }

    /**
     * Broadcasts the action to any receivers listening for the action.
     * @param action the action to broadcast.
     */
    public void broadcastUpdate(final String action) {
        final Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    public void broadcastUpdate(final String action, final String name, final Serializable extra) {
        final Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(name, extra);
        sendBroadcast(intent);
    }

    /**
     * Broadcasts the action to any receivers listening for the action.
     * @param action the action to broadcast.
     * @param characteristic the characteristic.
     */
    public void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(EXTRA_DATA, characteristic.getValue());
        sendBroadcast(intent);
    }

    /**
     * Gets the {@link PUPSBluetoothGattCallback} associated with this PUPSGattService.
     * @return
     */
    public PUPSBluetoothGattCallback getPupsBluetoothGattCallback() {
        return pupsBluetoothGattCallback;
    }

    /**
     * Gets the {@link BluetoothGatt} associated with this service.
     * @return the {@link BluetoothGatt} associated with this service.
     */
    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    /**
     * Sets the {@link BluetoothGatt} associated with this service.
     * @param bluetoothGatt the {@link BluetoothGatt} associated with this service.
     */
    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    /**
     * Enables receiving GATT notifications when GATT characteristics change on the connected device.
     */
    public void enableUARTServiceGATTNotifications()
    {
        BluetoothGattService uARTService = bluetoothGatt.getService(UART_SERVICE_UUID);
        if (uARTService == null) {
            Log.d(TAG, "UART service not found!");
            return;
        }
        BluetoothGattCharacteristic rxCharacteristic = uARTService.getCharacteristic(RX_CHARACTERISTIC_UUID);
        if (rxCharacteristic == null) {
            Log.d(TAG, "Rx Characteristic not found!");
            return;
        }
        Log.d(TAG, "Rx Characteristic found!");
        bluetoothGatt.setCharacteristicNotification(rxCharacteristic, true);
        BluetoothGattDescriptor descriptor = rxCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean temp = bluetoothGatt.writeDescriptor(descriptor);
        Log.d(TAG, "writeDescriptor: " + Boolean.toString(temp));
    }

}
