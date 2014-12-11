package com.itransition.android.dolbik.andiclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by p.dolbik on 09.12.2014.
 */
public class AnDiView extends View {

    // Compass direction
    private float bearing;
    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private float textHeight;







    private float pitch;
    private float roll;


    private int[] glassGradientColors;
    private float[] glassGradientPositions;

    private int[] glassGradientColorsInner;
    private float[] glassGradientPositionsInner;

    private int skyHorizonColorFrom;
    private int skyHorizonColorTo;
    private int groundHorizonColorFrom;
    private int groundHorizonColorTo;

    private enum CompassDirection { N, NE, E,  SE, S, SW, W, NW }

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

        Resources resources = getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(resources.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextSize(30);
        textHeight =  textPaint.getTextSize();

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(resources.getColor(R.color.marker_color));
        markerPaint.setAlpha(200);
        markerPaint.setStrokeWidth(5);
        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setShadowLayer(2, 1, 1, resources.getColor(R.color.shadow_color));


        // Translucent glass dome & volume effect
        glassGradientColors = new int[5];
        glassGradientPositions = new float[5];

        int glassColor = 245;
        glassGradientColors[4] = Color.argb(0, glassColor, glassColor, glassColor);
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




        // Sky & Earth
        skyHorizonColorFrom = resources.getColor(R.color.horizon_sky_from);
        skyHorizonColorTo = resources.getColor(R.color.horizon_sky_to);
        groundHorizonColorFrom = resources.getColor(R.color.horizon_ground_from);
        groundHorizonColorTo = resources.getColor(R.color.horizon_ground_to);
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


    public float getBearing() {
        return bearing;
    }
    public void setBearing(float bearing) {
        this.bearing = bearing;
        // Changed direction
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        float ringWidth = textHeight + 20;
        float secondRingWidth = 5;
        float fourthRingWidth = 5;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int px = width/2;
        int py = height/2;
        Point center = new Point(px, py);

        int radius = Math.min(px, py);
        Log.d("Pasha", "radius " + radius);

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
        Log.d("Pasha", "secondRadius "+secondRadius);

        RectF thirdBox = new RectF(
                center.x - secondRadius + secondRingWidth,
                center.y - secondRadius + secondRingWidth,
                center.x + secondRadius - secondRingWidth,
                center.y + secondRadius - secondRingWidth);

        float thirdRadius = thirdBox.height()/2;
        Log.d("Pasha", "thirdRadius " + thirdRadius);

        RectF fourthBox = new RectF(
                center.x - radius/2,
                center.y - radius/2,
                center.x + radius/2,
                center.y + radius/2);
        float fourthRadius = fourthBox.height()/2;
        Log.d("Pasha", "fourthBox " + fourthBox);

        RectF fifthBox = new RectF(
                center.x - fourthRadius + fourthRingWidth,
                center.y - fourthRadius + fourthRingWidth,
                center.x + fourthRadius - fourthRingWidth,
                center.y + fourthRadius - fourthRingWidth);
        float fifthRadius = fourthBox.height()/2;




        canvas.drawOval(firstBox, circlePaint);

        for( int i = 0; i < 8; i++) {
            CompassDirection cd = CompassDirection.values()[i];
            String text = cd.toString();
            float textSizeWidth = textPaint.measureText(text);

            PointF headStringCenter = new PointF(
                    center.x ,
                    firstBox.top + textHeight + 5);

                canvas.drawText(
                        text,
                        headStringCenter.x - textSizeWidth/2,
                        headStringCenter.y,
                        textPaint);
            canvas.rotate(45, center.x, center.y);
        }

        circlePaint.setColor(Color.BLACK);
        canvas.drawOval(secondBox, circlePaint);

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
        int currentTimeFontSize = 100;


        Paint fontPaint = new Paint();
        fontPaint.setColor(Color.WHITE);
        fontPaint.setTextSize(currentDateFontSize);
        float dateWidth = fontPaint.measureText(currentDate);
        canvas.drawText(currentDate, px - dateWidth/2, py - fifthRadius/2, fontPaint);

        fontPaint.setTextSize(currentTimeFontSize);
        float timeWidth = fontPaint.measureText(currentTime);
        float timeHeight = fontPaint.descent() - fontPaint.ascent();
        canvas.drawText(currentTime, px - timeWidth/2, py + timeHeight, fontPaint);







    }


}
