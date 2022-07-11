package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.BillListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.TransactionData;
import listener.BillListButtonClickListener;

public class BillActivity extends AppCompatActivity implements BillListButtonClickListener {

    TextView tvBillTitle,tvHeaderItemName,tvHeaderQuantity,tvHeaderAmount, tvLabelDiscountPercent, tvLabelDiscountAmount,
            tvLabelSubTotal,tvAmountSubTotal,tvLabelCharges,tvAmountCharges,tvLabelTax,tvAmountTax,tvLabelDiscount,tvAmountDiscount,
            tvLabelGrandTotal,tvAmountGrandTotal,tvHeaderPrice, tvLabelPaid, tvLabelChange, tvAmountChange;
    ListView lvBillItem;
    EditText etDiscountPercent,etDiscountAmount,etPaidAmount;
    Button btnDiscountCalculate,btnPay,btnPaidCalculate;

    private DBHelper db;
    BillListAdapter billListAdapter;

    final Context context = this;
    int warning_message=1,error_message=2,success_message=3,info_message=4,taxPercent,chargesPercent,billTableID,billWaiterID;
    String billTableName,confirmMessage,billWaiterName;
    public static List<TransactionData> lstViewOrder=new ArrayList<>();

    private Calendar calendar;
    String date,time,printerIP,currentTime;
    private Calendar cCalendar;
    ServerConnection serverconnection;
    private ProgressDialog progressDialog;
    double taxAmount;
    int deleteTranID,deleteSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();
        calendar=Calendar.getInstance();

        setLayoutResource();
        Intent intent=getIntent();
        billTableID=intent.getIntExtra("tableid", 0);
        billTableName=intent.getStringExtra("tablename");
        billWaiterID=intent.getIntExtra("waiterid",0);
        billWaiterName=intent.getStringExtra("waitername");
        String tablename="TABLE - "+billTableName+" Order #";
        tvBillTitle.setText(tablename);
        GetOrder getOrder=new GetOrder();
        getOrder.execute("");

        btnDiscountCalculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                calculateDiscount();
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                confirmMessage="Are you sure you want to pay bill for table "+billTableName+"?";
                showConfirmDialog();
            }
        });

        btnPaidCalculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Double netAmount=Double.parseDouble(tvAmountGrandTotal.getText().toString());
                if(etPaidAmount.getText().toString().length()==0)return;
                Double paidAmount=Double.parseDouble(etPaidAmount.getText().toString());
                Double changeAmount=paidAmount-netAmount;
                tvAmountChange.setText(changeAmount.toString());
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
    public void onItemDeletedClickListener(int position) {
        if(lstViewOrder.size()!=0) {
            deleteTranID = lstViewOrder.get(position).getTranid();
            deleteSID = lstViewOrder.get(position).getSid();
            DeleteItem deleteItem = new DeleteItem();
            deleteItem.execute("");
        }
    }

    @Override
    public void onQtyRemovedClickListener(int position) {
        if(lstViewOrder.size()!=0) {
            deleteTranID = lstViewOrder.get(position).getTranid();
            deleteSID = lstViewOrder.get(position).getSid();
            RemoveQty deleteItem = new RemoveQty();
            deleteItem.execute("");
        }
    }

    private void getCurrentTime(){
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(LoginActivity.TIME_FORMAT);
        currentTime=timeFormat.format(cCalendar.getTime());
    }

    public class PayBill extends AsyncTask<String,String,String> {
        String msg="",stringQty;
        int msg_type,integerQty;
        boolean isSuccess;
        float floatQty;
        double curDiscount;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    lstViewOrder=new ArrayList<>();
                    String last_order="select distinct(Name),sum(Qty),sum(CAST(ROUND(Amount, 2) AS MONEY)),SalePrice,ItemDis,isnull(Tastes,'') AS Tastes from InvTranSaleTemp where ItemDeleted=0 AND TableID="+billTableID+" group by Name,SalePrice,ItemDis,Tastes";
                    Statement st_last=con.createStatement();
                    ResultSet rs_last= st_last.executeQuery(last_order);
                    if(rs_last.next()) {
                        do {
                            TransactionData data = new TransactionData();
                            if(rs_last.getString(6).length()!=0)
                                data.setItemName(rs_last.getString(1) + "(" + rs_last.getString(6) + ")");
                            else data.setItemName(rs_last.getString(1));
                            data.setStringQty(rs_last.getString(2));

                            stringQty = rs_last.getString(2);
                            floatQty = Float.parseFloat(stringQty);
                            if (floatQty == Math.round(floatQty)) {
                                integerQty = Math.round(floatQty);
                                data.setIntegerQty(integerQty);
                                curDiscount=(rs_last.getDouble(4)*rs_last.getInt(5))/100;
                                data.setAmount(rs_last.getDouble(3)-(curDiscount*integerQty));
                            } else {
                                data.setFloatQty(floatQty);
                                curDiscount=(rs_last.getDouble(4)*rs_last.getInt(5))/100;
                                data.setAmount(rs_last.getDouble(3)-(curDiscount*floatQty));
                            }
                            data.setSalePrice(rs_last.getDouble(4));
                            lstViewOrder.add(data);
                        } while (rs_last.next());
                    }

                    String select_tranid="SELECT Tranid FROM InvMasterSaleTemp WHERE TableNameID="+billTableID;
                    Statement st_tranid=con.createStatement();
                    ResultSet rs=st_tranid.executeQuery(select_tranid);

                    if(rs.next()){
                        int tranid=rs.getInt(1);

                        String exec_proc="exec EntrySaleSaveTouchNormal @TRANID="+tranid;
                        Statement st_execproc=con.createStatement();
                        st_execproc.execute(exec_proc);

                        String exec_proc2="exec UpdateTaxAmountByTranID @TranID="+tranid+",@Tax="+taxAmount+",@WaiterName='"+billWaiterName+"'";
                        Statement st_execproc2=con.createStatement();
                        st_execproc2.execute(exec_proc2);
                    }
                    isSuccess=true;
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
                Intent i=new Intent(getApplicationContext(),PrintBillActivity.class);
                i.putExtra("tableid",billTableID);
                i.putExtra("table",billTableName);
                i.putExtra("waiter",billWaiterName);
                String subtotal=new DecimalFormat("#").format(Double.parseDouble(tvAmountSubTotal.getText().toString()));
                i.putExtra("subtotal",subtotal);
                String tax=new DecimalFormat("#").format(Double.parseDouble(tvAmountTax.getText().toString()));
                i.putExtra("tax",tax);
                String charges=new DecimalFormat("#").format(Double.parseDouble(tvAmountCharges.getText().toString()));
                i.putExtra("charges",charges);
                String grandtotal=new DecimalFormat("#").format(Double.parseDouble(tvAmountGrandTotal.getText().toString()));
                i.putExtra("grandtotal",grandtotal);
                String discount=new DecimalFormat("#").format(Double.parseDouble(tvAmountDiscount.getText().toString()));
                i.putExtra("discount",discount);
                if(etPaidAmount.getText().toString().length()!=0) {
                    String paid = new DecimalFormat("#").format(Double.parseDouble(etPaidAmount.getText().toString()));
                    i.putExtra("paid", paid);
                }
                else i.putExtra("paid","0");

                Double netAmount=Double.parseDouble(tvAmountGrandTotal.getText().toString());
                Double paidAmount,changeAmount=0.0;
                if(etPaidAmount.getText().toString().length()!=0){
                    paidAmount=Double.parseDouble(etPaidAmount.getText().toString());
                    changeAmount=paidAmount-netAmount;
                }
                String change=new DecimalFormat("#").format(changeAmount);
                i.putExtra("change",change);
                startActivity(i);
                finish();
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    public class GetOrder extends AsyncTask<String,String,String> {
        String msg="",stringQty;
        int integerQty,msg_type;
        float floatQty;
        boolean isSuccess;
        double curDiscount;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    lstViewOrder=new ArrayList<>();
                    //String get_order="select distinct(ItemName),sum(Qty),sum(CAST(ROUND(Amount, 2) AS MONEY)),SalePrice from InvTranSaleTemp where ItemDeleted=0 AND TableID="+billTableID+" group by ItemName,SalePrice";
                    String get_order="select Name,Qty,CAST(ROUND(Amount, 2) AS MONEY),SalePrice,Tranid,SID,ItemDis,isnull(Tastes,'') from InvTranSaleTemp where ItemDeleted=0 AND TableID="+billTableID+" ORDER BY SID";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(get_order);
                    if(rs.next()){
                        do{
                            TransactionData data=new TransactionData();
                            if(rs.getString(8).length()!=0)
                                data.setItemName(rs.getString(1) + "(" + rs.getString(8) + ")");
                            else data.setItemName(rs.getString(1));
                            data.setStringQty(rs.getString(2));

                            stringQty=rs.getString(2);
                            floatQty = Float.parseFloat(stringQty);
                            if(floatQty==Math.round(floatQty)){
                                integerQty=Math.round(floatQty);
                                data.setIntegerQty(integerQty);
                                curDiscount=(rs.getDouble(4)*rs.getInt(7))/100;
                                data.setAmount(rs.getDouble(3)-(curDiscount*integerQty));
                            }else{
                                data.setFloatQty(floatQty);
                                curDiscount=(rs.getDouble(4)*rs.getInt(7))/100;
                                data.setAmount(rs.getDouble(3)-(curDiscount*floatQty));
                            }

                            data.setSalePrice(rs.getDouble(4));
                            data.setTranid(rs.getInt(5));
                            data.setSid(rs.getInt(6));
                            lstViewOrder.add(data);
                        }while(rs.next());
                    }
                    isSuccess=true;
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
                showOrder();
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    public class DeleteItem extends AsyncTask<String,String,String> {
        String msg="";
        int msg_type;
        boolean isSuccess;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    String delete_item="delete from InvTranSaleTemp where Tranid="+deleteTranID+" and SID="+deleteSID;
                    Statement st=con.createStatement();
                    st.execute(delete_item);
                    isSuccess=true;
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
                GetOrder getOrder=new GetOrder();
                getOrder.execute("");
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    public class RemoveQty extends AsyncTask<String,String,String> {
        String msg="";
        int msg_type;
        boolean isSuccess;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    String select_qty="select Qty,SalePrice from InvTranSaleTemp where Tranid="+deleteTranID+" and SID="+deleteSID;
                    Statement st=con.createStatement();
                    ResultSet rs=st.executeQuery(select_qty);
                    if(rs.next()){
                        int qty=rs.getInt(1);
                        double salePrice=rs.getDouble(2);
                        qty-=1;
                        double amount=qty*salePrice;
                        String remove_qty="update InvTranSaleTemp set Qty="+ qty +",Amount="+ amount +" where Tranid="+deleteTranID+" and SID="+deleteSID;
                        Statement st_remove=con.createStatement();
                        st_remove.execute(remove_qty);
                    }
                    isSuccess=true;
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
                GetOrder getOrder=new GetOrder();
                getOrder.execute("");
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    private void showOrder(){
        DecimalFormat df2 = new DecimalFormat("#");
        double subTotal=0,grandTotal,chargesAmount;
        billListAdapter=new BillListAdapter(this,lstViewOrder);
        lvBillItem.setAdapter(billListAdapter);
        billListAdapter.setOnItemDeletedClickListener(this);
        getServiceTax();

        for(int i=0;i<lstViewOrder.size();i++){
            subTotal+=lstViewOrder.get(i).getAmount();
        }
        chargesAmount =(chargesPercent*subTotal)/100;

        Cursor cur=db.getAdvancedTaxSetting();
        if(cur.moveToFirst()){
            if(cur.getInt(0)==0){ // normal tax
                taxAmount =(taxPercent*subTotal)/100;
            }else{ // advanced tax
                //double amt=(subTotal+chargesAmount)/100;

                //String sAmt=String.valueOf(amt);
                //String[] arr=sAmt.split("\\.");
                //amt=Double.parseDouble(arr[0]);
                //amt=Double.parseDouble(df2.format(amt));
                taxAmount =(taxPercent*(subTotal+chargesAmount))/100;
                //taxAmount =taxPercent*amt; //get advanced tax amount
            }
        }

        subTotal = Math.round(subTotal);
        taxAmount = Math.round(taxAmount);
        chargesAmount = Math.round(chargesAmount);

        grandTotal=subTotal+ taxAmount + chargesAmount;
        //taxAmount =(taxPercent*subTotal)/100;

        //grandTotal=subTotal+ taxAmount + chargesAmount;

        tvAmountSubTotal.setText(String.valueOf(df2.format(subTotal)));
        tvAmountTax.setText(String.valueOf(df2.format(taxAmount)));
        tvAmountCharges.setText(String.valueOf(df2.format(chargesAmount)));
        tvAmountGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
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

    private void calculateDiscount(){
        double discountAmount=0,discount;
        int discountPercent=0;
        if(etDiscountAmount.getText().toString().length()!=0) discountAmount=Double.valueOf(etDiscountAmount.getText().toString());
        if(etDiscountPercent.getText().toString().length()!=0) discountPercent=Integer.parseInt(etDiscountPercent.getText().toString());
        if(discountPercent<0){
            showMessage(warning_message,"Percent should be greater than zero!");
            etDiscountPercent.requestFocus();
            return;
        }
        if(discountPercent>100){
            showMessage(warning_message,"Percent should be less than one hundred!");
            etDiscountPercent.requestFocus();
            return;
        }
        double subTotal=Double.valueOf(tvAmountSubTotal.getText().toString());
        discount =(discountPercent*subTotal)/100;
        discount=discount+discountAmount;

        DecimalFormat df2 = new DecimalFormat("0.##");
        tvAmountDiscount.setText(String.valueOf(df2.format(discount)));
        double tax=Double.valueOf(tvAmountTax.getText().toString());
        double charges=Double.valueOf(tvAmountCharges.getText().toString());
        double grandTotal=subTotal+tax+charges;
        grandTotal=grandTotal-discount;
        tvAmountGrandTotal.setText(String.valueOf(df2.format(grandTotal)));
    }

    private void showConfirmDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dialog_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        TextView tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
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
                PayBill payBill=new PayBill();
                payBill.execute("");
                passwordRequireDialog.dismiss();
            }
        });
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

    private void setLayoutResource(){
        tvBillTitle=(TextView)findViewById(R.id.tvBillTitle);
        tvHeaderItemName=(TextView)findViewById(R.id.tvHeaderItemName);
        tvHeaderQuantity=(TextView)findViewById(R.id.tvHeaderQuantity);
        tvHeaderAmount=(TextView)findViewById(R.id.tvHeaderAmount);
        tvHeaderPrice=(TextView)findViewById(R.id.tvHeaderPrice);
        tvLabelDiscountPercent =(TextView)findViewById(R.id.tvDiscountPercent);
        tvLabelDiscountAmount =(TextView)findViewById(R.id.tvDiscountAmount);
        tvLabelSubTotal=(TextView)findViewById(R.id.tvLabelSubTotal);
        tvAmountSubTotal=(TextView)findViewById(R.id.tvAmountSubTotal);
        tvLabelCharges=(TextView)findViewById(R.id.tvLabelCharges);
        tvAmountCharges=(TextView)findViewById(R.id.tvAmountCharges);
        tvLabelTax=(TextView)findViewById(R.id.tvLabelTax);
        tvAmountTax=(TextView)findViewById(R.id.tvAmountTax);
        tvLabelDiscount=(TextView)findViewById(R.id.tvLabelDiscount);
        tvAmountDiscount=(TextView)findViewById(R.id.tvAmountDiscount);
        tvLabelGrandTotal=(TextView)findViewById(R.id.tvLabelGrandTotal);
        tvAmountGrandTotal=(TextView)findViewById(R.id.tvAmountGrandTotal);
        lvBillItem=(ListView)findViewById(R.id.lvBillItem);
        etDiscountPercent=(EditText) findViewById(R.id.etDiscountPercent);
        etDiscountAmount=(EditText)findViewById(R.id.etDiscountAmount);
        etPaidAmount=(EditText)findViewById(R.id.etAmountPaidTotal);
        btnDiscountCalculate=(Button)findViewById(R.id.btnDiscountCalculate);
        btnPaidCalculate=(Button)findViewById(R.id.btnPaidCalculate);
        btnPay=(Button)findViewById(R.id.btnPay);
        tvLabelPaid =(TextView)findViewById(R.id.tvLabelPaidTotal);
        tvLabelChange =(TextView)findViewById(R.id.tvLabelChangeTotal);
        tvAmountChange =(TextView)findViewById(R.id.tvAmountChangeTotal);

        tvBillTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }
}
