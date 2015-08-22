package com.lebo.stickinapain.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by wenping on 2015/8/16.
 */
public class GameView extends View {

    private static final  String TAG = "GameView";
    private Context mContext;
    private int mBigCircleRadius;
    private int mSmallCircleRadius ;
    private int mLineLength;
    private int mLineWidth = 3;
    private int mBigTextSize = 30;
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
    // 初始的针
    private double[] mOriginPins;
    // 插入的针
    private ArrayList<Double> mStickPins = new ArrayList<Double>();

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

    public double[] getOriginPins() {
        return mOriginPins;
    }

    public void setOriginPins(double[] mOriginPins) {
        this.mOriginPins = mOriginPins;
    }

    private void init(){
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();
        // 大圆半径
        mBigCircleRadius = screen_width / 8;
        mBigTextSize = mBigCircleRadius;
        // 小圆半径
        mSmallCircleRadius = mBigCircleRadius / 4;
        mBigTextSize = mSmallCircleRadius;
        // 针线长度
        mLineLength = mBigCircleRadius * 3;

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
        mTextPaint.setTextSize(mBigTextSize);
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

        // 画初始的针
        drawOriginPins(canvas);
        // 画插入的针
        drawStickPins(canvas);
        // 虚线
        canvas.drawPath(mDashLinePath, mDashLinePaint);
        // 大圆
        canvas.drawCircle(mCircleX, mCircleY, mBigCircleRadius, mCirclePaint);
        // 大字
        canvas.drawText(mTotalPinsString, 0, mTotalPinsStringLength, mCircleX - mBigTextSize / 2, mCircleY + mBigTextSize / 2, mTextPaint);
        // 静止的小圆
        drawStaticCircles(canvas);
    }

    private void drawOriginPins(Canvas canvas){
        int totalOriginPins = mOriginPins.length;
        for(int i=0; i<totalOriginPins; i++){
            double angle = Math.toRadians(mAngle + mOriginPins[i]);
            int xEnd =  mCircleX - (int)(mLineLength * Math.sin(angle));
            int yEnd = mCircleY + (int)(mLineLength *Math.cos(angle));
            // 实线
            canvas.drawLine(mCircleX, mCircleY, xEnd, yEnd, mCirclePaint);
            // 小圆
            canvas.drawCircle(xEnd, yEnd, mSmallCircleRadius, mCirclePaint);
        }
    }

    private void drawStickPins(Canvas canvas){
        int totalStickPins = mStickPins.size();
        for(int i=0; i<totalStickPins ; i++){
            double angle = Math.toRadians(mAngle + mStickPins.get(i));
            int xEnd =  mCircleX - (int)(mLineLength * Math.sin(angle));
            int yEnd = mCircleY + (int)(mLineLength *Math.cos(angle));
            // 实线
            canvas.drawLine(mCircleX, mCircleY, xEnd, yEnd, mCirclePaint);
            // 小圆
            canvas.drawCircle(xEnd, yEnd, mSmallCircleRadius, mCirclePaint);
            // 小字
            canvas.drawText(String.valueOf(mTotalPins - i), 0, String.valueOf(mTotalPins - i).length(),
                    xEnd - mBigTextSize / 2, yEnd + mBigTextSize / 2, mSmallTextPaint);
        }
    }

    private void drawStaticCircles(Canvas canvas) {
        int circleY = mDashLineEndY + mSmallCircleRadius;
        int drawCircle = 0;
        String drawString;
        while(circleY + mSmallCircleRadius < screen_height - 100 &&
                mPinsLeft > drawCircle){

            canvas.drawCircle(mCircleX, circleY, mSmallCircleRadius, mCirclePaint);
            drawString = String.valueOf(mPinsLeft - drawCircle);
            canvas.drawText(drawString, 0, drawString.length(), mCircleX - mBigTextSize / 2, circleY + mBigTextSize / 2, mTextPaint);

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStickPins.add(360 - mAngle);
                mPinsLeft--;
                if(mPinsLeft > 0) {
                    invalidate();
                }else{
                    Toast.makeText(mContext, "You win!", Toast.LENGTH_LONG).show();
                    return false;
                }
                Log.d(TAG, mAngle + " added!");
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return true;
    }
}