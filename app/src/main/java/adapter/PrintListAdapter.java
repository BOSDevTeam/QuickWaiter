package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.TransactionData;

/**
 * Created by User on 10/26/2017.
 */
public class PrintListAdapter extends BaseAdapter {

    private Context context;
    List<TransactionData> lstTransactionData;
    float floatQty;

    public PrintListAdapter(Context context, List<TransactionData> lstTransactionData){
        this.context=context;
        this.lstTransactionData=lstTransactionData;
    }

    @Override
    public int getCount(){
        return lstTransactionData.size();
    }

    @Override
    public String getItem(int position){
        return lstTransactionData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvItemName, tvQuantity, tvAmount;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_print_bill, parent,false);
            holder=new ViewHolder();
            holder.tvItemName =(TextView) row.findViewById(R.id.btPrintListItem);
            holder.tvQuantity =(TextView) row.findViewById(R.id.btPrintListQty);
            holder.tvAmount =(TextView) row.findViewById(R.id.btPrintListAmount);

            holder.tvItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvAmount.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvItemName.setText(lstTransactionData.get(position).getItemName());
        floatQty = Float.parseFloat(lstTransactionData.get(position).getStringQty());
        if(floatQty==Math.round(floatQty)){
            holder.tvQuantity.setText(String.valueOf(lstTransactionData.get(position).getIntegerQty()));
        }else{
            holder.tvQuantity.setText(String.valueOf(lstTransactionData.get(position).getFloatQty()));
        }
        holder.tvAmount.setText(String.valueOf(lstTransactionData.get(position).getAmount()));

        return row;
    }
}
