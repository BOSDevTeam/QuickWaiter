package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.ItemSubGroupData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class SpItemSubGroupAdapter extends BaseAdapter{
    private Context context;
    List<ItemSubGroupData> lstItemSubGroup;

    public SpItemSubGroupAdapter(Context context, List<ItemSubGroupData> lstItemSubGroup){
        this.context=context;
        this.lstItemSubGroup = lstItemSubGroup;
    }

    public int getCount(){
        return lstItemSubGroup.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(final int position,View convertView,ViewGroup parent){
        View vi=convertView;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.spinner_item, null);
        }
        TextView tvSpinnerItem=(TextView)vi.findViewById(R.id.tvSpinnerItem);
        tvSpinnerItem.setTextColor(context.getResources().getColor(android.R.color.black));

        tvSpinnerItem.setText(lstItemSubGroup.get(position).getSubGroupName());

        return vi;
    }

}
