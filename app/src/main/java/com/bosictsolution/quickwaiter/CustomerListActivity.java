package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adapter.CustomerListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.CustomerData;
import listener.CustomerListGroupListener;

public class CustomerListActivity extends AppCompatActivity implements CustomerListGroupListener {

    Button btnSearch;
    ImageButton btnRefresh;
    EditText etSearch;
    ListView lvCustomer;
    TextView tvHeaderMan,tvHeaderWomen,tvHeaderChild,tvHeaderTotal;

    DBHelper db;
    ServerConnection serverConnection;
    private ProgressDialog progressDialog;
    CustomerListAdapter customerListAdapter;
    private static final String DATE_TIME_FORMAT="yyyy-MM-dd kk:mm:ss";
    private static final String DATE_FORMAT="yyyy-MM-dd";
    private static final String TIME_FORMAT="kk:mm";
    private Calendar cCalendar;
    List<CustomerData> lstCustomer=new ArrayList<>();

    int warning_message=1,error_message=2,success_message=3,info_message=4,editTranId,editedMan,editedWomen,editedChild,totalPeople,editedTotal;
    String searchTableName;
    int isSetAllCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        serverConnection=new ServerConnection();
        cCalendar=Calendar.getInstance();

        setLayoutResource();

        GetCustomerData getCustomerData=new GetCustomerData();
        getCustomerData.execute("");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etSearch.getText().toString().length()==0)return;
                searchTableName = etSearch.getText().toString();
                SearchByTable searchByTable=new SearchByTable();
                searchByTable.execute("");
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                GetCustomerData getCustomerData=new GetCustomerData();
                getCustomerData.execute("");
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
    public void onManPlusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        num += 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setMan(num);
    }

    @Override
    public void onManMinusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        if(num == 0) return;
        num -= 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setMan(num);
    }

    @Override
    public void onWomenPlusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        num += 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setWomen(num);
    }

    @Override
    public void onWomenMinusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        if(num == 0) return;
        num -= 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setWomen(num);
    }

    @Override
    public void onChildPlusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        num += 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setChild(num);
    }

    @Override
    public void onChildMinusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        if(num == 0) return;
        num -= 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setChild(num);
    }

    @Override
    public void onTotalPlusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        num += 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setTotal(num);
    }

    @Override
    public void onTotalMinusClickListener(int position,TextView tvNumber){
        int num=Integer.parseInt(tvNumber.getText().toString());
        if(num == 0) return;
        num -= 1;
        tvNumber.setText(String.valueOf(num));
        lstCustomer.get(position).setTotal(num);
    }

    @Override
    public void onSaveClickListener(int position){
        editTranId=lstCustomer.get(position).getTranId();

        if(isSetAllCustomer == 0) {
            editedMan = lstCustomer.get(position).getMan();
            editedWomen = lstCustomer.get(position).getWomen();
            editedChild = lstCustomer.get(position).getChild();
            totalPeople = editedMan + editedWomen + editedChild;
        }else if(isSetAllCustomer == 1){
            editedTotal = lstCustomer.get(position).getTotal();
        }

        UpdateCustomer updateCustomer=new UpdateCustomer();
        updateCustomer.execute("");
    }

    public class GetCustomerData extends AsyncTask<String,String,String> {
        String msg;
        int msg_type;
        boolean isSuccess;
        Date date;
        CustomerData customerData;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverConnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    lstCustomer=new ArrayList<>();
                    String sql_query;
                    SimpleDateFormat dateTimeFormat=new SimpleDateFormat(DATE_TIME_FORMAT);
                    sql_query="select Male,Female,Children,CDateTime,CustomerInfoID,c.TableID,c.Tranid,tb.Table_Name,PAH from CustomerInfo c inner join table_name tb on c.TableID=tb.Table_Name_ID inner join InvMasterSaleTemp ms on c.TableID=ms.TableNameID";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    while(rs.next()){
                        customerData=new CustomerData();

                        if(isSetAllCustomer == 0) {
                            customerData.setMan(rs.getInt(1));
                            customerData.setWomen(rs.getInt(2));
                            customerData.setChild(rs.getInt(3));
                        }else if(isSetAllCustomer == 1){
                            customerData.setTotal(rs.getInt(9));
                        }
                        customerData.setCustomerInfoId(rs.getInt(5));
                        customerData.setTableId(rs.getInt(6));
                        customerData.setTranId(rs.getInt(7));
                        customerData.setTableName(rs.getString(8));

                        try{
                            String datetime=rs.getString(4);
                            date=dateTimeFormat.parse(datetime);
                            cCalendar.setTime(date);
                            SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
                            customerData.setDate(dateFormat.format(cCalendar.getTime()));
                            SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
                            customerData.setTime(timeFormat.format(cCalendar.getTime()));
                        }catch(ParseException e){
                            Log.e("CustomerEntry", e.getMessage(), e);
                        }

                        lstCustomer.add(customerData);
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
                setCustomerList();
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    public class SearchByTable extends AsyncTask<String,String,String> {
        String msg;
        int msg_type;
        boolean isSuccess;
        Date date;
        CustomerData customerData;

        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverConnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    lstCustomer=new ArrayList<>();
                    String sql_query;
                    SimpleDateFormat dateTimeFormat=new SimpleDateFormat(DATE_TIME_FORMAT);
                    sql_query="select Male,Female,Children,CDateTime,CustomerInfoID,TableID,Tranid,tb.Table_Name,PAH from CustomerInfo c inner join table_name tb on c.TableID=tb.Table_Name_ID where tb.Table_Name='"+ searchTableName+"'";
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    while(rs.next()){
                        customerData=new CustomerData();
                        if(isSetAllCustomer == 0) {
                            customerData.setMan(rs.getInt(1));
                            customerData.setWomen(rs.getInt(2));
                            customerData.setChild(rs.getInt(3));
                        }else if(isSetAllCustomer == 1){
                            customerData.setTotal(rs.getInt(9));
                        }
                        customerData.setCustomerInfoId(rs.getInt(5));
                        customerData.setTableId(rs.getInt(6));
                        customerData.setTranId(rs.getInt(7));
                        customerData.setTableName(rs.getString(8));

                        try{
                            String datetime=rs.getString(4);
                            date=dateTimeFormat.parse(datetime);
                            cCalendar.setTime(date);
                            SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
                            customerData.setDate(dateFormat.format(cCalendar.getTime()));
                            SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
                            customerData.setTime(timeFormat.format(cCalendar.getTime()));
                        }catch(ParseException e){
                            Log.e("CustomerEntry", e.getMessage(), e);
                        }

                        lstCustomer.add(customerData);
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
                setCustomerList();
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    public class UpdateCustomer extends AsyncTask<String,String,String> {
        String msg;
        int msg_type;
        boolean isSuccess;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverConnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                    isSuccess=false;
                }else{
                    String sql_query="";
                    if(isSetAllCustomer == 0)
                        sql_query="Update CustomerInfo SET Male="+editedMan+",Female="+editedWomen+",Children="+editedChild+",PAH="+totalPeople+" WHERE Tranid="+editTranId;
                    else if(isSetAllCustomer == 1)
                        sql_query="Update CustomerInfo SET PAH="+editedTotal+" WHERE Tranid="+editTranId;
                    Statement st=con.createStatement();
                    st.execute(sql_query);

                    msg_type=success_message;
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
               showMessage(msg_type,"Edited Successfully!");
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    private void setCustomerList(){
        customerListAdapter=new CustomerListAdapter(this,lstCustomer);
        lvCustomer.setAdapter(customerListAdapter);
        customerListAdapter.setOnCustomerListGroupListener(this);
    }

    private void setLayoutResource(){
        lvCustomer=(ListView)findViewById(R.id.lvCustomer);
        btnSearch=(Button)findViewById(R.id.btnSearch);
        btnRefresh=(ImageButton)findViewById(R.id.btnRefresh);
        etSearch=(EditText) findViewById(R.id.etSearch);
        tvHeaderMan=(TextView) findViewById(R.id.tvHeaderMan);
        tvHeaderWomen=(TextView) findViewById(R.id.tvHeaderWomen);
        tvHeaderChild=(TextView) findViewById(R.id.tvHeaderChild);
        tvHeaderTotal=(TextView) findViewById(R.id.tvHeaderTotal);

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        Cursor cur = db.getSetAllCustomerFeature();
        if(cur.moveToFirst())isSetAllCustomer=cur.getInt(0);
        if(isSetAllCustomer == 1){
            tvHeaderMan.setVisibility(View.GONE);
            tvHeaderWomen.setVisibility(View.GONE);
            tvHeaderChild.setVisibility(View.GONE);
            tvHeaderTotal.setVisibility(View.VISIBLE);
        } else if (isSetAllCustomer == 0) {
            tvHeaderMan.setVisibility(View.VISIBLE);
            tvHeaderWomen.setVisibility(View.VISIBLE);
            tvHeaderChild.setVisibility(View.VISIBLE);
            tvHeaderTotal.setVisibility(View.GONE);
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
