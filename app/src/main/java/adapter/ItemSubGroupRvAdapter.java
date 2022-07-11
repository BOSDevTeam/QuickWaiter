package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.ItemSubGroupData;
import listener.ItemSubGroupCheckListener;

public class ItemSubGroupRvAdapter extends RecyclerView.Adapter<ItemSubGroupRvAdapter.ViewHolder>  {

    private Context context;
    List<ItemSubGroupData> lstItemSubGroup;
    ItemSubGroupCheckListener itemSubGroupCheckListener;

    public ItemSubGroupRvAdapter(List<ItemSubGroupData> lstItemSubGroup, Context context) {
        this.lstItemSubGroup = lstItemSubGroup;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_add_item_sub, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.tvGroupName.setText(lstItemSubGroup.get(position).getSubGroupName());
        holder.tvSubTitle.setText(lstItemSubGroup.get(position).getSubTitle());
        holder.tvCheckType.setText(lstItemSubGroup.get(position).getCheckType());

        if(lstItemSubGroup.get(position).isSelected())holder.chkSelect.setChecked(true);
        else holder.chkSelect.setChecked(false);

        holder.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSubGroupCheckListener != null) {
                    if(holder.chkSelect.isChecked()) itemSubGroupCheckListener.onCheckListener(position);
                    else itemSubGroupCheckListener.onUnCheckListener(position);
                }
            }
        });
        holder.btnSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemSubGroupCheckListener != null){
                    itemSubGroupCheckListener.onSubItemClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return lstItemSubGroup.size();
    }

    public void setListener(ItemSubGroupCheckListener itemSubGroupCheckListener){
        this.itemSubGroupCheckListener=itemSubGroupCheckListener;
    }

    public void setItems(List<ItemSubGroupData> list){
        this.lstItemSubGroup=list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName,tvSubTitle,tvCheckType;
        Button btnSubItem;
        CheckBox chkSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.tvGroupName);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            tvCheckType = (TextView) itemView.findViewById(R.id.tvCheckType);
            btnSubItem = (Button) itemView.findViewById(R.id.btnSubItem);
            chkSelect = (CheckBox) itemView.findViewById(R.id.chkSelect);
        }
    }
}
