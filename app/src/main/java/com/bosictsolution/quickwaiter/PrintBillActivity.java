package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import adapter.PrintListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.TransactionData;

public class PrintBillActivity extends AppCompatActivity {

    ImageView btPrintLogo;
    TextView btPrintShopName,btPrintShopDesc,btPrintAddress,btPrintPhone,btPrintSlipID,btPrintDateTime,
            btPrintTable,btPrintWaiter,btPrintHeaderItem,btPrintHeaderQty,btPrintHeaderPrice,btPrintHeaderAmount,btPrintSubTotal,btPrintSubTotalAmt,btPrintCommercialTax,
            btPrintCommercialTaxAmt,btPrintServiceCharges,btPrintServiceChargesAmt,btPrintDiscount,
            btPrintDiscountAmt,btPrintGrandTotal,btPrintGrandTotalAmt,btPrintPaid,
            btPrintPaidAmt,btPrintChange,btPrintChangeAmt,btPrintMessage,btPrintOtherMessage;
    TextView tvPrinting;
    LinearLayout btPrintLayout,layoutPrintList;
    ImageView btPrintLogo2;
    TextView btPrintShopName2,btPrintShopDesc2,btPrintAddress2,btPrintPhone2,btPrintSlipID2,btPrintDateTime2,
            btPrintTable2,btPrintWaiter2,btPrintHeaderItem2,btPrintHeaderQty2,btPrintHeaderPrice2,btPrintHeaderAmount2,btPrintSubTotal2,btPrintSubTotalAmt2,btPrintCommercialTax2,
            btPrintCommercialTaxAmt2,btPrintServiceCharges2,btPrintServiceChargesAmt2,btPrintDiscount2,
            btPrintDiscountAmt2,btPrintGrandTotal2,btPrintGrandTotalAmt2,btPrintPaid2,
            btPrintPaidAmt2,btPrintChange2,btPrintChangeAmt2,btPrintMessage2,btPrintOtherMessage2;
    LinearLayout btPrintLayout2,layoutPrintList2;
    Button btnPrint;
    PrintListAdapter printListAdapter;
    final Context context = this;
    private DBHelper db;
    private Calendar calendar;
    String date,time,billTableName,billWaiterName;
    int billTableID;
    String subtotal,tax,charges,grandtotal,discount,paid,change;
    private ESCPOSPrinter posPtr;
    float floatQty;
    ServerConnection serverconnection;
    private ProgressDialog progressDialog;
    int warning_message=1,error_message=2,success_message=3,info_message=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bill);
        tvPrinting=(TextView)findViewById(R.id.tvPrinting);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        billTableID=intent.getIntExtra("tableid",0);
        billTableName=intent.getStringExtra("table");
        billWaiterName=intent.getStringExtra("waiter");
        subtotal=intent.getStringExtra("subtotal");
        tax=intent.getStringExtra("tax");
        charges=intent.getStringExtra("charges");
        grandtotal=intent.getStringExtra("grandtotal");
        discount=intent.getStringExtra("discount");
        paid=intent.getStringExtra("paid");
        change=intent.getStringExtra("change");

        db=new DBHelper(this);
        serverconnection=new ServerConnection();
        calendar=Calendar.getInstance();

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        setPreviewLayoutResource();
        setPrintOutLayoutResource();
        startupPrintLayout();
        setupPreviewPrintOrderList(BillActivity.lstViewOrder);
        setupPrintOutOrderList(BillActivity.lstViewOrder);
        printCalculateAmount();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrintLayoutToBitmap();
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnPrint.performClick();
            }
        }, 200);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPreviewLayoutResource(){
        btPrintLogo=(ImageView)findViewById(R.id.btPrintLogo);
        btPrintLayout=(LinearLayout)findViewById(R.id.btPrintLayout);
        layoutPrintList=(LinearLayout)findViewById(R.id.layoutPrintList);
        btPrintShopName=(TextView)findViewById(R.id.btPrintShopName);
        btPrintShopDesc=(TextView)findViewById(R.id.btPrintShopDesc);
        btPrintAddress=(TextView)findViewById(R.id.btPrintAddress);
        btPrintPhone=(TextView)findViewById(R.id.btPrintPhone);
        btPrintSlipID=(TextView)findViewById(R.id.btPrintSlipID);
        btPrintDateTime=(TextView)findViewById(R.id.btPrintDateTime);
        btPrintTable=(TextView)findViewById(R.id.btPrintTable);
        btPrintWaiter=(TextView)findViewById(R.id.btPrintWaiter);
        btPrintHeaderItem=(TextView)findViewById(R.id.btPrintHeaderItem);
        btPrintHeaderQty=(TextView)findViewById(R.id.btPrintHeaderQty);
        btPrintHeaderPrice=(TextView)findViewById(R.id.btPrintHeaderPrice);
        btPrintHeaderAmount=(TextView)findViewById(R.id.btPrintHeaderAmount);
        btPrintSubTotal=(TextView)findViewById(R.id.btPrintSubTotal);
        btPrintSubTotalAmt=(TextView)findViewById(R.id.btPrintSubTotalAmt);
        btPrintCommercialTax=(TextView)findViewById(R.id.btPrintCommercialTax);
        btPrintCommercialTaxAmt=(TextView)findViewById(R.id.btPrintCommercialTaxAmt);
        btPrintServiceCharges=(TextView)findViewById(R.id.btPrintServiceCharges);
        btPrintServiceChargesAmt=(TextView)findViewById(R.id.btPrintServiceChargesAmt);
        btPrintDiscount=(TextView)findViewById(R.id.btPrintDiscount);
        btPrintDiscountAmt=(TextView)findViewById(R.id.btPrintDiscountAmt);
        btPrintGrandTotal=(TextView)findViewById(R.id.btPrintGrandTotal);
        btPrintGrandTotalAmt=(TextView)findViewById(R.id.btPrintGrandTotalAmt);
        btPrintPaid=(TextView)findViewById(R.id.btPrintPaid);
        btPrintPaidAmt=(TextView)findViewById(R.id.btPrintPaidAmt);
        btPrintChange=(TextView)findViewById(R.id.btPrintChange);
        btPrintChangeAmt=(TextView)findViewById(R.id.btPrintChangeAmt);
        btPrintMessage=(TextView)findViewById(R.id.btPrintMessage);
        btPrintOtherMessage=(TextView)findViewById(R.id.btPrintOtherMessage);
        btnPrint=(Button)findViewById(R.id.btnPrint);

        btPrintShopName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintShopDesc.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintAddress.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintPhone.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintSlipID.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintDateTime.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintWaiter.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderQty.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderAmount.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintSubTotal.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintSubTotalAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintCommercialTax.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintCommercialTaxAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintServiceCharges.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintServiceChargesAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintDiscount.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintDiscountAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintGrandTotal.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintGrandTotalAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintPaid.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintPaidAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintChange.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintChangeAmt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintOtherMessage.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
    }

    private void setPrintOutLayoutResource(){
        btPrintLogo2=(ImageView)findViewById(R.id.btPrintLogo2);
        btPrintLayout2=(LinearLayout)findViewById(R.id.btPrintLayout2);
        layoutPrintList2=(LinearLayout)findViewById(R.id.layoutPrintList2);
        btPrintShopName2=(TextView)findViewById(R.id.btPrintShopName2);
        btPrintShopDesc2=(TextView)findViewById(R.id.btPrintShopDesc2);
        btPrintAddress2=(TextView)findViewById(R.id.btPrintAddress2);
        btPrintPhone2=(TextView)findViewById(R.id.btPrintPhone2);
        btPrintSlipID2=(TextView)findViewById(R.id.btPrintSlipID2);
        btPrintDateTime2=(TextView)findViewById(R.id.btPrintDateTime2);
        btPrintTable2=(TextView)findViewById(R.id.btPrintTable2);
        btPrintWaiter2=(TextView)findViewById(R.id.btPrintWaiter2);
        btPrintHeaderItem2=(TextView)findViewById(R.id.btPrintHeaderItem2);
        btPrintHeaderQty2=(TextView)findViewById(R.id.btPrintHeaderQty2);
        btPrintHeaderPrice2=(TextView)findViewById(R.id.btPrintHeaderPrice2);
        btPrintHeaderAmount2=(TextView)findViewById(R.id.btPrintHeaderAmount2);
        btPrintSubTotal2=(TextView)findViewById(R.id.btPrintSubTotal2);
        btPrintSubTotalAmt2=(TextView)findViewById(R.id.btPrintSubTotalAmt2);
        btPrintCommercialTax2=(TextView)findViewById(R.id.btPrintCommercialTax2);
        btPrintCommercialTaxAmt2=(TextView)findViewById(R.id.btPrintCommercialTaxAmt2);
        btPrintServiceCharges2=(TextView)findViewById(R.id.btPrintServiceCharges2);
        btPrintServiceChargesAmt2=(TextView)findViewById(R.id.btPrintServiceChargesAmt2);
        btPrintDiscount2=(TextView)findViewById(R.id.btPrintDiscount2);
        btPrintDiscountAmt2=(TextView)findViewById(R.id.btPrintDiscountAmt2);
        btPrintGrandTotal2=(TextView)findViewById(R.id.btPrintGrandTotal2);
        btPrintGrandTotalAmt2=(TextView)findViewById(R.id.btPrintGrandTotalAmt2);
        btPrintPaid2=(TextView)findViewById(R.id.btPrintPaid2);
        btPrintPaidAmt2=(TextView)findViewById(R.id.btPrintPaidAmt2);
        btPrintChange2=(TextView)findViewById(R.id.btPrintChange2);
        btPrintChangeAmt2=(TextView)findViewById(R.id.btPrintChangeAmt2);
        btPrintMessage2=(TextView)findViewById(R.id.btPrintMessage2);
        btPrintOtherMessage2=(TextView)findViewById(R.id.btPrintOtherMessage2);

        btPrintShopName2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintShopDesc2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintAddress2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintPhone2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintSlipID2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintDateTime2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintTable2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintWaiter2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderItem2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderQty2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderPrice2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintHeaderAmount2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintSubTotal2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintSubTotalAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintCommercialTax2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintCommercialTaxAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintServiceCharges2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintServiceChargesAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintDiscount2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintDiscountAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintGrandTotal2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintGrandTotalAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintPaid2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintPaidAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintChange2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintChangeAmt2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintMessage2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btPrintOtherMessage2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
    }

    private void changePrintLayoutToBitmap(){
        Bitmap bitmap=Bitmap.createBitmap(btPrintLayout2.getWidth(), btPrintLayout2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        btPrintLayout2.draw(canvas);
        savePrintLayoutToQuickWaiterDB(bitmap);
        try{
            print(context);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String savePrintLayoutToQuickWaiterDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/QuickWaiterDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"print.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logoPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG,100,fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void startupPrintLayout(){
        try {
            File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/QuickWaiterDB");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File f=new File(directory, "shoplogo.png");
            if(f.exists()) {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                btPrintLogo.setImageBitmap(b);
                btPrintLogo2.setImageBitmap(b);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Cursor cur=db.getSlipFormat();
        if(cur.moveToFirst()){
            if(cur.getString(0).length()==0){
                btPrintShopName.setVisibility(View.GONE);
                btPrintShopName2.setVisibility(View.GONE);
            }
            else {
                btPrintShopName.setText(cur.getString(0));
                btPrintShopName2.setText(cur.getString(0));
            }
            if(cur.getString(1).length()==0){
                btPrintShopDesc.setVisibility(View.GONE);
                btPrintShopDesc2.setVisibility(View.GONE);
            }
            else {
                btPrintShopDesc.setText(cur.getString(1));
                btPrintShopDesc2.setText(cur.getString(1));
            }
            if(cur.getString(2).length()==0){
                btPrintPhone.setVisibility(View.GONE);
                btPrintPhone2.setVisibility(View.GONE);
            }
            else {
                btPrintPhone.setText("PH:"+cur.getString(2));
                btPrintPhone2.setText("PH:"+cur.getString(2));
            }
            if(cur.getString(4).length()==0){
                btPrintMessage.setVisibility(View.GONE);
                btPrintMessage2.setVisibility(View.GONE);
            }
            else {
                btPrintMessage.setText(cur.getString(4));
                btPrintMessage2.setText(cur.getString(4));
            }
            if(cur.getString(3).length()==0){
                btPrintAddress.setVisibility(View.GONE);
                btPrintAddress2.setVisibility(View.GONE);
            }
            else {
                btPrintAddress.setText(cur.getString(3));
                btPrintAddress2.setText(cur.getString(3));
            }
            if(cur.getString(5).length()==0){
                btPrintOtherMessage.setVisibility(View.GONE);
                btPrintOtherMessage2.setVisibility(View.GONE);
            }
            else {
                btPrintOtherMessage.setText(cur.getString(5));
                btPrintOtherMessage2.setText(cur.getString(5));
            }
        }

        calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(LoginActivity.DATE_FORMAT);
        date=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat(LoginActivity.TIME_FORMAT);
        time=timeFormat.format(calendar.getTime());
        btPrintDateTime.setText(date+"  "+time);
        btPrintDateTime2.setText(date+"  "+time);
        btPrintTable.setText("Table:"+billTableName);
        btPrintTable2.setText("Table:"+billTableName);
        btPrintWaiter.setText("Waiter:"+billWaiterName);
        btPrintWaiter2.setText("Waiter:"+billWaiterName);

        GetSlipID getSlipID=new GetSlipID();
        getSlipID.execute("");
    }

    public class GetSlipID extends AsyncTask<String,String,String> {
        String msg="";
        int slipid,msg_type;
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
                    String get_slipid="select SlipID from Systemsetting";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(get_slipid);
                    if(rs.next()){
                        slipid=rs.getInt(1);
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
                btPrintSlipID.setText("Slip No:"+slipid);
                btPrintSlipID2.setText("Slip No:"+slipid);
            }else{
                showMessage(msg_type,r);
            }
        }
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

    private void setupPreviewPrintOrderList(List<TransactionData> lstViewOrder){
        for (int i=0; i<lstViewOrder.size(); i++) {
            TransactionData data = lstViewOrder.get(i);
            LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_print_bill, null);
            TextView tvItemName =(TextView) row.findViewById(R.id.btPrintListItem);
            TextView tvQuantity =(TextView) row.findViewById(R.id.btPrintListQty);
            TextView tvPrice =(TextView) row.findViewById(R.id.btPrintListPrice);
            TextView tvAmount =(TextView) row.findViewById(R.id.btPrintListAmount);

            tvItemName.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvQuantity.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvPrice.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvAmount.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));

            tvItemName.setText(data.getItemName());
            floatQty = Float.parseFloat(data.getStringQty());
            if(floatQty==Math.round(floatQty)){
                tvQuantity.setText(String.valueOf(data.getIntegerQty()));
            }else{
                tvQuantity.setText(String.valueOf(data.getFloatQty()));
            }
            String price=new DecimalFormat("#").format(data.getSalePrice());
            tvPrice.setText(price);
            String amount=new DecimalFormat("#").format(data.getAmount());
            tvAmount.setText(amount);
            layoutPrintList.addView(row);
        }
    }

    private void setupPrintOutOrderList(List<TransactionData> lstViewOrder) {
        for (int i = 0; i < lstViewOrder.size(); i++) {
            TransactionData data = lstViewOrder.get(i);
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_print_bill, null);
            TextView tvItemName = (TextView) row.findViewById(R.id.btPrintListItem);
            TextView tvQuantity = (TextView) row.findViewById(R.id.btPrintListQty);
            TextView tvPrice =(TextView) row.findViewById(R.id.btPrintListPrice);
            TextView tvAmount = (TextView) row.findViewById(R.id.btPrintListAmount);

            tvItemName.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvQuantity.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvPrice.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvAmount.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));

            tvItemName.setText(data.getItemName());
            floatQty = Float.parseFloat(data.getStringQty());
            if (floatQty == Math.round(floatQty)) {
                tvQuantity.setText(String.valueOf(data.getIntegerQty()));
            } else {
                tvQuantity.setText(String.valueOf(data.getFloatQty()));
            }
            String price=new DecimalFormat("#").format(data.getSalePrice());
            tvPrice.setText(price);
            String amount=new DecimalFormat("#").format(data.getAmount());
            tvAmount.setText(amount);
            layoutPrintList2.addView(row);
        }
    }

    private void printCalculateAmount(){
        btPrintSubTotalAmt.setText(subtotal);
        btPrintCommercialTaxAmt.setText(tax);
        btPrintServiceChargesAmt.setText(charges);
        btPrintDiscountAmt.setText(discount);
        btPrintGrandTotalAmt.setText(grandtotal);
        btPrintPaidAmt.setText(paid);
        btPrintChangeAmt.setText(change);

        btPrintSubTotalAmt2.setText(subtotal);
        btPrintCommercialTaxAmt2.setText(tax);
        btPrintServiceChargesAmt2.setText(charges);
        btPrintDiscountAmt2.setText(discount);
        btPrintGrandTotalAmt2.setText(grandtotal);
        btPrintPaidAmt2.setText(paid);
        btPrintChangeAmt2.setText(change);
    }

    public void print(Context context) throws IOException
    {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/QuickWaiterDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"print.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }
}
