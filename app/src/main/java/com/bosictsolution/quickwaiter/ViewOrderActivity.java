package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adapter.ViewOrderListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.TransactionData;

public class ViewOrderActivity extends AppCompatActivity {

    private ViewOrderListAdapter viewOrderListAdapter;
    ListView lvViewOrder;
//    LinearLayout layoutOrder;
    TextView tvTable,tvTax, tvCharges,tvSubTotal, tvGrandTotal,tvLabelTax,tvLabelCharges,tvLabelSubTotal,tvLabelGrandTotal,tvHeaderItem,tvHeaderQuantity,tvHeaderAmount,
            tvHeaderSalePrice,tvLabelDiscount,tvDiscount;
    TableLayout layoutAmount;
    private DBHelper db;
    ServerConnection serverconnection;
    private ProgressDialog progressDialog;
    final Context context = this;
    int warning_message=1,error_message=2,success_message=3,info_message=4, tableid, taxPercent, chargesPercent,waiterid,allowBillPrint;
    String tableName,waiterName;
    double discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);

        setLayoutResource();

        serverconnection=new ServerConnection();
        db=new DBHelper(this);
        setTitle("Order Items");

        Intent intent=getIntent();
        tableid =intent.getIntExtra("tableid", 0);
        tableName=intent.getStringExtra("tablename");
        waiterid=intent.getIntExtra("waiterid", 0);
        waiterName=intent.getStringExtra("waitername");
        tvTable.setText("TABLE - "+tableName+" Current Order List");
        ViewOrder viewOrder=new ViewOrder();
        viewOrder.execute("");
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
    public void setTitle(CharSequence title){
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi=inflater.inflate(R.layout.action_bar_view_order, null);
        TextView tvTitle=(TextView)vi.findViewById(R.id.tvTitle);
        ImageButton btnGetBill=(ImageButton)vi.findViewById(R.id.btnGetBill);

        tvTitle.setText(title);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT);
        getSupportActionBar().setCustomView(vi, params);

        btnGetBill.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Cursor cur=db.getPrintBillSetting();
                if(cur.moveToFirst())allowBillPrint=cur.getInt(0);
                if(allowBillPrint==0){
                    RequestBill requestBill = new RequestBill();
                    requestBill.execute("");
                }else{
                    Intent i=new Intent(ViewOrderActivity.this,BillActivity.class);
                    i.putExtra("tableid",tableid);
                    i.putExtra("tablename",tableName);
                    i.putExtra("waiterid", waiterid);
                    i.putExtra("waitername",waiterName);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    public class RequestBill extends AsyncTask<String,String,String>{
        String msg;
        int msg_type;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                }else{
                    String sql_query="INSERT INTO AutoGirl(tableid,TableName,WaiterID,WaiterName) VALUES";
                    String sql_data=tableid+",'"+tableName+"',"+waiterid+",'"+waiterName+"'";
                    String insert_bill=sql_query+"("+sql_data+")";
                    Statement st=con.createStatement();
                    st.execute(insert_bill);
                    msg = tableName + " Bill is Ready!";
                    msg_type=success_message;
                }
            }catch(SQLException e){
                showMessage(error_message,e.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
            showMessage(msg_type,r);
        }
    }

    public class ViewOrder extends AsyncTask<String,String,String> {
        String msg="",stringQty;
        int integerQty,msg_type;
        float floatQty;
        boolean isSuccess;
        double curDiscount;
        List<TransactionData> lstViewOrder=new ArrayList<>();
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    //String get_order="select distinct(Name),sum(Qty),sum(CAST(ROUND(Amount, 2) AS MONEY)),ItemDis,SalePrice,[SID],isnull(Tastes,'') AS Tastes from InvTranSaleTemp where ItemDeleted=0 AND TableID="+ tableid +" group by Name,ItemDis,SalePrice,[SID],Tastes order by [SID]";
                    String get_order="select distinct(Name),sum(Qty),sum(CAST(ROUND(Amount, 2) AS MONEY)),ItemDis,SalePrice,isnull(Tastes,'') AS Tastes,isnull(UnitName,'') AS NormalTastesWithoutParcel from InvTranSaleTemp where ItemDeleted=0 AND TableID="+ tableid +" group by Name,ItemDis,SalePrice,Tastes,UnitName";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(get_order);
                    if(rs.next()){
                        do{
                            TransactionData data=new TransactionData();
                            data.setItemName(rs.getString(1));
                            data.setStringQty(rs.getString(2));
                            data.setSalePrice(rs.getDouble(5));

                            stringQty=rs.getString(2);
                            floatQty = Float.parseFloat(stringQty);
                            if(floatQty==Math.round(floatQty)){
                                integerQty=Math.round(floatQty);
                                data.setIntegerQty(integerQty);
                                curDiscount=(rs.getDouble(5)*rs.getInt(4))/100;
                                discount+=curDiscount*integerQty;
                                data.setAmount(rs.getDouble(3)-(curDiscount*integerQty));
                            }else{
                                data.setFloatQty(floatQty);
                                curDiscount=(rs.getDouble(5)*rs.getInt(4))/100;
                                discount+=curDiscount*floatQty;
                                data.setAmount(rs.getDouble(3)-(curDiscount*floatQty));
                            }
                            data.setAllTaste(rs.getString(6)+rs.getString(7));
                            lstViewOrder.add(data);
                        }while(rs.next());
                        isSuccess=true;
                    }
                }
            }catch(SQLException e){
                msg=e.getMessage();
                msg_type=error_message;
                isSuccess=false;
            }
            return msg;
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
            if(isSuccess) {
                showOrder(lstViewOrder);
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    private void showOrder(List<TransactionData> lstViewOrder) {
        DecimalFormat df2 = new DecimalFormat("#");
        double subTotal = 0, grandTotal, taxAmount = 0, chargesAmount;

        viewOrderListAdapter = new ViewOrderListAdapter(this, lstViewOrder);
        lvViewOrder.setAdapter(viewOrderListAdapter);

        getServiceTax();

        for (int i = 0; i < lstViewOrder.size(); i++) {
            subTotal += lstViewOrder.get(i).getAmount(); //get subtotal
        }
        chargesAmount = (chargesPercent * subTotal) / 100; //get charges amount

        Cursor cur=db.getHideCommercialTaxSetting();
        if(cur.moveToFirst()){
            if (cur.getInt(0) == 1) { // hide commercial tax
                tvLabelTax.setVisibility(View.GONE);
                tvTax.setVisibility(View.GONE);
            }else{
                cur = db.getAdvancedTaxSetting();
                if (cur.moveToFirst()) {
                    if (cur.getInt(0) == 0) { // normal tax
                        taxAmount = (taxPercent * subTotal) / 100;
                    } else { // advanced tax
                        taxAmount = (taxPercent * (subTotal + chargesAmount)) / 100;
                    }
                }
            }
        }

        subTotal = Math.round(subTotal);
        taxAmount = Math.round(taxAmount);
        chargesAmount = Math.round(chargesAmount);

        grandTotal = subTotal + taxAmount + chargesAmount;

        tvSubTotal.setText(String.valueOf(df2.format(subTotal)));
        tvTax.setText(String.valueOf(df2.format(taxAmount)));
        tvCharges.setText(String.valueOf(df2.format(chargesAmount)));
        tvDiscount.setText(String.valueOf(df2.format(discount)));
        tvGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
    }

    private void getServiceTax(){
        Cursor cur= db.getServiceTax();
        if(cur.getCount()==1){
            if(cur.moveToFirst()){
                taxPercent =cur.getInt(0);
                chargesPercent =cur.getInt(1);
            }
        }
    }

    private void setLayoutResource(){
        lvViewOrder =(ListView)findViewById(R.id.lvViewOrder);
        tvTable=(TextView)findViewById(R.id.tvTable);
        tvTax=(TextView)findViewById(R.id.tvTax);
        tvCharges =(TextView)findViewById(R.id.tvCharges);
        tvSubTotal=(TextView)findViewById(R.id.tvSubTotal);
        tvGrandTotal =(TextView)findViewById(R.id.tvGrandTotal);
        tvLabelTax=(TextView)findViewById(R.id.tvLabelTax);
        tvLabelCharges =(TextView)findViewById(R.id.tvLabelCharges);
        tvLabelSubTotal=(TextView)findViewById(R.id.tvLabelSubTotal);
        tvLabelGrandTotal =(TextView)findViewById(R.id.tvLabelGrandTotal);
        tvHeaderAmount =(TextView)findViewById(R.id.tvHeaderAmount);
        tvHeaderItem=(TextView)findViewById(R.id.tvHeaderItem);
        tvHeaderQuantity =(TextView)findViewById(R.id.tvHeaderQuantity);
        tvHeaderSalePrice =(TextView)findViewById(R.id.tvHeaderSalePrice);
        layoutAmount =(TableLayout)findViewById(R.id.layoutAmount);
        tvLabelDiscount=(TextView)findViewById(R.id.tvLabelDiscount);
        tvDiscount=(TextView)findViewById(R.id.tvDiscount);

        tvTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void showMessage(int msg_type,String msg_text){
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.message, (ViewGroup) findViewById(R.id.gpMessage));
        TextView tvMessage;
        if(msg_type==warning_message) {
            layout = inflater.inflate(R.layout.message_warning, (ViewGroup) findViewById(R.id.gpMessage));
        }
        else if(msg_type==error_message) {
            layout = inflater.inflate(R.layout.message_error, (ViewGroup) findViewById(R.id.gpMessage));
        }
        else if(msg_type==success_message) {
            layout = inflater.inflate(R.layout.message_success, (ViewGroup) findViewById(R.id.gpMessage));
        }
        else if(msg_type==info_message) {
            layout = inflater.inflate(R.layout.message_info, (ViewGroup) findViewById(R.id.gpMessage));
        }
        tvMessage=(TextView)layout.findViewById(R.id.tvMessage);
        tvMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        tvMessage.setText(msg_text);
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
