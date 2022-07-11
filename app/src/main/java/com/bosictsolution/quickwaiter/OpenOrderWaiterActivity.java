package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import adapter.OpenOrderWaiterListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.OpenOrderWaiterData;
import data.WaiterData;

public class OpenOrderWaiterActivity extends AppCompatActivity {

    ListView lvMainList;
    TextView tvWaiter;
    OpenOrderWaiterListAdapter openOrderListAdapter;
    DBHelper db;
    List<WaiterData> lstWaiterData;
    List<OpenOrderWaiterData> lstOpenOrderData;
    private Context context=this;
    ServerConnection serverconnection;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_order_waiter);

        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();
        lvMainList=(ListView) findViewById(R.id.lvMainList);
        tvWaiter=(TextView)findViewById(R.id.tvWaiter);
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
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                } else {

                    lstWaiterData=new ArrayList<>();
                    lstOpenOrderData=new ArrayList<>();

                    String select_waiter = "SELECT Distinct(ms.Userid),waiter.Description FROM InvMasterSaleTemp ms INNER JOIN Waiter waiter ON ms.Userid=waiter.ID ORDER BY ms.Userid";
                    Statement st_waiter = con.createStatement();
                    ResultSet rs_waiter = st_waiter.executeQuery(select_waiter);

                    while (rs_waiter.next()) {
                        WaiterData data=new WaiterData();
                        data.setWaiterid(rs_waiter.getInt(1));
                        data.setWaiterName(rs_waiter.getString(2));
                        lstWaiterData.add(data);
                    }

                    String select_open_order = "SELECT Distinct(ms.Tranid),ms.Userid,convert(varchar(10),ms.Date, 101) + right(convert(varchar(32),ms.Date,100),8),tab.Table_Name,isNull(cus.PAH,0) FROM InvMasterSaleTemp ms INNER JOIN InvTranSaleTemp ts ON ms.Tranid=ts.TranID INNER JOIN table_name tab ON ms.TableNameID=tab.Table_Name_ID LEFT JOIN CustomerInfo cus ON ms.Tranid=cus.Tranid ORDER BY ms.Tranid";
                    Statement st_open_order = con.createStatement();
                    ResultSet rs_open_order = st_open_order.executeQuery(select_open_order);

                    while (rs_open_order.next()) {
                        OpenOrderWaiterData data=new OpenOrderWaiterData();
                        data.setTranid(rs_open_order.getInt(1));
                        data.setWaiterid(rs_open_order.getInt(2));
                        data.setDate(rs_open_order.getString(3));
                        data.setTable(rs_open_order.getString(4));
                        data.setGuest(rs_open_order.getInt(5));
                        lstOpenOrderData.add(data);
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
        openOrderListAdapter=new OpenOrderWaiterListAdapter(this,lstWaiterData,lstOpenOrderData);
        lvMainList.setAdapter(openOrderListAdapter);
    }
}
