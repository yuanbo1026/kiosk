package de.nexxoo.kiosk_app.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by b.yuan on 24.08.2015.
 */
public class RectangleLayout extends FrameLayout {
	public RectangleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RectangleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectangleLayout(Context context) {
		super(context);
	}

	@SuppressWarnings("unused")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		int childHeightSize = getMeasuredHeight();
//		heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
//		heightMeasureSpec = 285;
//		widthMeasureSpec = 510;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}