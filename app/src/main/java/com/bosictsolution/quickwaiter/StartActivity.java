package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.AppConstant;
import common.DBHelper;
import common.ServerConnection;

public class StartActivity extends AppCompatActivity {

    TextView tvTitleServerProperty,tvTitleRegister;
    EditText etIPAddress,etDatabaseName,etServerUser,etServerPassword,etMacAddress,etRegisterKey;
    Button btnOK,btnRegister,btnImportData;
    LinearLayout layoutServerProperty,layoutImportData,layoutRegister;

    private static DBHelper db;
    ServerConnection serverconnection;

    private ProgressDialog progressDialog;
    final Context context = this;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES="QuickWaiterPrefs";

    public static final String finished_start="finished_start";
    String generateKey,macAddress,registerKey;
    int warning_message=1,error_message=2,success_message=3,info_message=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();

        setLayoutResource();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                insertServerProperty();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                register();
            }
        });

        btnImportData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ImportData importData=new ImportData();
                importData.execute("");
            }
        });
    }

    @Override
    public void onResume(){
        sharedpreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        if(sharedpreferences.contains(finished_start)){
            Intent i=new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        super.onResume();
    }

    private void insertServerProperty(){
        String ip=etIPAddress.getText().toString();
        if(ip.length()==0){
            showMessage(info_message,"Enter IP Address!");
            return;
        }
        String dbname=etDatabaseName.getText().toString();
        if(dbname.length()==0){
            showMessage(info_message,"Enter Database Name!");
            return;
        }
        String user=etServerUser.getText().toString();
        if(user.length()==0){
            showMessage(info_message,"Enter User!");
            return;
        }
        String password=etServerPassword.getText().toString();
        if(password.length()==0){
            showMessage(info_message,"Enter Password!");
            return;
        }
        db.deleteIPSetting();
        if(db.insertIPSetting(ip, user, password,dbname)){
            showMessage(success_message,"Success!");
            layoutServerProperty.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);
            layoutImportData.setVisibility(View.GONE);
            generateRegisterKey();
        }
    }

    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    private void generateRegisterKey(){
        //WifiManager wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        //WifiInfo wInfo=wifiManager.getConnectionInfo();
        //String macAddress=wInfo.getMacAddress();
        //etMacAddress.setText(macAddress);

        String macAddress=getMacAddr();
        etMacAddress.setText(macAddress);

        String[] arr=macAddress.split(":");
        String newMacAddress="",insertKey;
        List<String> keyList=new ArrayList<>();
        int index=0;
        for(int i=0;i<arr.length;i++){
            newMacAddress+=arr[i];
        }
        insertKey="blue"+newMacAddress+"ocean016";
        StringBuilder reverseKey=new StringBuilder();
        reverseKey.append(insertKey);
        reverseKey=reverseKey.reverse();
        while(index<reverseKey.length()){
            keyList.add(reverseKey.substring(index, Math.min(index+4, reverseKey.length())).trim());
            keyList.add("-");
            index+=4;
        }
        generateKey=keyList.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        generateKey=generateKey.substring(0,generateKey.length()-1).trim();
        generateKey=generateKey.replaceAll("\\s+", "");
    }

    private void register(){
        macAddress=etMacAddress.getText().toString();
        registerKey=etRegisterKey.getText().toString();

        if(macAddress.length()==0){
            showMessage(error_message,"Not Found MAC Address!");
            return;
        }
        else if(registerKey.length()==0){
            showMessage(info_message,"Enter Register Key");
            return;
        }
        else if(!generateKey.equals(registerKey)){
            showMessage(error_message,"Invalid Register Key!");
            return;
        }
        else{
            InsertUpdateRegisterKey insertUpdateRegisterKey=new InsertUpdateRegisterKey();
            insertUpdateRegisterKey.execute("");
        }
    }

    private void setLayoutResource(){
        tvTitleServerProperty=(TextView)findViewById(R.id.tvTitleServerProperty);
        tvTitleRegister=(TextView)findViewById(R.id.tvTitleRegister);
        etIPAddress=(EditText)findViewById(R.id.etIPAddress);
        etDatabaseName=(EditText)findViewById(R.id.etDatabaseName);
        etServerUser=(EditText)findViewById(R.id.etServerUser);
        etServerPassword=(EditText)findViewById(R.id.etServerPassword);
        etMacAddress=(EditText)findViewById(R.id.etMacAddress);
        etRegisterKey=(EditText)findViewById(R.id.etRegisterKey);
        btnOK=(Button)findViewById(R.id.btnOK);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnImportData=(Button)findViewById(R.id.btnImportData);
        layoutServerProperty=(LinearLayout)findViewById(R.id.layoutServerProperty);
        layoutRegister=(LinearLayout)findViewById(R.id.layoutRegister);
        layoutImportData=(LinearLayout)findViewById(R.id.layoutImportData);
        layoutRegister.setVisibility(View.GONE);
        layoutImportData.setVisibility(View.GONE);

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

    public static String getIPSetting(){
        String ip="",user="",pass="",database="";
        Cursor cur=db.getIPSetting();
        if(cur.getCount()==1){
            cur.moveToFirst();
            ip=cur.getString(0);
            user=cur.getString(1);
            pass=cur.getString(2);
            database=cur.getString(3);
        }
        return ip+","+user+","+pass+","+database;
    }

    public void insertSetting(){
        db.insertSetting("Manage Table");
        db.insertSetting("Bill Printing");
        db.insertSetting("Barcode");
        db.insertSetting("Use Full Layout");
        db.insertSetting("Open Order Waiter");
        db.insertSetting("Open Order Kitchen");
        db.insertSetting("Change Table");
        db.insertSetting("Advanced Tax");
        db.insertSetting("Print Order");
        db.insertSetting("Print Bill");
        db.insertSetting("Hide SalePrice");
        db.insertSetting("ItemSub");
        db.insertSetting(AppConstant.HideCommercialTax);
        db.insertSetting(AppConstant.StartTime);
    }

    public class InsertUpdateRegisterKey extends AsyncTask<String,String,String> {
        String msg = "";
        int msg_type;
        boolean isServerError;
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                    msg_type = error_message;
                    isServerError=true;
                } else {
                    String select_query="select * from License where HDDSr='"+macAddress+"'";
                    Statement st=con.createStatement();
                    ResultSet rs=st.executeQuery(select_query);
                    if(!rs.next()){
                        String insert_query = "INSERT INTO License(HDDSr,GenerateSerial) VALUES(" + "'" + macAddress + "','" + registerKey + "')";
                        Statement st_insert = con.createStatement();
                        st_insert.execute(insert_query);
                    }
                    msg = "Success!";
                    msg_type = success_message;
                }
            } catch (Exception e) {
                msg = e.getMessage();
                msg_type = error_message;
            }
            return msg;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setMessage("Loading....");
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.hide();
            if (msg_type == success_message) {
                db.deleteRegister();
                if (db.insertRegister(macAddress, registerKey)) {
                    showMessage(success_message, "Success!");
                }
                layoutServerProperty.setVisibility(View.GONE);
                layoutRegister.setVisibility(View.GONE);
                layoutImportData.setVisibility(View.VISIBLE);
            } else {
                if(isServerError){
                    layoutServerProperty.setVisibility(View.VISIBLE);
                    layoutRegister.setVisibility(View.GONE);
                    layoutImportData.setVisibility(View.GONE);
                }
                showMessage(msg_type, r);
            }
        }
    }

    public class ImportData extends AsyncTask<String,String,String>{
        int waiterid,tabletypeid,tableid,tasteid,mainmenuid,submenuid,sysitemid,tax,service,stype,outoforder,counterid,msg_type,featureid,isAllow,incomeid,tid,groupid,tasteSort,noDis,itemDis;
        String waitername,tabletypename,tablename,mainmenuname,submenuname,itemname,tastename,password,itemid,adminPassword,sortcode,msg="",featureName,shopName,tasteShort,printerip,stypeName,userPassword,title1,title2,title3,title4,message1,message2,ingredients,barcode;
        double price;
        byte[] image;
        Boolean isSuccess=false;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                }else{
                    db.truncateAllTable();
                    String select_waiter="select ID,Description,Password from Waiter";
                    String select_tabletype="select TableTypeId,TableTypeName from TableType";
                    String select_table="select Table_Name_ID,Table_Name,TableType from table_name";
                    String select_mainmenu="select class,name,counterid from InvMainMenu order by sortcode";
                    String select_submenu="select category,class,name,sortcode,IncomeID from InvSubMenu order by sortcode";
                    String select_item="select ItemID,Name,SubMenuID,Saleprice,Stype,OutofOrder,Ingredients,Barcode,NoDis,isnull(ItemDis,0) from InvItem";
                    String select_systemitem="select SysID,ItemID from SystemInvItem";
                    String select_taste="select TID,TName from TasteCode";
                    String select_stype="select SID,SName from SType";
                    String select_taste_multi="select TID,GroupID,TasteID,TasteName,TasteShort,TasteSort,Price from TasteMulti";
                    String select_systemsetting="select Tax,Service,AdminPassword,Title,Userpassword from Systemsetting";
                    String select_featuresetting="select FeatureID,FeatureName,isnull(isAllow,0) from FeatureSetting";
                    String select_printersetting="select id,PrinterIP from Printer_setting";
                    String select_slip_format="select title1,title2,title3,title4,Message1,Message2,Image from SlipFormat";

                    Statement st_waiter=con.createStatement();
                    Statement st_tabletype=con.createStatement();
                    Statement st_table=con.createStatement();
                    Statement st_mainmenu=con.createStatement();
                    Statement st_submenu=con.createStatement();
                    Statement st_item=con.createStatement();
                    Statement st_systemitem=con.createStatement();
                    Statement st_taste=con.createStatement();
                    Statement st_stype=con.createStatement();
                    Statement st_taste_multi=con.createStatement();
                    Statement st_systemsetting=con.createStatement();
                    Statement st_featuresetting=con.createStatement();
                    Statement st_printersetting=con.createStatement();
                    Statement st_slip_format=con.createStatement();

                    ResultSet rs_waiter=st_waiter.executeQuery(select_waiter);
                    ResultSet rs_tabletype=st_tabletype.executeQuery(select_tabletype);
                    ResultSet rs_table=st_table.executeQuery(select_table);
                    ResultSet rs_mainmenu=st_mainmenu.executeQuery(select_mainmenu);
                    ResultSet rs_submenu=st_submenu.executeQuery(select_submenu);
                    ResultSet rs_item=st_item.executeQuery(select_item);
                    ResultSet rs_systemitem=st_systemitem.executeQuery(select_systemitem);
                    ResultSet rs_taste=st_taste.executeQuery(select_taste);
                    ResultSet rs_stype=st_stype.executeQuery(select_stype);
                    ResultSet rs_taste_multi=st_taste_multi.executeQuery(select_taste_multi);
                    ResultSet rs_systemsetting=st_systemsetting.executeQuery(select_systemsetting);
                    ResultSet rs_featuresetting=st_featuresetting.executeQuery(select_featuresetting);
                    ResultSet rs_printersetting=st_printersetting.executeQuery(select_printersetting);
                    ResultSet rs_slip_format=st_slip_format.executeQuery(select_slip_format);

					/* for waiter */
                    while(rs_waiter.next()){
                        waiterid= rs_waiter.getInt(1);
                        waitername=rs_waiter.getString(2);
                        password=rs_waiter.getString(3);
                        db.insertWaiter(waiterid,waitername, password);
                    }
					/* for table type */
                    while(rs_tabletype.next()){
                        tabletypeid=rs_tabletype.getInt(1);
                        tabletypename=rs_tabletype.getString(2);
                        db.insertTableType(tabletypeid, tabletypename);
                    }
					/* for table */
                    while(rs_table.next()){
                        tableid=rs_table.getInt(1);
                        tablename=rs_table.getString(2);
                        tabletypeid=rs_table.getInt(3);
                        db.insertTable(tableid, tablename, tabletypeid);
                    }
					/* for main menu */
                    while(rs_mainmenu.next()){
                        mainmenuid=rs_mainmenu.getInt(1);
                        mainmenuname=rs_mainmenu.getString(2);
                        counterid=rs_mainmenu.getInt(3);
                        db.insertMainMenu(mainmenuid, mainmenuname,counterid);
                    }
					/* for sub menu */
                    while(rs_submenu.next()){
                        submenuid=rs_submenu.getInt(1);
                        mainmenuid=rs_submenu.getInt(2);
                        submenuname=rs_submenu.getString(3);
                        sortcode=rs_submenu.getString(4);
                        incomeid=rs_submenu.getInt(5);
                        db.insertSubMenu(submenuid, submenuname, mainmenuid,sortcode,incomeid);
                    }
					/* for item */
                    while(rs_item.next()){
                        itemid=rs_item.getString(1);
                        itemname=rs_item.getString(2);
                        submenuid=rs_item.getInt(3);
                        price=rs_item.getDouble(4);
                        stype=rs_item.getInt(5);
                        outoforder=rs_item.getInt(6);
                        ingredients=rs_item.getString(7);
                        barcode=rs_item.getString(8);
                        noDis=rs_item.getInt(9);
                        itemDis=rs_item.getInt(10);
                        db.insertItem(itemid, itemname, submenuid, price,stype,outoforder,ingredients,barcode,noDis,itemDis);
                    }
					/* for system item */
                    while(rs_systemitem.next()){
                        sysitemid=rs_systemitem.getInt(1);
                        itemid=rs_systemitem.getString(2);
                        db.insertSysItem(sysitemid, itemid);
                    }
					/* for taste */
                    while(rs_taste.next()){
                        tasteid=rs_taste.getInt(1);
                        tastename=rs_taste.getString(2);
                        db.insertTaste(tasteid, tastename);
                    }
                    /* for taste multi */
                    while(rs_taste_multi.next()){
                        tid=rs_taste_multi.getInt(1);
                        groupid=rs_taste_multi.getInt(2);
                        tasteid=rs_taste_multi.getInt(3);
                        tastename=rs_taste_multi.getString(4);
                        tasteShort=rs_taste_multi.getString(5);
                        tasteSort=rs_taste_multi.getInt(6);
                        price=rs_taste_multi.getDouble(7);
                        db.insertTasteMulti(tid,groupid,tasteid,tastename,tasteShort,tasteSort,price);
                    }
                    /* for stype */
                    while(rs_stype.next()){
                        stype=rs_stype.getInt(1);
                        stypeName=rs_stype.getString(2);
                        db.insertSType(stype, stypeName);
                    }
					/* for system setting */
                    while(rs_systemsetting.next()){
                        tax=rs_systemsetting.getInt(1);
                        service=rs_systemsetting.getInt(2);
                        adminPassword=rs_systemsetting.getString(3);
                        shopName=rs_systemsetting.getString(4);
                        userPassword=rs_systemsetting.getString(5);
                        db.insertSystemSetting(tax, service,adminPassword,shopName,userPassword);
                    }
                    /* for feature setting */
                    while(rs_featuresetting.next()){
                        featureid=rs_featuresetting.getInt(1);
                        featureName=rs_featuresetting.getString(2);
                        isAllow=rs_featuresetting.getInt(3);
                        db.insertFeature(featureid,featureName,isAllow);
                    }
                    /* for printer setting */
                    while(rs_printersetting.next()){
                        stype=rs_printersetting.getInt(1);
                        printerip=rs_printersetting.getString(2);
                        db.insertPrinterSetting(stype, printerip);
                    }
                     /* for slip format */
                    while(rs_slip_format.next()){
                        title1=rs_slip_format.getString(1);
                        title2=rs_slip_format.getString(2);
                        title3=rs_slip_format.getString(3);
                        title4=rs_slip_format.getString(4);
                        message1=rs_slip_format.getString(5);
                        message2=rs_slip_format.getString(6);
                        image = rs_slip_format.getBytes(7);
                        db.insertSlipFormat(title1,title2,title3,title4,message1,message2);
                        if (image != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                            saveLogo(bitmap);
                        }
                    }

                    msg="Import Successful!";
                    msg_type=success_message;
                    isSuccess=true;
                }

            }catch(Exception ex){
                isSuccess=false;
                msg_type=error_message;
                msg=ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
            progressDialog.setMessage("Importing Data.....");
        }
        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
            showMessage(msg_type,r);
            if(isSuccess){
                insertSetting();
                db.resetTable();
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString(finished_start, "finished_start");
                editor.commit();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void saveLogo(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/QuickWaiterDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File path=new File(directory,"shoplogo.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
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
    }
}
