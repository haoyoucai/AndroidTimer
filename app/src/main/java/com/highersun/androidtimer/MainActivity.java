package com.highersun.androidtimer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zcw.togglebutton.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView tvHour, tvMinute, tvSecond, tvMilliSecond, tvStart;
    private ToggleButton toggleButton;
    private SignCricleView signCricleView;
    private Handler handler;
    private Timer timer;
    private TimerTask timerTask;
    static int hour = -1;
    static int minute = -1;
    static int second = -1;
    static int millisecond = -1;
    final int TimeGapMilliSecond = 1;
    final int TimeGapSecond = 1000;
    final int TimeGapMinute = 60 * 1000;
    boolean isCutDown; // 是否是倒计时
    boolean isTimer = true; //是否在计时


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHour = (TextView) findViewById(R.id.tv_count_down_hour);
        tvMinute = (TextView) findViewById(R.id.tv_count_down_minute);
        tvSecond = (TextView) findViewById(R.id.tv_count_down_second);
        tvMilliSecond = (TextView) findViewById(R.id.tv_count_down_millisecond);
        signCricleView = (SignCricleView) findViewById(R.id.signCricleView);
        toggleButton = (ToggleButton) findViewById(R.id.tgbtn_cutdown);
        tvStart = (TextView) findViewById(R.id.tv_start);
        tvStart.setVisibility(View.GONE);
//
        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                isCutDown = on;
            }
        });


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                if (hour < 0 || minute < 0 || second < 0 || millisecond < 0) {
//                    throw new IllegalArgumentException("hour、minute、second or millisecond can't below zero");
//                }
                if (msg.what == 0) {  //msg.what == 0 倒计时


                    if (millisecond == 0) {
                        if (second == 0) {
                            if (minute == 0) {
                                if (hour == 0) {
                                    //时分秒毫秒都为0
                                    if (timer != null) {
                                        timer.cancel();
                                        timer = null;
                                    }
                                    if (timerTask != null) {
                                        timerTask = null;
                                    }
                                } else {
                                    hour--;
                                    minute = 59;
                                }
                            } else {
                                minute--;
                                second = 59;
                            }
                        } else {
                            second--;
                            signCricleView.startAnim(second);
                            millisecond = 999;
                        }
                    } else {
                        millisecond--;
                    }

                } else if (msg.what == 1) { // msg.what == -1 正计时
                    if (millisecond == 999) {
                        if (second == 59) {
                            if (minute == 59) {
                                hour++;
                                millisecond = 0;
                                second = 0;
                                minute = 0;
                            } else {
                                minute++;
                                second = 0;
                                millisecond = 0;
                            }
                        } else {
                            second++;
                            signCricleView.startAnim(second);
                            millisecond = 0;
                        }
                    } else {
                        millisecond++;
                    }
                }
                tvShow();
            }
        };
        setOriginTime(1, 1, 1, 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                if (isCutDown) {
                    msg.what = 0;
                } else {
                    msg.what = 1;
                }
                handler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, TimeGapMilliSecond);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask = null;
        }
    }

    public void tvShow() {
        tvMilliSecond.setText(alignment3String(millisecond));
        tvSecond.setText(alignmentString(second));
        tvMinute.setText(alignmentString(minute));
        tvHour.setText(alignmentString(hour));
    }


    public String alignmentString(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }

    public String alignment3String(int time) {
        if (time < 100) {
            return "0" + time;
        } else if (time < 10) {
            return "00" + time;
        } else {
            return "" + time;
        }
    }

    public void setOriginTime(int setHour, int setMinute, int setSecond, int setMilliSecond) {
        hour = setHour;
        minute = setMinute;
        second = setSecond;
        millisecond = setMilliSecond;
    }
}
