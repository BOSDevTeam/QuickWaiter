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

import common.DBHelper;
import data.ItemData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StItemListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<ItemData> lstItemData;
    DBHelper db;

    public StItemListAdapter(Context context, List<ItemData> lstItemData){
        this.context=context;
        this.lstItemData =lstItemData;
        db=new DBHelper(context);
    }

    public void setOnItemSubButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstItemData.size();
    }

    @Override
    public String getItem(int position){
        return lstItemData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvItemName, tvItemID,tvSubMenuName;
        ImageButton btnEdit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_item, parent,false);
            holder=new ViewHolder();
            holder.tvItemID =(TextView) row.findViewById(R.id.tvItemID);
            holder.tvItemName =(TextView) row.findViewById(R.id.tvItemName);
            holder.tvSubMenuName =(TextView) row.findViewById(R.id.tvSubMenuName);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);

            holder.tvItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvSubMenuName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvItemID.setText(lstItemData.get(position).getItemid());
        holder.tvItemName.setText(lstItemData.get(position).getItemName());
        holder.tvSubMenuName.setText(lstItemData.get(position).getSubMenuName());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onEditButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
