package adapter;

import android.graphics.Typeface;
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

import data.MainMenuData;
import listener.DialogMenuCheckListener;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class DialogManageMenuListAdapter extends BaseAdapter {

    private Context context;
    private List<MainMenuData> lstMainMenuData;
    DialogMenuCheckListener checkedListener;

    public DialogManageMenuListAdapter(Context context,List<MainMenuData> lstMainMenuData){
        this.context=context;
        this. lstMainMenuData =lstMainMenuData;
    }

    public void setOnCheckedListener(DialogMenuCheckListener checkedListener){
        this.checkedListener=checkedListener;
    }

    public int getCount(){
        return  lstMainMenuData.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    static class ViewHolder {
        protected TextView tvMainMenu;
        protected CheckBox chkMainMenu;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_manage_menu, null);
            viewHolder = new ViewHolder();
            viewHolder.chkMainMenu=(CheckBox)convertView.findViewById(R.id.chkMainMenu);
            viewHolder.tvMainMenu = (TextView) convertView.findViewById(R.id.tvMainMenu);

            viewHolder.tvMainMenu.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            viewHolder.chkMainMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    lstMainMenuData.get(getPosition).setSelected(buttonView.isChecked());
                    if(checkedListener!=null){
                        if(buttonView.isChecked()){
                            checkedListener.onMenuCheckedListener(getPosition);
                        }else{
                            checkedListener.onMenuUnCheckedListener(getPosition);
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvMainMenu, viewHolder.tvMainMenu);
            convertView.setTag(R.id.chkMainMenu, viewHolder.chkMainMenu);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.chkMainMenu.setTag(position);
        viewHolder.tvMainMenu.setText(lstMainMenuData.get(position).getMainMenuName());
        if(lstMainMenuData.get(position).getIsAllow()==1)viewHolder.chkMainMenu.setChecked(true);
        else viewHolder.chkMainMenu.setChecked(false);
        /*viewHolder.chkMainMenu.setChecked(lstMainMenuData.get(position).isSelected());*/
        return convertView;
    }
}
