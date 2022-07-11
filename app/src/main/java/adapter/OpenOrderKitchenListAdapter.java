package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.ArrayList;
import java.util.List;

import data.OpenOrderKitchenData;

/**
 * Created by User on 9/8/2017.
 */
public class OpenOrderKitchenListAdapter  extends BaseAdapter {

    private Context context;
    List<OpenOrderKitchenData> lstHeaderData,lstItemData;

    public OpenOrderKitchenListAdapter(Context context, List<OpenOrderKitchenData> lstHeaderData, List<OpenOrderKitchenData> lstItemData){
        this.context=context;
        this.lstHeaderData = lstHeaderData;
        this.lstItemData = lstItemData;
    }

    public int getCount(){
        return lstHeaderData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        TextView tvWaiter,tvTable,tvTranID,tvDateTime;
        LinearLayout layoutData;
        RecyclerView rvList;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_open_order_kitchen, null);

            viewHolder = new ViewHolder();
            viewHolder.tvWaiter =(TextView)convertView.findViewById(R.id.tvWaiter);
            viewHolder.tvTable =(TextView)convertView.findViewById(R.id.tvTable);
            viewHolder.tvTranID =(TextView)convertView.findViewById(R.id.tvTranID);
            viewHolder.tvDateTime =(TextView)convertView.findViewById(R.id.tvDateTime);
            viewHolder.rvList =(RecyclerView) convertView.findViewById(R.id.rvList);
            viewHolder.layoutData=(LinearLayout)convertView.findViewById(R.id.layoutData);
            viewHolder.tvWaiter.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            viewHolder.tvTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            viewHolder.tvTranID.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            viewHolder.tvDateTime.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvWaiter.setText(lstHeaderData.get(position).getWaiterName());
        viewHolder.tvTable.setText("Table# "+lstHeaderData.get(position).getTableName());
        viewHolder.tvTranID.setText("Order# "+String.valueOf(lstHeaderData.get(position).getTranid()));
        viewHolder.tvDateTime.setText("from "+lstHeaderData.get(position).getDatetime());

        int currentTableID=lstHeaderData.get(position).getTableid();
        List<OpenOrderKitchenData> lstItemDataByTable =new ArrayList<>();
        for(int i = 0; i< lstItemData.size(); i++){
            if(lstItemData.get(i).getTableid()==currentTableID){
                lstItemDataByTable.add(lstItemData.get(i));
            }
        }

        OpenOrderKitchenRecyclerAdapter adapter = new OpenOrderKitchenRecyclerAdapter(lstItemDataByTable, context);
        viewHolder.rvList.setAdapter(adapter);

        /**for (int i=0; i<lstItemDataByTable.size(); i++) {
            OpenOrderKitchenData data = lstItemDataByTable.get(i);
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_child_open_order_kitchen, null);
            TextView tvItem =(TextView) row.findViewById(R.id.tvItem);
            TextView tvQuantity =(TextView) row.findViewById(R.id.tvQuantity);

            tvItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            tvItem.setText(data.getItemName());
            tvQuantity.setText(data.getStringQty());

            viewHolder.lvList.addView(row);
        }**/
        //OpenOrderKitchenChildListAdapter adapter=new OpenOrderKitchenChildListAdapter(context, lstItemDataByTable);
        //viewHolder.lvList.setAdapter(adapter);

        return convertView;
    }
}
