package holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

/**
 * Created by User on 9/19/2017.
 */
public class OpenOrderKitchenViewHolder extends RecyclerView.ViewHolder {
    public TextView tvItem,tvQuantity;

    public OpenOrderKitchenViewHolder(View itemView) {
        super(itemView);
        tvItem = (TextView) itemView.findViewById(R.id.tvItem);
        tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
    }

}
