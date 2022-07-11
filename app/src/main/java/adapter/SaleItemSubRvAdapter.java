package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.ItemSubData;
import data.ItemSubGroupData;
import listener.ItemSubMultiCheckListener;
import listener.ItemSubSingleCheckListener;

public class SaleItemSubRvAdapter extends RecyclerView.Adapter<SaleItemSubRvAdapter.ViewHolder> implements ItemSubSingleCheckListener, ItemSubMultiCheckListener {

    private Context context;
    public List<ItemSubGroupData> lstItemSubGroup;
    SaleItemSubSingleAdapter saleItemSubSingleAdapter;
    SaleItemSubMultiAdapter saleItemSubMultiAdapter;

    public SaleItemSubRvAdapter(List<ItemSubGroupData> lstItemSubGroup, Context context) {
        this.lstItemSubGroup = lstItemSubGroup;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSingleCheckListener(int groupPosition, int position) {
        lstItemSubGroup.get(groupPosition).getLstItemSubData().forEach((x) -> x.setSelected(false));
        lstItemSubGroup.get(groupPosition).getLstItemSubData().get(position).setSelected(true);
        notifyDataChangedSingle(groupPosition, lstItemSubGroup.get(groupPosition).getLstItemSubData());
    }

    @Override
    public void onSingleUnCheckListener(int groupPosition, int position) {
        // nothing
    }

    private void notifyDataChangedSingle(int groupPosition, List<ItemSubData> list) {
        saleItemSubSingleAdapter.setItems(groupPosition, list);
        saleItemSubSingleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMultiCheckListener(int groupPosition, int position) {
        lstItemSubGroup.get(groupPosition).getLstItemSubData().get(position).setSelected(true);
        //notifyDataChangedMulti(lstItemSubGroup.get(groupPosition).getLstItemSubData());
    }

    @Override
    public void onMultiUnCheckListener(int groupPosition, int position) {
        lstItemSubGroup.get(groupPosition).getLstItemSubData().get(position).setSelected(false);
        //notifyDataChangedMulti(lstItemSubGroup.get(groupPosition).getLstItemSubData());
    }

    private void notifyDataChangedMulti(List<ItemSubData> list) {
        saleItemSubMultiAdapter.setItems(list);
        saleItemSubMultiAdapter.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sale_item_sub, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.tvSubTitle.setText(lstItemSubGroup.get(position).getSubTitle());
        if (lstItemSubGroup.get(position).isSingleCheck() == 1) {
            saleItemSubSingleAdapter = new SaleItemSubSingleAdapter(lstItemSubGroup.get(position).getLstItemSubData(), context, position, lstItemSubGroup);
            holder.rvItemSub.setAdapter(saleItemSubSingleAdapter);
            holder.rvItemSub.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            saleItemSubSingleAdapter.setListener(this);
        } else {
            saleItemSubMultiAdapter = new SaleItemSubMultiAdapter(lstItemSubGroup.get(position).getLstItemSubData(), context, position);
            holder.rvItemSub.setAdapter(saleItemSubMultiAdapter);
            holder.rvItemSub.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            saleItemSubMultiAdapter.setListener(this);
        }
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return lstItemSubGroup.size();
    }

    public void setItems(List<ItemSubGroupData> list) {
        this.lstItemSubGroup = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubTitle;
        RecyclerView rvItemSub;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            rvItemSub = (RecyclerView) itemView.findViewById(R.id.rvItemSub);
        }
    }
}
