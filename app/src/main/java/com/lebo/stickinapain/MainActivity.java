package com.lebo.stickinapain;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.lebo.stickinapain.view.GameView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    GameView mGameView;
    boolean mResumed;
    double mAngleInc =1;
    int mTimeInc = 100;
    private static final int refresh_game_view = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGameView = (GameView)findViewById(R.id.mGameVimew);
        mGameView.setOriginPins(new double[]{0, 90, 180});
    }

    private void startToUpdateGameView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mResumed){
                    mGameView.setmAngle((mGameView.getmAngle() + mAngleInc) % 360);
                    mHandler.sendEmptyMessage(refresh_game_view);
                    try {
                        Thread.sleep(mTimeInc);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        startToUpdateGameView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(refresh_game_view);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case refresh_game_view:
                    mGameView.invalidate();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
