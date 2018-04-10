package gyif.com.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/10 0010.
 * 自由缩放和拖动的View
 */

public abstract class ScaleCanvasView extends View implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener, View.OnTouchListener {

    private final int DEFAULT_WIDTH = 400;
    private final int DEFAULT_HEIGHT = 400;

    private float mScaleFactor = 1;//缩放因子，默认为1

    private float mLastScaleFactor = 1;//最后一次缩放因子

    private Matrix matrix;//用于控制canvas缩放和拖动的矩阵

    private ScaleGestureDetector scaleGestureDetector;


    private final int MODE_DRAG = 0;
    private final int MODE_SCALE = 1;
    private int mode = MODE_DRAG;

    private float dx, dy;

    private float mTransX, mTransY;

    private float downX, downY;

    public ScaleCanvasView(Context context) {
        super(context);
        init();
    }

    public ScaleCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        matrix = new Matrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把触摸事件分发给ScaleDetector
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;
                downX = event.getX();
                downY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = MODE_SCALE;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_DRAG) {
                    float moveX = event.getX();
                    float moveY = event.getY();
                    float dx = moveX - downX;
                    float dy = moveY - downY;

                    float[] values = new float[9];
                    matrix.getValues(values);
                    float mPreviousTransX = values[Matrix.MTRANS_X];
                    float mPreviousTransY = values[Matrix.MTRANS_Y];
                    matrix.postTranslate(dx - mPreviousTransX + mTransX, dy - mPreviousTransY + mTransY);

                } else if (mode == MODE_SCALE) {
                    scaleGestureDetector.onTouchEvent(event);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                float[] values = new float[9];
                matrix.getValues(values);
                mTransX = values[Matrix.MTRANS_X];
                mTransY = values[Matrix.MTRANS_Y];
                invalidate();
                break;
        }

        return scaleGestureDetector.onTouchEvent(event);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //控件真正的宽高
        int width = 0, height = 0;

        //如果是wrap_content或不指定大小，那么就取默认的值
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            width = DEFAULT_WIDTH;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }

        //如果是wrap_content或不指定大小，那么就取默认的值
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            height = DEFAULT_HEIGHT;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(matrix);
        drawCustom(canvas);
    }

    protected abstract void drawCustom(Canvas canvas);

    //缩放中
    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        mScaleFactor = mLastScaleFactor * scaleGestureDetector.getScaleFactor();
        //获得前一次的缩放因子
        float[] values = new float[9];
        matrix.getValues(values);

        float previousScaleFactor = values[Matrix.MSCALE_X];

        matrix.postScale(mScaleFactor / previousScaleFactor, mScaleFactor / previousScaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        invalidate();
        return false;
    }

    //开始缩放
    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    //缩放结束
    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        mLastScaleFactor *= scaleGestureDetector.getScaleFactor();

        float[] values = new float[9];
        matrix.getValues(values);
        mTransX = values[Matrix.MTRANS_X];
        mTransY = values[Matrix.MTRANS_Y];
    }

    //点击
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    //拖动
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d("TAG", "dx:" + v + "---" + "dy:" + v1);
        dx -= v;
        dy -= v1;
        float[] values = new float[9];
        matrix.getValues(values);
        float previousX = values[Matrix.MTRANS_X];
        float previousY = values[Matrix.MTRANS_Y];
        matrix.postTranslate(dx - previousX + mTransX, dy - previousY + mTransY);
        invalidate();
        return false;
    }

    //长按
    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    //滚动
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
