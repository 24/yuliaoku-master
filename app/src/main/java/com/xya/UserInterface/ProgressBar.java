package com.xya.UserInterface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ubuntu on 15-2-19.
 */
public class ProgressBar extends View {
    private int primaryColor = Color.RED;
    private int used = 1000;
    private int unused = 0;
    private Paint paint;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(primaryColor);
//        List<Integer> list = new ArrayList<>();
//        list.
//        int count = (used+unused)/used;
//        int[] colors = new int[]{primaryColor,};
//        Shader mShader = new SweepGradient(0f,0f,getWidth(),getHeight(),new int[]{Color.BLUE,Color.CYAN},null,Shader.TileMode.MIRROR);
//        paint.setShader(mShader);
        float left = (float) ((used * 1.0 / (used + unused)) * getWidth());
//        float right = (float) ((unused * 1.0 / (used + unused)) * getWidth());

        //canvas.drawRect(0f, 0f,getWidth(), getHeight(), paint);
        // canvas.drawRect(0f, 0f, left, getHeight(), paint);

        paint.setShadowLayer(8f, 0f, 4f, Color.BLACK);
        canvas.drawRoundRect(new RectF(0f, 0f, left, getHeight()), 3f, 3f, paint);
        paint.setAlpha(130);

        canvas.drawRoundRect(new RectF(0f, 0f, getWidth(), getHeight()), 3f, 3f, paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }


    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        invalidate();
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
        invalidate();
    }

    public void setTotal(int used, int unused) {
        this.used = used;
        this.unused = unused;
        invalidate();
    }

    public int getUnused() {
        return unused;
    }

    public void setUnused(int unused) {
        this.unused = unused;
        invalidate();
    }
}
