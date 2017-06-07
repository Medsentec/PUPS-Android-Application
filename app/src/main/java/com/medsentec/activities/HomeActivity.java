package com.medsentec.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.medsentec.R;
import com.medsentec.particle.ParticleUserFunctions;

import io.particle.android.sdk.cloud.ParticleCloudSDK;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ParticleCloudSDK.init(this);
    }

    public void login(View view) {
        TextView usernameView = (TextView) findViewById(R.id.emailText);
        TextView passwordView = (TextView) findViewById(R.id.passwordText);
        ParticleUserFunctions.login(this, usernameView.getText().toString(), passwordView.getText().toString());
    }

    public void logout(View view) {
        ParticleUserFunctions.logout(this);
    }

    /**
     * Enables bluetooth
     * If bluetooth is already enabled, it won't do anything
     * BLE wasn't transferring data fast enough, current implementation is switching to utilize particle.io
     * and their cloud enabled chips
     */
    @Deprecated
    public void enableBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null /*|| !bluetoothAdapter.isEnabled()*/) {
            Log.d(TAG, "Enabling bluetooth...");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * BLE wasn't transferring data fast enough, current implementation is switching to utilize particle.io
     * and their cloud enabled chips
     */
    @Deprecated
    public void toBluetoothSettings(View view) {
        Log.d(TAG, "Switching activity to " + BluetoothActivity.class.getSimpleName());
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ParticleUserFunctions.logout(this);
    }
}
