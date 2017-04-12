package com.medsentec.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Java Docs
 * Created by Justin Ho on 3/13/17.
 */

public class BLEDataRecord implements Serializable{

    private final List<Float> pressureData;
    private final List<Float> temperatureData;
    private final AxisData axisData;

    /**
     * TODO: Java Docs
     * @param pressureData
     * @param temperatureData
     * @param axisData
     */
    public BLEDataRecord(List<Float> pressureData, List<Float> temperatureData, AxisData axisData) {
        Validation.checkNull(pressureData);
        Validation.checkNull(temperatureData);
        Validation.checkNull(axisData);
        this.pressureData = new ArrayList<>(pressureData);
        this.temperatureData = new ArrayList<>(temperatureData);
        this.axisData = axisData;
    }

    /**
     * TODO: Java Docs
     * @return
     */
    public List<Float> getPressureData() {
        return new ArrayList<>(pressureData);
    }

    /**
     * TODO: Java Docs
     * @return
     */
    public List<Float> getTemperatureData() {
        return new ArrayList<>(temperatureData);
    }

    /**
     * TODO: Java Docs
     * @return
     */
    public AxisData getAxisData() {
        return axisData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BLEDataRecord that = (BLEDataRecord) o;

        if (!pressureData.equals(that.pressureData)) {
            return false;
        }
        if (!temperatureData.equals(that.temperatureData)) {
            return false;
        }
        return axisData.equals(that.axisData);

    }

    @Override
    public int hashCode() {
        int result = pressureData.hashCode();
        result = 31 * result + temperatureData.hashCode();
        result = 31 * result + axisData.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BLEDataRecord{" +
                "pressureData=" + getStringFromFloatList(pressureData) +
                ", temperatureData=" + getStringFromFloatList(temperatureData) +
                ", axisData=" + axisData +
                '}';
    }

    /**
     * TODO: Java Docs
     * @param valuesList
     * @return
     */
    private String getStringFromFloatList(List<Float> valuesList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < valuesList.size(); i++) {
            stringBuilder.append(valuesList.get(i).floatValue());
            if (i < valuesList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
