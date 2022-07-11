package com.bosictsolution.quickwaiter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.SpItemSubGroupAdapter;
import adapter.StItemListAdapter;
import adapter.StItemSubGroupListAdapter;
import adapter.StItemSubListAdapter;
import common.DBHelper;
import common.SystemSetting;
import data.ItemData;
import data.ItemSubData;
import data.ItemSubGroupData;
import listener.SetupEditDeleteButtonClickListener;

public class ItemSubSetupActivity extends AppCompatActivity implements SetupEditDeleteButtonClickListener {

    Button  btnItemModule,btnNewAddItemSubGroup,btnNewAddItemSub,btnSearchItem, btnItemSubGroupModule,btnItemSubModule,btnAddItemSub;
    ListView lvSetup;
    LinearLayout layoutSetupItem,layoutSetupItemSubGroup,layoutSetupItemSub;
    TextView tvConfirmMessage;
    DBHelper db;
    SystemSetting systemSetting=new SystemSetting();

    StItemListAdapter itemListAdapter;
    StItemSubGroupListAdapter itemSubGroupListAdapter;
    StItemSubListAdapter itemSubListAdapter;

    List<ItemData> lstItemData;
    List<ItemSubGroupData> lstItemSubGroupData,lstIncludeItemSubGroup=new ArrayList<>();
    List<ItemSubData> lstItemSubData;

    SpItemSubGroupAdapter spItemSubGroupAdapter;

    private Context context=this;
    int deleteid,editid;
    String confirmMessage,setupModuleName="",editItemId;

