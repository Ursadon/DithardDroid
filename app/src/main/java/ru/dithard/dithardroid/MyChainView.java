package ru.dithard.dithardroid;

import android.graphics.RectF;
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

/**
 * Created by nikolay on 16.11.16.
 */

public class MyChainView extends View {
    Bitmap bitmap;
    private float x, y;
    private Paint myPaint;
    private RectF rect;


    public MyChainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);
        myPaint = new Paint();
        myPaint.setColor(Color.LTGRAY);
        myPaint.setAntiAlias(true);
        x = this.getWidth();
        y = this.getHeight();
        rect = new RectF(0, 0, 500, 500);
        Log.e("[RECT: w/h]: ", this.getWidth() + "+" +  this.getHeight());
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

//

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!rect.contains(event.getX(), event.getY())) {
                    return true;
                }
                //x = event.getX();
                y = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!rect.contains(event.getX(), event.getY())) {
                    return true;
                }
                //x = event.getX();
                y = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                y = this.getHeight()/2;
                //y = event.getY();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        int width = this.getWidth();
        int height = this.getHeight();
        Log.d("[onDraw w/h]: ", width + "+" + height);

        super.onDraw(canvas);
        canvas.drawCircle(width/2, height/2, height/2, myPaint);
        canvas.drawBitmap(bitmap, x-(bitmap.getHeight()/2), y-(bitmap.getWidth()/2), null);
    }
}
