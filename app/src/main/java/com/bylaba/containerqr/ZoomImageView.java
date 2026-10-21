
package com.bylaba.containerqr;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomImageView extends AppCompatImageView {

    private final Matrix matrix = new Matrix();
    private final RectF imageBounds = new RectF();
    private final RectF viewBounds = new RectF();

    private final ScaleGestureDetector scaleDetector;

    private float scale = 1f;
    private float lastX;
    private float lastY;

    private static final float MIN_SCALE = 1f;
    private static final float MAX_SCALE = 4f;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.MATRIX);

        scaleDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(
                            ScaleGestureDetector detector
                    ) {
                        float factor = detector.getScaleFactor();
                        float newScale = Math.max(
                                MIN_SCALE,
                                Math.min(MAX_SCALE, scale * factor)
                        );

                        float appliedScale = newScale / scale;
                        scale = newScale;

                        matrix.postScale(
                                appliedScale,
                                appliedScale,
                                detector.getFocusX(),
                                detector.getFocusY()
                        );

                        limitarMovimiento();
                        setImageMatrix(matrix);

                        return true;
                    }
                }
        );
    }

    @Override
    protected void onSizeChanged(
            int w,
            int h,
            int oldw,
            int oldh
    ) {
        super.onSizeChanged(w, h, oldw, oldh);
        ajustarImagen();
    }

    private void ajustarImagen() {
        Drawable drawable = getDrawable();

        if (drawable == null
                || getWidth() == 0
                || getHeight() == 0) {
            return;
        }

        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return;
        }

        imageBounds.set(
                0,
                0,
                drawableWidth,
                drawableHeight
        );

        viewBounds.set(
                0,
                0,
                getWidth() - getPaddingLeft() - getPaddingRight(),
                getHeight() - getPaddingTop() - getPaddingBottom()
        );

        matrix.setRectToRect(
                imageBounds,
                viewBounds,
                Matrix.ScaleToFit.CENTER
        );

        matrix.postTranslate(
                getPaddingLeft(),
                getPaddingTop()
        );

        scale = 1f;
        setImageMatrix(matrix);
    }

    private void limitarMovimiento() {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        RectF bounds = new RectF(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight()
        );

        matrix.mapRect(bounds);

        float left = getPaddingLeft();
        float top = getPaddingTop();
        float right = getWidth() - getPaddingRight();
        float bottom = getHeight() - getPaddingBottom();

        float dx = 0;
        float dy = 0;

        if (bounds.width() <= right - left) {
            dx = (left + right - bounds.left - bounds.right) / 2f;
        } else if (bounds.left > left) {
            dx = left - bounds.left;
        } else if (bounds.right < right) {
            dx = right - bounds.right;
        }

        if (bounds.height() <= bottom - top) {
            dy = (top + bottom - bounds.top - bounds.bottom) / 2f;
        } else if (bounds.top > top) {
            dy = top - bounds.top;
        } else if (bounds.bottom < bottom) {
            dy = bottom - bounds.bottom;
        }

        matrix.postTranslate(dx, dy);
    }

    public void resetZoom() {
        ajustarImagen();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!scaleDetector.isInProgress() && scale > 1f) {
                    float dx = event.getX() - lastX;
                    float dy = event.getY() - lastY;

                    matrix.postTranslate(dx, dy);
                    limitarMovimiento();
                    setImageMatrix(matrix);
                }

                lastX = event.getX();
                lastY = event.getY();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performClick();
                return true;
        }

        return true;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (isLaidOut()) {
            ajustarImagen();
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}