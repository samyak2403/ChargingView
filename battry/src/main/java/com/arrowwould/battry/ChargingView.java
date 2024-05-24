package com.arrowwould.battry;


import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ChargingView extends View {
    private int currentProgress; // Current progress
    private int bgColor; // Background color
    private int chargingColor; // Charging color
    private int textColor; // Text color
    private float textSize; // Text size

    private Paint chargingPaint; // Paint for charging effect
    private Paint backgroundPaint; // Paint for background
    private Paint textPaint; // Paint for text

    private RectF chargingRectF = new RectF(); // Charging rectangle
    private RectF headRectF = new RectF(); // Charging head rectangle

    private Path wavePath; // Path for wave animation
    private Path clipPath; // Path to clip the view

    private float waveHeight; // Wave height

    private ValueAnimator waveAnimator; // Animator for wave animation
    private float waveAnimatorRatio = 0; // Animation ratio

    private Rect textBound = new Rect(); // Text bounds for measuring text

    public ChargingView(Context context) {
        super(context);
        init(null);
    }

    public ChargingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ChargingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChargingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            parseAttr(attrs);
        }

        setupPaints();
        setupAnimator();

        wavePath = new Path();
        clipPath = new Path();
    }

    private void parseAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ChargingView);

        bgColor = a.getColor(R.styleable.ChargingView_bg_color, Color.GRAY);
        chargingColor = a.getColor(R.styleable.ChargingView_chargingColor, Color.GREEN);
        currentProgress = a.getInteger(R.styleable.ChargingView_progress, 0);
        textSize = a.getDimension(R.styleable.ChargingView_progressTextSize, 20);
        textColor = a.getColor(R.styleable.ChargingView_progressTextColor, Color.WHITE);

        currentProgress = Math.min(currentProgress, 100);

        a.recycle();
    }

    private void setupPaints() {
        chargingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        chargingPaint.setColor(chargingColor);
        chargingPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(bgColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    private void setupAnimator() {
        waveAnimator = ValueAnimator.ofFloat(-2, 2);
        waveAnimator.setDuration(4000);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.addUpdateListener(animation -> {
            waveAnimatorRatio = (float) animation.getAnimatedValue();
            invalidate();
        });
        waveAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRects(w, h);
    }

    private void initRects(int width, int height) {
        float headWidth = width / 3f;
        float headHeight = height / 15f;

        headRectF.set((width - headWidth) / 2, 0, (width + headWidth) / 2, headHeight);
        chargingRectF.set(0, headHeight, width, height);

        clipPath.reset();
        clipPath.moveTo(0, headHeight);
        clipPath.lineTo(width / 2 - headWidth / 2, headHeight);
        clipPath.lineTo(width / 2 - headWidth / 2, 0);
        clipPath.lineTo(width / 2 + headWidth / 2, 0);
        clipPath.lineTo(width / 2 + headWidth / 2, headHeight);
        clipPath.lineTo(width, headHeight);
        clipPath.lineTo(width, height);
        clipPath.lineTo(0, height);
        clipPath.close();

        waveHeight = headHeight * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipPath(clipPath);

        canvas.drawRect(headRectF, backgroundPaint);

        canvas.save();
        canvas.translate(0, headRectF.height());
        canvas.drawRect(chargingRectF, backgroundPaint);

        drawWave(canvas);

        drawProgress(canvas);
        canvas.restore();
    }

    private void drawWave(Canvas canvas) {
        wavePath.reset();
        float progressHeight = chargingRectF.height() * (1 - currentProgress / 100f);

        wavePath.moveTo(-2 * chargingRectF.width() + waveAnimatorRatio * chargingRectF.width(), progressHeight);

        for (int i = -2; i < 2; i++) {
            wavePath.rQuadTo(chargingRectF.width() / 2, -waveHeight, chargingRectF.width(), 0);
            wavePath.rQuadTo(chargingRectF.width() / 2, waveHeight, chargingRectF.width(), 0);
        }

        wavePath.lineTo(chargingRectF.width(), chargingRectF.height());
        wavePath.lineTo(0, chargingRectF.height());
        wavePath.close();

        canvas.drawPath(wavePath, chargingPaint);
    }

    private void drawProgress(Canvas canvas) {
        String text = currentProgress + "%";
        textPaint.getTextBounds(text, 0, text.length(), textBound);

        float x = (chargingRectF.width() - textBound.width()) / 2;
        float y = (chargingRectF.height() + textBound.height()) / 2;

        canvas.drawText(text, x, y, textPaint);
    }

    public void setProgress(int progress) {
        this.currentProgress = Math.min(progress, 100);
        invalidate();
    }

    public int getProgress() {
        return currentProgress;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        backgroundPaint.setColor(bgColor);
        invalidate();
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setChargingColor(int chargingColor) {
        this.chargingColor = chargingColor;
        chargingPaint.setColor(chargingColor);
        invalidate();
    }

    public int getChargingColor() {
        return chargingColor;
    }
}
