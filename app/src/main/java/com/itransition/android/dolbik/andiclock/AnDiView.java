package com.itransition.android.dolbik.andiclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

    private Paint markerPaint;
    private Paint textPaint;
    private Paint mainCirclePaint;
    private Paint secondCirclePaint;
    private Paint thirdCirclePaint;
    private Paint fourthCirclePaint;
    private Paint fifthCirclePaint;
    private float textHeight;

    private int[] glassGradientColors;
    private float[] glassGradientPositions;

    private int[] glassGradientColorsInner;
    private float[] glassGradientPositionsInner;


    private enum CompassDirection { N, NE, E,  SE, S, SW, W, NW }

    private CountDownTimer countDownTimer;

    private int ringWidth;
    private float secondRingWidth;
    private float fourthRingWidth;
    private int heightDivisionHours = 20;
    private int heightDivisionMinute;

    private int height;
    private int width;

    private int px ;
    private int py;
    private Point center;

    private int radius;
    private RectF firstBox;
    private RectF secondBox;
    private RectF thirdBox;
    private RectF fourthBox;
    private RectF fifthBox;

    private float secondRadius;
    private float thirdRadius;
    private float fourthRadius;
    private float fifthRadius;

    private float clockNumber;
    private int clockNumberPosition;

    private Paint glassPaint;
    private Paint arrowsPaint;
    private Paint glassPaintInner;
    private Paint fontPaint ;




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

        Log.d("Pasha", AnDiView.this.getMeasuredWidth()+"");
        setFocusable(true);

        countDownTimer = new CountDownTimer(1000);
        countDownTimer.start();

        ringWidth = 20;
        secondRingWidth = 10;
        fourthRingWidth = 5;

        mainCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainCirclePaint.setColor(Color.BLACK);
        mainCirclePaint.setStrokeWidth(5);
        mainCirclePaint.setStyle(Paint.Style.STROKE);

        secondCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondCirclePaint.setColor(Color.BLACK);
        secondCirclePaint.setStrokeWidth(1);
        secondCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        thirdCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thirdCirclePaint.setColor(Color.RED);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
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

        clockNumber = 50;
        clockNumberPosition = 80;
        heightDivisionMinute = 10;

        glassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowsPaint =  new Paint();
        arrowsPaint.setColor(Color.WHITE);
        arrowsPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        glassPaintInner =  new Paint(Paint.ANTI_ALIAS_FLAG);


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

        fourthCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fourthCirclePaint.setColor(Color.BLACK);
        fourthCirclePaint.setStrokeWidth(10);
        fourthCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        fifthCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fifthCirclePaint.setColor(Color.RED);

        fontPaint = new Paint();
        fontPaint.setColor(Color.WHITE);

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

        height = getMeasuredHeight();
        width = getMeasuredWidth();

        px = width/2;
        py = height/2;
        center = new Point(px, py);

        radius = Math.min(px, py);

        firstBox = new RectF(
                center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius);

        secondBox = new RectF(
                center.x - radius + ringWidth,
                center.y - radius + ringWidth,
                center.x + radius - ringWidth,
                center.y + radius - ringWidth);
        secondRadius = secondBox.height()/2;

        thirdBox = new RectF(
                center.x - secondRadius + secondRingWidth,
                center.y - secondRadius + secondRingWidth,
                center.x + secondRadius - secondRingWidth,
                center.y + secondRadius - secondRingWidth);
        thirdRadius = thirdBox.height()/2;

        fourthBox = new RectF(
                center.x - radius/2,
                center.y - radius/2,
                center.x + radius/2,
                center.y + radius/2);
        fourthRadius = fourthBox.height()/2;

        fifthBox = new RectF(
                center.x - fourthRadius + fourthRingWidth,
                center.y - fourthRadius + fourthRingWidth,
                center.x + fourthRadius - fourthRingWidth,
                center.y + fourthRadius - fourthRingWidth);
        fifthRadius = fourthBox.height()/2;


        canvas.drawOval(firstBox, mainCirclePaint);
        canvas.drawOval(secondBox, secondCirclePaint);
        canvas.drawOval(thirdBox, thirdCirclePaint);


        canvas.save();
        canvas.rotate(30, center.x, center.y);
        for( int i = 0; i < 60; i++) {

            if( i % 5 == 0) {
                markerPaint.setStrokeWidth(10);
                canvas.drawLine(px, py-thirdRadius, px, py-thirdRadius+heightDivisionHours, markerPaint );
                String number = String.valueOf(i/5 +1);
                textPaint.setTextSize(clockNumber);
                float numberWidth = textPaint.measureText(number);
                canvas.drawText(number, px-numberWidth/2, py-thirdRadius+clockNumberPosition, textPaint);
            } else{
                markerPaint.setStrokeWidth(5);
                canvas.drawLine(px, py-thirdRadius, px, py-thirdRadius+heightDivisionMinute, markerPaint );
            }

            canvas.rotate(6, center.x, center.y);
        }
        canvas.restore();


        Calendar c = Calendar.getInstance();

        int minute = c.get(Calendar.MINUTE);
        canvas.drawCircle(px, py, 20, arrowsPaint);
        arrowsPaint.setStrokeWidth(10);
        canvas.save();
        canvas.rotate(minute*6, px, py);
        canvas.drawLine(px, py+50, px, py - thirdRadius + heightDivisionHours + heightDivisionMinute , arrowsPaint);
        canvas.restore();


        int hour = c.get(Calendar.HOUR_OF_DAY);
        arrowsPaint.setStrokeWidth(15);
        canvas.save();
        canvas.rotate((float) ( (hour*30) + (minute*0.5) ), px, py);
        canvas.drawLine(px, py+50, px, py - thirdRadius + clockNumberPosition +heightDivisionHours + heightDivisionMinute , arrowsPaint);
        canvas.restore();

        int seconds = c.get(Calendar.SECOND);
        arrowsPaint.setStrokeWidth(5);
        canvas.save();
        canvas.rotate(seconds*6, px, py);
        canvas.drawLine(px, py+50, px, py - thirdRadius , arrowsPaint);
        canvas.restore();


        RadialGradient glassShader = new RadialGradient(
                px, py, (int)thirdRadius,
                glassGradientColors,
                glassGradientPositions,
                Shader.TileMode.CLAMP);

        glassPaint.setShader(glassShader);
        canvas.drawOval(thirdBox, glassPaint);


        canvas.drawOval(fourthBox, fourthCirclePaint);

        canvas.drawOval(fifthBox, fifthCirclePaint);


        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdfDate.format(new Date());

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdfTime.format(new Date());

        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");
        String currentDayOfTheWeek = sdfDay.format(new Date());

        fontPaint.setTextSize((float) (fifthRadius/4.2));
        float dateWidth = fontPaint.measureText(currentDate);
        canvas.drawText(currentDate, px - dateWidth/2, py - fifthRadius/2, fontPaint);


        fontPaint.setTextSize((float) (fifthRadius/4.2));
        float currentDayOfTheWeekWidth = fontPaint.measureText(currentDayOfTheWeek);
        canvas.drawText(currentDayOfTheWeek, px - currentDayOfTheWeekWidth/2, py + fifthRadius/2 +40, fontPaint);


        fontPaint.setTextSize((float) (fifthRadius/2.2));
        fontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fontPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        float timeWidth = fontPaint.measureText(currentTime);
        Rect textBounds = new Rect();
        fontPaint.getTextBounds(currentTime, 0, currentTime.length(), textBounds);
        float timeHeight = textBounds.height();
        canvas.drawText(currentTime, px - timeWidth/2, py + timeHeight/2, fontPaint);


        RadialGradient glassShaderInner = new RadialGradient(
                px, py, (int)fourthRadius,
                glassGradientColorsInner,
                glassGradientPositionsInner,
                Shader.TileMode.CLAMP);

        glassPaintInner.setShader(glassShaderInner);
        canvas.drawOval(fourthBox, glassPaintInner);


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
