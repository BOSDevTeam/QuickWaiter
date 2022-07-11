package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.ItemSubData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StItemSubListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<ItemSubData> lstItemSubData;

    public StItemSubListAdapter(Context context, List<ItemSubData> lstItemSubData){
        this.context=context;
        this.lstItemSubData =lstItemSubData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstItemSubData.size();
    }

    @Override
    public String getItem(int position){
        return lstItemSubData.get(position).getSubGroupName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvSubName,tvPrice,tvGroupName;
        ImageButton btnEdit,btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_item_sub, parent,false);
            holder=new ViewHolder();
            holder.tvGroupName= (TextView) row.findViewById(R.id.tvGroupName);
            holder.tvSubName= (TextView) row.findViewById(R.id.tvSubName);
            holder.tvPrice= (TextView) row.findViewById(R.id.tvPrice);
            holder.btnEdit= (ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete= (ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvGroupName.setText(lstItemSubData.get(position).getSubGroupName());
        holder.tvSubName.setText(lstItemSubData.get(position).getSubName());
        holder.tvPrice.setText(String.valueOf(lstItemSubData.get(position).getPrice()));

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onEditButtonClickListener(position);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(setupEditDeleteButtonClickListener !=null){
                    setupEditDeleteButtonClickListener.onDeleteButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
