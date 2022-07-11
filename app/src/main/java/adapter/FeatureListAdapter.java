package adapter;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.FeatureSettingData;
import listener.FeatureCheckListener;

/**
 * Created by NweYiAung on 15-02-2017.
 */
public class FeatureListAdapter extends BaseAdapter {

    private Context context;
    private List<FeatureSettingData> lstFeatureData;
    FeatureCheckListener checkedListener;

    public FeatureListAdapter(Context context,List<FeatureSettingData> lstFeatureData){
        this.context=context;
        this.lstFeatureData =lstFeatureData;
    }

    public void setOnCheckedListener(FeatureCheckListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return  lstFeatureData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvFeatureName;
        protected CheckBox chkFeature;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_feature, null);
            viewHolder = new ViewHolder();
            viewHolder.chkFeature=(CheckBox)convertView.findViewById(R.id.chkFeature);
            viewHolder.tvFeatureName = (TextView) convertView.findViewById(R.id.tvFeatureName);

            viewHolder.chkFeature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    lstFeatureData.get(getPosition).setSelected(buttonView.isChecked());
                    if(checkedListener!=null){
                        if(buttonView.isChecked()){
                            checkedListener.onFeatureCheckedListener(getPosition);
                        }else{
                            checkedListener.onFeatureUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvFeatureName, viewHolder.tvFeatureName);
            convertView.setTag(R.id.chkFeature, viewHolder.chkFeature);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkFeature.setTag(position);
        viewHolder.tvFeatureName.setText(lstFeatureData.get(position).getFeatureName());
        if(lstFeatureData.get(position).getIsAllow()==1)
            viewHolder.chkFeature.setChecked(true);
        else
            viewHolder.chkFeature.setChecked(false);
        return convertView;
    }
}
