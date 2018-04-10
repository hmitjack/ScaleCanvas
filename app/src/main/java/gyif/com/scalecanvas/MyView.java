package gyif.com.scalecanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import gyif.com.library.ScaleCanvasView;

/**
 * Created by Administrator on 2018/4/10 0010.
 */

public class MyView extends ScaleCanvasView {

    private Paint paint;

    public MyView(Context context) {

        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
    }

    @Override
    protected void drawCustom(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2,100,paint);
    }

}
