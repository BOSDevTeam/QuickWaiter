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

import data.OpenOrderKitchenData;

/**
 * Created by User on 9/8/2017.
 */
public class OpenOrderKitchenChildListAdapter extends BaseAdapter {

    private Context context;
    List<OpenOrderKitchenData> lstOpenOrderKitchenData;

    public OpenOrderKitchenChildListAdapter(Context context, List<OpenOrderKitchenData> lstOpenOrderKitchenData){
        this.context=context;
        this.lstOpenOrderKitchenData = lstOpenOrderKitchenData;
    }

    public int getCount(){
        return lstOpenOrderKitchenData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        TextView tvItem, tvQuantity;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_child_open_order_kitchen, null);
            viewHolder = new ViewHolder();
            viewHolder.tvItem =(TextView)convertView.findViewById(R.id.tvItem);
            viewHolder.tvQuantity =(TextView)convertView.findViewById(R.id.tvQuantity);

            viewHolder.tvItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            viewHolder.tvQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvItem.setText(lstOpenOrderKitchenData.get(position).getItemName());
        viewHolder.tvQuantity.setText(lstOpenOrderKitchenData.get(position).getStringQty());

        return convertView;
    }
}
