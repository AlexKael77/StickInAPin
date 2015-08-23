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
    // big text
    private int mBigTextSize;
    private float mBigTextWidth;
    private float mBigTextHeight;
    // small text
    private int mSmallTextSize;
    private float mSmallTextHeight;

    private int screen_width;
    private int screen_height;

    private int mTotalPins;
    private int mPinsLeft;

    private double mAngle;
    private int mCircleX, mCircleY;
    private int mSmallCircleX, mSmallCircleY;
    private int mDashLineEndY;

    private Paint mCirclePaint;
    private Paint mBigTextPaint;
    private Paint mSmallTextPaint;
    private Paint mDashLinePaint;
    private Path mDashLinePath;

    // 初始的针
    private double[] mOriginPins;
    // 插入的针
    private ArrayList<Double> mStickPins = new ArrayList<Double>();

    private String mTotalPinsString;
    private int mTotalPinsStringLength;
    /**
     * 关卡失败
     */
    private boolean mLevelFailed;
    /**
     * 通关
     */
    private boolean mLevelPassed;
    /**
     * 当前等级
     */
    private int mLevel;
    /**
     * 两根针最小距离，小于这个距离游戏结束;单位为度
     */
    private double mMinPinDist;

    private GameListener mGameListener;

    public interface GameListener{
        /**
         * 游戏结束
         * @param win true:胜利；false:失败
         */
        public void onGameOver(boolean win);

        /**
         * 发射一针
         * @param index 第几针
         */
        public void onOnePinStick(int index);
    }

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

    public void setIsGameOver(boolean isGameOver) {
        this.mLevelFailed = isGameOver;
    }
    public boolean ismLevelFailed(){
        return mLevelFailed;
    }

    public int getmLevel() {
        return mLevel;
    }

    public void setmLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public void setmTotalPins(int mTotalPins) {
        this.mTotalPins = mTotalPins;
        mPinsLeft = mTotalPins;
        initText();
    }

    public int getmTotalPins() {
        return mTotalPins;
    }

    public GameListener getmGameListener() {
        return mGameListener;
    }

    public void setmGameListener(GameListener mGameListener) {
        this.mGameListener = mGameListener;
    }

    public void setmLevelPassed(boolean mLevelPassed) {
        this.mLevelPassed = mLevelPassed;
    }

    public boolean ismLevelPassed() {
        return mLevelPassed;
    }

    private void init(){
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();
        // 大圆半径
        mBigCircleRadius = screen_width / 8;
        // 小圆半径
        mSmallCircleRadius = mBigCircleRadius / 4;

        // 针线长度
        mLineLength = mBigCircleRadius * 3;
        mMinPinDist = 2 * Math.toDegrees(Math.atan(mSmallCircleRadius * 1.0d / mLineLength));
        Log.d(TAG, "mMinPinDist = " + mMinPinDist);

        // 大圆位置
        mCircleX = screen_width / 2;
        mCircleY = screen_height / 3;
        // 圆，实线的paint
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mContext.getResources().getColor(android.R.color.holo_blue_bright));
        mCirclePaint.setStrokeWidth(mLineWidth);
        // 虚线paint
        initDashLinePaint();
    }

    private void initText() {
        mTotalPinsString = String.valueOf(mTotalPins);
        mTotalPinsStringLength = mTotalPinsString.length();
        // big text
        {
            mBigTextSize = mBigCircleRadius;
            // paint
            mBigTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBigTextPaint.setColor(Color.WHITE);
            mBigTextPaint.setTextSize(mBigTextSize);

            mBigTextWidth = mBigTextPaint.measureText(mTotalPinsString);
            mBigTextHeight = mBigTextPaint.getFontMetrics().bottom - mBigTextPaint.getFontMetrics().top;
            Log.d(TAG, "mBigTextHeight=" + mBigTextHeight);
        }
        // small text
        {
            mSmallTextSize = mSmallCircleRadius;
            // paint
            mSmallTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mSmallTextPaint.setColor(Color.WHITE);
            mSmallTextPaint.setTextSize(mSmallTextSize);

            mSmallTextHeight = mSmallTextPaint.getFontMetrics().bottom - mSmallTextPaint.getFontMetrics().top;
            Log.d(TAG, "mBigTextHeight=" + mBigTextHeight);
        }
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
        // 大圆和文字
        drawBigCircleAndText(canvas);
        // 静止的小圆
        drawStaticCirclesAndText(canvas);
    }

    private void drawBigCircleAndText(Canvas canvas) {
        // 大圆
        canvas.drawCircle(mCircleX, mCircleY, mBigCircleRadius, mCirclePaint);
        // 大字(TODO: 为什么是mCircleY + mBigTextHeight / 4呢？？？)
        canvas.drawText(mTotalPinsString, 0, mTotalPinsStringLength, mCircleX - mBigTextWidth / 2, mCircleY + mBigTextHeight / 4, mBigTextPaint);
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
            String drawString = String.valueOf(mTotalPins - i);
            float textWidth = mSmallTextPaint.measureText(drawString);
            canvas.drawText(drawString, 0, drawString.length(),
                    xEnd - textWidth / 2, yEnd + mSmallTextHeight / 4, mSmallTextPaint);
        }
    }

    private void drawStaticCirclesAndText(Canvas canvas) {
        int circleY = mDashLineEndY + mSmallCircleRadius;
        int drawCircle = 0;
        String drawString;
        while(circleY + mSmallCircleRadius < screen_height - 100 &&
                mPinsLeft > drawCircle){
            canvas.drawCircle(mCircleX, circleY, mSmallCircleRadius, mCirclePaint);
            drawString = String.valueOf(mPinsLeft - drawCircle);

            float textWidth = mSmallTextPaint.measureText(drawString);
            canvas.drawText(drawString, 0, drawString.length(), mCircleX - textWidth / 2, circleY + mSmallTextHeight / 4, mSmallTextPaint);

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
                if(mLevelFailed){
                    break;
                }
                mLevelFailed = stickToAnotherPin();

                if(mGameListener != null){
                    mGameListener.onOnePinStick(mTotalPins - mPinsLeft + 1);
                }

                mStickPins.add(360 - mAngle);
                mPinsLeft--;
                if(mPinsLeft > 0) {
                    invalidate();
                }else if(!mLevelFailed){
                    // Level passed
                    Toast.makeText(mContext, "You win!", Toast.LENGTH_LONG).show();
                    if(mGameListener != null){
                        mGameListener.onGameOver(true);
                    }
                    mLevelPassed = true;
                    return false;
                }
                // Level failed
                if(mLevelFailed){
                    if(mGameListener != null){
                        mGameListener.onGameOver(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 检查是否插到其他的针上
     * @return true：当前针跟其他针插在一起；false: 当前针不跟其他针插在一起；
     */
    private boolean stickToAnotherPin(){
        int totalOriginalPins = mOriginPins.length;
        for(int i=0; i<totalOriginalPins; i++){
            double delta = Math.abs(mOriginPins[i] - (360 -mAngle));
            Log.d(TAG, "delta1 = " + delta);
            if(delta < mMinPinDist){
                return true;
            }
        }
        int totalStickPins = mStickPins.size();
        for(int i=0; i<totalStickPins; i++){
            double delta = Math.abs(mStickPins.get(i) - (360 -mAngle));
            Log.d(TAG, "delta2 = " + delta);
            if(delta < mMinPinDist){
                return true;
            }
        }
        return false;
    }
}