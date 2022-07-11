package com.bosictsolution.quickwaiter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import common.DBHelper;
import common.ServerConnection;

public class CustomerEntryActivity extends AppCompatActivity {

    TextView tvWaiterName,tvLabelTable,tvLabelDate,tvLabelTime,tvLabelMan,tvLabelWomen,tvLabelChild,tvLabelCustomer;
    EditText etMan,etWomen,etChild,etCustomer;
    Button btnDate,btnTime,btnConfirm, btnGetCustomerData;
    static Button btnChooseTable;
    LinearLayout layoutCustomer,layoutChild,layoutWomen,layoutMan;

    private static final int DATE_PICKER_DIALOG=1;
    private static final int TIME_PICKER_DIALOG=2;
    private static final String DATE_FORMAT="yyyy-MM-dd";
    private static final String TIME_FORMAT="kk:mm";
    private static final String DATE_TIME_FORMAT="yyyy-MM-dd kk:mm:ss";
    private Calendar cCalendar;
    final Context context = this;
    private ProgressDialog progressDialog;
    private DBHelper db;
    ServerConnection serverconnection;

    public static int choosed_table_id;
    public static String choosed_table_name;
    int tranid,warning_message=1,error_message=2,success_message=3,info_message=4,insertTableID,insertWaiterID,insertMen,insertWomen,insertChild,insertTotal,editTableID,insertAllCustomer;
    String insertDateTime,insertOrUpdate,editTableName;
    int isSetAllCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_entry);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();
        cCalendar=Calendar.getInstance();

        setLayoutResource();

        Intent intent=getIntent();
        tvWaiterName.setTag(intent.getIntExtra("waiterid",0));
        tvWaiterName.setText(intent.getStringExtra("waitername"));
        choosed_table_id=intent.getIntExtra("tableid", 0);
        choosed_table_name=intent.getStringExtra("tablename");
        btnChooseTable.setTag(choosed_table_id);
        btnChooseTable.setText(choosed_table_name);

        registerButtonListenersAndSetDefaultText();
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
    protected Dialog onCreateDialog(int id){
        switch(id){
            case DATE_PICKER_DIALOG:
                return showDatePicker();
            case TIME_PICKER_DIALOG:
                return showTimePicker();
        }
        return super.onCreateDialog(id);
    }

    private void registerButtonListenersAndSetDefaultText(){
        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_DIALOG);

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(TIME_PICKER_DIALOG);

            }
        });
        updateDateButtonText();
        updateTimeButtonText();
        btnChooseTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i=new Intent(CustomerEntryActivity.this,TableActivity.class);
                i.putExtra("role", "just_choice");
                i.putExtra("from_waiter_main", false);
                i.putExtra("from_customer_info", true);
                startActivity(i);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(btnChooseTable.getTag()==null){
                    showMessage(info_message,"Please Choose Table!");
                    return;
                }
                else{
                    if(validateControls()) {
                        insertTableID = Integer.parseInt(btnChooseTable.getTag().toString());
                        insertWaiterID = Integer.parseInt(tvWaiterName.getTag().toString());
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
                        insertDateTime = dateTimeFormat.format(cCalendar.getTime());

                        if(isSetAllCustomer == 0) {
                            insertMen = Integer.parseInt((etMan.getText().length() == 0) ? "0" : etMan.getText().toString());
                            insertWomen = Integer.parseInt((etWomen.getText().length() == 0) ? "0" : etWomen.getText().toString());
                            insertChild = Integer.parseInt((etChild.getText().length() == 0) ? "0" : etChild.getText().toString());
                            insertTotal = insertMen + insertWomen + insertChild;
                        }else if(isSetAllCustomer == 1){
                            insertAllCustomer = Integer.parseInt((etCustomer.getText().length() == 0) ? "0" : etCustomer.getText().toString());
                        }

                        insertOrUpdate = btnConfirm.getText().toString();
                        InsertUpdateCustomer insertUpdateCustomer = new InsertUpdateCustomer();
                        insertUpdateCustomer.execute("");
                    }
                }
            }
        });
        btnGetCustomerData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(btnChooseTable.getTag()==null){
                    showMessage(info_message,"Please Choose Table");
                    return;
                }
                else{
                    editTableID=Integer.parseInt(btnChooseTable.getTag().toString());
                    editTableName=String.valueOf(btnChooseTable.getText().toString());
                    GetCustomerData getCustomerData=new GetCustomerData();
                    getCustomerData.execute("");
                }

            }
        });
    }

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(CustomerEntryActivity.this,new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cCalendar.set(Calendar.YEAR,year);
                cCalendar.set(Calendar.MONTH, monthOfYear);
                cCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        },cCalendar.get(Calendar.YEAR),cCalendar.get(Calendar.MONTH),cCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private void updateDateButtonText(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
        String dateForButton=dateFormat.format(cCalendar.getTime());
        btnDate.setText(dateForButton);
    }

    private TimePickerDialog showTimePicker(){
        TimePickerDialog timePicker=new TimePickerDialog(CustomerEntryActivity.this,0,new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cCalendar.set(Calendar.MINUTE, minute);
                updateTimeButtonText();
            }
        },cCalendar.get(Calendar.HOUR_OF_DAY),cCalendar.get(Calendar.MINUTE),true);
        return timePicker;
    }

    private void updateTimeButtonText(){
        SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
        String timeForButton=timeFormat.format(cCalendar.getTime());
        btnTime.setText(timeForButton);
    }

    public class InsertUpdateCustomer extends AsyncTask<String,String,String> {
        String msg;
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
                    String select_tranid="select Tranid from InvMasterSaleTemp where TableNameID="+insertTableID;
                    Statement st_tranid=con.createStatement();
                    ResultSet rs=st_tranid.executeQuery(select_tranid);
                    if(rs.next()){
                        tranid=rs.getInt(1);
                    }
                    String sql_query="",sql_data="",insert_update_cusinfo;
                    if(insertOrUpdate.equals("CONFIRM")){
                        if(tranid!=0){
                            sql_query="select CustomerInfoID from CustomerInfo where Tranid="+tranid;
                            Statement st_cusinfo=con.createStatement();
                            ResultSet rs_cusinfo=st_cusinfo.executeQuery(sql_query);
                            if(rs_cusinfo.next()){
                                if(isSetAllCustomer == 0)
                                    sql_query="Update CustomerInfo SET TableID="+insertTableID+",WaiterID="+insertWaiterID+",CDateTime='"+insertDateTime+"',Male="+insertMen+",Female="+insertWomen+",Children="+insertChild+",PAH="+insertTotal+" WHERE Tranid="+tranid;
                                else if(isSetAllCustomer == 1)
                                    sql_query="Update CustomerInfo SET TableID="+insertTableID+",WaiterID="+insertWaiterID+",CDateTime='"+insertDateTime+"',PAH="+insertAllCustomer+" WHERE Tranid="+tranid;
                                insert_update_cusinfo=sql_query;
                                Statement st=con.createStatement();
                                st.execute(insert_update_cusinfo);
                            }else {
                                if (isSetAllCustomer == 0) {
                                    sql_query = "INSERT INTO CustomerInfo(TableID,WaiterID,CDateTime,Male,Female,Children,PAH,Tranid) VALUES";
                                    sql_data = insertTableID + "," + insertWaiterID + ",'" + insertDateTime + "'," + insertMen + "," + insertWomen + "," + insertChild + "," + insertTotal + "," + tranid;
                                }else if(isSetAllCustomer == 1){
                                    sql_query = "INSERT INTO CustomerInfo(TableID,WaiterID,CDateTime,PAH,Tranid) VALUES";
                                    sql_data = insertTableID + "," + insertWaiterID + ",'" + insertDateTime + "'," + insertAllCustomer + "," + tranid;
                                }
                                insert_update_cusinfo = sql_query + "(" + sql_data + ")";
                                Statement st = con.createStatement();
                                st.execute(insert_update_cusinfo);
                            }
                            msg="Success";
                            msg_type=success_message;
                            isSuccess=true;
                        }
                    }
                    else if(insertOrUpdate.equals("UPDATE")){
                        /*sql_query="Declare @maxcusinfoid int set @maxcusinfoid=(select Max(CustomerInfoID) from CustomerInfo where Tranid="+tranid+") Update CustomerInfo SET TableID="+insertTableID+",WaiterID="+insertWaiterID+",CDateTime='"+insertDateTime+"',Male="+insertMen+",Female="+insertWomen+",Children="+insertChild+",PAH="+insertTotal+" WHERE CustomerInfoID=@maxcusinfoid";*/
                        if(isSetAllCustomer == 0)
                            sql_query="Update CustomerInfo SET TableID="+insertTableID+",WaiterID="+insertWaiterID+",CDateTime='"+insertDateTime+"',Male="+insertMen+",Female="+insertWomen+",Children="+insertChild+",PAH="+insertTotal+" WHERE Tranid="+tranid;
                        else if(isSetAllCustomer == 1)
                            sql_query="Update CustomerInfo SET TableID="+insertTableID+",WaiterID="+insertWaiterID+",CDateTime='"+insertDateTime+"',PAH="+insertAllCustomer+" WHERE Tranid="+tranid;
                        insert_update_cusinfo=sql_query;
                        Statement st=con.createStatement();
                        st.execute(insert_update_cusinfo);
                        msg="Success";
                        msg_type=success_message;
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
            showMessage(msg_type,r);
            if(isSuccess) {
                choosed_table_id=0;
                choosed_table_name="";
                finish();
            }
        }
    }

    public class GetCustomerData extends AsyncTask<String,String,String> {
        String msg;
        int msg_type,men,women,child;
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
                    String sql_query;
                    sql_query="DECLARE @tranid int SET @tranid=(select Tranid from InvMasterSaleTemp where TableNameID="+editTableID+") select Male,Female,Children,CDateTime,PAH,CustomerInfoID from CustomerInfo where Tranid=@tranid";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    if(rs.next()){
                        men=rs.getInt(1);
                        women=rs.getInt(2);
                        child=rs.getInt(3);
                        SimpleDateFormat dateTimeFormat=new SimpleDateFormat(DATE_TIME_FORMAT);
                        Date date;
                        try{
                            String datetime=rs.getString(4);
                            date=dateTimeFormat.parse(datetime);
                            cCalendar.setTime(date);
                        }catch(ParseException e){
                            Log.e("CustomerEntry", e.getMessage(), e);
                        }
                        isSuccess=true;
                    }
                    else{
                        isSuccess=false;
                        msg=editTableName+" data not found!";
                        msg_type=info_message;
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
                etMan.setText(String.valueOf(men));
                etWomen.setText(String.valueOf(women));
                etChild.setText(String.valueOf(child));
                updateDateButtonText();
                updateTimeButtonText();
                btnConfirm.setText("UPDATE");
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    private boolean validateControls(){
        if(isSetAllCustomer == 0) {
            if (etMan.getText().length() == 0 && etWomen.getText().length() == 0 && etChild.getText().length() == 0) {
                showMessage(warning_message, "At least one person must have!");
                return false;
            }
        }else if(isSetAllCustomer == 1){
            if (etCustomer.getText().length() == 0) {
                showMessage(warning_message, "At least one person must have!");
                return false;
            }
        }
        return true;
    }

    private void setLayoutResource(){
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        etMan=(EditText)findViewById(R.id.etMan);
        etWomen=(EditText)findViewById(R.id.etWomen);
        etChild=(EditText)findViewById(R.id.etChild);
        etCustomer=(EditText)findViewById(R.id.etCustomer);
        btnChooseTable=(Button)findViewById(R.id.btnChooseTable);
        btnDate=(Button)findViewById(R.id.btnDate);
        btnTime=(Button)findViewById(R.id.btnTime);
        btnConfirm=(Button)findViewById(R.id.btnConfirm);
        btnGetCustomerData =(Button)findViewById(R.id.btnGetCustomerData);
        tvLabelTable=(TextView)findViewById(R.id.tvLabelTable);
        tvLabelDate=(TextView)findViewById(R.id.tvLabelDate);
        tvLabelTime=(TextView)findViewById(R.id.tvLabelTime);
        tvLabelMan=(TextView)findViewById(R.id.tvLabelMan);
        tvLabelWomen=(TextView)findViewById(R.id.tvLabelWomen);
        tvLabelChild=(TextView)findViewById(R.id.tvLabelChild);
        tvLabelCustomer=(TextView)findViewById(R.id.tvLabelCustomer);
        layoutCustomer=(LinearLayout) findViewById(R.id.layoutCustomer);
        layoutMan=(LinearLayout) findViewById(R.id.layoutMan);
        layoutWomen=(LinearLayout) findViewById(R.id.layoutWomen);
        layoutChild=(LinearLayout) findViewById(R.id.layoutChild);

        tvWaiterName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        btnChooseTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        Cursor cur = db.getSetAllCustomerFeature();
        if(cur.moveToFirst())isSetAllCustomer=cur.getInt(0);
        if(isSetAllCustomer == 1){
            layoutChild.setVisibility(View.GONE);
            layoutMan.setVisibility(View.GONE);
            layoutWomen.setVisibility(View.GONE);
            layoutCustomer.setVisibility(View.VISIBLE);
        } else if (isSetAllCustomer == 0) {
            layoutChild.setVisibility(View.VISIBLE);
            layoutMan.setVisibility(View.VISIBLE);
            layoutWomen.setVisibility(View.VISIBLE);
            layoutCustomer.setVisibility(View.GONE);
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
}
