package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import java.util.Date;
import java.util.List;

import adapter.BookingListAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.BookingData;
import listener.BookingButtonClickListener;

public class BookingListActivity extends AppCompatActivity implements BookingButtonClickListener {

    TextView tvWaiterName,tvHeaderBookingTableName,tvHeaderGuestName,tvHeaderBookingDate,tvHeaderBookingTime,tvHeaderPeople,tvHeaderPhone,tvConfirmMessage;
    Button btnNewBooking;
    ListView lvBooking;

    DBHelper db;
    ServerConnection serverConnection;
    final Context context = this;
    private ProgressDialog progressDialog;
    BookingListAdapter bookingListAdapter;

    int warning_message=1,error_message=2,success_message=3,info_message=4,waiterid,deleteBookingID;
    List<BookingData> lstBookingData=new ArrayList<>();
    String waiterName,deleteBookingConfirmMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db=new DBHelper(this);
        serverConnection=new ServerConnection();

        setLayoutResource();

        Intent intent=getIntent();
        waiterid=intent.getIntExtra("waiterid", 0);
        waiterName=intent.getStringExtra("waitername");
        tvWaiterName.setText(waiterName);

        btnNewBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                BookingEntryActivity.bookingTableID=0;
                Intent i=new Intent(getApplicationContext(),BookingEntryActivity.class);
                i.putExtra("NewBooking",true);
                i.putExtra("WaiterID",waiterid);
                i.putExtra("WaiterName",waiterName);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        GetBookingData getBookingData=new GetBookingData();
        getBookingData.execute("");
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
    public void onEditButtonClickListener(int position){
        int editBookingID=lstBookingData.get(position).getBookingid();
        Intent i=new Intent(getApplicationContext(),BookingEntryActivity.class);
        i.putExtra("NewBooking",false);
        i.putExtra("EditBookingID",editBookingID);
        i.putExtra("WaiterID",waiterid);
        i.putExtra("WaiterName",waiterName);
        startActivity(i);
    }

    @Override
    public void onDeleteButtonClickListener(int position){
        deleteBookingID=lstBookingData.get(position).getBookingid();
        deleteBookingConfirmMessage="Are you sure you want to delete this booking table "+lstBookingData.get(position).getBookingTableName()+"?";
        showConfirmDialog();
    }

    private void setLayoutResource(){
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        tvHeaderBookingTableName=(TextView)findViewById(R.id.tvHeaderBookingTableName);
        tvHeaderGuestName=(TextView)findViewById(R.id.tvHeaderGuestName);
        tvHeaderBookingDate=(TextView)findViewById(R.id.tvHeaderBookingDate);
        tvHeaderBookingTime=(TextView)findViewById(R.id.tvHeaderBookingTime);
        tvHeaderPeople=(TextView)findViewById(R.id.tvHeaderPeople);
        tvHeaderPhone=(TextView)findViewById(R.id.tvHeaderPhone);
        btnNewBooking=(Button)findViewById(R.id.btnNewBooking);
        lvBooking=(ListView)findViewById(R.id.lvBooking);

        tvWaiterName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

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

    public class GetBookingData extends AsyncTask<String,String,String> {
        String msg,date;
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
                    String sql_query="select BookingID,booking.TableID,tb.Table_Name as TableName,GuestName,Phone,DATEADD(dd, 0, DATEDIFF(dd, 0, [DateTime])) AS Date,ArrivalTime,People,Purpose,booking.Remark from Booking booking inner join table_name tb on booking.TableID=tb.Table_Name_ID where booking.Deleted=0 and booking.WaiterID="+waiterid;
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstBookingData=new ArrayList<>();
                    while(rs.next()) {
                        BookingData data = new BookingData();
                        data.setBookingid(rs.getInt("BookingID"));
                        data.setBookingTableid(rs.getInt("TableID"));
                        data.setBookingTableName(rs.getString("TableName"));
                        data.setGuestName(rs.getString("GuestName"));
                        data.setPhone(rs.getString("Phone"));
                        date=rs.getString("Date");
                        date=date.substring(0,10);
                        data.setDate(date);
                        data.setTime(rs.getString("ArrivalTime"));
                        data.setTotalPeople(rs.getInt("People"));
                        data.setPurpose(rs.getString("Purpose"));
                        data.setRemark(rs.getString("Remark"));
                        lstBookingData.add(data);
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
                setData();
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    public class DeleteBooking extends AsyncTask<String,String,String> {
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
                    String sql_query="update Booking set Deleted=1 where BookingID="+deleteBookingID;
                    Statement st=con.createStatement();
                    st.execute(sql_query);
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
            if(!isSuccess) {
                showMessage(msg_type,r);
            }
        }
    }

    private void setData(){
        bookingListAdapter=new BookingListAdapter(this,lstBookingData);
        lvBooking.setAdapter(bookingListAdapter);
        bookingListAdapter.setOnBookingButtonClickListener(this);
    }

    private void showConfirmDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dialog_confirm, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        tvConfirmMessage=(TextView)passwordView.findViewById(R.id.tvConfirmMessage);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        tvConfirmMessage.setText(deleteBookingConfirmMessage);

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
                DeleteBooking deleteBooking=new DeleteBooking();
                deleteBooking.execute("");
                GetBookingData bookingData=new GetBookingData();
                bookingData.execute("");
                passwordRequireDialog.dismiss();
            }
        });
    }
}
