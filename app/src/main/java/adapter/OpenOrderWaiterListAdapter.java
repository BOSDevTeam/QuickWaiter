package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.ArrayList;
import java.util.List;

import data.OpenOrderWaiterData;
import data.WaiterData;

/**
 * Created by User on 8/31/2017.
 */
public class OpenOrderWaiterListAdapter extends BaseAdapter {

    private Context context;
    List<WaiterData> lstWaiterData;
    List<OpenOrderWaiterData> lstOpenOrderWaiterData;

    public OpenOrderWaiterListAdapter(Context context, List<WaiterData> lstWaiterData, List<OpenOrderWaiterData> lstOpenOrderWaiterData){
        this.context=context;
        this.lstWaiterData =lstWaiterData;
        this.lstOpenOrderWaiterData = lstOpenOrderWaiterData;
    }

    public int getCount(){
        return lstWaiterData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        TextView tvWaiterName;
        RecyclerView rvList;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_open_order_waiter, null);
            viewHolder = new ViewHolder();
            viewHolder.tvWaiterName=(TextView)convertView.findViewById(R.id.tvWaiterName);
            viewHolder.rvList =(RecyclerView) convertView.findViewById(R.id.rvList);
            viewHolder.tvWaiterName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvWaiterName.setText(lstWaiterData.get(position).getWaiterName());
        int currentWaiterID=lstWaiterData.get(position).getWaiterid();
        List<OpenOrderWaiterData> lstOpenOrderWaiterDataByWaiter =new ArrayList<>();
        for(int i = 0; i< lstOpenOrderWaiterData.size(); i++){
            if(lstOpenOrderWaiterData.get(i).getWaiterid()==currentWaiterID){
                lstOpenOrderWaiterDataByWaiter.add(lstOpenOrderWaiterData.get(i));
            }
        }
        if(lstOpenOrderWaiterDataByWaiter.size()!=0) {
            viewHolder.tvWaiterName.setVisibility(View.VISIBLE);
            viewHolder.rvList.setVisibility(View.VISIBLE);
            OpenOrderWaiterRecyclerAdapter adapter = new OpenOrderWaiterRecyclerAdapter(lstOpenOrderWaiterDataByWaiter, context);
            viewHolder.rvList.setAdapter(adapter);
            viewHolder.rvList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }else{
            viewHolder.tvWaiterName.setVisibility(View.GONE);
            viewHolder.rvList.setVisibility(View.GONE);
        }

        return convertView;
    }
}
