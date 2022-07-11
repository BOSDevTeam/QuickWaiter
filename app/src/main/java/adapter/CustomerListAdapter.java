package adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import common.DBHelper;
import data.CustomerData;
import listener.CustomerListGroupListener;

/**
 * Created by NweYiAung on 21-02-2017.
 */
public class CustomerListAdapter extends BaseAdapter {

    private Context context;
    CustomerListGroupListener customerListGroupListener;
    List<CustomerData> lstCustomerData;
    DBHelper db;
    int isSetAllCustomer;

    public CustomerListAdapter(Context context, List<CustomerData> lstCustomerData){
        this.context=context;
        this.lstCustomerData =lstCustomerData;
        db = new DBHelper(context);
    }

    public void setOnCustomerListGroupListener(CustomerListGroupListener customerListGroupListener){
        this.customerListGroupListener =customerListGroupListener;
    }

    @Override
    public int getCount(){
        return lstCustomerData.size();
    }

    @Override
    public String getItem(int position){
        return lstCustomerData.get(position).getTableName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvTable,tvDate,tvTime,tvMan,tvWomen,tvChild,tvTotal;
        ImageButton btnPlusMan,btnMinusMan,btnPlusWomen,btnMinusWomen,btnPlusChild,btnMinusChild,btnMinusTotal,btnPlusTotal;
        Button btnSave;
        LinearLayout layoutMan,layoutWomen,layoutChild,layoutTotal;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_customer, parent,false);
            holder=new ViewHolder();
            holder.tvTable=(TextView) row.findViewById(R.id.tvTable);
            holder.tvDate=(TextView) row.findViewById(R.id.tvDate);
            holder.tvTime=(TextView) row.findViewById(R.id.tvTime);
            holder.tvMan=(TextView) row.findViewById(R.id.tvMan);
            holder.tvWomen=(TextView) row.findViewById(R.id.tvWomen);
            holder.tvTotal=(TextView) row.findViewById(R.id.tvTotal);
            holder.tvChild=(TextView) row.findViewById(R.id.tvChild);
            holder.btnPlusMan=(ImageButton) row.findViewById(R.id.btnPlusMan);
            holder.btnMinusMan=(ImageButton) row.findViewById(R.id.btnMinusMan);
            holder.btnPlusWomen=(ImageButton) row.findViewById(R.id.btnPlusWomen);
            holder.btnMinusWomen=(ImageButton) row.findViewById(R.id.btnMinusWomen);
            holder.btnPlusChild=(ImageButton) row.findViewById(R.id.btnPlusChild);
            holder.btnMinusChild=(ImageButton) row.findViewById(R.id.btnMinusChild);
            holder.btnPlusTotal=(ImageButton) row.findViewById(R.id.btnPlusTotal);
            holder.btnMinusTotal=(ImageButton) row.findViewById(R.id.btnMinusTotal);
            holder.btnSave=(Button) row.findViewById(R.id.btnSave);
            holder.layoutMan=(LinearLayout) row.findViewById(R.id.layoutMan);
            holder.layoutWomen=(LinearLayout) row.findViewById(R.id.layoutWomen);
            holder.layoutChild=(LinearLayout) row.findViewById(R.id.layoutChild);
            holder.layoutTotal=(LinearLayout) row.findViewById(R.id.layoutTotal);

            holder.tvTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        Cursor cur = db.getSetAllCustomerFeature();
        if(cur.moveToFirst())isSetAllCustomer=cur.getInt(0);
        if(isSetAllCustomer == 1){
            holder.layoutMan.setVisibility(View.GONE);
            holder.layoutWomen.setVisibility(View.GONE);
            holder.layoutChild.setVisibility(View.GONE);
            holder.layoutTotal.setVisibility(View.VISIBLE);
        } else if (isSetAllCustomer == 0) {
            holder.layoutMan.setVisibility(View.VISIBLE);
            holder.layoutWomen.setVisibility(View.VISIBLE);
            holder.layoutChild.setVisibility(View.VISIBLE);
            holder.layoutTotal.setVisibility(View.GONE);
        }

        holder.tvTable.setText(lstCustomerData.get(position).getTableName());
        holder.tvDate.setText(lstCustomerData.get(position).getDate());
        holder.tvTime.setText(lstCustomerData.get(position).getTime());

        if(isSetAllCustomer == 0) {
            holder.tvMan.setText(String.valueOf(lstCustomerData.get(position).getMan()));
            holder.tvWomen.setText(String.valueOf(lstCustomerData.get(position).getWomen()));
            holder.tvChild.setText(String.valueOf(lstCustomerData.get(position).getChild()));
        }else if(isSetAllCustomer == 1){
            holder.tvTotal.setText(String.valueOf(lstCustomerData.get(position).getTotal()));
        }

        holder.btnPlusMan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onManPlusClickListener(position,holder.tvMan);
                }
            }
        });

        holder.btnMinusMan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onManMinusClickListener(position,holder.tvMan);
                }
            }
        });

        holder.btnPlusWomen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onWomenPlusClickListener(position,holder.tvWomen);
                }
            }
        });

        holder.btnMinusWomen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onWomenMinusClickListener(position,holder.tvWomen);
                }
            }
        });

        holder.btnPlusChild.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onChildPlusClickListener(position,holder.tvChild);
                }
            }
        });

        holder.btnMinusChild.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onChildMinusClickListener(position,holder.tvChild);
                }
            }
        });

        holder.btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onSaveClickListener(position);
                }
            }
        });

        holder.btnPlusTotal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onTotalPlusClickListener(position,holder.tvTotal);
                }
            }
        });

        holder.btnMinusTotal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(customerListGroupListener !=null){
                    customerListGroupListener.onTotalMinusClickListener(position,holder.tvTotal);
                }
            }
        });

        return row;
    }
}
