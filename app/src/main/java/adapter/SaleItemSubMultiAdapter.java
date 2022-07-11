package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.ItemSubData;
import listener.ItemSubMultiCheckListener;

public class SaleItemSubMultiAdapter extends RecyclerView.Adapter<SaleItemSubMultiAdapter.ViewHolder>  {

    private Context context;
    List<ItemSubData> lstItemSub;
    ItemSubMultiCheckListener itemSubMultiCheckListener;
    int groupPosition;

    public SaleItemSubMultiAdapter(List<ItemSubData> lstItemSub, Context context,int groupPosition) {
        this.lstItemSub = lstItemSub;
        this.context = context;
        this.groupPosition=groupPosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sub_multi, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.chkSubName.setText(lstItemSub.get(position).getSubName());
        //holder.tvPrice.setText(String.valueOf(lstItemSub.get(position).getPrice()));

        holder.tvPrice.setText("(+"+lstItemSub.get(position).getPrice()+" MMK)");

        if(lstItemSub.get(position).isSelected())holder.chkSubName.setChecked(true);
        else holder.chkSubName.setChecked(false);

        holder.chkSubName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSubMultiCheckListener != null) {
                    if(holder.chkSubName.isChecked()) itemSubMultiCheckListener.onMultiCheckListener(groupPosition,position);
                    else itemSubMultiCheckListener.onMultiUnCheckListener(groupPosition,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return lstItemSub.size();
    }

    public void setItems(List<ItemSubData> list){
        this.lstItemSub=list;
    }

    public void setListener(ItemSubMultiCheckListener itemSubMultiCheckListener){
        this.itemSubMultiCheckListener=itemSubMultiCheckListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkSubName;
        TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            chkSubName = (CheckBox) itemView.findViewById(R.id.chkSubName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }
}
