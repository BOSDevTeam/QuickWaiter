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

import data.ItemSubGroupData;
import listener.SetupEditDeleteButtonClickListener;

/**
 * Created by NweYiAung on 01-03-2017.
 */
public class StItemSubGroupListAdapter extends BaseAdapter {

    private Context context;
    SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener;
    List<ItemSubGroupData> lstItemSubGroupData;

    public StItemSubGroupListAdapter(Context context, List<ItemSubGroupData> lstItemSubGroupData){
        this.context=context;
        this.lstItemSubGroupData =lstItemSubGroupData;
    }

    public void setOnSetupEditDeleteButtonClickListener(SetupEditDeleteButtonClickListener setupEditDeleteButtonClickListener){
        this.setupEditDeleteButtonClickListener =setupEditDeleteButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstItemSubGroupData.size();
    }

    @Override
    public String getItem(int position){
        return lstItemSubGroupData.get(position).getSubGroupName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvGroupName,tvSubTitle,tvCheckType;
        ImageButton btnEdit,btnDelete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_st_item_sub_group, parent,false);
            holder=new ViewHolder();
            holder.tvGroupName= (TextView) row.findViewById(R.id.tvGroupName);
            holder.tvSubTitle= (TextView) row.findViewById(R.id.tvSubTitle);
            holder.tvCheckType= (TextView) row.findViewById(R.id.tvCheckType);
            holder.btnEdit= (ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnDelete= (ImageButton) row.findViewById(R.id.btnDelete);

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvGroupName.setText(lstItemSubGroupData.get(position).getSubGroupName());
        holder.tvSubTitle.setText(lstItemSubGroupData.get(position).getSubTitle());
        holder.tvCheckType.setText(lstItemSubGroupData.get(position).getCheckType());

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
