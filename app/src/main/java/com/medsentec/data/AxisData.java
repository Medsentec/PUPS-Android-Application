package com.medsentec.data;

import java.io.Serializable;

/**
 * Created by Justin Ho on 3/13/17.
 */
public class AxisData implements Serializable{

    private final Float xAxisData;
    private final Float yAxisData;
    private final Float zAxisData;

    public AxisData() {
        this(new Float(0), new Float(0), new Float(0));
    }

    public AxisData(Float xAxisData, Float yAxisData, Float zAxisData) {
        Validation.checkNull(xAxisData);
        Validation.checkNull(yAxisData);
        Validation.checkNull(zAxisData);
        this.xAxisData = xAxisData;
        this.yAxisData = yAxisData;
        this.zAxisData = zAxisData;
    }

    public Float getxAxisData() {
        return xAxisData;
    }

    public Float getyAxisData() {
        return yAxisData;
    }

    public Float getzAxisData() {
        return zAxisData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AxisData axisData = (AxisData) o;

        if (!xAxisData.equals(axisData.xAxisData)) {
            return false;
        }
        if (!yAxisData.equals(axisData.yAxisData)) {
            return false;
        }
        return zAxisData.equals(axisData.zAxisData);

    }

    @Override
    public int hashCode() {
        int result = xAxisData.hashCode();
        result = 31 * result + yAxisData.hashCode();
        result = 31 * result + zAxisData.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AxisData{" +
                xAxisData.floatValue() + "," +
                yAxisData.floatValue() + "," +
                zAxisData.floatValue() +
                '}';
    }
}
