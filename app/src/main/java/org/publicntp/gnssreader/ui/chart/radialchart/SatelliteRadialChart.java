package org.publicntp.gnssreader.ui.chart.radialchart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.model.SatelliteModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SatelliteRadialChart extends View {
    private List<SatelliteModel> satelliteModels = new ArrayList<>();

    private Paint blackFill;
    private Paint textFill;
    private Paint cardinalTextFill;
    private Paint whiteFill;
    private Paint greyFill;
    private Paint lightGreyFill;
    private Paint darkGreyFill;
    private Paint greyStroke;

    public SatelliteRadialChart(Context context) {
        super(context);
        init();
    }

    public SatelliteRadialChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SatelliteRadialChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SatelliteRadialChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public float satelliteScale() {
        return .75f;
    }

    public void init() {
        cardinalTextFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        cardinalTextFill.setColor(ContextCompat.getColor(this.getContext(), R.color.black));
        cardinalTextFill.setTextAlign(Paint.Align.CENTER);
        cardinalTextFill.setTextSize(75f);

        textFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        textFill.setColor(ContextCompat.getColor(this.getContext(), R.color.black));
        textFill.setTextSize(50f * satelliteScale());

        whiteFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        whiteFill.setColor(ContextCompat.getColor(this.getContext(), R.color.white));

        blackFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackFill.setColor(ContextCompat.getColor(this.getContext(), R.color.black));

        greyFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        greyFill.setColor(ContextCompat.getColor(this.getContext(), R.color.grey));

        lightGreyFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightGreyFill.setColor(ContextCompat.getColor(this.getContext(), R.color.greylight));

        darkGreyFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        darkGreyFill.setColor(ContextCompat.getColor(this.getContext(), R.color.greydark));

        greyStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        greyStroke.setColor(ContextCompat.getColor(this.getContext(), R.color.greylight));
        greyStroke.setStyle(Paint.Style.STROKE);
        greyStroke.setStrokeWidth(6f);
    }

    public void setSatelliteModels(List<SatelliteModel> satelliteModels) {
        this.satelliteModels.clear();
        this.satelliteModels.addAll(satelliteModels);
        List<Integer> prns = satelliteModels.stream().map(s -> s.prn).collect(Collectors.toList());
        this.invalidate();
    }

    float azimuth = 0f;
    Long lastSet;
    public void setCompassReading(float degrees) {
        //float endRotation = 360f - degrees;
        //float startRotation = this.getRotation();
        if(degrees < 0) {
            degrees = 360f - degrees;
        }

        if(degrees > 360) {
            degrees = degrees % 360;
        }

        if(lastSet == null || System.currentTimeMillis() - lastSet > 300 || Math.abs(azimuth - degrees) > 15f) {
            RotateAnimation rotateAnimation = new RotateAnimation(-azimuth, -degrees, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
            lastSet = System.currentTimeMillis();
            azimuth = degrees;
            rotateAnimation.setDuration(200);
            rotateAnimation.setRepeatCount(0);
            rotateAnimation.setFillAfter(true);
            this.startAnimation(rotateAnimation);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(getChartBounds(canvas), whiteFill);
        drawRadarGuides(canvas);
        satelliteModels.forEach((sat) -> drawSatellite(sat, canvas));
    }

    private float getRadius(Canvas canvas) {
        Rect canvasBoundary = canvas.getClipBounds();
        float radius = Math.min(canvasBoundary.height(), canvasBoundary.width()) / 2;
        radius = radius * .97f; // To make room for stroke width
        return radius;
    }

    // Our chart must operate within a square, so this is that square
    private Rect getChartBounds(Canvas canvas) {
        Rect canvasBoundary = canvas.getClipBounds();

        float radius = getRadius(canvas);

        float trueLeft = canvasBoundary.centerX() - radius;
        float trueRight = canvasBoundary.centerX() + radius;
        float trueTop = canvasBoundary.centerY() - radius;
        float trueBottom = canvasBoundary.centerY() + radius;

        return new Rect((int) trueLeft, (int) trueTop, (int) trueRight, (int) trueBottom);
    }

    private void drawRadarGuides(Canvas canvas) {
        Rect bounds = getChartBounds(canvas);

        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.height() / 4, greyStroke);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.height() / 2, greyStroke);

        canvas.drawLine(bounds.left, bounds.centerY(), bounds.right, bounds.centerY(), greyStroke); //horizontal
        canvas.drawLine(bounds.centerX(), bounds.top, bounds.centerX(), bounds.bottom, greyStroke); //vertical

        float legLength = (float) Math.sqrt(Math.pow(getRadius(canvas), 2) / 2);
        canvas.drawLine(bounds.centerX() + legLength, bounds.centerY() - legLength, bounds.centerX() - legLength, bounds.centerY() + legLength, greyStroke); //top-right to bottom-left
        canvas.drawLine(bounds.centerX() - legLength, bounds.centerY() - legLength, bounds.centerX() + legLength, bounds.centerY() + legLength, greyStroke); //top-left to bottom-right

        canvas.drawText("N", bounds.centerX(), bounds.top + bounds.height()*.05f, cardinalTextFill);
    }

    private void drawSatellite(SatelliteModel satelliteModel, Canvas canvas) {
        Rect bounds = canvas.getClipBounds();
        float radius = getRadius(canvas) * .80f; // don't let satellites go out of bounds

        // Degrees in this instance are counted clockwise from North (0)
        float azimuthDegreesRelativeToNorth = 90f - satelliteModel.azimuthDegrees;

        int magnitude = (int) (radius * Math.cos(Math.toRadians(satelliteModel.elevationDegrees)));
        int x = (int) (magnitude * Math.cos(Math.toRadians(azimuthDegreesRelativeToNorth)));
        int y = (int) (magnitude * Math.sin(Math.toRadians(azimuthDegreesRelativeToNorth)));
        x = bounds.centerX() + x;
        y = bounds.centerY() + y;

        float shapeRadius = 30f * satelliteScale();
        if (satelliteModel.usedInFix) {
            canvas.drawRect(new Rect((int) (x - shapeRadius), (int) (y - shapeRadius), (int) (x + shapeRadius), (int) (y + shapeRadius)), greyLevel(satelliteModel.Cn0DbHz));
        } else {
            canvas.drawCircle(x, y, shapeRadius, greyLevel(satelliteModel.Cn0DbHz));
        }
        canvas.drawText(satelliteModel.prn + "", x + shapeRadius * 1.2f, y + shapeRadius / 2, textFill);
    }

    private Paint greyLevel(float cnodbhz) {
        if (cnodbhz < 1) {
            return lightGreyFill;
        }
        if (cnodbhz < 5) {
            return greyFill;
        }
        return darkGreyFill;
    }
}