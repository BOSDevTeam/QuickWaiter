package com.bosictsolution.quickwaiter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import adapter.WaiterSpinnerAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.WaiterData;

public class LoginActivity extends AppCompatActivity {

    Spinner spUserName;
    EditText etPassword;
    Button btnLogin,btnUpdateData,btnExit,btnSetting;
    TextView tvTitleLogin;

    private DBHelper db;
    ServerConnection serverconnection;
    WaiterSpinnerAdapter waiterSpinnerAdapter;

    private ProgressDialog progressDialog;
    final Context context = this;
    List<WaiterData> lstWaiterData;
    public int login_waiter_id;
    static String display_checked_tables="";
    int warning_message=1,error_message=2,success_message=3,info_message=4;
    public static final String DATE_FORMAT="yyyy-MM-dd";
    public static final String TIME_FORMAT="hh:mm a";
    public static final String DATE_TIME_FORMAT="yyyy-MM-dd hh:mm:ss";
    public static final String MM_DATE_FORMAT="dd-MM-yyyy";
    public static final String ORDER_PRINT_TIME_FORMAT="hh:mm:ss a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=600){
            setContentView(R.layout.activity_login);
        }else{
            setContentView(R.layout.activity_login);
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();

        setLayoutResource();

        etPassword.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                login();
            }
        });

        btnUpdateData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showUpdateDataDialog();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                moveTaskToBack(true);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminPasswordDialog();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        getWaiter();
        etPassword.setText("");
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == R.id.menuServerProperty) {
            showUserPasswordDialog();
            return true;
        } else if (itemId == R.id.menuSetting) {
            showAdminPasswordDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    /**
     * Asynchronous Data Update Class
     */

    public class UpdateData extends AsyncTask<String,String,String>{
        String msg="";
        int msg_type;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    msg_type=error_message;
                }else{
                    String updateTableName;
                    String[] arr= display_checked_tables.split(",");
                    for(int i=0;i<arr.length;i++){
                        updateTableName=arr[i];
                        if(updateTableName.equals("Waiter")){
                            int waiterID;
                            String waiterName,password;
                            db.truncateWaiterTemp();
                            String select_waiter="select ID,Description,Password from Waiter";
                            Statement st_waiter=con.createStatement();
                            ResultSet rs_waiter=st_waiter.executeQuery(select_waiter);

                            while(rs_waiter.next()){
                                waiterID= rs_waiter.getInt(1);
                                waiterName=rs_waiter.getString(2);
                                password=rs_waiter.getString(3);
                                db.insertWaiterTemp(waiterID, waiterName, password);
                                Cursor cur=db.isWaiterID(waiterID);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateWaiter(waiterID, waiterName, password);
                                }
                                else{ /*not exist*/
                                    db.insertWaiter(waiterID,waiterName, password);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllWaiter();
                            while(cur.moveToNext()){
                                int curWaiterID=cur.getInt(0);
                                Cursor cursor=db.isWaiterIDinTemp(curWaiterID);
                                if(cursor.getCount()==0){
                                    db.deleteWaiter(curWaiterID);
                                }
                            }
                        }
                        else if(updateTableName.equals("Table")){
                            int tableID,tableTypeID;
                            String tableName;
                            db.truncateTableTemp();
                            String select_table="select Table_Name_ID,Table_Name,TableType from table_name";
                            Statement st_table=con.createStatement();
                            ResultSet rs_table=st_table.executeQuery(select_table);

                            while(rs_table.next()){
                                tableID= rs_table.getInt(1);
                                tableName=rs_table.getString(2);
                                tableTypeID=rs_table.getInt(3);

                                db.insertTableTemp(tableID, tableName,tableTypeID);
                                Cursor cur=db.isTableID(tableID);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateTable(tableID, tableName,tableTypeID);
                                }
                                else{ /*not exist*/
                                    db.insertTable(tableID,tableName,tableTypeID);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllTable();
                            while(cur.moveToNext()){
                                int curTableID=cur.getInt(0);
                                Cursor cursor=db.isTableIDinTemp(curTableID);
                                if(cursor.getCount()==0){
                                    db.deleteTable(curTableID);
                                }
                            }
                        }
                        else if(updateTableName.equals("Table Type")){
                            int tableTypeID;
                            String tableTypeName;
                            db.truncateTableTypeTemp();
                            String select_table_type="select TableTypeId,TableTypeName from TableType";
                            Statement st_table_type=con.createStatement();
                            ResultSet rs_table_type=st_table_type.executeQuery(select_table_type);

                            while(rs_table_type.next()){
                                tableTypeID= rs_table_type.getInt(1);
                                tableTypeName=rs_table_type.getString(2);

                                db.insertTableTypeTemp(tableTypeID, tableTypeName);
                                Cursor cur=db.isTableTypeID(tableTypeID);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateTableType(tableTypeID, tableTypeName);
                                }
                                else{ /*not exist*/
                                    db.insertTableType(tableTypeID,tableTypeName);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllTableType();
                            while(cur.moveToNext()){
                                int curTableTypeID=cur.getInt(0);
                                Cursor cursor=db.isTableTypeIDinTemp(curTableTypeID);
                                if(cursor.getCount()==0){
                                    db.deleteTableType(curTableTypeID);
                                }
                            }
                        }
                        else if(updateTableName.equals("Main Menu")){
                            int mainMenuID,counterID;
                            String mainMenuName;
                            db.truncateMainMenuTemp();
                            String select_main_menu="select class,name,counterid from InvMainMenu";
                            Statement st_main_menu=con.createStatement();
                            ResultSet rs_main_menu=st_main_menu.executeQuery(select_main_menu);

                            while(rs_main_menu.next()){
                                mainMenuID= rs_main_menu.getInt(1);
                                mainMenuName=rs_main_menu.getString(2);
                                counterID=rs_main_menu.getInt(3);

                                db.insertMainMenuTemp(mainMenuID, mainMenuName,counterID);
                                Cursor cur=db.isMainMenuID(mainMenuID);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateMainMenu(mainMenuID, mainMenuName,counterID);
                                }
                                else{ /*not exist*/
                                    db.insertMainMenu(mainMenuID,mainMenuName,counterID);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllMainMenu();
                            while(cur.moveToNext()){
                                int curMainMenuID=cur.getInt(0);
                                Cursor cursor=db.isMainMenuIDinTemp(curMainMenuID);
                                if(cursor.getCount()==0){
                                    db.deleteMainMenu(curMainMenuID);
                                }
                            }
                        }
                        else if(updateTableName.equals("Sub Menu")){
                            int subMenuID,mainMenuID,incomeid;
                            String subMenuName,sortCode;
                            db.truncateSubMenuTemp();
                            String select_submenu="select category,class,name,sortcode,IncomeID from InvSubMenu order by sortcode";
                            Statement st_submenu=con.createStatement();
                            ResultSet rs_submenu=st_submenu.executeQuery(select_submenu);

                            while(rs_submenu.next()){
                                subMenuID= rs_submenu.getInt(1);
                                mainMenuID=rs_submenu.getInt(2);
                                subMenuName=rs_submenu.getString(3);
                                sortCode=rs_submenu.getString(4);
                                incomeid=rs_submenu.getInt(5);

                                db.insertSubMenuTemp(subMenuID, subMenuName,mainMenuID,sortCode,incomeid);
                                Cursor cur=db.isSubMenuID(subMenuID);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateSubMenu(subMenuID, subMenuName,mainMenuID,sortCode,incomeid);
                                }
                                else{ /*not exist*/
                                    db.insertSubMenu(subMenuID,subMenuName,mainMenuID,sortCode,incomeid);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllSubMenu();
                            while(cur.moveToNext()){
                                int curSubMenuID=cur.getInt(0);
                                Cursor cursor=db.isSubMenuIDinTemp(curSubMenuID);
                                if(cursor.getCount()==0){
                                    db.deleteSubMenu(curSubMenuID);
                                }
                            }
                        }
                        else if(updateTableName.equals("Item")){
                            int sysID,subMenuID,sType,outOfOrder,noDis=0,itemDis;
                            String itemID,itemName,ingredients,barcode;
                            double price;
                            db.truncateItem();
                            String select_item="select ItemID,Name,SubMenuID,Saleprice,Stype,OutofOrder,Ingredients,Barcode,noDis,isnull(ItemDis,0) from InvItem";
                            String select_system_item="select SysID,ItemID from SystemInvItem";
                            Statement st_item=con.createStatement();
                            Statement st_system_item=con.createStatement();
                            ResultSet rs_item=st_item.executeQuery(select_item);
                            ResultSet rs_system_item=st_system_item.executeQuery(select_system_item);

							/* for item */
                            while(rs_item.next()){
                                itemID=rs_item.getString(1);
                                itemName=rs_item.getString(2);
                                subMenuID=rs_item.getInt(3);
                                price=rs_item.getDouble(4);
                                sType=rs_item.getInt(5);
                                outOfOrder=rs_item.getInt(6);
                                ingredients=rs_item.getString(7);
                                barcode=rs_item.getString(8);
                                noDis=rs_item.getInt(9);
                                itemDis=rs_item.getInt(10);
                                db.insertItem(itemID, itemName, subMenuID, price,sType,outOfOrder,ingredients,barcode,noDis,itemDis);
                            }
							/* for system item */
                            while(rs_system_item.next()){
                                sysID=rs_system_item.getInt(1);
                                itemID=rs_system_item.getString(2);
                                db.insertSysItem(sysID, itemID);
                            }
                        }
                        else if(updateTableName.equals("Taste")){
                            int tasteID;
                            String tasteName;
                            db.truncateTasteTemp();
                            String select_taste="select TID,TName from TasteCode";
                            Statement st_taste=con.createStatement();
                            ResultSet rs_taste=st_taste.executeQuery(select_taste);

                            while(rs_taste.next()){
                                tasteID= rs_taste.getInt(1);
                                tasteName=rs_taste.getString(2);

                                db.insertTasteTemp(tasteID, tasteName);
                                Cursor cur=db.isTasteID(tasteID);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateTaste(tasteID, tasteName);
                                }
                                else{ /*not exist*/
                                    db.insertTaste(tasteID,tasteName);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllTaste();
                            while(cur.moveToNext()){
                                int curTasteID=cur.getInt(0);
                                Cursor cursor=db.isTasteIDinTemp(curTasteID);
                                if(cursor.getCount()==0){
                                    db.deleteTaste(curTasteID);
                                }
                            }
                        }
                        else if(updateTableName.equals("Taste Multi")){
                            int tasteID,tid,groupid,tasteSort;
                            String tasteName,tasteShort;
                            double price;
                            db.truncateTasteMultiTemp();
                            String select_taste_multi="select TID,GroupID,TasteID,TasteName,TasteShort,TasteSort,Price from TasteMulti";
                            Statement st_taste_multi=con.createStatement();
                            ResultSet rs_taste_multi=st_taste_multi.executeQuery(select_taste_multi);

                            while(rs_taste_multi.next()){
                                tid= rs_taste_multi.getInt(1);
                                groupid=rs_taste_multi.getInt(2);
                                tasteID=rs_taste_multi.getInt(3);
                                tasteName=rs_taste_multi.getString(4);
                                tasteShort=rs_taste_multi.getString(5);
                                tasteSort=rs_taste_multi.getInt(6);
                                price=rs_taste_multi.getDouble(7);

                                db.insertTasteMultiTemp(tid,groupid,tasteID,tasteName,tasteShort,tasteSort,price);
                                Cursor cur=db.isTasteMultiID(tid);
                                if(cur.getCount()!=0){ /* exist */
                                    db.updateTasteMulti(tid,groupid,tasteID,tasteName,tasteShort,tasteSort,price);
                                }
                                else{ /*not exist*/
                                    db.insertTasteMulti(tid,groupid,tasteID,tasteName,tasteShort,tasteSort,price);
                                }
                            }

							/* for delete */
                            Cursor cur=db.getAllTasteMulti();
                            while(cur.moveToNext()){
                                int curTID=cur.getInt(0);
                                Cursor cursor=db.isTIDinTemp(curTID);
                                if(cursor.getCount()==0){
                                    db.deleteTasteMulti(curTID);
                                }
                            }
                        }
                        else if(updateTableName.equals("SType")){
                            int stypeid;
                            String stypeName;
                            db.truncateSType();
                            String select_stype="select SID,SName from SType";
                            Statement st_stype=con.createStatement();
                            ResultSet rs_stype=st_stype.executeQuery(select_stype);

                            while(rs_stype.next()){
                                stypeid=rs_stype.getInt(1);
                                stypeName=rs_stype.getString(2);
                                db.insertSType(stypeid, stypeName);
                            }
                        }
                        else if(updateTableName.equals("System Setting")){
                            int tax,service;
                            String adminPassword,shopName,userPassword;
                            db.truncateSystemSetting();
                            String select_system_setting="select Tax,Service,AdminPassword,Title,Userpassword from Systemsetting";
                            Statement st_system_setting=con.createStatement();
                            ResultSet rs_system_setting=st_system_setting.executeQuery(select_system_setting);

                            if(rs_system_setting.next()){
                                tax=rs_system_setting.getInt(1);
                                service=rs_system_setting.getInt(2);
                                adminPassword=rs_system_setting.getString(3);
                                shopName=rs_system_setting.getString(4);
                                userPassword=rs_system_setting.getString(5);
                                db.insertSystemSetting(tax, service,adminPassword,shopName,userPassword);
                            }
                        }else if(updateTableName.equals("Feature Setting")){
                            int featureid,isAllow;
                            String featureName;
                            db.truncateFeatureSetting();
                            String select_feature="select FeatureID,FeatureName,isnull(isAllow,0) from FeatureSetting";
                            Statement st_feature=con.createStatement();
                            ResultSet rs_feature=st_feature.executeQuery(select_feature);

                            while(rs_feature.next()){
                                featureid=rs_feature.getInt(1);
                                featureName=rs_feature.getString(2);
                                isAllow=rs_feature.getInt(3);
                                db.insertFeature(featureid, featureName,isAllow);
                            }
                        }else if(updateTableName.equals("Printer Setting")){
                            int stype;
                            String printerip;
                            db.truncatePrinterSetting();
                            String select_printer_setting="select id,PrinterIP from Printer_setting";
                            Statement st_printer_setting=con.createStatement();
                            ResultSet rs_printer_setting=st_printer_setting.executeQuery(select_printer_setting);

                            while(rs_printer_setting.next()){
                                stype=rs_printer_setting.getInt(1);
                                printerip=rs_printer_setting.getString(2);
                                db.insertPrinterSetting(stype, printerip);
                            }
                        }else if(updateTableName.equals("Slip Format")){
                            String title1,title2,title3,title4,message1,message2;
                            byte[] image;
                            db.truncateSlipFormat();
                            String select_slip_format="select title1,title2,title3,title4,Message1,Message2,Image from SlipFormat";
                            Statement st_slip_format=con.createStatement();
                            ResultSet rs_slip_format=st_slip_format.executeQuery(select_slip_format);

                            if(rs_slip_format.next()){
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
                        }
                    }
                    msg="Update Successful!";
                    msg_type=success_message;
                }
            }
            catch(Exception e){
                msg=e.getMessage();
                msg_type=error_message;
            }
            return msg;
        }
        @Override
        protected void onPreExecute(){
            progressDialog.show();
            progressDialog.setMessage("Updating Data....");
        }
        @Override
        protected void onPostExecute(String r){
            progressDialog.hide();
            showMessage(msg_type,r);
            getWaiter();
            display_checked_tables="";
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

    /**
     * Methods
     */
    private void getWaiter(){
        Cursor cur= db.getAllWaiter();
        lstWaiterData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                WaiterData data=new WaiterData();
                data.setWaiterid(cur.getInt(0));
                data.setWaiterName(cur.getString(1));
                data.setPassword(cur.getString(2));
                lstWaiterData.add(data);
            }
        }
        waiterSpinnerAdapter=new WaiterSpinnerAdapter(this,lstWaiterData);
        spUserName.setAdapter(waiterSpinnerAdapter);
    }

    private void login(){
        int selectedPosition=spUserName.getSelectedItemPosition();
        String password=etPassword.getText().toString();
        if(password.length()==0){
            showMessage(info_message,"Enter Password!");
            etPassword.requestFocus();
            return;
        }
        String passwordByPosition=lstWaiterData.get(selectedPosition).getPassword();
        if(!password.equals(passwordByPosition)){
            showMessage(error_message,"Wrong Password!");
            etPassword.requestFocus();
            return;
        }
        else{
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            login_waiter_id= lstWaiterData.get(selectedPosition).getWaiterid();
            String waiter_name=lstWaiterData.get(selectedPosition).getWaiterName();
            intent.putExtra("waiterid", login_waiter_id);
            intent.putExtra("waitername", waiter_name);
            startActivity(intent);
        }
    }

    private void showAdminPasswordDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dialog_password, null);
        AlertDialog.Builder passwordDialog=new AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etAdminPassword=(EditText)passwordView.findViewById(R.id.etPassword);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

        passwordDialog.setCancelable(true);
        final AlertDialog passwordRequireDialog=passwordDialog.create();
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
                String inputAdminPassword=etAdminPassword.getText().toString();
                if(inputAdminPassword.length()==0){
                    showMessage(info_message,"Enter Password!");
                    return;
                }
                Cursor cur=db.getAdminPassword();
                if(cur.getCount()!=0){
                    cur.moveToFirst();
                    String adminPassword=cur.getString(0);
                    if(adminPassword.equals(inputAdminPassword)){
                        Intent i=new Intent(getApplicationContext(),SettingActivity.class);
                        startActivity(i);
                        passwordRequireDialog.dismiss();
                    }else{
                        showMessage(error_message,"Invalid Password!");
                    }
                }
            }
        });
    }

    private void showServerPropertyDialog(){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dialog_server_property, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);
        final EditText etIAddress=(EditText)view.findViewById(R.id.etIPAddress);
        final EditText etUser=(EditText)view.findViewById(R.id.etServerUser);
        final EditText etPassword=(EditText)view.findViewById(R.id.etServerPassword);
        final EditText etDatabaseName=(EditText)view.findViewById(R.id.etDatabaseName);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);

        dialog.setCancelable(true);
        final AlertDialog serverPropertyDialog=dialog.create();
        serverPropertyDialog.show();

        Cursor cur=db.getIPSetting();
        if(cur.moveToNext()){
            etIAddress.setText(cur.getString(0));
            etUser.setText(cur.getString(1));
            etPassword.setText(cur.getString(2));
            etDatabaseName.setText(cur.getString(3));
        }
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                String ip=etIAddress.getText().toString();
                if(ip.length()==0){
                    showMessage(info_message,"Enter IP Address!");
                    return;
                }
                String dbname=etDatabaseName.getText().toString();
                if(dbname.length()==0){
                    showMessage(info_message,"Enter Database Name!");
                    return;
                }
                String user=etUser.getText().toString();
                if(user.length()==0){
                    showMessage(info_message,"Enter User!");
                    return;
                }
                String password=etPassword.getText().toString();
                if(password.length()==0){
                    showMessage(info_message,"Enter Password!");
                    return;
                }
                db.deleteIPSetting();
                db.insertIPSetting(ip, user, password,dbname);
                showMessage(success_message,"Success!");
                serverPropertyDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                serverPropertyDialog.dismiss();
            }
        });
    }

    private void showUpdateDataDialog(){
        int allowTasteMulti=0;
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dialog_update_data, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        final TextView tvLabelChooseUpdateTable=(TextView)view.findViewById(R.id.tvLabelChooseUpdateTable);
        final CheckBox chkAll=(CheckBox)view.findViewById(R.id.chkAll);
        final CheckBox chkWaiter=(CheckBox)view.findViewById(R.id.chkWaiter);
        final CheckBox chkTable=(CheckBox)view.findViewById(R.id.chkTable);
        final CheckBox chkTableType=(CheckBox)view.findViewById(R.id.chkTableType);
        final CheckBox chkItem=(CheckBox)view.findViewById(R.id.chkItem);
        final CheckBox chkSubmenu=(CheckBox)view.findViewById(R.id.chkSubmenu);
        final CheckBox chkMainmenu=(CheckBox)view.findViewById(R.id.chkMainmenu);
        final CheckBox chkTaste=(CheckBox)view.findViewById(R.id.chkTaste);
        final CheckBox chkTasteMulti=(CheckBox)view.findViewById(R.id.chkTasteMulti);
        final CheckBox chkSystemsetting=(CheckBox)view.findViewById(R.id.chkSystemsetting);
        final CheckBox chkFeaturesetting=(CheckBox)view.findViewById(R.id.chkFeatureSetting);
        final CheckBox chkPrinterSetting=(CheckBox)view.findViewById(R.id.chkPrinterSetting);
        final CheckBox chkSType=(CheckBox)view.findViewById(R.id.chkSType);
        final CheckBox chkSlipFormat=(CheckBox)view.findViewById(R.id.chkSlipFormat);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
        final Button btnOk=(Button)view.findViewById(R.id.btnOk);

        Cursor cur_taste_multi=db.getUseTasteMultiFeature();
        if(cur_taste_multi.moveToFirst())allowTasteMulti=cur_taste_multi.getInt(0);
        if(allowTasteMulti==1){
            chkTasteMulti.setVisibility(View.VISIBLE);
            chkTaste.setVisibility(View.GONE);
        }else{
            chkTasteMulti.setVisibility(View.GONE);
            chkTaste.setVisibility(View.VISIBLE);
        }

        dialog.setCancelable(true);
        final AlertDialog updateDialog=dialog.create();
        updateDialog.show();

        chkAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(chkAll.isChecked()){
                    chkWaiter.setChecked(true);
                    chkTable.setChecked(true);
                    chkTableType.setChecked(true);
                    chkItem.setChecked(true);
                    chkSubmenu.setChecked(true);
                    chkMainmenu.setChecked(true);
                    chkTaste.setChecked(true);
                    chkTasteMulti.setChecked(true);
                    chkSystemsetting.setChecked(true);
                    chkFeaturesetting.setChecked(true);
                    chkPrinterSetting.setChecked(true);
                    chkSType.setChecked(true);
                    chkSlipFormat.setChecked(true);
                }else{
                    chkWaiter.setChecked(false);
                    chkTable.setChecked(false);
                    chkTableType.setChecked(false);
                    chkItem.setChecked(false);
                    chkSubmenu.setChecked(false);
                    chkMainmenu.setChecked(false);
                    chkTaste.setChecked(false);
                    chkTasteMulti.setChecked(false);
                    chkSystemsetting.setChecked(false);
                    chkFeaturesetting.setChecked(false);
                    chkPrinterSetting.setChecked(false);
                    chkSType.setChecked(false);
                    chkSlipFormat.setChecked(false);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                updateDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                display_checked_tables="";
                if(chkWaiter.isChecked())display_checked_tables=display_checked_tables+","+chkWaiter.getText().toString();
                if(chkTable.isChecked())display_checked_tables=display_checked_tables+","+chkTable.getText().toString();
                if(chkTableType.isChecked())display_checked_tables=display_checked_tables+","+chkTableType.getText().toString();
                if(chkItem.isChecked())display_checked_tables=display_checked_tables+","+chkItem.getText().toString();
                if(chkSubmenu.isChecked())display_checked_tables=display_checked_tables+","+chkSubmenu.getText().toString();
                if(chkMainmenu.isChecked())display_checked_tables=display_checked_tables+","+chkMainmenu.getText().toString();
                if(chkTaste.isChecked())display_checked_tables=display_checked_tables+","+chkTaste.getText().toString();
                if(chkTasteMulti.isChecked())display_checked_tables=display_checked_tables+","+chkTasteMulti.getText().toString();
                if(chkSystemsetting.isChecked())display_checked_tables=display_checked_tables+","+chkSystemsetting.getText().toString();
                if(chkFeaturesetting.isChecked())display_checked_tables=display_checked_tables+","+chkFeaturesetting.getText().toString();
                if(chkPrinterSetting.isChecked())display_checked_tables=display_checked_tables+","+chkPrinterSetting.getText().toString();
                if(chkSType.isChecked())display_checked_tables=display_checked_tables+","+chkSType.getText().toString();
                if(chkSlipFormat.isChecked())display_checked_tables=display_checked_tables+","+chkSlipFormat.getText().toString();
                if(display_checked_tables!=""){
                    updateDialog.dismiss();
                    UpdateData updatedata=new UpdateData();
                    updatedata.execute("");
                }else{
                    showMessage(info_message,"Choose Table!");
                }
            }
        });
    }

    private void showUserPasswordDialog(){
        LayoutInflater reg=LayoutInflater.from(context);
        View passwordView=reg.inflate(R.layout.dialog_password, null);
        android.app.AlertDialog.Builder passwordDialog=new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etUserPassword=(EditText)passwordView.findViewById(R.id.etPassword);
        final Button btnCancel=(Button)passwordView.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)passwordView.findViewById(R.id.btnOK);

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
                String inputUserPassword=etUserPassword.getText().toString();
                if(inputUserPassword.length()==0){
                    showMessage(info_message,"Enter Password!");
                    return;
                }
                Cursor cur=db.getUserPassword();
                if(cur.getCount()!=0){
                    cur.moveToFirst();
                    String userPassword=cur.getString(0);
                    if(userPassword.equals(inputUserPassword)){
                        showServerPropertyDialog();
                        passwordRequireDialog.dismiss();
                    }else{
                        showMessage(error_message,"Invalid Password!");
                    }
                }
            }
        });
    }

    private void setLayoutResource(){
        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnUpdateData=(Button)findViewById(R.id.btnUpdateData);
        btnExit=(Button)findViewById(R.id.btnExit);
        spUserName=(Spinner)findViewById(R.id.spUserName);
        etPassword=(EditText) findViewById(R.id.txtPassword);
        tvTitleLogin=(TextView) findViewById(R.id.tvTitleLogin);
        btnSetting=(Button)findViewById(R.id.btnSetting);

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
