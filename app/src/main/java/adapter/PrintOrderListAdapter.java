package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import common.DBHelper;
import data.PrintOrderData;
import data.TransactionData;

public class PrintOrderListAdapter extends BaseAdapter {
    private Context context;
    List<PrintOrderData> lstPrintRealData;
    DBHelper db;

    public PrintOrderListAdapter(Context context, List<PrintOrderData> lstPrintRealData){
        this.context=context;
        db=new DBHelper(this.context);
        this.lstPrintRealData=lstPrintRealData;
    }

    @Override
    public int getCount(){
        return lstPrintRealData.size();
    }

    @Override
    public String getItem(int position){
        return lstPrintRealData.get(position).getsTypeName();
    }

    @Override
    public long getItemId(int position){
        return lstPrintRealData.get(position).getsTypeId();
    }

    static class ViewHolder {
        TextView tvDate,tvTime, tvTable, tvUser, tvHeaderItem, tvHeaderQty,tvLblTable,tvLblUser;
        LinearLayout layoutPrintList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final PrintOrderListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_print_order_parent, parent,false);
            holder = new PrintOrderListAdapter.ViewHolder();
            holder.tvTime = (TextView) row.findViewById(R.id.tvTime);
            holder.tvDate = (TextView) row.findViewById(R.id.tvDate);
            holder.tvTable = (TextView) row.findViewById(R.id.tvTable);
            holder.tvUser = (TextView) row.findViewById(R.id.tvUser);
            holder.tvLblTable = (TextView) row.findViewById(R.id.tvLblTable);
            holder.tvLblUser = (TextView) row.findViewById(R.id.tvLblUser);
            holder.tvHeaderItem = (TextView) row.findViewById(R.id.tvHeaderItem);
            holder.tvHeaderQty = (TextView) row.findViewById(R.id.tvHeaderQty);
            holder.layoutPrintList = (LinearLayout) row.findViewById(R.id.layoutPrintList);

            holder.tvTime.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvDate.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvUser.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvLblTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvLblUser.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvHeaderItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvHeaderQty.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (PrintOrderListAdapter.ViewHolder) row.getTag();
        }

        holder.tvDate.setText(lstPrintRealData.get(position).getDate());
        holder.tvTime.setText(lstPrintRealData.get(position).getTime());
        holder.tvTable.setText(lstPrintRealData.get(position).getTableName());
        holder.tvUser.setText(lstPrintRealData.get(position).getUserName());

        for (int i=0; i<lstPrintRealData.get(position).getLstTran().size(); i++) {
            TransactionData data = lstPrintRealData.get(position).getLstTran().get(i);
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row1 = layoutInflater.inflate(R.layout.list_print_order_child, null);
            TextView tvItemName =(TextView) row1.findViewById(R.id.tvPrintListItem);
            TextView tvQuantity =(TextView) row1.findViewById(R.id.tvPrintListQty);

            tvItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            if(data.getTaste().length()!=0 && data.getTasteMulti().length()!=0) tvItemName.setText(data.getItemName()+"("+data.getTaste()+","+data.getTasteMulti()+")");
            else if(data.getTaste().length()!=0 && data.getTasteMulti().length()==0)tvItemName.setText(data.getItemName()+"("+data.getTaste()+")");
            else if(data.getTaste().length()==0 && data.getTasteMulti().length()!=0)tvItemName.setText(data.getItemName()+"("+data.getTasteMulti()+")");
            else tvItemName.setText(data.getItemName());
            float floatQty = Float.parseFloat(data.getStringQty());
            if(floatQty==Math.round(floatQty)){
                tvQuantity.setText(String.valueOf(data.getIntegerQty()));
            }else{
                tvQuantity.setText(String.valueOf(data.getFloatQty()));
            }
            holder.layoutPrintList.addView(row1);
        }

        return row;
    }
}
