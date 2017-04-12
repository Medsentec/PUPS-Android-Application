package com.medsentec.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Justin Ho on 3/14/17.
 */

public class BLEDataRecordParser implements PartialDataParser<BLEDataRecord> {

    private static String BEGINNING_DELIMETER = "$";
    private static String TEMP_DELIMETER = "T";
    private static String ACC_DELIMETER = "A";
    private static String BATT_DELIMETER = "V";
    private static String PACKET_DELIMETER = "P";
    private static String LENGTH_DELIMETER = "L";
    private static String END_DELIMETER = "*";
    private static String LIST_DELIMETER = ",";

    private StringBuilder dataStringBuilder;
    private com.medsentec.bluetooth.PUPSGattService PUPSGattService;

    public BLEDataRecordParser() {
        this.dataStringBuilder = new StringBuilder();
    }

    public BLEDataRecordParser(com.medsentec.bluetooth.PUPSGattService PUPSGattService) {
        this();
        this.PUPSGattService = PUPSGattService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BLEDataRecord parseStringForData(String rawData) throws ParseException {
        Validation.checkNull(rawData);

        List<String> pressureDataStringList = new ArrayList<>();
        List<Float> pressureDataList = new ArrayList<>();
        List<String> tempDataStringList = new ArrayList<>();
        List<Float> tempDataList = new ArrayList<>();
        Float xData = new Float(0.0);
        Float yData = new Float(0.0);
        Float zData = new Float(0.0);

        try {
            String pressureDataListString = rawData.substring(rawData.indexOf(BEGINNING_DELIMETER) + 1, rawData.indexOf(TEMP_DELIMETER) - 1);
            pressureDataStringList = Arrays.asList(pressureDataListString.split(LIST_DELIMETER));

            for (String pressureDataString : pressureDataStringList) {
                pressureDataList.add(Float.parseFloat(pressureDataString));
            }

            String tempDataListString = rawData.substring(rawData.indexOf(TEMP_DELIMETER) + 2, rawData.indexOf(ACC_DELIMETER) - 1);
            tempDataStringList = Arrays.asList(tempDataListString.split(LIST_DELIMETER));
            for (String tempDataString : tempDataStringList) {
                tempDataList.add(Float.parseFloat(tempDataString));
            }

            String accDataListString = rawData.substring(rawData.indexOf(ACC_DELIMETER) + 2, rawData.indexOf(END_DELIMETER) - 1);
            List<String> accDataStringList = Arrays.asList(accDataListString.split(LIST_DELIMETER));
            xData = Float.parseFloat(accDataStringList.get(0));
            yData = Float.parseFloat(accDataStringList.get(1));
            zData = Float.parseFloat(accDataStringList.get(2));
        }
        catch (Exception ex) {
            throw new ParseException("", 0);
        }
        BLEDataRecord bleDataRecord = new BLEDataRecord(pressureDataList, tempDataList, new AxisData(xData, yData, zData));
        return bleDataRecord;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void parsePartialString(String partialDataString) {
        if(partialDataString.contains(BEGINNING_DELIMETER)) {
            // Some strings contain both the end delimiter (*) and start delim ($)
            if(partialDataString.contains(END_DELIMETER) && (partialDataString.indexOf(BEGINNING_DELIMETER) > partialDataString.indexOf(END_DELIMETER))) { // make sure end comes before next start
//                Log.d(TAG, "Contains $ and * ");
                this.dataStringBuilder.append(partialDataString.substring(0, partialDataString.indexOf(END_DELIMETER) + 1));
                //  TODO: send string to an intent which can be processed by another service
                this.PUPSGattService.broadcastUpdate(PUPSGattService.DATA_STRING_BUILT,
                        PUPSGattService.DATA_STRING_BUILT, this.dataStringBuilder.toString());
                this.dataStringBuilder = new StringBuilder();
                this.dataStringBuilder.append(partialDataString.substring(partialDataString.lastIndexOf(BEGINNING_DELIMETER), partialDataString.length()));
            }
            else {
//                Log.d(TAG, "Contains $");
                this.dataStringBuilder = new StringBuilder();
                this.dataStringBuilder.append(partialDataString.substring(partialDataString.lastIndexOf(BEGINNING_DELIMETER), partialDataString.length()));
            }

        }
        else if (partialDataString.contains("*")) {
//            Log.d(TAG, "Contains *");
            this.dataStringBuilder.append(partialDataString.substring(0, partialDataString.indexOf(END_DELIMETER) + 1));
//            Log.d(TAG, "Final String: " + data);
            this.PUPSGattService.broadcastUpdate(PUPSGattService.DATA_STRING_BUILT,
                    PUPSGattService.DATA_STRING_BUILT, this.dataStringBuilder.toString());
        }
        else {
//            Log.d(TAG, "Contains neither");
            this.dataStringBuilder.append(partialDataString);
        }
    }
}
