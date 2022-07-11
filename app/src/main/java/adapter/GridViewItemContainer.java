package adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Arrays;

/**
 * Created by User on 9/18/2017.
 */
public class GridViewItemContainer extends RelativeLayout {

    private View[] viewsInRow;

    public GridViewItemContainer(Context context) {
        super(context);
    }

    public GridViewItemContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GridViewItemContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewsInRow(View[] viewsInRow) {
        if  (viewsInRow != null) {
            if (this.viewsInRow == null) {
                this.viewsInRow = Arrays.copyOf(viewsInRow, viewsInRow.length);
            }
            else {
                System.arraycopy(viewsInRow, 0, this.viewsInRow, 0, viewsInRow.length);
            }
        }
        else if (this.viewsInRow != null){
            Arrays.fill(this.viewsInRow, null);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (viewsInRow == null) {
            return;
        }

        int measuredHeight = getMeasuredHeight();
        int maxHeight = measuredHeight;
        for (View siblingInRow : viewsInRow) {
            if (siblingInRow != null) {
                maxHeight = Math.max(maxHeight, siblingInRow.getMeasuredHeight());
            }
        }

        if (maxHeight == measuredHeight) {
            return;
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(maxHeight, heightSize), MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                break;

            case MeasureSpec.EXACTLY:
                // No debate here. Final measuring already took place. That's it.
                break;

            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                break;

        }
    }
}
