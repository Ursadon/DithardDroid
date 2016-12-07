package ru.dithard.dithardroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.content.LocalBroadcastManager;
import android.content.Intent;
/**
 * Created by nikolay on 16.11.16.
 */

public class MyJoystickHorizontal extends View {
    Bitmap bitmap;
    private float x=0;
    private Paint myPaint;
    private RectF rect;
    private float start_x = 0;
    private boolean direction_UD = true;
    final View view=this; //smth;

    public MyJoystickHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);
        myPaint = new Paint();
        myPaint.setColor(Color.LTGRAY);
        myPaint.setAntiAlias(true);
        myPaint.setStrokeWidth(25f);
    }
    @Override
    protected void onFinishInflate() {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.getHeight(); //height is ready
                Log.e("[RECT_Fxxxx: w/h]: ", view.getWidth() + "+" +  view.getHeight());
                rect = new RectF(0, 0, view.getWidth(), view.getHeight());
            }
        });
        Log.e("[RECT_FINISH: w/h]: ", this.getWidth() + "+" +  this.getHeight());
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!rect.contains(event.getX()-(bitmap.getHeight()/2), event.getY()) || !rect.contains(event.getX()+(bitmap.getHeight()/2), event.getY())) {
                    return false;
                }
                //x = event.getX();
                start_x = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                if(!rect.contains(event.getX()-(bitmap.getHeight()/2), event.getY()) || !rect.contains(event.getX()+(bitmap.getHeight()/2), event.getY())) {
                    return false;
                }
                //x = event.getX();
                x = start_x - event.getX();
                sendMessage(x);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                x = 0;
                //y = event.getY();
                invalidate();
                Log.e("[MotionEvent st/x]: ", start_x + "+" + x);
                sendMessage(x);
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

        myPaint.setColor(Color.LTGRAY);
        canvas.drawCircle(width/2, height/2, height/2, myPaint);

        myPaint.setColor(Color.GREEN);
        canvas.drawLine(width/2, 0, width/2, height, myPaint);

        myPaint.setColor(Color.RED);
        canvas.drawLine(0, height/2, width, height/2, myPaint);
        canvas.drawBitmap(bitmap, (width/2)-(x+(bitmap.getWidth()/2)), (height/2)-(bitmap.getHeight()/2), null);
    }

    private void sendMessage(float x) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("joystick-motion-x");
        // You can also include some extra data.
        intent.putExtra("message",  Float.toString(Math.round(x)));
        LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(intent);
    }
}
