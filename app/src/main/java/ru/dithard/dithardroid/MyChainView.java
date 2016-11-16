package ru.dithard.dithardroid;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Color;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by nikolay on 16.11.16.
 */

public class MyChainView extends View {
    Bitmap bitmap;
    private float x=0, y=0;
    private Paint myPaint;
    private RectF rect;
    private float start_y = 0;
    public MyChainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);
        myPaint = new Paint();
        myPaint.setColor(Color.LTGRAY);
        myPaint.setAntiAlias(true);
        //TODO: remove this absolute values
        rect = new RectF(0, 0, 500, 500);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!rect.contains(event.getX(), event.getY()-100)) {
                    return false;
                }
                //x = event.getX();
                start_y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!rect.contains(event.getX(), event.getY()-(bitmap.getWidth()/2)) || !rect.contains(event.getX(), event.getY()+(bitmap.getWidth()/2))) {
                    return false;
                }
                //x = event.getX();
                y = start_y - event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                y = 0;
                //y = event.getY();
                invalidate();
                Log.e("[MotionEvent st/y]: ", start_y + "+" + y);
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = this.getWidth();
        int height = this.getHeight();
        //Log.e("[onDraw w/h]: ", width + "+" + height);

        super.onDraw(canvas);
        canvas.drawCircle(width/2, height/2, height/2, myPaint);
        canvas.drawBitmap(bitmap, (width/2)-(bitmap.getWidth()/2), (height/2)-(y+(bitmap.getHeight()/2)), null);
    }
}
