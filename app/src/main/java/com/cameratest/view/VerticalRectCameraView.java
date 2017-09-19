package com.cameratest.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.cameratest.util.DisplayUtil;

/**
 * Created by 17041427 on 2017/8/14.
 *
 * 竖着的拍照矩形框
 *
 * 难点：竖着的text文字，在Rect的中间
 *
 * 思路：创建一定大小的矩形bitmap的canvas，然后在这个bitmap的canvas上绘制文字，
 * 然后在ondraw方法中绘制这个带有文字的bitmap，并先旋转90度bitmap
 *
 * 知识点：new Canvas(Bitmap bitmap); canvas.drawText(text, x, y, paint); canvas.drawBitmap(bitmap, x, y, paint)
 *
 */

public class VerticalRectCameraView extends View {

    private int panelWidth;
    private int panelHeght;

    private int viewWidth;
    private int viewHeight;

    public int rectWidth;
    public int rectHeght;

    private int rectTop;
    private int rectLeft;
    private int rectRight;
    private int rectBottom;

    private int lineLen;
    private int lineWidht;
    private static final int LINE_WIDTH = 5;
    private static final int TOP_BAR_HEIGHT = 50;
    private static final int BOTTOM_BTN_HEIGHT = 66;

//    private static final int TOP_BAR_HEIGHT = Constant.RECT_VIEW_TOP;
//    private static final int BOTTOM_BTN_HEIGHT = Constant.RECT_VIEW_BOTTOM;

    private static final int LEFT_PADDING = 50;
    private static final int RIGHT_PADDING = 50;
    private static final String TIPS = "请将身份证人像正面放到框内，保持身份证信息清晰可见";

    private Paint linePaint;
    private Paint wordPaint;
//    private Rect rect;
    private int baseline;
    private Bitmap textBitMap;
    private Canvas textCanvas;
    public VerticalRectCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Activity activity = (Activity) context;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        panelWidth = wm.getDefaultDisplay().getWidth();//拿到屏幕的宽
        panelHeght = wm.getDefaultDisplay().getHeight();//拿到屏幕的高

        viewHeight = panelHeght;
        viewWidth = panelWidth;

        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity,LEFT_PADDING + RIGHT_PADDING);

        rectHeght = (int) (rectWidth * 86 / 54);
        rectHeght = panelHeght - (int) DisplayUtil.dp2px(activity,100 + 100);
        // 相对于此view
        rectTop = (viewHeight - rectHeght) / 2;
        rectLeft = (viewWidth - rectWidth) / 2;
        rectBottom = rectTop + rectHeght;
        rectRight = rectLeft + rectWidth;

        lineLen = panelWidth / 8;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wordPaint.setStrokeWidth(3);
        wordPaint.setTextSize(35);

        //为何高度50？是和下面的rect的相互对应Rect(0, 0, viewWidth, 50)，这样好计算baseline
        textBitMap = Bitmap.createBitmap(viewWidth,50, Bitmap.Config.ARGB_8888);
        textCanvas = new Canvas(textBitMap);

        //重制rect  并画文字  吧文字置于rect中间
        Rect rect = new Rect(0, 0, viewWidth, 50);
        Paint.FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        wordPaint.setTextAlign(Paint.Align.CENTER);
        wordPaint.setColor(Color.WHITE);

        Log.d("", "OneCameraTopRectView: text.x="+rect.centerX()+",text.y="+baseline);
        //text.x=540,text.y=38
        textCanvas.drawText(TIPS,rect.centerX(), baseline, wordPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wordPaint.setColor(Color.TRANSPARENT);
//        canvas.drawRect(rect, wordPaint);

        //画蒙层
        wordPaint.setColor(0xa0000000);
        //底部矩形蒙层(将下面的4给矩形一个个绘制可以看出)
        Rect rect = new Rect(0, viewHeight/2+rectHeght/2, viewWidth, viewHeight);
        canvas.drawRect(rect, wordPaint);
        //顶部矩形蒙层
        rect = new Rect(0, 0, viewWidth, viewHeight/2-rectHeght/2);
        canvas.drawRect(rect, wordPaint);
        //左边矩形蒙层
        rect = new Rect(0, viewHeight/2-rectHeght/2, (viewWidth-rectWidth)/2, viewHeight/2+rectHeght/2);
        canvas.drawRect(rect, wordPaint);
        //右边矩形蒙层
        rect = new Rect(viewWidth-(viewWidth-rectWidth)/2, viewHeight/2-rectHeght/2, viewWidth, viewHeight/2+rectHeght/2);
        canvas.drawRect(rect, wordPaint);


        //在整个最大矩形的右边，绘制bitmap(文字已经绘制在上面)
        canvas.drawBitmap(DisplayUtil.adjustPhotoRotation(textBitMap,90)
                ,viewWidth - DisplayUtil.dp2px(getContext(),40)
                ,(viewHeight-viewWidth)/2f
                ,null);

        //绘制矩形4个角,8条线
        canvas.drawLine(rectLeft, rectTop, rectLeft + lineLen, rectTop,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectTop, rectRight, rectTop,
                linePaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectRight, rectTop, rectRight, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom, rectLeft + lineLen, rectBottom,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectBottom, rectRight, rectBottom,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom - lineLen, rectLeft, rectBottom,
                linePaint);
        canvas.drawLine(rectRight, rectBottom - lineLen, rectRight, rectBottom,
                linePaint);
    }

    public int getRectLeft() {
        return rectLeft;
    }

    public int getRectTop() {
        return rectTop;
    }

    public int getRectRight() {
        return rectRight;
    }

    public int getRectBottom() {
        return rectBottom;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

}