    private static final int ADD_ITEM_SUB_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_sub_setup);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        db=new DBHelper(this);
        setLayoutResource();
        setTitle("Item Sub Setup");

        btnItemModule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupModuleName= btnItemModule.getText().toString();
                showItemList();
                layoutSetupItem.setVisibility(View.VISIBLE);
                layoutSetupItemSubGroup.setVisibility(View.GONE);
                layoutSetupItemSub.setVisibility(View.GONE);
                btnItemModule.setTextColor(getResources().getColor(R.color.colorAccent));
                btnItemSubGroupModule.setTextColor(getResources().getColor(R.color.colorBlack));
                btnItemSubModule.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });
        btnItemSubGroupModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupModuleName= btnItemSubGroupModule.getText().toString();
                showItemSubGroupList();
                layoutSetupItemSubGroup.setVisibility(View.VISIBLE);
                layoutSetupItem.setVisibility(View.GONE);
                layoutSetupItemSub.setVisibility(View.GONE);
                btnItemModule.setTextColor(getResources().getColor(R.color.colorBlack));
                btnItemSubGroupModule.setTextColor(getResources().getColor(R.color.colorAccent));
                btnItemSubModule.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });
        btnItemSubModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupModuleName= btnItemSubModule.getText().toString();
                showItemSubList();
                layoutSetupItemSub.setVisibility(View.VISIBLE);
                layoutSetupItemSubGroup.setVisibility(View.GONE);
                layoutSetupItem.setVisibility(View.GONE);
                btnItemModule.setTextColor(getResources().getColor(R.color.colorBlack));
                btnItemSubGroupModule.setTextColor(getResources().getColor(R.color.colorBlack));
                btnItemSubModule.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
        btnNewAddItemSubGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSubGroupDialog("","",1,false);
            }
        });
        btnNewAddItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSubDialog(0,"",0,false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditButtonClickListener(int position){
        if(setupModuleName.equals(btnItemModule.getText().toString())){
            lstIncludeItemSubGroup=db.getItemSubGroupByItemID(lstItemData.get(position).getItemid());
            showItemDialog(lstItemData.get(position).getItemid(),lstItemData.get(position).getItemName());
        }
        else if(setupModuleName.equals(btnItemSubGroupModule.getText().toString())){
            editid=lstItemSubGroupData.get(position).getPkId();
            showItemSubGroupDialog(lstItemSubGroupData.get(position).getSubGroupName(),lstItemSubGroupData.get(position).getSubTitle(),lstItemSubGroupData.get(position).isSingleCheck(),true);
        }
        else if(setupModuleName.equals(btnItemSubModule.getText().toString())){
            editid=lstItemSubData.get(position).getPkId();
            int subGroupId=lstItemSubData.get(position).getSubGroupId();
            int subGroupPosition=0;
            for(int i=0;i<lstItemSubGroupData.size();i++){
                if(lstItemSubGroupData.get(i).getPkId()==subGroupId){
                    subGroupPosition=i;
                    break;
                }
            }
            showItemSubDialog(subGroupPosition,lstItemSubData.get(position).getSubName(),lstItemSubData.get(position).getPrice(),true);
        }
    }

    @Override
    public void onDeleteButtonClickListener(int position){
        if(setupModuleName.equals(btnItemSubGroupModule.getText().toString())){
            confirmMessage="Are you sure you want to delete item sub group "+lstItemSubGroupData.get(position).getSubGroupName()+"?";
        }
        else if(setupModuleName.equals(btnItemSubModule.getText().toString())){
            confirmMessage="Are you sure you want to delete item sub "+lstItemSubData.get(position).getSubName()+"?";
        }
        showConfirmDialog(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
           if(requestCode == ADD_ITEM_SUB_REQUEST){
                lstIncludeItemSubGroup =(List<ItemSubGroupData>) data.getSerializableExtra("LstItemSubGroup");
                if(lstIncludeItemSubGroup.size() != 0 && btnAddItemSub != null) btnAddItemSub.setText("Include "+lstIncludeItemSubGroup.size()+" Item Sub Group");
                else btnAddItemSub.setText("Add Item Sub");
            }
        }
    }

    private void showItemList(){
        lstItemData=new ArrayList<>();
        Cursor cur=db.getAllItem();
        while(cur.moveToNext()){
            ItemData data=new ItemData();
            data.setItemid(cur.getString(0));
            data.setItemName(cur.getString(1));
            data.setSubMenuName(cur.getString(2));
            lstItemData.add(data);
        }
        itemListAdapter=new StItemListAdapter(this,lstItemData);
        lvSetup.setAdapter(itemListAdapter);
        itemListAdapter.setOnItemSubButtonClickListener(this);
    }

    private void showItemSubGroupList(){
        lstItemSubGroupData=new ArrayList<>();
        Cursor cur=db.getItemSubGroup();
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
            lstItemSubGroupData.add(data);
        }
        itemSubGroupListAdapter=new StItemSubGroupListAdapter(this,lstItemSubGroupData);
        lvSetup.setAdapter(itemSubGroupListAdapter);
        itemSubGroupListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void showItemSubList(){
        lstItemSubData=new ArrayList<>();
        Cursor cur=db.getItemSub();
        while(cur.moveToNext()){
            ItemSubData data=new ItemSubData();
            data.setPkId(cur.getInt(0));
            data.setSubGroupId(cur.getInt(1));
            data.setSubName(cur.getString(2));
            data.setPrice(cur.getInt(3));
            data.setSubGroupName(cur.getString(4));
            lstItemSubData.add(data);
        }
        itemSubListAdapter=new StItemSubListAdapter(this,lstItemSubData);
        lvSetup.setAdapter(itemSubListAdapter);
        itemSubListAdapter.setOnSetupEditDeleteButtonClickListener(this);
    }

    private void bindItemSubGroup(Spinner spItemSubGroup){
        lstItemSubGroupData =new ArrayList<>();
        Cursor cur=db.getItemSubGroup();
        while(cur.moveToNext()){
            ItemSubGroupData data=new ItemSubGroupData();
            data.setPkId(cur.getInt(0));
            data.setSubGroupName(cur.getString(1));
            lstItemSubGroupData.add(data);
        }
        if(lstItemSubGroupData.size()!=0) {
            spItemSubGroupAdapter = new SpItemSubGroupAdapter(this, lstItemSubGroupData);
            spItemSubGroup.setAdapter(spItemSubGroupAdapter);
        }
    }

    /**
     * end spinner bind methods
     */

    /**
     * start common methods
     */
    private void showConfirmDialog(final int position){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dialog_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(confirmMessage);
        passwordDialog.setCancelable(true);
        final android.app.AlertDialog passwordRequireDialog=passwordDialog.create();
        passwordRequireDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                passwordRequireDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               if(setupModuleName.equals(btnItemSubGroupModule.getText().toString())){
                    deleteid=lstItemSubGroupData.get(position).getPkId();
                    if(db.deleteItemSubGroup(deleteid)){
                        Toast.makeText(context,"Delete Successful!",Toast.LENGTH_SHORT).show();
                        showItemSubGroupList();
                    }
                }
                else if(setupModuleName.equals(btnItemSubModule.getText().toString())){
                    deleteid=lstItemSubData.get(position).getPkId();
                    if(db.deleteItemSub(deleteid)){
                        Toast.makeText(context,"Delete Successful!",Toast.LENGTH_SHORT).show();
                        showItemSubList();
                    }
                }
                passwordRequireDialog.dismiss();
            }
        });
    }

    private void setLayoutResource(){
        layoutSetupItem=(LinearLayout)findViewById(R.id.layoutSetupItem);
        layoutSetupItemSubGroup=(LinearLayout)findViewById(R.id.layoutSetupItemSubGroup);
        layoutSetupItemSub=(LinearLayout)findViewById(R.id.layoutSetupItemSub);

        lvSetup=(ListView)findViewById(R.id.lvSetup);

        btnItemModule =(Button)findViewById(R.id.btnItemModule);
        btnItemSubGroupModule =(Button)findViewById(R.id.btnItemSubGroupModule);
        btnItemSubModule =(Button)findViewById(R.id.btnItemSubModule);

        btnNewAddItemSubGroup =(Button)findViewById(R.id.btnAddNewItemSubGroup);
        btnNewAddItemSub =(Button)findViewById(R.id.btnAddNewItemSub);
    }

    private void showItemSubGroupDialog(String groupName, String subTitle, int isSingleCheck, final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_item_sub_group, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etGroupName = (EditText) view.findViewById(R.id.etGroupName);
        final EditText etSubTitle = (EditText) view.findViewById(R.id.etSubTitle);
        final RadioButton rdoAllowSingle = (RadioButton) view.findViewById(R.id.rdoAllowSingle);
        final RadioButton rdoAllowMulti = (RadioButton) view.findViewById(R.id.rdoAllowMulti);
        final Button btnClose = (Button) view.findViewById(R.id.btnClose);
        final Button btnSave = (Button) view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        if(edit){
            etGroupName.setText(groupName);
            etSubTitle.setText(subTitle);
            if(isSingleCheck == 1){
                rdoAllowSingle.setChecked(true);
                rdoAllowMulti.setChecked(false);
            }else{
                rdoAllowSingle.setChecked(false);
                rdoAllowMulti.setChecked(true);
            }
        }

        rdoAllowSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdoAllowSingle.isChecked())rdoAllowMulti.setChecked(false);
            }
        });
        rdoAllowMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rdoAllowMulti.isChecked())rdoAllowSingle.setChecked(false);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showItemSubGroupList();
                setupDialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int isSingleCheck=0;
                if(etGroupName.getText().toString().length()==0){
                    Toast.makeText(context,"Enter Group Name!",Toast.LENGTH_SHORT).show();
                    etGroupName.requestFocus();
                    return;
                }else if(etSubTitle.getText().toString().length()==0){
                    Toast.makeText(context,"Enter Sub Title!",Toast.LENGTH_SHORT).show();
                    etSubTitle.requestFocus();
                    return;
                }
                if(rdoAllowSingle.isChecked())isSingleCheck=1;
                else isSingleCheck=0;
                if(!edit) {
                    if (db.insertItemSubGroup(etGroupName.getText().toString(),etSubTitle.getText().toString(),isSingleCheck)) {
                        Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
                        etGroupName.setText("");
                        etSubTitle.setText("");
                    }
                }else{
                    if (db.updateItemSubGroup(editid,etGroupName.getText().toString(),etSubTitle.getText().toString(),isSingleCheck)) {
                        Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
                        etGroupName.setText("");
                        etSubTitle.setText("");
                        showItemSubGroupList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showItemSubDialog(int itemSubGroupPosition,String subName,int price,final boolean edit){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_item_sub, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final Spinner spItemSubGroup= (Spinner) view.findViewById(R.id.spItemSubGroup);
        final EditText etSubName= (EditText) view.findViewById(R.id.etSubName);
        final EditText etPrice= (EditText) view.findViewById(R.id.etPrice);
        final Button btnClose= (Button) view.findViewById(R.id.btnClose);
        final Button btnSave= (Button) view.findViewById(R.id.btnSave);

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        bindItemSubGroup(spItemSubGroup);

        if(edit){
            spItemSubGroup.setSelection(itemSubGroupPosition);
            etSubName.setText(subName);
            etPrice.setText(String.valueOf(price));
        }

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showItemSubList();
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(lstItemSubGroupData.size()==0){
                    Toast.makeText(context,"Firstly, fill item sub group!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int position= spItemSubGroup.getSelectedItemPosition();
                int itemSubGroupId=lstItemSubGroupData.get(position).getPkId();
                if(etSubName.getText().toString().length()==0){
                    Toast.makeText(context,"Enter Sub Name!",Toast.LENGTH_SHORT).show();
                    etSubName.requestFocus();
                    return;
                }else if(etPrice.getText().toString().length()==0){
                    Toast.makeText(context,"Enter Sub Price!",Toast.LENGTH_SHORT).show();
                    etPrice.requestFocus();
                    return;
                }
                if(!edit) {
                    if (db.insertItemSub(itemSubGroupId,etSubName.getText().toString(),Integer.parseInt(etPrice.getText().toString()))) {
                        Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
                        etSubName.setText("");
                        etPrice.setText("");
                    }
                }else{
                    if (db.updateItemSub(editid,itemSubGroupId,etSubName.getText().toString(),Integer.parseInt(etPrice.getText().toString()))) {
                        Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
                        etSubName.setText("");
                        etPrice.setText("");
                        showItemSubList();
                        setupDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showItemDialog(String itemid,String name){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dg_st_item, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etItemID=(EditText)view.findViewById(R.id.etItemID);
        final EditText etItemName=(EditText)view.findViewById(R.id.etItemName);
        final Button btnClose=(Button)view.findViewById(R.id.btnClose);
        final Button btnSave=(Button)view.findViewById(R.id.btnSave);
        btnAddItemSub= (Button) view.findViewById(R.id.btnAddItemSub);

        etItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        dialog.setCancelable(false);
        final android.app.AlertDialog setupDialog=dialog.create();
        setupDialog.show();

        etItemID.setText(itemid);
        etItemName.setText(name);
        if(lstIncludeItemSubGroup.size()!=0)btnAddItemSub.setText("Include "+lstIncludeItemSubGroup.size()+" Item Sub Group");

        btnAddItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,AddItemSubActivity.class);
                i.putExtra("LstIncludeItemSubGroup",(Serializable) lstIncludeItemSubGroup);
                startActivityForResult(i,ADD_ITEM_SUB_REQUEST);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setupDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String itemid = etItemID.getText().toString();
                db.deleteItemAndSub(itemid);
                for (int i = 0; i < lstIncludeItemSubGroup.size(); i++) {
                    db.insertItemAndSub(itemid, lstIncludeItemSubGroup.get(i).getPkId(), i + 1);
                }
                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                setupDialog.dismiss();
            }
        });
    }
}