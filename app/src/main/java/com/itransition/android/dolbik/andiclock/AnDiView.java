package com.itransition.android.dolbik.andiclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by p.dolbik on 09.12.2014.
 */
public class AnDiView extends View {

    // Compass direction
    private Paint markerPaint;
    private Paint textPaint;
    private Paint mainCirclePaint;
    private Paint circlePaint;
    private float textHeight;

    private int[] glassGradientColors;
    private float[] glassGradientPositions;

    private int[] glassGradientColorsInner;
    private float[] glassGradientPositionsInner;


    private enum CompassDirection { N, NE, E,  SE, S, SW, W, NW }

    private CountDownTimer countDownTimer;




    public AnDiView(Context context) {
        super(context);
        initAnDiView();
    }

    public AnDiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnDiView();
    }

    public AnDiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnDiView();
    }



    // Initialization view
    private void initAnDiView() {
        setFocusable(true);

        countDownTimer = new CountDownTimer(1000);
        countDownTimer.start();

        mainCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainCirclePaint.setColor(getResources().getColor(R.color.background_color));
        mainCirclePaint.setStrokeWidth(1);
        mainCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(getResources().getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextSize(30);
        textHeight =  textPaint.getTextSize();

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(getResources().getColor(R.color.marker_color));
        markerPaint.setAlpha(200);
        markerPaint.setStrokeWidth(5);
        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setShadowLayer(2, 1, 1, getResources().getColor(R.color.shadow_color));


        // Translucent glass dome & volume effect
        glassGradientColors = new int[5];
        glassGradientPositions = new float[5];

        int glassColor = 245;
        glassGradientColors[4] = Color.argb(10, glassColor, glassColor, glassColor);
        glassGradientColors[3] = Color.argb(100, glassColor, glassColor, glassColor);
        glassGradientColors[2] = Color.argb(80, glassColor, glassColor, glassColor);
        glassGradientColors[1] = Color.argb(0, glassColor, glassColor, glassColor);
        glassGradientColors[0] = Color.argb(0, glassColor, glassColor, glassColor);

        glassGradientPositions[4] = 1-0.0f;
        glassGradientPositions[3] = 1-0.06f;
        glassGradientPositions[2] = 1-0.10f;
        glassGradientPositions[1] = 1-0.20f;
        glassGradientPositions[0] = 1-1.0f;


        glassGradientColorsInner = new int[5];
        glassGradientPositionsInner = new float[5];

        glassGradientColorsInner[4] = Color.argb(100, glassColor, glassColor, glassColor);
        glassGradientColorsInner[3] = Color.argb(100, glassColor, glassColor, glassColor);
        glassGradientColorsInner[2] = Color.argb(80, glassColor, glassColor, glassColor);
        glassGradientColorsInner[1] = Color.argb(0, glassColor, glassColor, glassColor);
        glassGradientColorsInner[0] = Color.argb(0, glassColor, glassColor, glassColor);

        glassGradientPositionsInner[4] = 1-0.0f;
        glassGradientPositionsInner[3] = 1-0.06f;
        glassGradientPositionsInner[2] = 1-0.10f;
        glassGradientPositionsInner[1] = 1-0.20f;
        glassGradientPositionsInner[0] = 1-1.0f;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth  = measure(widthMeasureSpec);
        int measureHeight = measure(heightMeasureSpec);

        // Return min size between measureWidth & measureHeight
        int d = Math.min(measureWidth, measureHeight);

        setMeasuredDimension(d, d);
    }


    // Calculate size view
    private int measure( int measureSpec ) {
       int result = 0;

        // Decoding parameters measureSpec
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if( specMode == MeasureSpec.UNSPECIFIED ) {
            // If edge not define? return this size
            result = 200;
        } else{
            // Return max size
            result = specSize;
        }

        return result;
    }




    @Override
    protected void onDraw(Canvas canvas) {

        float ringWidth = textHeight + 20;
        float secondRingWidth = 5;
        float fourthRingWidth = 5;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int px = width/2 - 50;
        int py = height/2 - 50;
        Point center = new Point(px, py);

        int radius = Math.min(px, py);

        RectF firstBox = new RectF(
                center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius);

        RectF secondBox = new RectF(
                center.x - radius + ringWidth,
                center.y - radius + ringWidth,
                center.x + radius - ringWidth,
                center.y + radius - ringWidth);

        float secondRadius = secondBox.height()/2;

        RectF thirdBox = new RectF(
                center.x - secondRadius + secondRingWidth,
                center.y - secondRadius + secondRingWidth,
                center.x + secondRadius - secondRingWidth,
                center.y + secondRadius - secondRingWidth);

        float thirdRadius = thirdBox.height()/2;

        RectF fourthBox = new RectF(
                center.x - radius/2,
                center.y - radius/2,
                center.x + radius/2,
                center.y + radius/2);
        float fourthRadius = fourthBox.height()/2;

        RectF fifthBox = new RectF(
                center.x - fourthRadius + fourthRingWidth,
                center.y - fourthRadius + fourthRingWidth,
                center.x + fourthRadius - fourthRingWidth,
                center.y + fourthRadius - fourthRingWidth);
        float fifthRadius = fourthBox.height()/2;



        canvas.drawOval(firstBox, mainCirclePaint);

        for( int i = 0; i < 8; i++) {
            CompassDirection cd = CompassDirection.values()[i];
            String text = cd.toString();
            float textSizeWidth = textPaint.measureText(text);

            PointF headStringCenter = new PointF(
                    center.x ,
                    firstBox.top + textHeight + 10);

                canvas.drawText(
                        text,
                        headStringCenter.x - textSizeWidth/2,
                        headStringCenter.y,
                        textPaint);
            canvas.rotate(45, center.x, center.y);
        }

        circlePaint.setColor(Color.BLACK);
        canvas.drawOval(secondBox, circlePaint);
        canvas.save();

        circlePaint.setColor(Color.RED);
        canvas.drawOval(thirdBox, circlePaint);



        canvas.save();
        canvas.rotate(30, center.x, center.y);
        for( int i = 0; i < 60; i++) {

            if( i % 5 == 0) {
                markerPaint.setStrokeWidth(10);
                canvas.drawLine(px, py-thirdRadius, px, py-thirdRadius+20, markerPaint );
                String number = String.valueOf(i/5 +1);
                textPaint.setTextSize(40);
                float numberWidth = textPaint.measureText(number);
                canvas.drawText(number, px-numberWidth/2, py-thirdRadius +60, textPaint);
            } else{
                markerPaint.setStrokeWidth(5);
                canvas.drawLine(px, py-thirdRadius, px, py-thirdRadius+10, markerPaint );
            }

            canvas.rotate(6, center.x, center.y);
        }
        canvas.restore();


        RadialGradient glassShader = new RadialGradient(
                px, py, (int)thirdRadius,
                glassGradientColors,
                glassGradientPositions,
                Shader.TileMode.CLAMP);

        Paint glassPaint = new Paint();
        glassPaint.setShader(glassShader);

        canvas.drawOval(thirdBox, glassPaint);

        Calendar c = Calendar.getInstance();

        int minute = c.get(Calendar.MINUTE);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(px, py, 20, circlePaint);
        circlePaint.setStrokeWidth(10);
        canvas.save();
        canvas.rotate(minute*6, px, py);
        canvas.drawLine(px, py+50, px, py - thirdRadius + 70 , circlePaint);
        canvas.restore();


        int hour = c.get(Calendar.HOUR_OF_DAY);
        circlePaint.setStrokeWidth(15);
        canvas.save();
        canvas.rotate((float) ( (hour*30) + (minute*0.5) ), px, py);
        canvas.drawLine(px, py+50, px, py - thirdRadius + 110 , circlePaint);
        canvas.restore();


        int seconds = c.get(Calendar.SECOND);
        circlePaint.setStrokeWidth(5);
        canvas.save();
        canvas.rotate(seconds*6, px, py);
        canvas.drawLine(px, py+50, px, py - thirdRadius , circlePaint);
        canvas.restore();




       circlePaint.setColor(Color.BLACK);
        circlePaint.setStrokeWidth(10);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawOval(fourthBox, circlePaint);

        circlePaint.setColor(Color.RED);
        canvas.drawOval(fifthBox, circlePaint);


        RadialGradient glassShaderInner = new RadialGradient(
                px, py, (int)fourthRadius,
                glassGradientColorsInner,
                glassGradientPositionsInner,
                Shader.TileMode.CLAMP);

        Paint glassPaintInner = new Paint();
        glassPaintInner.setShader(glassShaderInner);

        canvas.drawOval(fourthBox, glassPaintInner);



        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdfDate.format(new Date());
        int currentDateFontSize = 50;

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdfTime.format(new Date());
        int currentTimeFontSize = 110;


        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");
        String currentDayOfTheWeek = sdfDay.format(new Date());
        int currentDayOfTheWeekSize = 50;


        Paint fontPaint = new Paint();
        fontPaint.setColor(Color.WHITE);
        fontPaint.setTextSize(currentDateFontSize);
        float dateWidth = fontPaint.measureText(currentDate);
        canvas.drawText(currentDate, px - dateWidth/2, py - fifthRadius/2, fontPaint);


        fontPaint.setTextSize(currentDayOfTheWeekSize);
        float currentDayOfTheWeekWidth = fontPaint.measureText(currentDayOfTheWeek);
        canvas.drawText(currentDayOfTheWeek, px - currentDayOfTheWeekWidth/2, py + fifthRadius/2 +40, fontPaint);


        fontPaint.setTextSize(currentTimeFontSize);
        fontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fontPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        float timeWidth = fontPaint.measureText(currentTime);
        Rect textBounds = new Rect();
        fontPaint.getTextBounds(currentTime, 0, currentTime.length(), textBounds);
        float timeHeight = textBounds.height();
        canvas.drawText(currentTime, px - timeWidth/2, py + timeHeight/2, fontPaint);


    }



    private  class CountDownTimer {
        private long countDownInterval;
        private boolean status;

        public CountDownTimer( long pCountDownInterval) {

            this.countDownInterval = pCountDownInterval;
            status = false;
            Initialize();
        }

        private void stop() {
            status = false;
        }
        private void start() {
            status = true;
        }

        private void onFinish(){
            Log.d("Pasha", "onFinish");

        }

        public void Initialize()
        {
            final Handler handler = new Handler();
            Log.d("Pasha", "starting");
            final Runnable counter = new Runnable(){

                public void run(){
                    handler.postDelayed(this, countDownInterval);
                    if(!status) {
                        handler.removeCallbacks(this);
                        onFinish();
                    } else {
                        AnDiView.this.invalidate();
                    }
                }
            };
            handler.postDelayed(counter, 0);
        }
    }


}
