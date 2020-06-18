package com.dreamfish.restreminders.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import com.dreamfish.restreminders.R;

import androidx.annotation.Nullable;

public class MainTimerCanvases extends View {

  public MainTimerCanvases(Context context) {
    super(context);
    init();
  }
  public MainTimerCanvases(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }
  public MainTimerCanvases(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }
  public MainTimerCanvases(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private Paint mPaint;
  private Path mCursorPath;

  public int getStrokeWidth() {
    return strokeWidth;
  }
  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }
  public int getRadius() {
    return radius;
  }
  public void setRadius(int radius) {
    this.radius = radius;
  }
  public int getValue() {
    return value;
  }
  public void setValue(int value) {
    this.value = value;
  }

  /**
   * 描边线的粗细
   */
  private int strokeWidth = 4;
  private int cursorHeight = 50;
  private int radius = 150;
  private int value = 0;

  private int colorActive = 0;
  private int colorNormal = 0;

  private void init() {
    mPaint = new Paint();
    initPaint();
  }
  private void initPaint() {

    colorActive = getResources().getColor(R.color.colorPrimary, null);
    colorNormal = getResources().getColor(R.color.colorPrimaryLight, null);

    mPaint.reset();
    mPaint.setColor(colorActive);
    mPaint.setStyle(Paint.Style.STROKE);//设置描边
    mPaint.setStrokeWidth(strokeWidth);//设置描边线的粗细
    mPaint.setAntiAlias(true);//设置抗锯齿，使圆形更加圆滑

    mCursorPath = new Path();
  }

  @Override
  protected void onDraw(Canvas canvas) {

    super.onDraw(canvas);

    //计算12点处刻度的开始坐标
    int startX = getWidth() / 2;
    int startY = getHeight() / 2 - radius;//y坐标即园中心点的y坐标-半径

    //绘制指针
    canvas.save();

    mPaint.setColor(value == 0 ? colorNormal : colorActive);

    float halfSide = (float) (cursorHeight / Math.sqrt(3));
    float cursorY = startY + 70;

    mCursorPath.moveTo(startX, cursorY);// 此点为多边形的起点
    mCursorPath.lineTo(startX - halfSide, cursorY + halfSide);
    mCursorPath.lineTo(startX + halfSide, cursorY + halfSide);
    mCursorPath.close(); // 使这些点构成封闭的多边形

    canvas.rotate(value, getWidth() / 2, getHeight() / 2);//以圆中心进行旋转
    canvas.drawPath(mCursorPath, mPaint);

    canvas.restore();

    //绘制刻度
    canvas.save();

    mPaint.setColor(value == 0 ? colorNormal : colorActive);
    boolean activeSetted = false;

    //计算12点处的结束坐标
    int stopX = startX;
    int stopY = startY + 50;//非整点处的线长度为15

    float degree = 360.0f / 240.0f;
    float degreeNow = 0.0f;

    //计算画布每次旋转的角度
    for(int i = 0; i < 240; i++){

      if(degreeNow >= value && !activeSetted) {
        mPaint.setColor(colorNormal);
        activeSetted = true;
      }

      canvas.drawLine(startX, startY, stopX, stopY, mPaint);
      canvas.rotate(degree, getWidth() / 2, getHeight() / 2);//以圆中心进行旋转

      degreeNow += degree;
    }

    //绘制完后，记得把画布状态复原
    canvas.restore();


  }
}
