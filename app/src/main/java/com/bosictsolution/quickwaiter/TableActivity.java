package com.bosictsolution.quickwaiter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import common.DBHelper;
import common.ServerConnection;
import common.SystemSetting;
import data.TableData;
import data.TableTypeData;

public class TableActivity extends AppCompatActivity {

    ListView lvTableType;
    GridView gvEmptyTable, gvOccupiedTable, gvBookingTable;
    TextView tvTableTypeName, tvLabelEmptyTable, tvLabelOccupiedTable, tvLabelBookingTable;
    LinearLayout tableLayout;
    ImageView imgBookingTable;

    private DBHelper db;
    ServerConnection serverconnection;
    ArrayAdapter adapter;
    private ProgressDialog progressDialog;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    final Context context = this;
    SystemSetting systemSetting = new SystemSetting();

    static boolean from_main, from_customer_entry;
    String role, billTableName, billWaiterName, viewOrderTableName;
    List<TableTypeData> lstTableTypeData = new ArrayList<>();
    List<TableData> lstTableDataByTableTypeID = new ArrayList<>();
    List<TableData> lstOccupiedTableDataByTableTypeID = new ArrayList<>();
    List<TableData> lstEmptyTableDataByTableTypeID = new ArrayList<>();
    List<TableData> lstBookingTableDataByTableTypeID = new ArrayList<>();
    int warning_message = 1, error_message = 2, success_message = 3, info_message = 4;
    int selectedTableTypeID, billTableID, billWaiterID, viewOrderTableID, allowBooking, allowUseFixedLayout, allowBillPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHelper(this);
        Cursor cur_fixed_layout = db.getFullLayoutSetting();
        if (cur_fixed_layout.moveToFirst()) allowUseFixedLayout = cur_fixed_layout.getInt(0);
        if (allowUseFixedLayout == 0) {
            Configuration config = getResources().getConfiguration();
            if (config.smallestScreenWidthDp >= 600) {
                setContentView(R.layout.activity_table);
                ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayHomeAsUpEnabled(true);
            } else {
                setContentView(R.layout.activity_table);
                drawerLayout = (DrawerLayout) findViewById(R.id.sliderLayout);
                ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setDisplayShowCustomEnabled(true);
            }
        } else {
            setContentView(R.layout.activity_table);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        serverconnection = new ServerConnection();

        Cursor cur = db.getBookingTableFeature();
        if (cur.moveToFirst()) allowBooking = cur.getInt(0);

        setLayoutResource();

        Intent i = getIntent();
        role = i.getStringExtra("role");
        if (role.equals("just_choice")) {
            from_main = i.getBooleanExtra("from_waiter_main", false);
            from_customer_entry = i.getBooleanExtra("from_customer_info", false);
        }

        gvOccupiedTable.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (role.equals("view")) {
                    viewOrderTableID = lstOccupiedTableDataByTableTypeID.get(position).getTableid();
                    viewOrderTableName = lstOccupiedTableDataByTableTypeID.get(position).getTableName();
                    billWaiterID = Integer.parseInt(MainActivity.tvWaiterName.getTag().toString());
                    billWaiterName = MainActivity.tvWaiterName.getText().toString();
                    ViewOrder viewOrder = new ViewOrder();
                    viewOrder.execute("");
                } else if (role.equals("bill")) {
                    billTableID = lstOccupiedTableDataByTableTypeID.get(position).getTableid();
                    billTableName = lstOccupiedTableDataByTableTypeID.get(position).getTableName();
                    billWaiterID = Integer.parseInt(MainActivity.tvWaiterName.getTag().toString());
                    billWaiterName = MainActivity.tvWaiterName.getText().toString();

                    Cursor cur = db.getPrintBillSetting();
                    if (cur.moveToFirst()) allowBillPrint = cur.getInt(0);
                    if (allowBillPrint == 0) {
                        RequestBill requestBill = new RequestBill();
                        requestBill.execute("");
                    } else {
                        Intent i = new Intent(TableActivity.this, BillActivity.class);
                        i.putExtra("tableid", lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                        i.putExtra("tablename", lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                        i.putExtra("waiterid", Integer.parseInt(MainActivity.tvWaiterName.getTag().toString()));
                        i.putExtra("waitername", MainActivity.tvWaiterName.getText().toString());
                        startActivity(i);
                        finish();
                    }
                } else if (role.equals("cusinfo")) {
                    Intent i = new Intent(TableActivity.this, CustomerEntryActivity.class);
                    i.putExtra("tableid", lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                    i.putExtra("tablename", lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                    i.putExtra("waiterid", Integer.parseInt(MainActivity.tvWaiterName.getTag().toString()));
                    i.putExtra("waitername", MainActivity.tvWaiterName.getText().toString());
                    startActivity(i);
                    finish();
                } else if (role.equals("booking")) {
                    showMessage(error_message, "Not allow booking for this table!");
                } else if (role.equals("just_choice")) {
                    if (from_main == true) {
                        MainActivity.choosed_table_id = lstOccupiedTableDataByTableTypeID.get(position).getTableid();
                        MainActivity.choosed_table_name = lstOccupiedTableDataByTableTypeID.get(position).getTableName();
                        MainActivity.isEmptyTable = false;
                        MainActivity.start_time = "";
                    }
                    if (from_customer_entry == true) {
                        CustomerEntryActivity.btnChooseTable.setTag(lstOccupiedTableDataByTableTypeID.get(position).getTableid());
                        CustomerEntryActivity.btnChooseTable.setText(lstOccupiedTableDataByTableTypeID.get(position).getTableName());
                    }
                    finish();
                }
            }
        });

        gvBookingTable.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (role.equals("just_choice")) {
                    if (from_main == true) {
                        MainActivity.choosed_table_id = lstBookingTableDataByTableTypeID.get(position).getTableid();
                        MainActivity.choosed_table_name = lstBookingTableDataByTableTypeID.get(position).getTableName();
                        finish();
                    }
                    if (from_customer_entry == true) {
                        showMessage(warning_message, lstBookingTableDataByTableTypeID.get(position).getTableName() + " is Booking Table. Not Allow to Add Customer!");
                    }
                } else if (role.equals("cusinfo")) {
                    showMessage(warning_message, lstBookingTableDataByTableTypeID.get(position).getTableName() + " is Booking Table. Not Allow to Add Customer!");
                } else if (role.equals("booking")) {
                    showMessage(warning_message, lstBookingTableDataByTableTypeID.get(position).getTableName() + " has already chosen for another Booking!");
                } else {
                    showMessage(warning_message, lstBookingTableDataByTableTypeID.get(position).getTableName() + " is Booking Table. Cannot Find Order!");
                }
            }
        });

        gvEmptyTable.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (role.equals("just_choice")) {
                    if (from_main == true) {
                        MainActivity.choosed_table_id = lstEmptyTableDataByTableTypeID.get(position).getTableid();
                        MainActivity.choosed_table_name = lstEmptyTableDataByTableTypeID.get(position).getTableName();
                        MainActivity.isEmptyTable = true;
                        if (db.allowStartTimeSetting()) showTimeDialog();
                        else finish();
                    }
                    if (from_customer_entry == true) {
                        showMessage(warning_message, lstEmptyTableDataByTableTypeID.get(position).getTableName() + " is Empty Table. Not Allow to Add Customer!");
                    }
                } else if (role.equals("cusinfo")) {
                    showMessage(warning_message, lstEmptyTableDataByTableTypeID.get(position).getTableName() + " is Empty Table. Not Allow to Add Customer!");
                } else if (role.equals("booking")) {
                    BookingEntryActivity.bookingTableID = lstEmptyTableDataByTableTypeID.get(position).getTableid();
                    BookingEntryActivity.btnChooseTable.setText(lstEmptyTableDataByTableTypeID.get(position).getTableName());
                    finish();
                } else {
                    showMessage(warning_message, lstEmptyTableDataByTableTypeID.get(position).getTableName() + " is Empty Table. Cannot Find Order!");
                }
            }
        });

        lvTableType.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTableTypeID = lstTableTypeData.get(position).getTableTypeID();
                String tableTypeName = lstTableTypeData.get(position).getTableTypeName();
                tvTableTypeName.setText(tableTypeName);
                if (drawerLayout != null) drawerLayout.closeDrawer(lvTableType);
                getTableByTableTypeID(selectedTableTypeID);
                GetOccupiedAndBookingTable getOccupiedAndBookingTable = new GetOccupiedAndBookingTable();
                getOccupiedAndBookingTable.execute("");
            }
        });

        if (drawerLayout != null) {
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.mipmap.menu, R.string.app_name, R.string.app_name) {
                @SuppressLint("RestrictedApi")
                public void onDrawerClosed(View view) {
                    invalidateOptionsMenu();
                }
            };
        }

        if (drawerLayout != null) {
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            if (savedInstanceState == null) {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllTableType();
        if (selectedTableTypeID == 0) {
            selectedTableTypeID = lstTableTypeData.get(0).getTableTypeID();
            String first_table_type_name = lstTableTypeData.get(0).getTableTypeName();
            tvTableTypeName.setText(first_table_type_name);
        }
        getTableByTableTypeID(selectedTableTypeID);
        GetOccupiedAndBookingTable getOccupiedAndBookingTable = new GetOccupiedAndBookingTable();
        getOccupiedAndBookingTable.execute("");
    }

    // when using the ActionBarDrawerToggle, must call it during onPostCreate() and onConfigurationChanged()

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle != null) {
            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllTableType() {
        Cursor cur = db.getAllTableType();
        List<String> lstTableTypeName = new ArrayList<>();
        lstTableTypeData = new ArrayList<>();
        if (cur.getCount() != 0) {
            while (cur.moveToNext()) {
                TableTypeData data = new TableTypeData();
                data.setTableTypeID(cur.getInt(0));
                data.setTableTypeName(cur.getString(1));
                lstTableTypeName.add(cur.getString(1));
                lstTableTypeData.add(data);
            }
            if (!cur.isClosed()) {
                cur.close();
            }
        }
        adapter = new ArrayAdapter(this, R.layout.list_table_type, R.id.tvListRowItem, lstTableTypeName);
        lvTableType.setAdapter(adapter);
    }

    private void getTableByTableTypeID(int tableTypeID) {
        int manageTable = 0;
        lstTableDataByTableTypeID = new ArrayList<>();
        Cursor c = db.getManageTableSetting();
        if (c.moveToFirst()) manageTable = c.getInt(0);
        Cursor cur = null;
        if (manageTable == 0) cur = db.getTableByTableTypeID(tableTypeID);
        else cur = db.getOnlyActiveTable(tableTypeID);
        if (cur.getCount() != 0) {
            while (cur.moveToNext()) {
                TableData data = new TableData();
                data.setTableid(cur.getInt(0));
                data.setTableName(cur.getString(1));
                lstTableDataByTableTypeID.add(data);
            }
        }
    }

    public class GetOccupiedAndBookingTable extends AsyncTask<String, String, String> {
        String msg;
        boolean isSuccess;

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                } else {
                    lstOccupiedTableDataByTableTypeID = new ArrayList<>();
                    String sql_query = "select distinct(TableID),tb.Table_Name from InvTranSaleTemp ts inner join table_name tb on ts.TableID=tb.Table_Name_ID where tb.TableType=" + selectedTableTypeID + " order by TableID";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql_query);
                    while (rs.next()) {
                        TableData data = new TableData();
                        data.setTableid(rs.getInt(1));
                        data.setTableName(rs.getString(2));
                        lstOccupiedTableDataByTableTypeID.add(data);
                    }
                    if (allowBooking == 1) {
                        lstBookingTableDataByTableTypeID = new ArrayList<>();
                        String sql_query2 = "select TableID,tb.Table_Name from Booking booking inner join table_name tb on booking.TableID=tb.Table_Name_ID where tb.TableType=" + selectedTableTypeID + "and booking.Deleted=0 order by TableID";
                        Statement st2 = con.createStatement();
                        ResultSet rs2 = st2.executeQuery(sql_query2);
                        while (rs2.next()) {
                            TableData data = new TableData();
                            data.setTableid(rs2.getInt(1));
                            data.setTableName(rs2.getString(2));
                            lstBookingTableDataByTableTypeID.add(data);
                        }
                    }
                    isSuccess = true;
                }
            } catch (SQLException e) {
                showMessage(error_message, e.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.hide();
            if (!isSuccess) {
                showMessage(error_message, r);
                lvTableType.setEnabled(false);
            } else {
                lvTableType.setEnabled(true);
                showTableByTableTypeID();
            }
        }
    }

    private void showTableByTableTypeID() {
        List<String> lstEmptyTableName = new ArrayList<>();
        List<String> lstOccupiedTableName = new ArrayList<>();
        List<String> lstBookingTableName = new ArrayList<>();
        List<Integer> lstTableID = new ArrayList<>();
        List<Integer> lstOccupiedTableID = new ArrayList<>();
        List<Integer> lstBookingTableID = new ArrayList<>();
        lstEmptyTableDataByTableTypeID = new ArrayList<>();
        int noOfColumns = 5, totalHeight, count, adapterCount, modulo;

        for (int i = 0; i < lstTableDataByTableTypeID.size(); i++) {
            lstTableID.add(lstTableDataByTableTypeID.get(i).getTableid());
        }
        for (int i = 0; i < lstOccupiedTableDataByTableTypeID.size(); i++) {
            lstOccupiedTableID.add(lstOccupiedTableDataByTableTypeID.get(i).getTableid());
            lstOccupiedTableName.add(lstOccupiedTableDataByTableTypeID.get(i).getTableName());
        }
        for (int i = 0; i < lstBookingTableDataByTableTypeID.size(); i++) {
            lstBookingTableID.add(lstBookingTableDataByTableTypeID.get(i).getTableid());
            lstBookingTableName.add(lstBookingTableDataByTableTypeID.get(i).getTableName());
        }
        for (int i = 0; i < lstTableID.size(); i++) {
            if (!lstOccupiedTableID.contains(lstTableID.get(i))) {
                if (!lstBookingTableID.contains(lstTableID.get(i))) {
                    lstEmptyTableName.add(lstTableDataByTableTypeID.get(i).getTableName());
                    TableData data = new TableData();
                    data.setTableid(lstTableDataByTableTypeID.get(i).getTableid());
                    data.setTableName(lstTableDataByTableTypeID.get(i).getTableName());
                    lstEmptyTableDataByTableTypeID.add(data);
                }
            }
        }

        if (lstTableDataByTableTypeID.size() != 0) {
            if (lstOccupiedTableDataByTableTypeID.size() != 0) {
                adapter = new ArrayAdapter(this, R.layout.list_occupied_table, R.id.tvOccupiedTable, lstOccupiedTableName);
                totalHeight = 0;
                count = adapter.getCount();
                adapterCount = 0;
                if (count != 0) {
                    adapterCount = Math.round(count / noOfColumns);
                    modulo = count % noOfColumns;
                    if (modulo != 0) {
                        adapterCount += 1;
                    }
                }
                for (int size = 0; size < adapterCount; size++) {
                    View listItem = adapter.getView(size, null, gvOccupiedTable);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = gvOccupiedTable.getLayoutParams();
                params.height = totalHeight;
                gvOccupiedTable.setLayoutParams(params);
                gvOccupiedTable.setAdapter(adapter);
                gvOccupiedTable.setVisibility(View.VISIBLE);
            } else {
                gvOccupiedTable.setVisibility(View.GONE);
            }
            if (lstBookingTableDataByTableTypeID.size() != 0) {
                adapter = new ArrayAdapter(this, R.layout.list_booking_table, R.id.tvBookingTable, lstBookingTableName);
                totalHeight = 0;
                count = adapter.getCount();
                adapterCount = 0;
                if (count != 0) {
                    adapterCount = Math.round(count / noOfColumns);
                    modulo = count % noOfColumns;
                    if (modulo != 0) {
                        adapterCount += 1;
                    }
                }
                for (int size = 0; size < adapterCount; size++) {
                    View listItem = adapter.getView(size, null, gvBookingTable);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = gvBookingTable.getLayoutParams();
                params.height = totalHeight;
                gvBookingTable.setLayoutParams(params);
                gvBookingTable.setAdapter(adapter);
                gvBookingTable.setVisibility(View.VISIBLE);
            } else {
                gvBookingTable.setVisibility(View.GONE);
            }

            adapter = new ArrayAdapter(this, R.layout.list_empty_table, R.id.tvEmptyTable, lstEmptyTableName);
            totalHeight = 0;
            count = adapter.getCount();
            adapterCount = 0;
            if (count != 0) {
                adapterCount = Math.round(count / noOfColumns);
                modulo = count % noOfColumns;
                if (modulo != 0) {
                    adapterCount += 1;
                }
            }
            for (int size = 0; size < adapterCount; size++) {
                View listItem = adapter.getView(size, null, gvEmptyTable);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = gvEmptyTable.getLayoutParams();
            params.height = totalHeight;
            gvEmptyTable.setLayoutParams(params);
            gvEmptyTable.setAdapter(adapter);
        } else {
            List<String> lstEmpty = new ArrayList<>();
            lstEmpty.add("No Tables!");
            adapter = new ArrayAdapter(this, R.layout.list_empty, R.id.tvEmpty, lstEmpty);
            gvEmptyTable.setAdapter(adapter);
            gvOccupiedTable.setVisibility(View.GONE);
            gvBookingTable.setVisibility(View.GONE);
        }
    }

    public class RequestBill extends AsyncTask<String, String, String> {
        String msg;
        int msg_type;

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                    msg_type = error_message;
                } else {
                    String sql_query = "INSERT INTO AutoGirl(tableid,TableName,WaiterID,WaiterName,TableTypeID) VALUES";
                    String sql_data = billTableID + ",'" + billTableName + "'," + billWaiterID + ",'" + billWaiterName + "'," + selectedTableTypeID;
                    String insert_bill = sql_query + "(" + sql_data + ")";
                    Statement st = con.createStatement();
                    st.execute(insert_bill);
                    msg = billTableName + " Bill is Ready!";
                    msg_type = success_message;
                }
            } catch (SQLException e) {
                showMessage(error_message, e.getMessage());
            }
            return msg;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.hide();
            showMessage(msg_type, r);
        }
    }

    public class ViewOrder extends AsyncTask<String, String, String> {
        String msg;
        int msg_type;
        boolean isSuccess;

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = serverconnection.CONN();
                if (con == null) {
                    msg = "Error in connection with SQL server";
                    msg_type = error_message;
                    isSuccess = false;
                } else {
                    String get_order = "select ItemName,Qty,Amount from InvTranSaleTemp where ItemDeleted=0 AND TableID=" + viewOrderTableID;
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(get_order);
                    if (rs.next()) {
                        Intent i = new Intent(TableActivity.this, ViewOrderActivity.class);
                        i.putExtra("tableid", viewOrderTableID);
                        i.putExtra("tablename", viewOrderTableName);
                        i.putExtra("waiterid", billWaiterID);
                        i.putExtra("waitername", billWaiterName);
                        startActivity(i);
                        isSuccess = true;
                    } else {
                        msg = "Not Found Order";
                        msg_type = info_message;
                        isSuccess = false;
                    }
                }
            } catch (SQLException e) {
                msg = e.getMessage();
                msg_type = error_message;
                isSuccess = false;
            }
            return msg;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.hide();
            if (isSuccess == false) showMessage(msg_type, r);
        }
    }

    private void setLayoutResource() {
        lvTableType = (ListView) findViewById(R.id.lvTableType);
        gvEmptyTable = (GridView) findViewById(R.id.gvEmptyTable);
        tvTableTypeName = (TextView) findViewById(R.id.tvTableTypeName);
        gvOccupiedTable = (GridView) findViewById(R.id.gvOccupiedTable);
        gvOccupiedTable.setVisibility(View.GONE);
        tableLayout = (LinearLayout) findViewById(R.id.tableLayout);
        tvLabelEmptyTable = (TextView) findViewById(R.id.tvLabelEmptyTable);
        tvLabelOccupiedTable = (TextView) findViewById(R.id.tvLabelOccupiedTable);
        gvBookingTable = (GridView) findViewById(R.id.gvBookingTable);
        tvLabelBookingTable = (TextView) findViewById(R.id.tvLabelBookingTable);
        imgBookingTable = (ImageView) findViewById(R.id.imgBookingTable);

        if (allowBooking != 1) {
            gvBookingTable.setVisibility(View.GONE);
            tvLabelBookingTable.setVisibility(View.GONE);
            imgBookingTable.setVisibility(View.GONE);
        } else {
            gvBookingTable.setVisibility(View.VISIBLE);
            tvLabelBookingTable.setVisibility(View.VISIBLE);
            imgBookingTable.setVisibility(View.VISIBLE);
        }

        tvTableTypeName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void showMessage(int msg_type, String msg_text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.message, (ViewGroup) findViewById(R.id.gpMessage));
        TextView tvMessage;
        if (msg_type == warning_message) {
            layout = inflater.inflate(R.layout.message_warning, (ViewGroup) findViewById(R.id.gpMessage));
        } else if (msg_type == error_message) {
            layout = inflater.inflate(R.layout.message_error, (ViewGroup) findViewById(R.id.gpMessage));
        } else if (msg_type == success_message) {
            layout = inflater.inflate(R.layout.message_success, (ViewGroup) findViewById(R.id.gpMessage));
        } else if (msg_type == info_message) {
            layout = inflater.inflate(R.layout.message_info, (ViewGroup) findViewById(R.id.gpMessage));
        }
        tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        tvMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/BOS-PETITE.TTF"));
        tvMessage.setText(msg_text);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void showTimeDialog() {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.dg_start_time, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setView(view);
        final Spinner spTimePeriod = (Spinner) view.findViewById(R.id.spTimePeriod);
        final EditText etHour = (EditText) view.findViewById(R.id.etHour);
        final EditText etMinute = (EditText) view.findViewById(R.id.etMinute);
        final Button btnOK = (Button) view.findViewById(R.id.btnOK);
        final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        String currentTime = systemSetting.getCurrentTime();

        // 10:00 am
        String arr[] = currentTime.split(":");
        String hour = arr[0];  //get hour
        String str = arr[1];
        String arr2[] = str.split("\\s+");
        String minute = arr2[0];  //get minute
        String period = arr2[1];  //get period

        etHour.setText(hour);
        etMinute.setText(minute);

        List<String> list = new ArrayList<>();
        list.add("AM");
        list.add("PM");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        spTimePeriod.setAdapter(arrayAdapter);

        if (period.equals("am")) spTimePeriod.setSelection(0);
        else spTimePeriod.setSelection(1);

        dialog.setCancelable(true);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String hour = etHour.getText().toString();
                String minute = etMinute.getText().toString();
                String period = spTimePeriod.getSelectedItem().toString();
                MainActivity.start_time = hour + ":" + minute + " " + period;
                alertDialog.dismiss();
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MainActivity.start_time = "";
                alertDialog.dismiss();
                finish();
            }
        });
    }
}
