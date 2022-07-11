package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import adapter.OpenOrderKitchenListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.OpenOrderKitchenData;

public class OpenOrderKitchenActivity extends AppCompatActivity {

    GridView lvMainList;
    DBHelper db;
    private Context context=this;
    ServerConnection serverconnection;
    private ProgressDialog progressDialog;
    List<OpenOrderKitchenData> lstHeaderData,lstItemData;
    OpenOrderKitchenListAdapter openOrderKitchenListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_order_kitchen);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();
        lvMainList=(GridView) findViewById(R.id.lvMainList);
        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        getTempData();
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

    private void getTempData() {
        GetTempData getTempData = new GetTempData();
        getTempData.execute("");
    }

    public class GetTempData extends AsyncTask<String,String,String> {
        String msg="";
        int msg_type;
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                } else {

                    lstHeaderData =new ArrayList<>();
                    lstItemData=new ArrayList<>();

                    String select_header = "select Distinct(ms.TableNameID),tbl.Table_Name,ms.WaiterName,ms.Tranid,convert(varchar(10),ms.Date, 101) + right(convert(varchar(32),ms.Date,100),8) from InvMasterSaleTemp ms inner join table_name tbl on ms.TableNameID=tbl.Table_Name_ID inner join InvTranSaleTemp ts on ms.Tranid=ts.Tranid";
                    Statement st_header = con.createStatement();
                    ResultSet rs_header = st_header.executeQuery(select_header);

                    while (rs_header.next()) {
                        OpenOrderKitchenData data=new OpenOrderKitchenData();
                        data.setTableid(rs_header.getInt(1));
                        data.setTableName(rs_header.getString(2));
                        data.setWaiterName(rs_header.getString(3));
                        data.setTranid(rs_header.getInt(4));
                        data.setDatetime(rs_header.getString(5));
                        lstHeaderData.add(data);
                    }

                    String select_item = "select TableID,Name,Qty from InvTranSaleTemp";
                    Statement st_item = con.createStatement();
                    ResultSet rs_item = st_item.executeQuery(select_item);

                    while (rs_item.next()) {
                        OpenOrderKitchenData data=new OpenOrderKitchenData();
                        data.setTableid(rs_item.getInt(1));
                        data.setItemName(rs_item.getString(2));
                        data.setStringQty(rs_item.getString(3));
                        lstItemData.add(data);
                    }
                }
            }
            catch(Exception e){
                msg=e.getMessage();
            }
            return msg;
        }
        @Override
        protected void onPreExecute(){
            progressDialog.show();
            progressDialog.setMessage("Loading....");
        }
        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
            setAdapter();
        }
    }

    private void setAdapter(){
        openOrderKitchenListAdapter=new OpenOrderKitchenListAdapter(this, lstHeaderData,lstItemData);
        lvMainList.setAdapter(openOrderKitchenListAdapter);
    }
}
