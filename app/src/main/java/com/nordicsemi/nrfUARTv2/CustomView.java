package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Michele on 11/2/16.
 */

public class CustomView extends View {
    public static final String TAG = "CustomView";
    private Rect rectangle;
    private Paint paint;
    private Integer color;

    private final int rows = 10;
    private final int cols = 10;

    private Rect[][] myGrid = new Rect[rows][cols];
    private Paint[][] myGridPaint = new Paint[rows][cols];
    private Integer[][] myColors = new Integer[rows][cols];


    public CustomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        int left = 50;
        int top = 35;
        int right = 85;
        int bottom = 70;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
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
        Log.d(TAG, "updating array");
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                Integer num = newValues[i][j];
                Log.d(TAG, Integer.toString(num));

                if(num >= 0 && num < 10) {
                    myColors[i][j] = Color.BLACK;
                }
                else if (num >= 10 && num < 20) {
                    myColors[i][j] = Color.CYAN;
                }
                else if (num >= 20 && num < 30) {
                    myColors[i][j] = Color.GREEN;
                }
                else if (num >=30 && num < 40) {
                    myColors[i][j] = Color.YELLOW;
                }
                else if (num >= 40 && num < 50) {
                    myColors[i][j] = Color.MAGENTA;
                }
                else if (num >= 50 && num < 60) {
                    myColors[i][j] = Color.DKGRAY;
                }
                else myColors[i][j] = Color.RED;
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                myGridPaint[i][j].setColor(myColors[i][j]);
                canvas.drawRect(myGrid[i][j], myGridPaint[i][j]);
            }
        }

    }
}

