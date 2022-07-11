package com.bosictsolution.quickwaiter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

import common.DBHelper;
import common.ServerConnection;
import data.BookingData;

public class BookingEntryActivity extends AppCompatActivity {

    TextView tvWaiterName,tvLabelDate,tvLabelTime;
    EditText etGuestName,etPhone,etTotalPeople,etPurpose,etRemark;
    Button btnDate,btnTime,btnBookNow;
    static Button btnChooseTable;

    private static final int DATE_PICKER_DIALOG=1;
    private static final String DATE_FORMAT="yyyy-MM-dd";
    private static final String TIME_FORMAT="hh:mm a";
    private static final String DATE_TIME_FORMAT="yyyy-MM-dd hh:mm:ss";
    private Calendar dateCalendar,timeCalendar;
    final Context context = this;
    private ProgressDialog progressDialog;
    private DBHelper db;
    ServerConnection serverconnection;
    TimePickerDialog timepickerdialog;

    static int bookingTableID;
    int warning_message=1,error_message=2,success_message=3,info_message=4,waiterid,editBookingID,people;
    String waiterName,guestName,phone,datetime,purpose,remark,format,arrivalTime;
    boolean newBooking;
    List<BookingData> lstBookingData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_entry);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        setLayoutResource();

        db=new DBHelper(this);
        serverconnection=new ServerConnection();
        dateCalendar=Calendar.getInstance();
        timeCalendar=Calendar.getInstance();

        Intent intent=getIntent();
        newBooking=intent.getBooleanExtra("NewBooking",false);
        waiterid=intent.getIntExtra("WaiterID",0);
        waiterName=intent.getStringExtra("WaiterName");
        editBookingID=intent.getIntExtra("EditBookingID",0);
        tvWaiterName.setText(waiterName);

        if(newBooking) {
            btnBookNow.setText("Book Now");
            updateDateButtonText();
            updateTimeButtonText();
        }
        else {
            GetEditBookingData getEditBookingData=new GetEditBookingData();
            getEditBookingData.execute("");
        }

        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showTimePicker();
            }
        });
        btnChooseTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i=new Intent(getApplicationContext(),TableActivity.class);
                i.putExtra("role", "booking");
                startActivity(i);
            }
        });
        btnBookNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(validateControls()){
                    arrivalTime=btnTime.getText().toString();
                    guestName=etGuestName.getText().toString();
                    phone=etPhone.getText().toString();
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
                    datetime = dateTimeFormat.format(dateCalendar.getTime());
                    people=Integer.parseInt(etTotalPeople.getText().toString());
                    purpose=etPurpose.getText().toString();
                    remark=etRemark.getText().toString();
                    if(btnBookNow.getText().toString().equals("Book Now")){
                        InsertBookingData insertBookingData=new InsertBookingData();
                        insertBookingData.execute("");
                    }else if(btnBookNow.getText().toString().equals("Update")){
                        UpdateBookingData updateBookingData=new UpdateBookingData();
                        updateBookingData.execute("");
                    }
                }
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
    protected Dialog onCreateDialog(int id){
        switch(id){
            case DATE_PICKER_DIALOG:
                return showDatePicker();
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog showDatePicker(){
        DatePickerDialog datePicker=new DatePickerDialog(BookingEntryActivity.this,new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR,year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateButtonText();
            }
        },dateCalendar.get(Calendar.YEAR),dateCalendar.get(Calendar.MONTH),dateCalendar.get(Calendar.DAY_OF_MONTH));
        return datePicker;
    }

    private void updateDateButtonText(){
        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
        String dateForButton=dateFormat.format(dateCalendar.getTime());
        btnDate.setText(dateForButton);
    }

    private void showTimePicker(){
        timepickerdialog = new TimePickerDialog(BookingEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String sMinute=String.valueOf(minute);
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        }
                        else if (hourOfDay == 12) {
                            format = "PM";
                        }
                        else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        }
                        else {
                            format = "AM";
                        }
                        if(minute==0) btnTime.setText(hourOfDay + ":" + "00" + format);
                        else if(sMinute.length()==1)btnTime.setText(hourOfDay+":"+"0"+minute+format);
                        else btnTime.setText(hourOfDay + ":" + minute + format);
                    }
            }, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), false);
        timepickerdialog.show();
    }

    private void updateTimeButtonText(){
        SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
        String timeForButton=timeFormat.format(timeCalendar.getTime());
        btnTime.setText(timeForButton);
    }

    private void setLayoutResource(){
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        etGuestName=(EditText) findViewById(R.id.etGuestName);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etTotalPeople=(EditText)findViewById(R.id.etTotalPeople);
        etPurpose=(EditText)findViewById(R.id.etPurpose);
        etRemark=(EditText)findViewById(R.id.etRemark);
        btnDate=(Button)findViewById(R.id.btnDate);
        btnTime=(Button)findViewById(R.id.btnTime);
        btnChooseTable=(Button)findViewById(R.id.btnChooseTable);
        btnBookNow=(Button)findViewById(R.id.btnBookingNow);
        tvLabelDate=(TextView)findViewById(R.id.tvLabelDate);
        tvLabelTime=(TextView)findViewById(R.id.tvLabelTime);

        tvWaiterName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        etGuestName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ZawgyiOne2008.ttf"));
        etPhone.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ZawgyiOne2008.ttf"));
        etTotalPeople.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ZawgyiOne2008.ttf"));
        etPurpose.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ZawgyiOne2008.ttf"));
        etRemark.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ZawgyiOne2008.ttf"));

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

    private boolean validateControls(){
        if(etGuestName.getText().toString().length()==0){
            showMessage(warning_message,"Enter Guest Name");
            etGuestName.requestFocus();
            return false;
        }
        if(etPhone.getText().toString().length()==0){
            showMessage(warning_message,"Enter Phone");
            etPhone.requestFocus();
            return false;
        }
        if(etTotalPeople.getText().toString().length()==0){
            showMessage(warning_message,"Enter Number of People");
            etTotalPeople.requestFocus();
            return false;
        }
        if(bookingTableID==0){
            showMessage(warning_message,"Choose Table");
            return false;
        }
        return true;
    }

    public class GetEditBookingData extends AsyncTask<String,String,String> {
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
                    String sql_query="select BookingID,booking.TableID,tb.Table_Name as TableName,GuestName,Phone,DateTime,ArrivalTime,People,Purpose,booking.Remark from Booking booking inner join table_name tb on booking.TableID=tb.Table_Name_ID where BookingID="+editBookingID;
                    Statement st=con.createStatement();
                    ResultSet rs= st.executeQuery(sql_query);
                    lstBookingData=new ArrayList<>();
                    if(rs.next()) {
                        BookingData data = new BookingData();
                        data.setBookingid(rs.getInt("BookingID"));
                        data.setBookingTableid(rs.getInt("TableID"));
                        data.setBookingTableName(rs.getString("TableName"));
                        data.setGuestName(rs.getString("GuestName"));
                        data.setPhone(rs.getString("Phone"));
                        data.setDate(rs.getString("DateTime"));
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
                fillData();
            }else{
                showMessage(msg_type,r);
            }
        }
    }

    private void fillData(){
        if(lstBookingData.size()!=0) {
            etGuestName.setText(lstBookingData.get(0).getGuestName());
            etPhone.setText(lstBookingData.get(0).getPhone());
            etTotalPeople.setText(String.valueOf(lstBookingData.get(0).getTotalPeople()));
            etPurpose.setText(lstBookingData.get(0).getPurpose());
            etRemark.setText(lstBookingData.get(0).getRemark());
            btnChooseTable.setText(lstBookingData.get(0).getBookingTableName());
            btnTime.setText(lstBookingData.get(0).getTime());
            bookingTableID=lstBookingData.get(0).getBookingTableid();
            editBookingID=lstBookingData.get(0).getBookingid();
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            Date date;
            try {
                String sDate = lstBookingData.get(0).getDate();
                date = dateTimeFormat.parse(sDate);
                dateCalendar.setTime(date);
            } catch (ParseException e) {
                Log.e("BookingEntry", e.getMessage(), e);
            }
            updateDateButtonText();
            timeCalendar=Calendar.getInstance();
            btnBookNow.setText("Update");
        }
    }

    public class InsertBookingData extends AsyncTask<String,String,String> {
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
                    String sql_query = "INSERT INTO Booking(WaiterID,TableID,GuestName,Phone,DateTime,People,Purpose,Remark,ArrivalTime,Deleted) VALUES";
                    String sql_data = waiterid + "," + bookingTableID + ",'" + guestName + "','" + phone + "','" + datetime + "'," + people + ",'" + purpose + "','" + remark+"','"+arrivalTime+"',"+0;
                    String insert_query = sql_query + "(" + sql_data + ")";
                    Statement st=con.createStatement();
                    st.execute(insert_query);
                    isSuccess=true;
                    msg_type=success_message;
                    msg="Success!";
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
            if(isSuccess)finish();
        }
    }

    public class UpdateBookingData extends AsyncTask<String,String,String> {
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
                    String sql_query="Update Booking SET WaiterID="+waiterid+",TableID="+bookingTableID+",GuestName='"+guestName+"',Phone='"+phone+"',DateTime='"+datetime+"',People="+people+",Purpose='"+purpose+"',Remark='"+remark+"',ArrivalTime='"+arrivalTime+"' WHERE BookingID="+editBookingID;
                    Statement st=con.createStatement();
                    st.execute(sql_query);

                    isSuccess=true;
                    msg_type=success_message;
                    msg="Success!";
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
            if(isSuccess)finish();
        }
    }
}
