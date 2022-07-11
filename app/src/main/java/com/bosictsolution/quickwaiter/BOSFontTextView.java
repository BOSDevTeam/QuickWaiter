package com.bosictsolution.quickwaiter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by NweYiAung on 11-02-2017.
 */
public class BOSFontTextView extends TextView {

    public BOSFontTextView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
    }
}
