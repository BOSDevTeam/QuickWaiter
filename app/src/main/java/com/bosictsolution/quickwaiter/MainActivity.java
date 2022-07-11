package com.bosictsolution.quickwaiter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;
import com.andprn.request.android.RequestHandler;

import adapter.DialogTasteGridAdapter;
import adapter.DialogTasteMultiGridAdapter;
import adapter.ItemListAdapter;
import adapter.MenuExpandableListAdapter;
import adapter.OrderListAdapter;
import adapter.SaleItemSubRvAdapter;
import common.DBHelper;
import common.ServerConnection;
import data.ItemData;
import data.ItemSubGroupData;
import data.MainMenuData;
import data.STypeData;
import data.SubMenuData;
import data.TasteData;
import data.TasteMultiData;
import data.TransactionData;
import listener.DialogTasteClickListener;
import listener.DialogTasteMultiClickListener;
import listener.OrderButtonClickListener;

public class MainActivity extends AppCompatActivity implements DialogTasteClickListener, OrderButtonClickListener, DialogTasteMultiClickListener {

    ServerConnection serverconnection;
    DBHelper db;

    private ProgressDialog progDialog;
    final Context context = this;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Calendar cCalendar;

    MenuExpandableListAdapter expListAdapter;
    OrderListAdapter orderListAdapter;
    DialogTasteGridAdapter dialogTasteGridAdapter;
    DialogTasteMultiGridAdapter dialogTasteMultiGridAdapter;
    ItemListAdapter itemListAdapter;

    private static final String TAG = "WiFiConnectMenu";
    private PrinterWiFiPort wifiPort;
    private String printerIPAddress;
    private Thread hThread;
    String ioException;
    private ESCPOSPrinter posPtr;

    static TextView tvTaste,tvTasteMulti,tvTastePrice;
    ExpandableListView expList;
    ListView lvItem, lvOrder;
    TextView tvSubMenuName, tvTableName,tvShowTaste,tvShowTastePrice;
    static TextView tvWaiterName;
    Button btnChooseTable,btnSendOrder;
    TextView tvPrintLabelTable,tvPrintLabelWaiter,tvPrintDate,tvPrintTable,tvPrintWaiter,tvPrintTime,tvPrintHeaderItem,tvPrintHeaderQty,tvTable;
    LinearLayout layoutPrintList;
    ScrollView layoutPrint;
    ImageButton btnBarcode;

    List<String> listDataHeader;
    HashMap<String,List<String>> listDataChild;
    List<SubMenuData> lstSubMenuData=new ArrayList<>();
    List<MainMenuData> lstMainMenuData=new ArrayList<>();
    List<ItemData> lstItemData=new ArrayList<>();
    List<TasteData> lstTaste=new ArrayList();
    List<STypeData> lstSType=new ArrayList();
    List<TasteMultiData> lstTasteMulti=new ArrayList();
    List<TransactionData> lstOrderItem=new ArrayList<>();

    static int taste_position;
    public static int choosed_table_id;
    public static String choosed_table_name,start_time;
    public static boolean isEmptyTable;
    int waiter_id;
    String waiter_name,insert_order;
    static boolean isTasteEdit;
    int warning_message=1,error_message=2,success_message=3,info_message=4;
    int orderWaiterID,orderTableID,allowOrderTime,allowAutoTaste,allowCustomerInfo,allowBookingTable,allowOutOfOrder,allowNotPairItemNameAndTaste,allowUseFixedLayout,allowTasteMulti,allowBarcode,allowItemDiscount;
    String orderWaiterName,orderTableName,currentTime="",currentDate,printer_message="";
    private static final String TIME_FORMAT="hh:mm a";
    private static final String DATE_FORMAT="yyyy-MM-dd";
    Configuration config;
    int selectedGroupPosition=-1;
    public static List<TransactionData> lstOrder;
    SaleItemSubRvAdapter saleItemSubRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=new DBHelper(this);
        serverconnection=new ServerConnection();

        Cursor cur = db.getFullLayoutSetting();
        if (cur.moveToFirst()) allowUseFixedLayout = cur.getInt(0);
        if(allowUseFixedLayout==0) {
            config = getResources().getConfiguration();
            if (config.smallestScreenWidthDp >= 600) {
                setContentView(R.layout.activity_main);
                ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayShowCustomEnabled(true);
                actionbar.setDisplayShowTitleEnabled(false);
            } else {
                setContentView(R.layout.activity_main);
                drawerLayout = (DrawerLayout) findViewById(R.id.sliderLayout);
                ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setDisplayShowCustomEnabled(true);
                actionbar.setDisplayShowTitleEnabled(false);
            }
        }else{
            setContentView(R.layout.activity_main);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayShowCustomEnabled(true);
            actionbar.setDisplayShowTitleEnabled(false);
        }

        setLayoutResource();
        //setPrintLayoutResource();

        Intent intent=getIntent();
        waiter_id=intent.getIntExtra("waiterid", 0);
        waiter_name=intent.getStringExtra("waitername");
        tvWaiterName.setText(waiter_name);
        tvWaiterName.setTag(waiter_id);

        //getAllowedMainMenu();
        //getAllSubMenu();
        //setMenuToExpList();
        setEmptyItem();
        getAllTaste();
        getAllSType();

        Cursor cur_allow_bill=db.getBillPrintSetting();
        if(cur_allow_bill.moveToFirst()){
            if(cur_allow_bill.getInt(0)==1)connectPrinter();
            else setTitle("");
        }

        expList.setOnChildClickListener(new OnChildClickListener(){
            @Override
            public boolean onChildClick(ExpandableListView parent,View view,int groupPosition,int childPosition,long id){
                selectedGroupPosition=groupPosition;
                String subMenuName= listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                tvSubMenuName.setText(subMenuName);
                int mainMenuID=lstMainMenuData.get(groupPosition).getMainMenuID();
                List<Integer> lstSubMenuID= new ArrayList<>();
                for(int i=0;i<lstSubMenuData.size();i++){
                    if(lstSubMenuData.get(i).mainMenuID==mainMenuID){
                        lstSubMenuID.add(lstSubMenuData.get(i).subMenuID);
                    }
                }
                int subMenuID=lstSubMenuID.get(childPosition);
                getItemBySubMenuID(subMenuID);
                if(drawerLayout!=null)drawerLayout.closeDrawer(expList);
                return false;
            }
        });

