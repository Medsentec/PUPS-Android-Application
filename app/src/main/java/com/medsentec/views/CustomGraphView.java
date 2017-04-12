package com.medsentec.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.medsentec.data.BLEDataRecord;

import java.util.List;

/**
 * Created by Michele on 11/2/16.
 */

public class CustomGraphView extends View {

    public static final String TAG = CustomGraphView.class.getSimpleName();
    private Rect rectangle;
    private Paint paint;
    private Integer color;

    private final int ROWS = 10;
    private final int COLS = 10;

    private Rect[][] myGrid = new Rect[ROWS][COLS];
    private Paint[][] myGridPaint = new Paint[ROWS][COLS];
    private Integer[][] myColors = new Integer[ROWS][COLS];


    public CustomGraphView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        int left = 50;
        int top = 35;
        int right = 85;
        int bottom = 70;

        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLS; j++) {
                if(j%2 == 0) {
                    myColors[i][j] = Color.WHITE;
                }
                else {
                    myColors[i][j] = Color.GREEN;
                }

                myGridPaint[i][j] = new Paint();
                myGrid[i][j] = new Rect(left, top, right, bottom);
                left += 35;
                right += 35;
            }
            left = 50;
            right = 85;
            top += 35;
            bottom += 35;
        }



        // create a rectangle that we'll draw later
        // rectangle = new Rect(x, y, sideLength, sideLength);
        // create the Paint and set its color
//        paint = new Paint();
//        color = Color.GRAY;

    }


    public void changeColor(int newColor) {
        myColors[0][0] = newColor;
        invalidate();
    }

    public void updateArrayValues(Integer[][] newValues) {
//        Log.d(TAG, "updating array");
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLS; j++) {
                Integer num = newValues[i][j];
//                Log.d(TAG, Integer.toString(num));
                updateArrayValue(i, j, num);
            }
        }
        invalidate();
    }

    public void updateArrayValues(BLEDataRecord bleDataRecord) {
        try {
            List<Float> pressureData = bleDataRecord.getPressureData();
            Integer[][] pressureDataArray = new Integer[ROWS][COLS];
            int k = 0;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    pressureDataArray[i][j] = pressureData.get(k++).intValue();
                }
            }
            updateArrayValues(pressureDataArray);
        }
        catch (IndexOutOfBoundsException ioobe) {
            Log.e(TAG, "BAD DATA");
        }

    }

    private void updateArrayValue(int row, int column, Integer value) {
        if(value >= 0 && value < 10) {
            myColors[row][column] = Color.BLACK;
        }
        else if (value >= 10 && value < 20) {
            myColors[row][column] = Color.CYAN;
        }
        else if (value >= 20 && value < 30) {
            myColors[row][column] = Color.GREEN;
        }
        else if (value >=30 && value < 40) {
            myColors[row][column] = Color.YELLOW;
        }
        else if (value >= 40 && value < 50) {
            myColors[row][column] = Color.MAGENTA;
        }
        else if (value >= 50 && value < 60) {
            myColors[row][column] = Color.DKGRAY;
        }
        else {
            myColors[row][column] = Color.RED;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLS; j++) {
                myGridPaint[i][j].setColor(myColors[i][j]);
                canvas.drawRect(myGrid[i][j], myGridPaint[i][j]);
            }
        }

    }
}

