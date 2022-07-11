package listener;

import android.widget.TextView;

/**
 * Created by NweYiAung on 21-02-2017.
 */
public interface CustomerListGroupListener {
    void onManPlusClickListener(int position,TextView tvNumber);
    void onManMinusClickListener(int position,TextView tvNumber);
    void onWomenPlusClickListener(int position,TextView tvNumber);
    void onWomenMinusClickListener(int position,TextView tvNumber);
    void onChildPlusClickListener(int position,TextView tvNumber);
    void onChildMinusClickListener(int position,TextView tvNumber);
    void onTotalPlusClickListener(int position,TextView tvNumber);
    void onTotalMinusClickListener(int position,TextView tvNumber);
    void onSaveClickListener(int position);
}
