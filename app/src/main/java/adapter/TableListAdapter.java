package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.ManageTableActivity;
import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.TableData;
import listener.TableCheckListener;

/**
 * Created by User on 1/2/2018.
 */
public class TableListAdapter extends BaseAdapter {

    private Context context;
    List<TableData> lstTableData;
    TableCheckListener checkedListener;

    public TableListAdapter(Context context,List<TableData> lstTableData){
        this.context=context;
        this.lstTableData = lstTableData;
    }

    public void setOnCheckedListener(TableCheckListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return lstTableData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        CheckBox chkTable;
        TextView tvTableName;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_table, null);

            holder = new ViewHolder();
            holder.chkTable = (CheckBox) convertView.findViewById(R.id.chkTable);
            holder.tvTableName = (TextView) convertView.findViewById(R.id.tvTableName);
            holder.tvTableName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            holder.chkTable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    lstTableData.get(getPosition).setSelected(buttonView.isChecked());
                    if (checkedListener != null) {
                        if (buttonView.isChecked()) {
                            checkedListener.onTableCheckedListener(getPosition);
                        } else {
                            checkedListener.onTableUnCheckedListener(getPosition);
                        }
                    }
                }
            });

            convertView.setTag(holder);
            convertView.setTag(R.id.chkTable, holder.chkTable);
            convertView.setTag(R.id.tvTableName, holder.tvTableName);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.chkTable.setTag(position);

        holder.tvTableName.setText(lstTableData.get(position).getTableName());
        //if (lstTableData.get(position).getIsActive() == 1) holder.chkTable.setChecked(true);
        //else holder.chkTable.setChecked(false);
        holder.chkTable.setChecked(lstTableData.get(position).isSelected());

        return convertView;
    }
}
