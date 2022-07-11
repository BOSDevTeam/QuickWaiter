package adapter;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import common.DBHelper;
import data.ItemData;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ItemListAdapter extends BaseAdapter{

    private Context context;
    List<ItemData> lstItemData;
    DBHelper db;

    public ItemListAdapter(Context context,List<ItemData> lstItemData){
        this.context=context;
        this.lstItemData=lstItemData;
        db=new DBHelper(context);
    }

    public int getCount(){
        return lstItemData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        TextView tvName,tvPrice;
        LinearLayout layoutItem;
    }
    public View getView(final int position,View convertView,ViewGroup parent){
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_item, null);

            holder=new ViewHolder();
            holder.tvName=(TextView) convertView.findViewById(R.id.tvListItemName);
            holder.tvPrice=(TextView) convertView.findViewById(R.id.tvListItemPrice);
            holder.layoutItem=(LinearLayout)convertView.findViewById(R.id.layoutItem);

            holder.tvName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder) convertView.getTag();
        }

        Cursor cur=db.getHideSalePriceSetting();
        if(cur.moveToFirst()){
            if(cur.getInt(0)==1)holder.tvPrice.setVisibility(View.INVISIBLE);
            else if(cur.getInt(0)==0) holder.tvPrice.setVisibility(View.VISIBLE);
        }

        if(lstItemData.get(position).getOutOfOrder()==1)holder.layoutItem.setBackgroundResource(R.color.colorSoftBlack);
        else holder.layoutItem.setBackgroundResource(R.drawable.bg_list_item);
        holder.tvName.setText(lstItemData.get(position).getItemName());
        holder.tvPrice.setText(String.valueOf(lstItemData.get(position).getPrice()));

        return convertView;
    }
}
