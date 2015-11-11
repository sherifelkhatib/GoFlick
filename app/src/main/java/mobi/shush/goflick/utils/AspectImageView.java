package mobi.shush.goflick.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import mobi.shush.goflick.R;

/**
 * Created by Shush on 11/11/2015.
 */
public class AspectImageView extends ImageView {

    public AspectImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public AspectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AspectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(getBackground() == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getBackground().getIntrinsicHeight()
                / getBackground().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            setBackground(drawable);
        else
            setBackgroundDrawable(drawable);
    }
}
