package adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class MenuExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String,List<String>> _listDataChild;

    public MenuExpandableListAdapter(Context context,List<String> listDataHeader,HashMap<String,List<String>> listDataChild){
        this._context=context;
        this._listDataHeader=listDataHeader;
        this._listDataChild=listDataChild;
    }

    @Override
    public Object getChild(int groupPosition,int childPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition,int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition,final int childPosition,boolean isLastChild,View convertView,ViewGroup parent){
        final String childText=(String)getChild(groupPosition,childPosition);
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.exp_list_child, null);
        }
        TextView txtListChild=(TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setTypeface(Typeface.createFromAsset(_context.getAssets(), "fonts/BOS-PETITE.TTF"));
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition){
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition,boolean isExpanded,View convertView,ViewGroup parent){
        String headerTitle=(String)getGroup(groupPosition);
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.exp_list_group, null);
        }
        TextView lblListHeader=(TextView)convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(Typeface.createFromAsset(_context.getAssets(), "fonts/BOS-PETITE.TTF"));
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public int getGroupCount(){
        return this._listDataHeader.size();
    }

    @Override
    public boolean hasStableIds(){
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition,int childPosition){
        return true;
    }
}