        if(drawerLayout!=null){
            actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.mipmap.menu,R.string.app_name,R.string.app_name){
                @SuppressLint("RestrictedApi")
                public void onDrawerClosed(View view){
                    invalidateOptionsMenu();
                }
            };
        }

        if(drawerLayout!=null){
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            if(savedInstanceState==null){
            }
        }

        lvItem.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id){
                if(saleItemSubRvAdapter != null)saleItemSubRvAdapter.lstItemSubGroup=new ArrayList<>();
                if(lstItemData.size()==0)return;
                int outOfOrder=lstItemData.get(position).getOutOfOrder();
                //int incomeid=lstItemData.get(position).getIncomeid();
                if(outOfOrder==1){
                    showMessage(warning_message,"Out of Order Item!");
                    return;
                }else{
                    String itemId=lstItemData.get(position).getItemid();
                    List<ItemSubGroupData> lstItemSubGroupData=db.getItemSubByItemID(itemId);
                    if(lstItemSubGroupData.size()!=0){
                        showItemSubDialog(lstItemSubGroupData,position);
                    }else {
                        isShowTasteAndPlaceOrder(position);
                    }
                }
            }
        });

        btnChooseTable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                chooseTable();
            }
        });

        btnSendOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(choosed_table_id!=0) {
                    Cursor cur = db.getOrderTimeFeature();
                    if (cur.moveToFirst()) allowOrderTime = cur.getInt(0);
                    if (allowOrderTime == 1) getCurrentTime();
                    Cursor cur2=db.getNotPairItemNameAndTasteFeature();
                    if(cur2.moveToFirst())allowNotPairItemNameAndTaste=cur2.getInt(0);
                    if (lstOrderItem.size() != 0) {
                        orderWaiterID = Integer.parseInt(tvWaiterName.getTag().toString());
                        orderWaiterName = tvWaiterName.getText().toString();
                        orderTableID = Integer.parseInt(tvTableName.getTag().toString());
                        orderTableName = tvTableName.getText().toString();
                        SendOrder sendOrder = new SendOrder();
                        sendOrder.execute();
                    }
                    /**if(lstOrderItem.size()!=0){
                        preparePrintBySType();
                    }**/
                }else{
                    showMessage(info_message,"Choose Table!");
                }
            }
        });

        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getAllowedMainMenu();
        getAllSubMenu();
        setMenuToExpList();
        if(selectedGroupPosition!=-1) {
            expList.expandGroup(selectedGroupPosition);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                showDetailDialog(contents);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    //when using the ActionBarDrawerToggle, must call it during onPostCreate() and onConfigurationChanged()
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        if(actionBarDrawerToggle!=null){
            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(actionBarDrawerToggle!=null){
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        if(choosed_table_id!=0){
            tvTableName.setTag(choosed_table_id);
        }
        if(choosed_table_name!=""){
            tvTableName.setText(choosed_table_name);
        }
    }

    @Override
    public void onBackPressed(){
        showMessage(info_message,"Please Logout");
    }

    @Override
    public void setTitle(CharSequence title){
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi=inflater.inflate(R.layout.action_bar_main, null);
        ImageButton btnRefresh=(ImageButton)vi.findViewById(R.id.btnRefresh);
        ImageButton btnDiscount=(ImageButton)vi.findViewById(R.id.btnDiscount);
        ImageButton btnViewOrder=(ImageButton)vi.findViewById(R.id.btnViewOrder);
        ImageButton btnGetBill=(ImageButton)vi.findViewById(R.id.btnGetBill);
        ImageButton btnCustomerEntry=(ImageButton)vi.findViewById(R.id.btnCustomerEntry);
        ImageButton btnCustomerList=(ImageButton)vi.findViewById(R.id.btnCustomerList);
        ImageButton btnBooking=(ImageButton)vi.findViewById(R.id.btnBooking);
        TextView tvShopName=(TextView)vi.findViewById(R.id.tvShopName);
        TextView tvPrinterMessage=(TextView)vi.findViewById(R.id.tvPrinterMessage);
        Button btnConnect=(Button)vi.findViewById(R.id.btnConnect);

        if(config!=null) {
            if (config.smallestScreenWidthDp >= 600) {
                tvShopName.setVisibility(View.VISIBLE);
            } else {
                tvShopName.setVisibility(View.GONE);
            }
        }

        btnConnect.setVisibility(View.GONE);
        tvPrinterMessage.setVisibility(View.GONE);

        tvPrinterMessage.setText(printer_message);
        if(printer_message.equals("PRINTER ONLINE!")){
            tvPrinterMessage.setTextColor(getResources().getColor(R.color.colorGray));
            btnConnect.setVisibility(View.INVISIBLE);
            tvPrinterMessage.setVisibility(View.VISIBLE);
        }
        else  if(printer_message.equals("PRINTER OFFLINE!")){
            tvPrinterMessage.setTextColor(getResources().getColor(R.color.colorApp));
            btnConnect.setVisibility(View.VISIBLE);
            tvPrinterMessage.setVisibility(View.VISIBLE);
        }

        Cursor cur_shop_name=db.getShopName();
        if(cur_shop_name.moveToFirst())tvShopName.setText(cur_shop_name.getString(0));
        Cursor cur_cus_info=db.getCustomerInfoFeature();
        if(cur_cus_info.moveToFirst())allowCustomerInfo=cur_cus_info.getInt(0);
        if(allowCustomerInfo==1) {
            btnCustomerEntry.setVisibility(View.VISIBLE);
            btnCustomerList.setVisibility(View.VISIBLE);
        }
        else {
            btnCustomerEntry.setVisibility(View.GONE);
            btnCustomerList.setVisibility(View.GONE);
        }
        Cursor cur_book_table=db.getBookingTableFeature();
        if(cur_book_table.moveToFirst())allowBookingTable=cur_book_table.getInt(0);
        if(allowBookingTable==1)btnBooking.setVisibility(View.VISIBLE);
        else btnBooking.setVisibility(View.GONE);
        Cursor cur_out_of_order=db.getOutOfOrderFeature();
        if(cur_out_of_order.moveToFirst())allowOutOfOrder=cur_out_of_order.getInt(0);
        if(allowOutOfOrder==1)btnRefresh.setVisibility(View.VISIBLE);
        else btnRefresh.setVisibility(View.GONE);
        Cursor cur_item_dis=db.getItemDiscountFeature();
        if(cur_item_dis.moveToFirst())allowItemDiscount=cur_item_dis.getInt(0);
        if(allowItemDiscount==1)btnDiscount.setVisibility(View.VISIBLE);
        else btnDiscount.setVisibility(View.GONE);

        ActionBar.LayoutParams params=new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT);
        getSupportActionBar().setCustomView(vi, params);

        btnRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                RefreshAllItem refresh=new RefreshAllItem();
                refresh.execute("");
            }
        });
        btnDiscount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                UpdateDiscountItem refresh=new UpdateDiscountItem();
                refresh.execute("");
            }
        });
        btnViewOrder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionViewOrder();
            }
        });
        btnGetBill.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionBill();
            }
        });
        btnCustomerEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionCustomerEntry();
            }
        });
        btnCustomerList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionCustomerList();
            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionBooking();
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                connectPrinter();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        int isAllow=0;
        super.onCreateOptionsMenu(menu);
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_main,menu);

        Cursor curWaiter=db.getOpenOrderWaiterSetting();
        if(curWaiter.moveToFirst())isAllow=curWaiter.getInt(0);
        if(isAllow==1)menu.getItem(0).setVisible(true);
        else menu.getItem(0).setVisible(false);

        Cursor curKitchen=db.getOpenOrderKitchenSetting();
        if(curKitchen.moveToFirst())isAllow=curKitchen.getInt(0);
        if(isAllow==1)menu.getItem(1).setVisible(true);
        else menu.getItem(1).setVisible(false);

        Cursor curChangeTable=db.getChangeTableSetting();
        if(curChangeTable.moveToFirst())isAllow=curChangeTable.getInt(0);
        if(isAllow==1)menu.getItem(2).setVisible(true);
        else menu.getItem(2).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle!=null){
            if(actionBarDrawerToggle.onOptionsItemSelected(item)){
                return true;
            }
        }
        int itemId = item.getItemId();
        if (itemId == R.id.menuLogout) {
            lstOrderItem=new ArrayList<>();
            clearChoosedTable();
            try {wifiDisConn();
            }catch (IOException e){
            }catch (InterruptedException ie){
            }
            finish();
            return true;
        } else if (itemId == R.id.menuServerProperty) {
            showUserPasswordDialog();
        }else if (itemId == R.id.menuOpenOrderWaiter) {
            Intent i=new Intent(this,OpenOrderWaiterActivity.class);
            startActivity(i);
        }else if (itemId == R.id.menuOpenOrderKitchen) {
            Intent i=new Intent(this,OpenOrderKitchenActivity.class);
            startActivity(i);

        }else if (itemId == R.id.menuChangeTable) {

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Async classes
     */

    public class RefreshAllItem extends AsyncTask<String,String,String> {
        String msg="";
        boolean isSuccess=false;
        int msg_type;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    isSuccess=false;
                    msg_type=error_message;
                }else{
                    int outoforder;
                    String itemid;
                    //db.truncateItem();
                    String select_item="select ItemID,OutofOrder from InvItem";
                    Statement st_item=con.createStatement();
                    ResultSet rs_item=st_item.executeQuery(select_item);

                    while(rs_item.next()){
                        itemid=rs_item.getString(1);
                        outoforder=rs_item.getInt(2);
                        db.updateOutOfOrderItem(itemid,outoforder);
                    }

                    msg="Success!";
                    isSuccess=true;
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
            progDialog.show();
            progDialog.setMessage("Refreshing All Items!");
        }
        @Override
        protected void onPostExecute(String r){
            progDialog.hide();
            showMessage(msg_type,r);
            if(isSuccess){
                setEmptyItem();
            }
        }
    }

    public class UpdateDiscountItem extends AsyncTask<String,String,String> {
        String msg="";
        boolean isSuccess=false;
        int msg_type;
        @Override
        protected String doInBackground(String... params){
            try{
                Connection con=serverconnection.CONN();
                if(con==null){
                    msg="Error in connection with SQL server";
                    isSuccess=false;
                    msg_type=error_message;
                }else{
                    int noDis;
                    String itemid;
                    //db.truncateItem();
                    String select_item="select ItemID,NoDis from InvItem";
                    Statement st_item=con.createStatement();
                    ResultSet rs_item=st_item.executeQuery(select_item);

                    while(rs_item.next()){
                        itemid=rs_item.getString(1);
                        noDis=rs_item.getInt(2);
                        db.updateDiscountItem(itemid,noDis);
                    }

                    msg="Success!";
                    isSuccess=true;
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
            progDialog.show();
            progDialog.setMessage("Refreshing All Items!");
        }
        @Override
        protected void onPostExecute(String r){
            progDialog.hide();
            showMessage(msg_type,r);
            if(isSuccess){
                setEmptyItem();
            }
        }
    }

    public class SendOrder extends AsyncTask<String,String,String> {
        String msg = "", orderItemID, orderItemName, orderAllTaste = "", orderItemNameTaste, insert_multi_taste,orderMultiTaste;
        boolean isSuccess = false;
        int msg_type, tranid, orderSysID, orderCounterID, orderSType, orderIntQuantity, orderNoDis, orderItemDis, orderPNumber, allowPrint;
        float floatQty, orderFloatQuantity;
        double orderAmount, orderPrice;

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                    isSuccess = false;
                    msg_type = error_message;
                } else {
                    String exec_proc = "exec GetTranIDTablet @TableID=" + orderTableID + ",@Waiter='" + orderWaiterName + "',@WaiterID=" + orderWaiterID;
                    Statement st_execproc = con.createStatement();
                    st_execproc.executeQuery(exec_proc);

                    String select_existtranid = "select Tranid from InvMasterSaleTemp where TableNameID=" + orderTableID;
                    Statement st_existtranid = con.createStatement();
                    ResultSet rs_existtranid = st_existtranid.executeQuery(select_existtranid);
                    if (rs_existtranid.next()) {
                        tranid = rs_existtranid.getInt(1);
                    }

                    String select_autosend = "SELECT TableID FROM autosend WHERE TableID=" + orderTableID;
                    Statement st_select = con.createStatement();
                    st_select.execute(select_autosend);
                    ResultSet rs = st_select.executeQuery(select_autosend);
                    if (rs.next()) {
                        String update_autosend = "UPDATE autosend SET TableID=" + orderTableID + ",TableName='" + orderTableName + "' WHERE TableID=" + orderTableID;
                        Statement st_update = con.createStatement();
                        st_update.execute(update_autosend);
                    } else {
                        String insert_autosend = "INSERT INTO autosend (TableID,TableName) Values (" + orderTableID + ",'" + orderTableName + "')";
                        Statement st_insert = con.createStatement();
                        st_insert.execute(insert_autosend);
                    }

                    if(db.allowStartTimeSetting() && isEmptyTable==true){
                        String update_mastersale = "UPDATE InvMasterSaleTemp SET StartTime='" + start_time + "' WHERE Tranid=" + tranid;
                        Statement st_update_mastersale = con.createStatement();
                        st_update_mastersale.execute(update_mastersale);
                    }

                    insert_order = "";
                    insert_multi_taste = "";
                    for (int i = 0; i < lstOrderItem.size(); i++) {
                        orderSysID = lstOrderItem.get(i).getSysid();
                        orderCounterID = lstOrderItem.get(i).getCounterID();
                        orderItemID = lstOrderItem.get(i).getItemid();
                        orderItemName = lstOrderItem.get(i).getItemName() + " " + lstOrderItem.get(i).getAllItemSub();
                        if (lstOrderItem.get(i).getTaste().length() != 0 && lstOrderItem.get(i).getTasteMulti().length() != 0)
                            orderAllTaste = lstOrderItem.get(i).getTaste() + "," + lstOrderItem.get(i).getTasteMulti();
                        else if (lstOrderItem.get(i).getTaste().length() != 0 && lstOrderItem.get(i).getTasteMulti().length() == 0)
                            orderAllTaste = lstOrderItem.get(i).getTaste();
                        else if (lstOrderItem.get(i).getTaste().length() == 0 && lstOrderItem.get(i).getTasteMulti().length() != 0)
                            orderAllTaste = lstOrderItem.get(i).getTasteMulti();
                        else orderAllTaste = "";
                        if (allowNotPairItemNameAndTaste == 0) {
                            if (orderAllTaste.length() != 0) {
                                orderItemNameTaste = lstOrderItem.get(i).getItemName() + "(" + orderAllTaste + ")";
                                orderAllTaste="";
                            } else {
                                orderItemNameTaste = lstOrderItem.get(i).getItemName();
                            }
                        } else {
                            orderItemNameTaste = lstOrderItem.get(i).getItemName();
                        }
                        orderMultiTaste=lstOrderItem.get(i).getTasteMulti();
                        orderPrice = lstOrderItem.get(i).getSalePrice() + lstOrderItem.get(i).getTastePrice();
                        orderSType = lstOrderItem.get(i).getStype();
                        orderNoDis = lstOrderItem.get(i).getNoDis();
                        orderItemDis = lstOrderItem.get(i).getItemDis();
                        orderPNumber = lstOrderItem.get(i).getpNumber();
                        floatQty = Float.parseFloat(lstOrderItem.get(i).getStringQty());
                        if (floatQty == Math.round(floatQty)) {
                            orderIntQuantity = Integer.parseInt(lstOrderItem.get(i).getStringQty());
                            orderAmount = orderPrice * orderIntQuantity;
                            String sql_query = "INSERT INTO InvTranSaleTemp(Tranid,SysID,TableID,TBName,WaiterID,Waitername,ItemID,ItemName,Name,Tastes,Qty,SalePrice,Amount,UnitQty,Remark,UnitName,FoodDis,Stype,Status,ItemNDis,ItemDis,PNumber,MultiTaste) VALUES ";
                            String sql_data = tranid + "," + orderSysID + "," + orderTableID + ",'" + orderTableName + "'," + orderWaiterID + ",'" + orderWaiterName + "'," + orderItemID + ",N'" + orderItemName + "',N'" + orderItemNameTaste + "','" + orderAllTaste + "'," + orderIntQuantity + "," + orderPrice + "," + orderAmount + "," + orderIntQuantity + ",'" + orderTableName + "','" + currentTime + "'," + orderCounterID + "," + orderSType + "," + 0 + "," + orderNoDis + "," + orderItemDis + "," + orderPNumber + ",'" + orderMultiTaste + "'";
                            insert_order = insert_order + " " + sql_query + "(" + sql_data + ")";
                        } else {
                            orderFloatQuantity = floatQty;
                            orderAmount = orderPrice * orderFloatQuantity;
                            String sql_query = "INSERT INTO InvTranSaleTemp(Tranid,SysID,TableID,TBName,WaiterID,Waitername,ItemID,ItemName,Name,Tastes,Qty,SalePrice,Amount,UnitQty,Remark,UnitName,FoodDis,Stype,Status,ItemNDis,ItemDis,PNumber,MultiTaste) VALUES ";
                            String sql_data = tranid + "," + orderSysID + "," + orderTableID + ",'" + orderTableName + "'," + orderWaiterID + ",'" + orderWaiterName + "'," + orderItemID + ",N'" + orderItemName + "',N'" + orderItemNameTaste + "','" + orderAllTaste + "'," + orderFloatQuantity + "," + orderPrice + "," + orderAmount + "," + orderFloatQuantity + ",'" + orderTableName + "','" + currentTime + "'," + orderCounterID + "," + orderSType + "," + 0 + "," + orderNoDis + "," + orderItemDis + "," + orderPNumber+ ",'" + orderMultiTaste + "'";
                            insert_order = insert_order + " " + sql_query + "(" + sql_data + ")";
                        }

                        // for insert taste multi
                        /*if (lstOrderItem.get(i).getTasteMulti().length() != 0) {
                            String sql_query = "INSERT INTO ConnectMultiTasteTemp(Tranid,ItemID,ItemName,SysID,TasteName,TasteQty) VALUES ";
                            String sql_data = tranid + "," + orderItemID + ",N'" + orderItemName + "'," + orderSysID + ",N'" + lstOrderItem.get(i).getTasteMulti() + "'," + orderIntQuantity;
                            insert_multi_taste = insert_multi_taste + " " + sql_query + "(" + sql_data + ")";
                        }*/
                    }
                    Statement st = con.createStatement();
                    st.execute(insert_order);

                    //if (insert_multi_taste.length() != 0) st.execute(insert_multi_taste);

                    // for booking tableid delete
                    String sql_query_booking_table = "select * from Booking where Deleted=0 and TableID=" + orderTableID;
                    Statement st_booking_table = con.createStatement();
                    ResultSet rs_booking_table = st_booking_table.executeQuery(sql_query_booking_table);
                    if (rs_booking_table.next()) {
                        String sql_query_booking_delete = "update Booking set Deleted=1 where TableID=" + orderTableID;
                        Statement st_booking_delete = con.createStatement();
                        st_booking_delete.execute(sql_query_booking_delete);
                    }

                    msg = orderTableName + " Order Sent!";
                    isSuccess = true;
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
            progDialog.show();
            progDialog.setMessage("Sending....");
        }

        @Override
        protected void onPostExecute(String r) {
            progDialog.hide();
            showMessage(msg_type, r);
            if (isSuccess) {
                Cursor cur = db.getPrintOrderSetting();
                if (cur.moveToFirst()) allowPrint = cur.getInt(0);
                if (allowPrint == 1) printOrder();
                clearOrder();
            }
        }
    }

    /**
     * Printer methods
     */

    private void preparePrintBySType(){
        List<TransactionData> lstTransaction=new ArrayList<>();
        for(int i=0;i<lstSType.size();i++){
            for(int j=0;j<lstOrderItem.size();j++){
                if(lstSType.get(i).getStypeid()==lstOrderItem.get(j).getStype()){
                    TransactionData data=new TransactionData();
                    data.setItemName(lstOrderItem.get(j).getItemName());
                    data.setStringQty(lstOrderItem.get(j).getStringQty());
                    lstTransaction.add(data);
                }
                if(lstOrderItem.size()-1==j){
                    if(lstTransaction.size()!=0) {
                        connectPrinter(lstSType.get(i).getStypeid());
                        setupPrintOrderList(lstTransaction);
                        lstTransaction = new ArrayList<>();
                        /**try {
                            wifiDisConn();
                        }catch (IOException e){

                        }catch (InterruptedException ie){

                        }**/
                    }
                }
            }
        }
        clearOrder();
    }

    private void changePrintLayoutToBitmap(){
        Bitmap bitmap=Bitmap.createBitmap(layoutPrint.getWidth(), layoutPrint.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        layoutPrint.draw(canvas);
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

    private void setupPrintOrderList(List<TransactionData> lstOrder){
        float floatQty;
        tvPrintTable.setText(orderTableName);
        tvPrintWaiter.setText(orderWaiterName);
        tvPrintDate.setText(getCurrentDate());
        tvPrintTime.setText(getCurrentTime());
        for (int i=0; i<lstOrder.size(); i++) {
            TransactionData data = lstOrder.get(i);
            LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_print_order, null);
            TextView tvPrintItemName =(TextView) row.findViewById(R.id.tvPrintItemName);
            TextView tvPrintQty =(TextView) row.findViewById(R.id.tvPrintQty);

            tvPrintItemName.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));
            tvPrintQty.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/BOS-PETITE.TTF"));

            tvPrintItemName.setText(data.getItemName());
            floatQty = Float.parseFloat(data.getStringQty());
            if(floatQty==Math.round(floatQty)){
                tvPrintQty.setText(String.valueOf(data.getIntegerQty()));
            }else{
                tvPrintQty.setText(String.valueOf(data.getFloatQty()));
            }
            layoutPrintList.addView(row);
        }
        changePrintLayoutToBitmap();
    }

    public void print(Context context) throws IOException {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/WaiterOneDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"print.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
    }

    private void connectPrinter(int stype){
        Cursor cur=db.getPrinterIPBySType(stype);
        if(cur.moveToFirst()){
            printerIPAddress =cur.getString(0);
        }
        wifiPort = PrinterWiFiPort.getInstance();
        try{
            wifiConn(printerIPAddress);
        }
        catch (IOException e)
        {
            Log.e(TAG,e.getMessage(),e);
        }
    }

    private void connectPrinter(){
        Cursor cur=db.getBillPrinter();
        if(cur.moveToFirst()){
            printerIPAddress =cur.getString(0);
        }
        wifiPort = PrinterWiFiPort.getInstance();
        try{
            wifiConn(printerIPAddress);
        }
        catch (IOException e)
        {
            Log.e(TAG,e.getMessage(),e);
        }
    }

    private void wifiConn(String ipAddr) throws IOException {
        new connTask().execute(ipAddr);
    }

    private void wifiDisConn() throws IOException, InterruptedException {
        if(wifiPort!=null) {
            wifiPort.disconnect();
            if (hThread != null) hThread.interrupt();
            Toast toast = Toast.makeText(context, "Disconnected!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class connTask extends AsyncTask<String, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()
        {
            dialog.setTitle("Printer Connect");
            dialog.setMessage("Connecting");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {
                wifiPort.connect(params[0]);
                printerIPAddress = params[0];
                retVal = new Integer(0);
            }
            catch (IOException e)
            {
                Log.e(TAG,e.getMessage(),e);
                retVal = new Integer(-1);
                ioException=e.getMessage();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(result.intValue() == 0)
            {
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();
                if(dialog.isShowing())
                    dialog.dismiss();
                printer_message="PRINTER ONLINE!";
                setTitle("");
            }
            else
            {
                if(dialog.isShowing())
                    dialog.dismiss();
                printer_message="PRINTER OFFLINE!";
                setTitle("");
                AlertView.showAlert("Failed", "Check Devices!"+ioException, context);
            }
            super.onPostExecute(result);
        }
    }

    /**
     * Interface listener methods
     */

    @Override
    public void onPlusButtonClickListener(int position,EditText editText){
        float floatQty = Float.parseFloat(editText.getText().toString());
        int integerQty;
        if(floatQty==Math.round(floatQty)){
            integerQty=Integer.parseInt(editText.getText().toString());
            integerQty=integerQty+1;
            if(integerQty<1)
                integerQty=1;
            editText.setText(String.valueOf(integerQty));
            lstOrderItem.get(position).setStringQty(String.valueOf(integerQty));
            lstOrderItem.get(position).setIntegerQty(integerQty);
        }else{
            showMessage(info_message,"Change quantity on clicking calculator!");
        }
    }

    @Override
    public void onMinusButtonClickListener(int position,EditText editText){
        float floatQty = Float.parseFloat(editText.getText().toString());
        int integerQty;
        if(floatQty==Math.round(floatQty)){
            integerQty=Integer.parseInt(editText.getText().toString());
            integerQty=integerQty-1;
            if(integerQty<1)
                integerQty=1;
            editText.setText(String.valueOf(integerQty));
            lstOrderItem.get(position).setStringQty(String.valueOf(integerQty));
            lstOrderItem.get(position).setIntegerQty(integerQty);
        }else{
            showMessage(info_message,"Change quantity on clicking calculator!");
        }
    }

    @Override
    public void onCalculatorButtonClickListener(int position,EditText editText){
        showCalculatorDialog(position,editText,null,1);
    }

    @Override
    public void onPNumberButtonClickListener(int position,TextView textView){
        showCalculatorDialog(position,null,textView,2);
    }

    @Override
    public void onTasteButtonClickListener(int position,TextView textView) {
        String curTaste = lstOrderItem.get(position).getTaste();
        isTasteEdit = true;
        taste_position = position;
        tvTaste = textView;
        showTasteDialog(position);
        tvShowTaste.setText(curTaste);
    }

    @Override
    public void onTasteMultiButtonClickListener(int position, TextView textView, TextView tvPrice) {
        String curTaste = lstOrderItem.get(position).getTasteMulti();
        int incomeid = lstOrderItem.get(position).getIncomeid();
        double curTastePrice = lstOrderItem.get(position).getTastePrice();
        isTasteEdit = true;
        taste_position = position;
        tvTasteMulti = textView;
        tvTastePrice = tvPrice;
        showTasteMultiDialog(incomeid, position);
        tvShowTaste.setText(curTaste);
        tvShowTastePrice.setText(String.valueOf(curTastePrice));
    }

    @Override
    public void onTasteClickListener(int position) {
        String curTaste = tvShowTaste.getText().toString();
        if (curTaste.length() != 0) tvShowTaste.setText(curTaste +","+ lstTaste.get(position).getTasteName());
        else tvShowTaste.setText(lstTaste.get(position).getTasteName());
    }

    @Override
    public void onTasteMultiClickListener(int position) {
        String curTaste = tvShowTaste.getText().toString();
        double curTastePrice = 0;
        if (tvShowTastePrice.getText().toString().trim().length() != 0) curTastePrice = Double.parseDouble(tvShowTastePrice.getText().toString().trim());
        if (curTaste.length() != 0) {
            tvShowTaste.setText(curTaste + ","+ lstTasteMulti.get(position).getTasteName());
            tvShowTastePrice.setText(String.valueOf(curTastePrice + lstTasteMulti.get(position).getPrice()));
        }else{
            tvShowTaste.setText(lstTasteMulti.get(position).getTasteName());
            tvShowTastePrice.setText(String.valueOf(lstTasteMulti.get(position).getPrice()));
        }
    }

    @Override
    public void onCancelButtonClickListener(int position,View row){
        lstOrderItem.remove(position);
        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);
    }

    /**
     * Methods
     */

    private void chooseTable(){
        Intent intent=new Intent(MainActivity.this,TableActivity.class);
        intent.putExtra("from_waiter_main", true);
        intent.putExtra("from_customer_info", false);
        intent.putExtra("role", "just_choice");
        startActivity(intent);
    }

    private void clearChoosedTable(){
        tvTableName.setTag(null);
        tvTableName.setText("");
        choosed_table_id=0;
        choosed_table_name="";
    }

    private void setEmptyItem(){
        tvSubMenuName.setText("Items");
        List<String> listEmptyItem=new ArrayList<>();
        listEmptyItem.add("No Items!");
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.list_empty,R.id.tvEmpty,listEmptyItem);
        lvItem.setAdapter(adapter);
    }

    private void getAllowedMainMenu(){
        lstMainMenuData=new ArrayList<>();
        Cursor cur=db.getAllowedMainMenu();
        if(cur.getCount()!=0) {
            while (cur.moveToNext()) {
                MainMenuData data=new MainMenuData();
                data.setMainMenuID(cur.getInt(0));
                data.setMainMenuName(cur.getString(1));
                data.setCounterid(cur.getInt(2));
                lstMainMenuData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
    }

    private void getAllSubMenu(){
        Cursor cur=db.getAllSubMenu();
        lstSubMenuData=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                SubMenuData data=new SubMenuData();
                data.setSubMenuID(cur.getInt(0));
                data.setSubMenuName(cur.getString(1));
                data.setMainMenuID(cur.getInt(2));
                data.setMainMenuName(cur.getString(3));
                data.setIncomeid(cur.getInt(4));
                lstSubMenuData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
    }

    private void getAllSType(){
        Cursor cur=db.getAllSType();
        lstSType=new ArrayList<>();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                STypeData data=new STypeData();
                data.setStypeid(cur.getInt(0));
                data.setStypeName(cur.getString(1));
                lstSType.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
        }
    }

    private void setMenuToExpList(){
        listDataHeader=new ArrayList<>();
        listDataChild=new HashMap<>();
        for(int i=0;i<lstMainMenuData.size();i++){
            int mainMenuID=lstMainMenuData.get(i).getMainMenuID();
            String mainMenuName=lstMainMenuData.get(i).getMainMenuName();

            List<String> lstSubMenuName=new ArrayList<>();
            for(int j=0;j<lstSubMenuData.size();j++){
                if(lstSubMenuData.get(j).getMainMenuID()==mainMenuID){
                    lstSubMenuName.add(lstSubMenuData.get(j).getSubMenuName());
                }
            }
            if(lstSubMenuName.size()!=0){
                listDataChild.put(mainMenuName, lstSubMenuName);
                listDataHeader.add(mainMenuName);
            }
        }
        expListAdapter=new MenuExpandableListAdapter(this,listDataHeader,listDataChild);
        expList.setAdapter(expListAdapter);
    }

    private void getItemBySubMenuID(int subMenuID){
        lstItemData=new ArrayList<>();
        Cursor cur=db.getItemBySubMenuID(subMenuID);
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                ItemData data=new ItemData();
                data.setItemid(cur.getString(0));
                data.setItemName(cur.getString(1));
                data.setPrice(cur.getDouble(2));
                data.setSysid(cur.getInt(3));
                data.setCounterID(cur.getInt(4));
                data.setStype(cur.getInt(5));
                data.setOutOfOrder(cur.getInt(6));
                data.setIncomeid(cur.getInt(7));
                data.setNoDis(cur.getInt(8));
                data.setItemDis(cur.getInt(9));
                lstItemData.add(data);
            }
            if(!cur.isClosed()){
                cur.close();
            }
            itemListAdapter=new ItemListAdapter(this,lstItemData);
            lvItem.setAdapter(itemListAdapter);
        }
        else{
            setEmptyItem();
        }
    }

    private void getAllTaste(){
        lstTaste=new ArrayList();
        Cursor cur=db.getAllTaste();
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TasteData data=new TasteData();
                data.setTasteid(cur.getInt(0));
                data.setTasteName(cur.getString(1));
                lstTaste.add(data);
            }
        }
    }

    private void placeOrder(int position,String taste,String tasteMulti,double tastePrice){
        String itemSub="";
        int itemSubPrice=0;

        if(saleItemSubRvAdapter != null){
            for(int i=0;i<saleItemSubRvAdapter.lstItemSubGroup.size();i++){
                for(int x=0;x<saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().size();x++){
                    if(saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().get(x).isSelected()){
                        itemSubPrice+=saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().get(x).getPrice();
                        itemSub+=saleItemSubRvAdapter.lstItemSubGroup.get(i).getLstItemSubData().get(x).getSubName()+",";
                    }
                }
            }
        }
        if(itemSub.length()!=0) itemSub=itemSub.substring(0, itemSub.length() - 1);

        TransactionData data=new TransactionData();
        data.setItemid(lstItemData.get(position).getItemid());
        data.setItemName(lstItemData.get(position).getItemName());
        data.setSysid(lstItemData.get(position).getSysid());
        data.setStype(lstItemData.get(position).getStype());
        data.setSalePrice(lstItemData.get(position).getPrice()+itemSubPrice);
        data.setCounterID(lstItemData.get(position).getCounterID());
        data.setIncomeid(lstItemData.get(position).getIncomeid());
        data.setStringQty("1");
        data.setIntegerQty(1);
        data.setTaste(taste);
        data.setTasteMulti(tasteMulti);
        data.setNoDis(lstItemData.get(position).getNoDis());
        data.setItemDis(lstItemData.get(position).getItemDis());
        data.setTastePrice(tastePrice);
        data.setAllItemSub(itemSub);
        data.setUseTasteMulti(allowTasteMulti);

        lstOrderItem.add(data);
        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);
    }

    private void placeOrderByBarcode(List<ItemData> lstItemData){
        TransactionData data=new TransactionData();
        data.setItemid(lstItemData.get(0).getItemid());
        data.setItemName(lstItemData.get(0).getItemName());
        data.setSysid(lstItemData.get(0).getSysid());
        data.setStype(lstItemData.get(0).getStype());
        data.setSalePrice(lstItemData.get(0).getPrice());
        data.setCounterID(lstItemData.get(0).getCounterID());
        data.setIncomeid(lstItemData.get(0).getIncomeid());
        data.setStringQty("1");
        data.setIntegerQty(1);
        data.setTaste("");
        data.setAmount(lstItemData.get(0).getPrice());

        lstOrderItem.add(data);

        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);
    }

    private void clearOrder(){
        lvOrder.setVisibility(View.VISIBLE);
        lstOrderItem=new ArrayList<>();
        orderListAdapter=new OrderListAdapter(this,lstOrderItem);
        lvOrder.setAdapter(orderListAdapter);
        orderListAdapter.setOnOrderButtonClickListener(this);
    }

    private String getCurrentDate(){
        cCalendar= Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_FORMAT);
        return currentDate=dateFormat.format(cCalendar.getTime());
    }

    private String getCurrentTime(){
        cCalendar= Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat(TIME_FORMAT);
        return currentTime=timeFormat.format(cCalendar.getTime());
    }

    private void setLayoutResource(){
        expList =(ExpandableListView)findViewById(android.R.id.list);
        lvItem =(ListView)findViewById(R.id.lvItem);
        lvOrder =(ListView) findViewById(R.id.lvOrder);
        btnSendOrder=(Button)findViewById(R.id.btnSendOrder);
        tvWaiterName=(TextView)findViewById(R.id.tvWaiterName);
        tvSubMenuName =(TextView)findViewById(R.id.tvSubMenuName);
        btnChooseTable =(Button)findViewById(R.id.btnChooseTable);
        tvTableName =(TextView)findViewById(R.id.tvTableName);
        btnBarcode=(ImageButton)findViewById(R.id.btnBarcode);
        tvTable =(TextView)findViewById(R.id.tvTable);

        tvTableName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvSubMenuName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvWaiterName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        progDialog=new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(false);

        Cursor cur_allow_barcode=db.getBarcodeSetting();
        if(cur_allow_barcode.moveToFirst()){
            if(cur_allow_barcode.getInt(0)==1)btnBarcode.setVisibility(View.VISIBLE);
            else btnBarcode.setVisibility(View.GONE);
        }
    }

    private void printOrder(){
        lstOrder = new ArrayList<>();
        String stringQty;
        int integerQty;
        float floatQty;

        for(int i=0;i<lstOrderItem.size();i++){
            TransactionData data = new TransactionData();
            data.setItemName(lstOrderItem.get(i).getItemName());
            data.setStringQty(lstOrderItem.get(i).getStringQty());

            stringQty = lstOrderItem.get(i).getStringQty();
            floatQty = Float.parseFloat(stringQty);
            if (floatQty == Math.round(floatQty)) {
                integerQty = Math.round(floatQty);
                data.setIntegerQty(integerQty);
            } else {
                data.setFloatQty(floatQty);
            }
            data.setTaste(lstOrderItem.get(i).getTaste());
            data.setTasteMulti(lstOrderItem.get(i).getTasteMulti());
            data.setStype(lstOrderItem.get(i).getStype());
            lstOrder.add(data);
        }

        Intent i = new Intent(getApplicationContext(), PrintOrderActivity.class);
        i.putExtra("TableId", choosed_table_id);
        i.putExtra("TableName", choosed_table_name);
        i.putExtra("UserName", waiter_name);
        startActivity(i);
    }

    private void setPrintLayoutResource(){
        layoutPrint =(ScrollView) findViewById(R.id.layoutPrint);
        tvPrintLabelTable=(TextView)findViewById(R.id.tvPrintLabelTable);
        tvPrintLabelWaiter =(TextView)findViewById(R.id.tvPrintLabelWaiter);
        tvPrintTable =(TextView)findViewById(R.id.tvPrintTable);
        tvPrintWaiter =(TextView)findViewById(R.id.tvPrintWaiter);
        tvPrintDate =(TextView)findViewById(R.id.tvPrintDate);
        tvPrintTime =(TextView)findViewById(R.id.tvPrintTime);
        tvPrintHeaderItem =(TextView)findViewById(R.id.tvPrintHeaderItem);
        tvPrintHeaderQty =(TextView)findViewById(R.id.tvPrintHeaderQty);
        layoutPrintList =(LinearLayout) findViewById(R.id.layoutPrintList);

        tvPrintLabelTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintLabelWaiter.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintTable.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintWaiter.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintDate.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintTime.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintHeaderItem.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrintHeaderQty.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
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

    private void actionCustomerEntry(){
        Intent intent_cus_entry=new Intent(this,TableActivity.class);
        intent_cus_entry.putExtra("role", "cusinfo");
        startActivity(intent_cus_entry);
    }

    private void actionCustomerList(){
        Intent i=new Intent(this,CustomerListActivity.class);
        startActivity(i);
    }

    private void actionBill(){
        Intent intent_bill=new Intent(this,TableActivity.class);
        intent_bill.putExtra("role", "bill");
        startActivity(intent_bill);
    }

    private void actionViewOrder(){
        Intent intent_view_order=new Intent(this,TableActivity.class);
        intent_view_order.putExtra("role", "view");
        startActivity(intent_view_order);
    }

    private void actionBooking(){
        Intent intent_booking=new Intent(this,BookingListActivity.class);
        intent_booking.putExtra("waiterid",waiter_id);
        intent_booking.putExtra("waitername",waiter_name);
        startActivity(intent_booking);
    }

    /**
     * Dialogs
     */

    private void showCalculatorDialog(final int position,final EditText etOrderQty,final TextView tvPNumber,final int type){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dialog_calculator, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        final EditText etQty=(EditText)view.findViewById(R.id.etQty);
        final ImageButton btnBackspace=(ImageButton)view.findViewById(R.id.btnBackspace);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnOne=(Button)view.findViewById(R.id.btnOne);
        final Button btnTwo=(Button)view.findViewById(R.id.btnTwo);
        final Button btnThree=(Button)view.findViewById(R.id.btnThree);
        final Button btnFour=(Button)view.findViewById(R.id.btnFour);
        final Button btnFive=(Button)view.findViewById(R.id.btnFive);
        final Button btnSix=(Button)view.findViewById(R.id.btnSix);
        final Button btnSeven=(Button)view.findViewById(R.id.btnSeven);
        final Button btnEight=(Button)view.findViewById(R.id.btnEight);
        final Button btnNine=(Button)view.findViewById(R.id.btnNine);
        final Button btnZero=(Button)view.findViewById(R.id.btnZero);
        final Button btnPoint=(Button)view.findViewById(R.id.btnPoint);
        final Button btnClear=(Button)view.findViewById(R.id.btnClear);

        if(type==2)btnPoint.setEnabled(false);

        dialog.setCancelable(true);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        etQty.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT=2;
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(event.getRawX() >= (etQty.getRight() - etQty.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(etQty.getText().toString().length()!=0){
                            String searchkey=etQty.getText().toString();
                            searchkey=searchkey.substring(0,searchkey.length()-1);
                            etQty.setText(searchkey);
                        }
                    }
                }
                return false;
            }
        });

        btnBackspace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    searchkey=searchkey.substring(0,searchkey.length()-1);
                    etQty.setText(searchkey);
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(type==1) {
                    if (etQty.getText().toString().length() != 0) {
                        float floatQty;
                        int integerQty;
                        String stringQty = etQty.getText().toString();
                        char lastChar = stringQty.charAt(stringQty.length() - 1);
                        if (lastChar == '.') {
                            showMessage(info_message, "Please enter number behind point!");
                            return;
                        } else {
                            floatQty = Float.parseFloat(stringQty);
                            if (floatQty == Math.round(floatQty)) {
                                integerQty = Math.round(floatQty);
                                etOrderQty.setText(String.valueOf(integerQty));
                                lstOrderItem.get(position).setStringQty(String.valueOf(integerQty));
                                lstOrderItem.get(position).setIntegerQty(integerQty);
                            } else {
                                etOrderQty.setText(String.valueOf(floatQty));
                                lstOrderItem.get(position).setStringQty(String.valueOf(floatQty));
                                lstOrderItem.get(position).setFloatQty(floatQty);
                            }
                        }
                        alertDialog.dismiss();

                    } else {
                        showMessage(info_message, "Enter Quantity");
                    }
                }else if(type==2){
                    if(etQty.getText().toString().length()!=0){
                        tvPNumber.setText(etQty.getText().toString());
                        lstOrderItem.get(position).setpNumber(Integer.parseInt(etQty.getText().toString()));

                        alertDialog.dismiss();
                    }else{
                        showMessage(info_message,"Enter Number");
                    }
                }
            }
        });

        btnOne.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnOne.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnOne.getText().toString());
                }
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnTwo.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnTwo.getText().toString());
                }
            }
        });

        btnThree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnThree.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnThree.getText().toString());
                }
            }
        });

        btnFour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnFour.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnFour.getText().toString());
                }
            }
        });

        btnFive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnFive.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnFive.getText().toString());
                }
            }
        });

        btnSix.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnSix.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnSix.getText().toString());
                }
            }
        });

        btnSeven.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnSeven.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnSeven.getText().toString());
                }
            }
        });

        btnEight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnEight.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnEight.getText().toString());
                }
            }
        });

        btnNine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnNine.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnNine.getText().toString());
                }
            }
        });

        btnZero.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnZero.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnZero.getText().toString());
                }
            }
        });

        btnPoint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    String searchkey=etQty.getText().toString();
                    String newkey=searchkey+btnPoint.getText().toString();
                    etQty.setText(newkey);
                }else{
                    etQty.setText(btnPoint.getText().toString());
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(etQty.getText().toString().length()!=0){
                    etQty.setText("");
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

    private void showServerPropertyDialog(){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dialog_server_property, null);
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(context);
        dialog.setView(view);
        final EditText etIAddress=(EditText)view.findViewById(R.id.etIPAddress);
        final EditText etUser=(EditText)view.findViewById(R.id.etServerUser);
        final EditText etPassword=(EditText)view.findViewById(R.id.etServerPassword);
        final EditText etDatabaseName=(EditText)view.findViewById(R.id.etDatabaseName);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);

        dialog.setCancelable(true);
        final android.app.AlertDialog serverPropertyDialog=dialog.create();
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

    private void showTasteDialog(final int position){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dialog_taste, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);
        tvShowTaste=(TextView)view.findViewById(R.id.tvTaste);
        tvShowTastePrice=(TextView)view.findViewById(R.id.tvTastePrice);
        final GridView gvTaste=(GridView)view.findViewById(R.id.gvTaste);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
        final Button btnClear=(Button)view.findViewById(R.id.btnClear);

        tvShowTaste.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        dialogTasteGridAdapter=new DialogTasteGridAdapter(this,lstTaste);
        gvTaste.setAdapter(dialogTasteGridAdapter);
        dialogTasteGridAdapter.setOnTasteClickListener(this);

        dialog.setCancelable(false);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(!isTasteEdit){
                    placeOrder(position,tvShowTaste.getText().toString(),"",0);
                }else{
                    tvTaste.setText("");
                    tvTaste.setText(tvShowTaste.getText().toString());
                    lstOrderItem.get(taste_position).setTaste(tvShowTaste.getText().toString());
                }
                isTasteEdit=false;
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(!isTasteEdit){
                    placeOrder(position,"","",0);
                }
                isTasteEdit=false;
                alertDialog.dismiss();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tvShowTaste.setText("");
            }
        });
    }

    private void showTasteMultiDialog(int incomeid,final int position){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dialog_taste, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);
        tvShowTaste=(TextView)view.findViewById(R.id.tvTaste);
        tvShowTastePrice=(TextView)view.findViewById(R.id.tvTastePrice);
        final GridView gvTaste=(GridView)view.findViewById(R.id.gvTaste);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
        final Button btnClear=(Button)view.findViewById(R.id.btnClear);

        tvShowTaste.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        lstTasteMulti=new ArrayList();
        Cursor cur=db.getTasteMultiByGroupID(incomeid);
        if(cur.getCount()!=0){
            while(cur.moveToNext()){
                TasteMultiData data=new TasteMultiData();
                data.setTasteid(cur.getInt(2));
                data.setTasteName(cur.getString(3));
                data.setPrice(cur.getDouble(6));
                lstTasteMulti.add(data);
            }
        }else{
            if(!isTasteEdit){
                placeOrder(position,"","",0);
            }else{
                showMessage(info_message,"No Taste for this Item!");
                isTasteEdit=false;
            }
            return;
        }

        dialogTasteMultiGridAdapter=new DialogTasteMultiGridAdapter(this,lstTasteMulti);
        gvTaste.setAdapter(dialogTasteMultiGridAdapter);
        dialogTasteMultiGridAdapter.setOnTasteClickListener(this);

        dialog.setCancelable(false);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(!isTasteEdit){
                    if(tvShowTastePrice.getText().toString().trim().length()!=0)
                        placeOrder(position,"",tvShowTaste.getText().toString(),Double.parseDouble(tvShowTastePrice.getText().toString().trim()));
                    else placeOrder(position,"",tvShowTaste.getText().toString(),0);
                }else{
                    tvTasteMulti.setText(tvShowTaste.getText().toString());
                    tvTastePrice.setText(tvShowTastePrice.getText().toString());
                    lstOrderItem.get(taste_position).setTasteMulti(tvShowTaste.getText().toString());
                    if(tvShowTastePrice.getText().toString().trim().length()!=0)
                        lstOrderItem.get(taste_position).setTastePrice(Double.parseDouble(tvShowTastePrice.getText().toString().trim()));
                    else lstOrderItem.get(taste_position).setTastePrice(0);
                }
                isTasteEdit=false;
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick( View arg0) {
                if(!isTasteEdit){
                    placeOrder(position,"","",0);
                }
                isTasteEdit=false;
                alertDialog.dismiss();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tvShowTaste.setText("");
                tvShowTastePrice.setText("0.0");
            }
        });
    }

    private void showDetailDialog(final String barcode){
        LayoutInflater reg=LayoutInflater.from(context);
        View view=reg.inflate(R.layout.dialog_item_detail, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        final TextView tvItemID=(TextView)view.findViewById(R.id.tvItemID);
        final TextView tvItemName=(TextView)view.findViewById(R.id.tvItemName);
        final TextView tvPrice=(TextView)view.findViewById(R.id.tvPrice);
        final TextView tvIngredient=(TextView)view.findViewById(R.id.tvIngredient);
        final TextView tvLabelIngredient=(TextView)view.findViewById(R.id.tvLabelIngredient);
        final Button btnCancel=(Button)view.findViewById(R.id.btnCancel);
        final Button btnOK=(Button)view.findViewById(R.id.btnOK);

        tvItemID.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvPrice.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvIngredient.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
        tvLabelIngredient.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        final List<ItemData> lstItem=new ArrayList<>();
        Cursor cur=db.getItemByBarcode(barcode);
        if(cur.moveToFirst()){
            ItemData data=new ItemData();
            data.setItemid(cur.getString(0));
            data.setItemName(cur.getString(1));
            data.setPrice(cur.getDouble(2));
            data.setIngredients(cur.getString(3));
            data.setSysid(cur.getInt(4));
            data.setCounterID(cur.getInt(5));
            data.setStype(cur.getInt(6));
            data.setOutOfOrder(cur.getInt(7));
            data.setIncomeid(cur.getInt(8));
            lstItem.add(data);
            tvItemID.setText(cur.getString(0));
            tvItemName.setText(cur.getString(1));
            tvPrice.setText(String.valueOf(cur.getDouble(2)));
            tvIngredient.setText(cur.getString(3));

            dialog.setCancelable(false);
            final AlertDialog alertDialog=dialog.create();
            alertDialog.show();

            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    alertDialog.dismiss();
                }
            });

            btnOK.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (lstItem.size() != 0) {
                        placeOrderByBarcode(lstItem);
                    }
                    alertDialog.dismiss();
                }
            });
        }else{
            showMessage(info_message,"No Data!");
        }
    }

    private void showItemSubDialog(List<ItemSubGroupData> lstItemSubGroupData,int position){
        LayoutInflater li=LayoutInflater.from(context);
        View view=li.inflate(R.layout.dg_sale_item_sub, null);
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setView(view);

        final ImageButton btnClose= (ImageButton) view.findViewById(R.id.btnClose);
        final RecyclerView rvRootItemSub= (RecyclerView) view.findViewById(R.id.rvRootItemSub);
        final Button btnOk= (Button) view.findViewById(R.id.btnOk);

        saleItemSubRvAdapter =new SaleItemSubRvAdapter(lstItemSubGroupData,context);
        rvRootItemSub.setAdapter(saleItemSubRvAdapter);
        rvRootItemSub.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        dialog.setCancelable(false);
        final AlertDialog alertDialog=dialog.create();
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowTasteAndPlaceOrder(position);
                alertDialog.dismiss();
            }
        });
    }

    private void isShowTasteAndPlaceOrder(int position){
        int incomeid=lstItemData.get(position).getIncomeid();
        Cursor cur_auto_taste=db.getAutoTasteFeature();
        if(cur_auto_taste.moveToFirst())allowAutoTaste=cur_auto_taste.getInt(0);
        Cursor cur_taste_multi=db.getUseTasteMultiFeature();
        if(cur_taste_multi.moveToFirst())allowTasteMulti=cur_taste_multi.getInt(0);
        if(allowAutoTaste==1) {
            if(allowTasteMulti==1) showTasteMultiDialog(incomeid,position);
            else showTasteDialog(position);
        }
        else {
            placeOrder(position,"","",0);
        }
    }

}
