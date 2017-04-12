package com.medsentec.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.medsentec.R;
import com.medsentec.bluetooth.PUPSBinder;
import com.medsentec.bluetooth.PUPSGattService;
import com.medsentec.data.BLEDataRecord;
import com.medsentec.data.BLEDataRecordParser;
import com.medsentec.data.PartialDataParser;
import com.medsentec.views.CustomGraphView;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * A custom Activity associated with UI events that happen upon connecting a bluetooth device.
 */
public class DeviceConnectedActivity extends AppCompatActivity {

    private final static String TAG = DeviceConnectedActivity.class.getSimpleName();

    private PUPSGattService bluetoothService;
    private PUPSServiceConnection bluetoothServiceConnection;
    private TextView textView;
    private ScrollView scrollView;
    private boolean isPaused = false;
    private PartialDataParser<BLEDataRecord> partialDataParser;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connected);
        bluetoothServiceConnection = new PUPSServiceConnection();
        Intent bluetoothServiceIntent = new Intent(this, PUPSGattService.class);
        bindService(bluetoothServiceIntent, bluetoothServiceConnection, BIND_AUTO_CREATE);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        partialDataParser = new BLEDataRecordParser();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = !isPaused;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothServiceConnection);
        bluetoothService.getBluetoothGatt().disconnect();
        bluetoothService.getBluetoothGatt().close();
        unbindService(bluetoothServiceConnection);
        partialDataParser = null;
    }

    //  TODO: Java Docs
    private static void flushTextView(TextView textView) {
        if (textView.getLineCount() > 500) {
            //  flush after 500 lines
            textView.setText("");
        }
    }

    /**
     * The Service connection associated with this activity.
     */
    private class PUPSServiceConnection extends BroadcastReceiver implements ServiceConnection {

        private boolean firstDataReceived;

        /**
         * Creates a new instance of {@link PUPSServiceConnection}.
         */
        public PUPSServiceConnection() {
            super();
            firstDataReceived = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "**********************onReceive****************");
            final String ACTION_RECEIVED = intent.getAction();
            switch (ACTION_RECEIVED) {
                case PUPSGattService.ACTION_GATT_CONNECTED:
//                    Log.d(TAG, "ACTION_GATT_CONNECTED");
                    firstDataReceived = true;
                    break;
                case PUPSGattService.ACTION_GATT_SERVICES_DISCOVERED:
//                    Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                    bluetoothService.enableUARTServiceGATTNotifications();
                    break;
                case PUPSGattService.ACTION_DATA_AVAILABLE:
//                    Log.d(TAG, "ACTION_DATA_AVAILABLE");
                    try {
                        final byte[] data = intent.getByteArrayExtra(PUPSGattService.EXTRA_DATA);
                        String text = new String(data, "UTF-8");
                        //  If the data available is the first data received from the device, drop the data to "flush" the stream
                        if (firstDataReceived) {
                            firstDataReceived = false;
                            break;
                        }
//                        textView.append(text + "\n");
//                        if (!isPaused) {
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
                        partialDataParser.parsePartialString(text);
//                        Log.d(TAG, "Data: " + text);
                    }
                    catch (UnsupportedEncodingException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    break;
                case PUPSGattService.ACTION_GATT_DISCONNECTED:
//                    Log.d(TAG, "ACTION_GATT_DISCONNECTED");
                    break;
                case PUPSGattService.DATA_STRING_BUILT:
                    flushTextView(textView);
                    String dataString = (String) intent.getSerializableExtra(PUPSGattService.DATA_STRING_BUILT);
                    BLEDataRecord bleDataRecord = null;
                    CustomGraphView customGraphView = (CustomGraphView) findViewById(R.id.customGraphView);
                    try {
                        bleDataRecord = partialDataParser.parseStringForData(dataString);
//                        textView.append(bleDataRecord.toString() + "\n");
//                        if (!isPaused) {
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
                        customGraphView.updateArrayValues(bleDataRecord);
//                        Log.d(TAG, "BLEDataRecord: " + bleDataRecord.toString());
                    }
                    catch (ParseException e) {
                        Log.e(TAG, "Error parsing data string: " + dataString);
                    }
                    break;
                default:
                    //  TODO: change to Log.w or Log.e
                    Log.e(TAG, "Unsupported action: " + ACTION_RECEIVED);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PUPSBinder binder = (PUPSBinder) service;
            bluetoothService = binder.getService();
            IntentFilter intentFilter = new IntentFilter(PUPSGattService.ACTION_GATT_CONNECTED);
            intentFilter.addAction(PUPSGattService.ACTION_DATA_AVAILABLE);
            intentFilter.addAction(PUPSGattService.ACTION_GATT_SERVICES_DISCOVERED);
            intentFilter.addAction(PUPSGattService.DATA_STRING_BUILT);
            registerReceiver(this, intentFilter);
            partialDataParser = new BLEDataRecordParser(bluetoothService);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
            unregisterReceiver(this);
        }
    }
}
