package com.bosictsolution.quickwaiter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import adapter.ItemSubGroupRvAdapter;
import adapter.ItemSubRvAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.ItemSubData;
import data.ItemSubGroupData;
import listener.ItemSubGroupCheckListener;

public class AddItemSubActivity extends AppCompatActivity implements ItemSubGroupCheckListener {

    RecyclerView rvItemSubGroup;
    Button btnOk,btnCancel;
    private Context context=this;
    DBHelper db;
    List<ItemSubGroupData> lstItemSubGroup,lstIncludeItemSubGroup=new ArrayList<>();
    SystemSetting systemSetting=new SystemSetting();
    ItemSubGroupCheckListener itemSubGroupCheckListener;
    ItemSubGroupRvAdapter itemSubGroupRvAdapter;
    ItemSubRvAdapter itemSubRvAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_sub);
        setLayoutResource();
        db=new DBHelper(this);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);
        setTitle("Choose Item Sub Groups");

        Intent i=getIntent();
        lstIncludeItemSubGroup = (List<ItemSubGroupData>) i.getSerializableExtra("LstIncludeItemSubGroup");

        getItemSubGroup();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ItemSubGroupData> list=new ArrayList<>();
                for(int i=0;i<lstItemSubGroup.size();i++){
                    if(lstItemSubGroup.get(i).isSelected())list.add(lstItemSubGroup.get(i));
                }
                Intent data=new Intent();
                data.putExtra("LstItemSubGroup",(Serializable) list);
                setResult(RESULT_OK,data);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckListener(int position) {
        lstItemSubGroup.get(position).setSelected(true);
    }

    @Override
    public void onUnCheckListener(int position) {
        lstItemSubGroup.get(position).setSelected(false);
    }

    @Override
    public void onSubItemClickListener(int position) {
        showItemSubDialog(lstItemSubGroup.get(position).getPkId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getItemSubGroup(){
        lstItemSubGroup=new ArrayList<>();
        Cursor cur = db.getItemSubGroup();
        while(cur.moveToNext()){
            ItemSubGroupData data=new ItemSubGroupData();
            data.setPkId(cur.getInt(0));
            data.setSubGroupName(cur.getString(1));
            data.setSubTitle(cur.getString(2));
            if(cur.getInt(3)==1){
                data.setSingleCheck(1);
                data.setCheckType(systemSetting.SINGLE_CHECK);
            }else{
                data.setSingleCheck(0);
                data.setCheckType(systemSetting.MULTI_CHECK);
            }
            if(lstIncludeItemSubGroup.size()!=0){
                List<ItemSubGroupData> result = lstIncludeItemSubGroup.stream()
                        .filter(a -> Objects.equals(a.getPkId(), cur.getInt(0)))
                        .collect(Collectors.toList());

                if(result.size() != 0) data.setSelected(true);
            }
            lstItemSubGroup.add(data);
        }

        itemSubGroupRvAdapter=new ItemSubGroupRvAdapter(lstItemSubGroup,context);
        rvItemSubGroup.setAdapter(itemSubGroupRvAdapter);
        rvItemSubGroup.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        itemSubGroupRvAdapter.setListener(this);
    }

    private void showItemSubDialog(int subGroupId){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_item_sub, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final RecyclerView rvItemSub = (RecyclerView) view.findViewById(R.id.rvItemSub);
        final ImageButton btnClose = (ImageButton) view.findViewById(R.id.btnClose);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        List<ItemSubData> lstItemSubData = db.getItemSubByGroup(subGroupId);
        itemSubRvAdapter=new ItemSubRvAdapter(lstItemSubData,context);
        rvItemSub.setAdapter(itemSubRvAdapter);
        rvItemSub.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupDialog.dismiss();
            }
        });
    }

    private void setLayoutResource(){
        btnOk= (Button) findViewById(R.id.btnOk);
        btnCancel= (Button) findViewById(R.id.btnCancel);
        rvItemSubGroup= (RecyclerView) findViewById(R.id.rvItemSubGroup);
    }
}