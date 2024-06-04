package listener;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public interface OrderButtonClickListener {

    void onPlusButtonClickListener(int position,EditText editText);
    void onMinusButtonClickListener(int position, EditText editText);
    void onTasteButtonClickListener(int position,TextView textView);
    void onTasteMultiButtonClickListener(int position,TextView textView,TextView tvTastePrice);
    void onCancelButtonClickListener(int position,View view);
    void onCalculatorButtonClickListener(int position,EditText editText);
    void onPNumberButtonClickListener(int position,TextView textView);
    void onMoreButtonClickListener(int position,TextView textView);
}
