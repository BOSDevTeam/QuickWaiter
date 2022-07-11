package adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import data.WaiterData;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class WaiterSpinnerAdapter extends BaseAdapter{
    private Context context;
    List<WaiterData> lstWaiterData;

    public WaiterSpinnerAdapter(Context context, List<WaiterData> lstWaiterData){
        this.context=context;
        this.lstWaiterData =lstWaiterData;
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

    public View getView(final int position,View convertView,ViewGroup parent){
        View vi=convertView;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.spinner_item, null);
        }
        TextView tvSpinnerItem=(TextView)vi.findViewById(R.id.tvSpinnerItem);

        tvSpinnerItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        tvSpinnerItem.setText(lstWaiterData.get(position).getWaiterName());

        return vi;
    }

}
