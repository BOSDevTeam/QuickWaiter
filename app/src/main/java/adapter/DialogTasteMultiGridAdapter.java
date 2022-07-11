package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.TasteMultiData;
import listener.DialogTasteMultiClickListener;

/**
 * Created by User on 6/21/2017.
 */
public class DialogTasteMultiGridAdapter extends BaseAdapter {

    private Context context;
    private List<TasteMultiData> lstTasteMulti;
    DialogTasteMultiClickListener tasteMultiClickListener;

    public DialogTasteMultiGridAdapter(Context context,List<TasteMultiData> lstTasteMulti){
        this.context=context;
        this.lstTasteMulti = lstTasteMulti;
    }

    public void setOnTasteClickListener(DialogTasteMultiClickListener tasteMultiClickListener){
        this.tasteMultiClickListener =tasteMultiClickListener;
    }

    public int getCount(){
        return lstTasteMulti.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View vi=convertView;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi=inflater.inflate(R.layout.list_taste, null);
        }
        LinearLayout layoutTaste=(LinearLayout)vi.findViewById(R.id.layoutTaste);
        TextView tvListTaste=(TextView)vi.findViewById(R.id.tvListTaste);
        TextView tvListPrice=(TextView)vi.findViewById(R.id.tvListPrice);

        tvListTaste.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvListPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        tvListTaste.setText(lstTasteMulti.get(position).getTasteName());
        tvListPrice.setText(String.valueOf(lstTasteMulti.get(position).getPrice()));

        layoutTaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(tasteMultiClickListener !=null){
                    tasteMultiClickListener.onTasteMultiClickListener(position);
                }
            }
        });

        return vi;
    }
}
