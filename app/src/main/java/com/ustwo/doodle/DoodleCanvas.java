package com.ustwo.doodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * DoodleCanvas which can be drawn doodle image by a user's touch events
 */
public class DoodleCanvas extends View {
    final static int DEFAULT_STROKE_WIDTH = 30;
    Bitmap mBitmap;
    Paint mBitmapPaint;
    Canvas mCanvas;
    Paint mTouchPaint;
    Path mPath;

    float mCurrentX;
    float mCurrentY;

    public DoodleCanvas(Context context) {
        super(context);
        initialise();
    }

    public DoodleCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public DoodleCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    /**
     * Initialise the variables in {@link DoodleCanvas}
     */
    void initialise() {
        mPath = new Path();
        mBitmapPaint = new Paint();
        setupPaint();
    }

    /**
     * Setup a paint for drawing on the Canvas
     */
    void setupPaint() {
        mTouchPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mTouchPaint.setStyle(Paint.Style.STROKE);
        mTouchPaint.setStrokeJoin(Paint.Join.ROUND);
        mTouchPaint.setStrokeCap(Paint.Cap.ROUND);
        mTouchPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        mTouchPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // create the bitmap and canvas as the View size
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mCurrentX = x;
                mCurrentY = y;
                mPath.moveTo(x, y);
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                mPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX)/2, (y + mCurrentY)/2);
                mCanvas.drawPath(mPath, mTouchPaint);
                mCurrentX = x;
                mCurrentY = y;

                invalidate();       // refresh screen
                return true;
            }

            case MotionEvent.ACTION_UP: {
                mPath.reset();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the bitmap on the canvas
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    }

// region public methods
    /**
     * Set the paint colour used for drawing by touch
     * @param color - The new color to set in the paint.
     */
    int mTouchPaintColor;
    public void setPaintColor(int color) {
        mTouchPaint.setColor(color);
        mTouchPaintColor = color;
    }

    /**
     * Return the paint's color
     * @return the paint's color.
     */
    public int getPaintColor() {
        return mTouchPaintColor;
//        return mTouchPaint.getColor();
    }
// endregion

}
