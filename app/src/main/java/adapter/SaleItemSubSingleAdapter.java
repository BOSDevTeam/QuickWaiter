package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.ItemSubData;
import data.ItemSubGroupData;
import listener.ItemSubSingleCheckListener;

public class SaleItemSubSingleAdapter extends RecyclerView.Adapter<SaleItemSubSingleAdapter.ViewHolder> {

    private Context context;
    List<ItemSubData> lstItemSub;
    List<ItemSubGroupData> lstItemSubGroup;
    ItemSubSingleCheckListener itemSubSingleCheckListener;
    int groupPosition;

    public SaleItemSubSingleAdapter(List<ItemSubData> lstItemSub, Context context, int groupPosition, List<ItemSubGroupData> lstItemSubGroup) {
        this.lstItemSub = lstItemSub;
        this.context = context;
        this.groupPosition = groupPosition;
        this.lstItemSubGroup = lstItemSubGroup;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sub_single, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.rdoSubName.setText(lstItemSub.get(position).getSubName());
        holder.tvPrice.setText("(+" + lstItemSub.get(position).getPrice() + " MMK)");

        if (lstItemSub.get(position).isSelected()) holder.rdoSubName.setChecked(true);
        else holder.rdoSubName.setChecked(false);

        holder.rdoSubName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                if (itemSubSingleCheckListener != null) {
                    if (holder.rdoSubName.isChecked())
                        itemSubSingleCheckListener.onSingleCheckListener(groupPosition, position);
                    else
                        itemSubSingleCheckListener.onSingleUnCheckListener(groupPosition, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return lstItemSub.size();
    }

    public void setItems(int groupPosition, List<ItemSubData> list) {
        this.lstItemSubGroup.get(groupPosition).setLstItemSubData(list);
    }

    public void setListener(ItemSubSingleCheckListener itemSubSingleCheckListener) {
        this.itemSubSingleCheckListener = itemSubSingleCheckListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rdoSubName;
        TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            rdoSubName = (RadioButton) itemView.findViewById(R.id.rdoSubName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }
}
