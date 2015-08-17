package com.lebo.stickinapain.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by wenping on 2015/8/16.
 */
public class GameView extends View {

    private Context mContext;
    private int mCircleRadius = 50;
    private int mSmallCircleRadius = 20;
    private int mLineLength = 200;
    private int mLineWidth = 3;
    private int mTextSize = 30;
    private int mSmallTextSize = 12;
    private int screen_width;
    private int screen_height;

    private int mTotalPins = 10;
    private int mPinsLeft = mTotalPins;

    private double mAngle;
    private int mCircleX, mCircleY;
    private int mSmallCircleX, mSmallCircleY;
    private int mDashLineEndY;

    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mSmallTextPaint;
    private Paint mDashLinePaint;

    private Path mDashLinePath;

    private String mTotalPinsString = String.valueOf(mTotalPins);
    private int mTotalPinsStringLength = mTotalPinsString.length();

    public GameView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private void init(){
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();
        // 大圆位置
        mCircleX = screen_width / 2;
        mCircleY = screen_height / 3;
        // 圆，实线的paint
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mContext.getResources().getColor(android.R.color.holo_blue_bright));
        mCirclePaint.setStrokeWidth(mLineWidth);
        // 大字paint
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        // 小字paint
        mSmallTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallTextPaint.setColor(Color.WHITE);
        mSmallTextPaint.setTextSize(mSmallTextSize);
        // 虚线paint
        initDashLinePaint();
    }

    private void initDashLinePaint(){
        mDashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashLinePaint.setStyle(Paint.Style.STROKE);
        mDashLinePaint.setColor(Color.GREEN);
        mDashLinePaint.setStrokeWidth(mLineWidth);
        PathEffect effects = new DashPathEffect(new float[] {10, 10}, 1);
        mDashLinePaint.setPathEffect(effects);

        mDashLinePath = new Path();
        mDashLinePath.moveTo(mCircleX, mCircleY);
        mDashLineEndY = mCircleY + mLineLength + 2 * mSmallCircleRadius;
        mDashLinePath.lineTo(mCircleX, mDashLineEndY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int xEnd =  mCircleX - (int)(mLineLength * Math.sin(Math.toRadians(mAngle)));
        int yEnd = mCircleY + (int)(mLineLength *Math.cos(Math.toRadians(mAngle)));
        // 实线
        canvas.drawLine(mCircleX, mCircleY, xEnd, yEnd, mCirclePaint);
        // 虚线
        canvas.drawPath(mDashLinePath, mDashLinePaint);
        // 大圆
        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePaint);
        // 大字
        canvas.drawText(mTotalPinsString, 0, mTotalPinsStringLength, mCircleX - mTextSize / 2, mCircleY + mTextSize / 2, mTextPaint);
        // 小圆
        canvas.drawCircle(xEnd, yEnd, mSmallCircleRadius, mCirclePaint);
        // 小字
        // 静止的小圆
        drawStaticCircles(canvas);
    }

    private void drawStaticCircles(Canvas canvas) {
        int circleY = mDashLineEndY + mSmallCircleRadius;
        int drawCircle = 0;
        String drawString;
        while(circleY + mSmallCircleRadius < screen_height - 100 &&
                mPinsLeft > drawCircle){

            canvas.drawCircle(mCircleX, circleY, mSmallCircleRadius, mCirclePaint);
            drawString = String.valueOf(mPinsLeft - drawCircle);
            canvas.drawText(drawString, 0, drawString.length(), mCircleX - mTextSize / 2, circleY + mTextSize / 2, mTextPaint);

            circleY += 4 * mSmallCircleRadius;
            drawCircle++;
        }
    }

    public void setmAngle(double angle){
        mAngle = angle;
    }

    public double getmAngle(){
        return mAngle;
    }
}