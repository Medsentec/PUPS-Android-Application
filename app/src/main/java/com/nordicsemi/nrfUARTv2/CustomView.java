package com.nordicsemi.nrfUARTv2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Michele on 11/2/16.
 */

public class CustomView extends View {
    private Rect rectangle;
    private Paint paint;
    private Integer color;

    private Rect[][] myGrid = new Rect[10][10];
    private Paint[][] myGridPaint = new Paint[10][10];
    private Integer[][] myColors = new Integer[10][10];


    public CustomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int x = 50;
        int y = 50;
        int sideLength = 200;

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                myColors[i][j] = Color.WHITE;
                myGridPaint[i][j] = new Paint();
                myGrid[i][j] = new Rect(x, y, sideLength, sideLength);
                x += 200;
            }
            x = 50;
            y += 200;
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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                myGridPaint[i][j].setColor(myColors[i][j]);
                canvas.drawRect(myGrid[i][j], myGridPaint[i][j]);
            }
        }

    }
}

