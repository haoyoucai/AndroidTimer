package com.highersun.androidtimer;

/**
 * Author: ShenDanLai on 2016/9/14.
 * Email: 17721129316@163.com
 */

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: ShenDanLai on 2016/9/12.
 * Email: 17721129316@163.com
 */

public class SignCricleView extends View {
    // 最外层圆环渐变色环颜色
    private final int[] mColors = new int[]{
            0xFFFF0000,
            0xFFFFD600,
            0xFF00FF00
    };
    // 宽度
    private int width;

    // 高度
    private int height;

    // 半径
    private int radius;


    // 最外层渐变圆环画笔
    private Paint mGradientRingPaint;

    //默认的颜色
    private Paint mDefaultRingPaint;


    // 绘制外层圆环的矩形
    private RectF mOuterArc;


    private static final float mStartAngle = 90;

    // 圆环结束角度圆环结束角度
    private static final float mEndAngle = 255;




    // View默认宽高值
    private int defaultSize;


    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    private float mCurrentAngle = 0;
    private float mTotalAngle = 263f;

    //时间角度,每分钟动画
    private float mTimeAngle = mTotalAngle/59;

    public SignCricleView(Context context) {

        this(context, null);
    }

    public SignCricleView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public SignCricleView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //设置默认宽高值
        defaultSize = dp2px(200);

        //设置图片线条的抗锯齿
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter
                (0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //最外层圆环渐变画笔设置
        mGradientRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置圆环渐变色渲染
        mGradientRingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        float position[] = {0.1f, 0.3f, 0.8f};
        Shader mShader = new SweepGradient(width / 2, radius, mColors, position);
        mGradientRingPaint.setShader(mShader);
//        mGradientRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mGradientRingPaint.setStyle(Paint.Style.STROKE);
        mGradientRingPaint.setStrokeWidth(40);
        PathEffect mPathEffect = new DashPathEffect(new float[]{10f, 10f}, 1);
        mGradientRingPaint.setPathEffect(mPathEffect);


        mDefaultRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDefaultRingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        mDefaultRingPaint.setStyle(Paint.Style.STROKE);
        mDefaultRingPaint.setStrokeWidth(40);
        mDefaultRingPaint.setColor(Color.parseColor("#A0A5A5A5"));
        PathEffect mPathEffect1 = new DashPathEffect(new float[]{10f, 10f}, 1);
        mDefaultRingPaint.setPathEffect(mPathEffect1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(resolveMeasure(widthMeasureSpec, defaultSize),
                resolveMeasure(heightMeasureSpec, defaultSize));
    }


    /**
     * 根据传入的值进行测量
     *
     * @param measureSpec
     * @param defaultSize
     */
    public int resolveMeasure(int measureSpec, int defaultSize) {

        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {

            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;

            case MeasureSpec.AT_MOST:
                //设置warp_content时设置默认值
                result = Math.min(specSize, defaultSize);
                break;
            case MeasureSpec.EXACTLY:
                //设置math_parent 和设置了固定宽高值
                break;

            default:
                result = defaultSize;
        }

        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        //确定View宽高
        width = w;
        height = h;

        //圆环半径
        radius = width / 2;

        //外层圆环
        float oval1 = radius - mGradientRingPaint.getStrokeWidth() * 0.5f;
        mOuterArc = new RectF(-oval1, -oval1, oval1, oval1);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //设置画布绘图无锯齿
        canvas.setDrawFilter(mPaintFlagsDrawFilter);

        drawArc(canvas);
    }


    /**
     * 分别绘制外层 中间 内层圆环
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {

        canvas.save();
        canvas.translate(width / 2, height / 2);

        //画最外层的渐变圆环
        canvas.rotate(50);
        canvas.drawArc(mOuterArc, mStartAngle, mTotalAngle, false, mDefaultRingPaint);
        canvas.drawArc(mOuterArc, mStartAngle, mCurrentAngle, false, mGradientRingPaint);

    }

    /**
     * dp2px
     *
     * @param values
     * @return
     */
    public int dp2px(int values) {

        float density = getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5f);
    }

    public void startAnim(int second) {


        ValueAnimator mAngleAnim = ValueAnimator.ofFloat(mCurrentAngle, second*mTimeAngle);
        mAngleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAngleAnim.setDuration(3000);
        mAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mCurrentAngle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mAngleAnim.start();
    }

}
