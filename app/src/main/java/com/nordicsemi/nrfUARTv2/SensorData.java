package com.nordicsemi.nrfUARTv2;

/**
 * Created by Michele on 10/19/16.
 */
import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class SensorData {
    private List<String> pressureReadings;
    private List<String> temperatureReadings;
    private List<String> axesReadings;
    private Float voltageReading;
    private Integer packetSize;
    private String timestamp;
    public static final String TAG = "SensorData";

    public SensorData(List<String> pressureData, List<String> additionalData, String date) {
        pressureReadings = pressureData;
        temperatureReadings = additionalData.subList(additionalData.indexOf("T") + 1, additionalData.indexOf("O"));
        axesReadings = additionalData.subList(additionalData.indexOf("O") + 1, additionalData.indexOf("V"));
        voltageReading = Float.parseFloat(additionalData.get(additionalData.indexOf("V") + 1));
        packetSize = Integer.parseInt(additionalData.get(additionalData.size() - 2));
        timestamp = date;



    }

    public void printDataToDebug () {
        Log.d(TAG, "Pressure readings: " + pressureReadings);
        Log.d(TAG, "Temp: " + temperatureReadings);
        Log.d(TAG, "Axes: " + axesReadings);
        Log.d(TAG, "Voltage: " + voltageReading);
        Log.d(TAG, "Packet size: " + packetSize);
    }

    public List<String> getPressureReadings () {
        return pressureReadings;
    }

    public List<String> getTemperatureReadings() {
        return temperatureReadings;
    }

    public List<String> getAxesReadings () {
        return axesReadings;
    }

    public Float getVoltageReading() {
        return voltageReading;
    }

    public Integer getPacketSize () {
        return packetSize;
    }


}
