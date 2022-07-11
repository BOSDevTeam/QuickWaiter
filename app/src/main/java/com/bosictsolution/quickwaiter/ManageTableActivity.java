package com.bosictsolution.quickwaiter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.TableListAdapter;
import common.DBHelper;
import data.TableData;
import data.TableTypeData;
import listener.TableCheckListener;

public class ManageTableActivity extends AppCompatActivity implements TableCheckListener {

    TextView tvTableType,tvTableTypeName;
    ListView lvTableType,lvTable;
    CheckBox chkAll;
    Button btnApply;

    private DBHelper db;
    ArrayAdapter adapter;
    final Context context = this;

    TableListAdapter tableListAdapter;

    List<TableTypeData> lstTableTypeData=new ArrayList<>();
    List<TableData> lstTableDataByTableTypeID =new ArrayList<>();
    List<Integer> lstCheckedTableIDByTableType =new ArrayList<>();

    int selectedTableTypeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_table);

        db=new DBHelper(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        setLayoutResource();
        setTitle("Manage Table");

        getAllTableType();
        selectedTableTypeID=lstTableTypeData.get(0).getTableTypeID();
        String first_table_type_name=lstTableTypeData.get(0).getTableTypeName();
        tvTableTypeName.setText(first_table_type_name);
        getTableByTableTypeID(selectedTableTypeID);

        lvTableType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lstCheckedTableIDByTableType.size()!=0) saveActiveTable();
                selectedTableTypeID=lstTableTypeData.get(position).getTableTypeID();
                String tableTypeName=lstTableTypeData.get(position).getTableTypeName();
                tvTableTypeName.setText(tableTypeName);
                getTableByTableTypeID(selectedTableTypeID);
            }
        });
        chkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lstCheckedTableIDByTableType=new ArrayList<>();
                if(chkAll.isChecked()){
                    db.activeTableByTableTypeID(selectedTableTypeID);
                }else {
                    db.inActiveTableByTableTypeID(selectedTableTypeID);
                }
                getTableByTableTypeID(selectedTableTypeID);
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveActiveTable();
                Toast.makeText(getApplicationContext(), "Table Applied", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTableCheckedListener(int position){
        int tableid=lstTableDataByTableTypeID.get(position).getTableid();
        if(!lstCheckedTableIDByTableType.contains(tableid)) {
            lstCheckedTableIDByTableType.add(tableid);
        }
    }

    @Override
    public void onTableUnCheckedListener(int position){
        int removeIndex= lstCheckedTableIDByTableType.indexOf(lstTableDataByTableTypeID.get(position).getTableid());
        if(removeIndex!=-1) {
            lstCheckedTableIDByTableType.remove(removeIndex);
        }
    }

    private void saveActiveTable(){
        if(lstCheckedTableIDByTableType.size()!=0) {
            db.inActiveTableByTableTypeID(selectedTableTypeID);
            for (int i = 0; i < lstCheckedTableIDByTableType.size(); i++) {
                db.activeTableByTableID(lstCheckedTableIDByTableType.get(i));
            }
        }
    }

    private void getAllTableType(){
        Cursor cur=db.getAllTableType();
        List<String> lstTableTypeName=new ArrayList<>();
        lstTableTypeData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TableTypeData data=new TableTypeData();
                data.setTableTypeID(cur.getInt(0));
                data.setTableTypeName(cur.getString(1));
                lstTableTypeName.add(cur.getString(1));
                lstTableTypeData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
        adapter=new ArrayAdapter(this,R.layout.list_manage_table_type,R.id.tvListRowItem,lstTableTypeName);
        lvTableType.setAdapter(adapter);
    }

    private void getTableByTableTypeID(int tableTypeID){
        lstCheckedTableIDByTableType =new ArrayList<>();
        lstTableDataByTableTypeID =new ArrayList<>();
        Cursor cur=db.getTableByTableTypeID(tableTypeID);
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TableData data=new TableData();
                data.setTableid(cur.getInt(0));
                data.setTableName(cur.getString(1));
                data.setIsActive(cur.getInt(2));
                if(cur.getInt(2)==1){
                    data.setSelected(true);
                    lstCheckedTableIDByTableType.add(cur.getInt(0));
                }
                else data.setSelected(false);
                lstTableDataByTableTypeID.add(data);
            }
        }
        if(lstTableDataByTableTypeID.size()==lstCheckedTableIDByTableType.size())chkAll.setChecked(true);
        else chkAll.setChecked(false);
        tableListAdapter=new TableListAdapter(context,lstTableDataByTableTypeID);
        lvTable.setAdapter(tableListAdapter);
        tableListAdapter.setOnCheckedListener(this);
    }

    private void setLayoutResource(){
        tvTableType =(TextView)findViewById(R.id.tvTableType);
        tvTableTypeName =(TextView)findViewById(R.id.tvTableTypeName);
        lvTable=(ListView)findViewById(R.id.lvTable);
        lvTableType =(ListView)findViewById(R.id.lvTableType);
        chkAll=(CheckBox)findViewById(R.id.chkAll);
        btnApply=(Button)findViewById(R.id.btnApply);

        tvTableTypeName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
    }
}
