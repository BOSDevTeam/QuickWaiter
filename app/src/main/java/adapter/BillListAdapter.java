package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.TransactionData;
import listener.BillListButtonClickListener;

/**
 * Created by User on 10/26/2017.
 */
public class BillListAdapter extends BaseAdapter {

    private Context context;
    List<TransactionData> lstTransactionData;
    float floatQty;
    BillListButtonClickListener billListButtonClickListener;

    public BillListAdapter(Context context,List<TransactionData> lstTransactionData){
        this.context=context;
        this.lstTransactionData=lstTransactionData;
    }

    public void setOnItemDeletedClickListener(BillListButtonClickListener billListButtonClickListener){
        this.billListButtonClickListener = billListButtonClickListener;
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
        TextView tvItemName, tvQuantity, tvAmount,tvPrice;
        ImageButton btnItemDelete,btnQtyRemove;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_bill, parent,false);
            holder=new ViewHolder();
            holder.tvItemName =(TextView) row.findViewById(R.id.tvItemName);
            holder.tvQuantity =(TextView) row.findViewById(R.id.tvQuantity);
            holder.tvPrice=(TextView) row.findViewById(R.id.tvPrice);
            holder.tvAmount =(TextView) row.findViewById(R.id.tvAmount);
            holder.btnItemDelete=(ImageButton)row.findViewById(R.id.btnItemDelete);
            holder.btnQtyRemove=(ImageButton)row.findViewById(R.id.btnQtyRemove);

            holder.tvItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvAmount.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

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
            /**int qty=lstTransactionData.get(position).getIntegerQty();
            if(qty>1)holder.btnQtyRemove.setVisibility(View.VISIBLE);
            else holder.btnQtyRemove.setVisibility(View.INVISIBLE);**/
        }else{
            holder.tvQuantity.setText(String.valueOf(lstTransactionData.get(position).getFloatQty()));
        }
        holder.tvAmount.setText(String.valueOf(lstTransactionData.get(position).getAmount()));
        holder.tvPrice.setText(String.valueOf(lstTransactionData.get(position).getSalePrice()));

        holder.btnItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(billListButtonClickListener !=null){
                    billListButtonClickListener.onItemDeletedClickListener(position);
                }
            }
        });

        holder.btnQtyRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(billListButtonClickListener !=null){
                    billListButtonClickListener.onQtyRemovedClickListener(position);
                }
            }
        });

        return row;
    }
}
