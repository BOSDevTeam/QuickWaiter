package com.bosictsolution.quickwaiter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import adapter.DialogManageMenuListAdapter;
import adapter.FeatureListAdapter;
import common.AppConstant;
import common.DBHelper;
import common.ServerConnection;
import data.FeatureSettingData;
import data.MainMenuData;
import listener.DialogMenuCheckListener;
import listener.FeatureCheckListener;

public class SettingActivity extends AppCompatActivity implements FeatureCheckListener, DialogMenuCheckListener {

    Button btnManageMenu, btnDeveloperMode, btnOK, btnAddItemSub;
    ListView lvFeature, lvMainMenu;
    TextView tvTitBasicFeature, tvTitManageTable, tvTitBillPrint, tvTitBarcode, tvTitOther, tvEnDisManageTable, tvEnDisBillPrint, tvEnDisBarcode, tvEditManageTable;
    Switch swManageTable, swBillPrint, swBarcode, swItemSub,swStartTime;
    CheckBox chkFullLayout, chkOpenOrderWaiter, chkOpenOrderKitchen, chkChangeTable, chkAdvancedTax, chkPrintOrder, chkPrintBill, chkHidePrice;
    EditText etPrinterIP;

    private DBHelper db;
    ServerConnection serverconnection;
    FeatureListAdapter featureListAdapter;
    DialogManageMenuListAdapter dialogManageMenuListAdapter;
    private ProgressDialog progressDialog;

    final Context context = this;
    List<FeatureSettingData> lstFeatureSettingData = new ArrayList<>();
    private static List<MainMenuData> lstMainMenuData;
    private static ArrayList<Integer> lstCheckedMainMenu = new ArrayList<>();
    int warning_message = 1, error_message = 2, success_message = 3, info_message = 4, updateFeatureID, allowType, isAllow = 1, isNotAllow = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        db = new DBHelper(this);
        serverconnection = new ServerConnection();

        setLayoutResource();

        btnManageMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showManageMenuDialog();
            }
        });
        btnDeveloperMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDevPasswordDialog();
            }
        });
        swManageTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swManageTable.isChecked()) {
                    db.updateSettingByName("Manage Table", 1);
                    tvEnDisManageTable.setText("Enabled");
                    tvEditManageTable.setEnabled(true);
                    tvEditManageTable.setTextColor(getResources().getColor(R.color.colorPrimary));
                    Intent i = new Intent(getApplicationContext(), ManageTableActivity.class);
                    startActivity(i);
                } else {
                    db.updateSettingByName("Manage Table", 0);
                    tvEnDisManageTable.setText("Disabled");
                    tvEditManageTable.setEnabled(false);
                    tvEditManageTable.setTextColor(getResources().getColor(R.color.colorGray));
                }
            }
        });
        tvEditManageTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ManageTableActivity.class);
                startActivity(i);
            }
        });
        swBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swBarcode.isChecked()) {
                    db.updateSettingByName("Barcode", 1);
                    tvEnDisBarcode.setText("Enabled");
                } else {
                    db.updateSettingByName("Barcode", 0);
                    tvEnDisBarcode.setText("Disabled");
                }
            }
        });
        swItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swItemSub.isChecked()) {
                    db.updateSettingByName("ItemSub", 1);
                    btnAddItemSub.setEnabled(true);
                } else {
                    db.updateSettingByName("ItemSub", 0);
                    btnAddItemSub.setEnabled(false);
                }
            }
        });
        swStartTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateSettingByName(AppConstant.StartTime, 1);
                } else {
                    db.updateSettingByName(AppConstant.StartTime, 0);
                }
            }
        });
        swBillPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swBillPrint.isChecked()) {
                    db.updateSettingByName("Bill Printing", 1);
                    swBillPrint.setChecked(true);
                    tvEnDisBillPrint.setText("Enabled");
                    etPrinterIP.setEnabled(true);
                    btnOK.setEnabled(true);
                    chkPrintOrder.setEnabled(true);
                    chkPrintBill.setEnabled(true);
                    Cursor c = db.getBillPrinter();
                    if (c.moveToFirst()) {
                        etPrinterIP.setText(c.getString(0));
                    }
                } else {
                    db.updateSettingByName("Bill Printing", 0);
                    db.updateSettingByName("Print Order", 0);
                    swBillPrint.setChecked(false);
                    tvEnDisBillPrint.setText("Disabled");
                    etPrinterIP.setEnabled(false);
                    btnOK.setEnabled(false);
                    chkPrintOrder.setEnabled(false);
                    chkPrintOrder.setChecked(false);
                    chkPrintBill.setEnabled(false);
                    chkPrintBill.setEnabled(false);
                    etPrinterIP.setText("");
                }
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPrinterIP.getText().toString().length() == 0) {
                    showMessage(warning_message, "Enter Printer IP Address!");
                    return;
                }
                if (db.insertBillPrinter(etPrinterIP.getText().toString())) {
                    showMessage(success_message, "Success!");
                }
            }
        });
        chkFullLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkFullLayout.isChecked()) {
                    db.updateSettingByName("Use Full Layout", 1);
                } else {
                    db.updateSettingByName("Use Full Layout", 0);
                }
            }
        });
        chkOpenOrderKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkOpenOrderKitchen.isChecked()) {
                    db.updateSettingByName("Open Order Kitchen", 1);
                } else {
                    db.updateSettingByName("Open Order Kitchen", 0);
                }
            }
        });
        chkOpenOrderWaiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkOpenOrderWaiter.isChecked()) {
                    db.updateSettingByName("Open Order Waiter", 1);
                } else {
                    db.updateSettingByName("Open Order Waiter", 0);
                }
            }
        });
        chkChangeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkChangeTable.isChecked()) {
                    db.updateSettingByName("Change Table", 1);
                } else {
                    db.updateSettingByName("Change Table", 0);
                }
            }
        });
        chkAdvancedTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkAdvancedTax.isChecked()) {
                    db.updateSettingByName("Advanced Tax", 1);
                } else {
                    db.updateSettingByName("Advanced Tax", 0);
                }
            }
        });
        chkPrintOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkPrintOrder.isChecked()) {
                    db.updateSettingByName("Print Order", 1);
                } else {
                    db.updateSettingByName("Print Order", 0);
                }
            }
        });
        chkPrintBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkPrintBill.isChecked()) {
                    db.updateSettingByName("Print Bill", 1);
                } else {
                    db.updateSettingByName("Print Bill", 0);
                }
            }
        });
        chkHidePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkHidePrice.isChecked()) {
                    db.updateSettingByName("Hide SalePrice", 1);
                } else {
                    db.updateSettingByName("Hide SalePrice", 0);
                }
            }
        });
        btnAddItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this, ItemSubSetupActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getFeature();
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
    public void onFeatureCheckedListener(int position) {
        allowType = isAllow;
        updateFeatureID = lstFeatureSettingData.get(position).getFeatureID();
        AllowFeature allowFeature = new AllowFeature();
        allowFeature.execute("");
    }

    @Override
    public void onFeatureUnCheckedListener(int position) {
        allowType = isNotAllow;
        updateFeatureID = lstFeatureSettingData.get(position).getFeatureID();
        AllowFeature allowFeature = new AllowFeature();
        allowFeature.execute("");
    }

    @Override
    public void onMenuCheckedListener(int position) {
        int checkedMainMenuID = lstMainMenuData.get(position).getMainMenuID();
        if (!lstCheckedMainMenu.contains(checkedMainMenuID)) {
            lstCheckedMainMenu.add(lstMainMenuData.get(position).getMainMenuID());
        }
    }

    @Override
    public void onMenuUnCheckedListener(int position) {
        int removeIndex = lstCheckedMainMenu.indexOf(lstMainMenuData.get(position).getMainMenuID());
        if (removeIndex != -1) {
            lstCheckedMainMenu.remove(removeIndex);
        }
    }

    private void setLayoutResource() {
        btnManageMenu = (Button) findViewById(R.id.btnManageMenu);
        btnDeveloperMode = (Button) findViewById(R.id.btnDeveloperMode);
        lvFeature = (ListView) findViewById(R.id.lvFeature);
        tvTitBasicFeature = (TextView) findViewById(R.id.tvTitBasicFeature);
        tvTitOther = (TextView) findViewById(R.id.tvTitOther);
        tvTitManageTable = (TextView) findViewById(R.id.tvTitManageTable);
        tvEnDisManageTable = (TextView) findViewById(R.id.tvEnDisManageTable);
        tvTitBillPrint = (TextView) findViewById(R.id.tvTitBillPrint);
        tvEnDisBillPrint = (TextView) findViewById(R.id.tvEnDisBillPrint);
        tvTitBarcode = (TextView) findViewById(R.id.tvTitBarcode);
        tvEnDisBarcode = (TextView) findViewById(R.id.tvEnDisBarcode);
        tvEditManageTable = (TextView) findViewById(R.id.tvEditManageTable);
        swManageTable = (Switch) findViewById(R.id.swManageTable);
        swBarcode = (Switch) findViewById(R.id.swBarcode);
        swBillPrint = (Switch) findViewById(R.id.swBillPrint);
        chkOpenOrderKitchen = (CheckBox) findViewById(R.id.chkOpenOrderKitchen);
        chkOpenOrderWaiter = (CheckBox) findViewById(R.id.chkOpenOrderWaiter);
        chkFullLayout = (CheckBox) findViewById(R.id.chkFullLayout);
        chkChangeTable = (CheckBox) findViewById(R.id.chkChangeTable);
        chkAdvancedTax = (CheckBox) findViewById(R.id.chkAdvancedTax);
        chkHidePrice = (CheckBox) findViewById(R.id.chkHidePrice);
        etPrinterIP = (EditText) findViewById(R.id.etPrinterIP);
        btnOK = (Button) findViewById(R.id.btnOK);
        chkPrintOrder = (CheckBox) findViewById(R.id.chkPrintOrder);
        chkPrintBill = (CheckBox) findViewById(R.id.chkPrintBill);
        swItemSub = (Switch) findViewById(R.id.swItemSub);
        btnAddItemSub = (Button) findViewById(R.id.btnAddItemSub);
        swStartTime = (Switch) findViewById(R.id.swStartTime);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        Cursor cur = db.getSetting();
        while (cur.moveToNext()) {
            String name = cur.getString(0);
            int allow = cur.getInt(1);
            if (name.equals("Manage Table")) {
                if (allow == 1) {
                    swManageTable.setChecked(true);
                    tvEnDisManageTable.setText("Enabled");
                    tvEditManageTable.setEnabled(true);
                    tvEditManageTable.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    swManageTable.setChecked(false);
                    tvEnDisManageTable.setText("Disabled");
                    tvEditManageTable.setEnabled(false);
                    tvEditManageTable.setTextColor(getResources().getColor(R.color.colorGray));
                }
            } else if (name.equals("Bill Printing")) {
                if (allow == 1) {
                    swBillPrint.setChecked(true);
                    tvEnDisBillPrint.setText("Enabled");
                    etPrinterIP.setEnabled(true);
                    btnOK.setEnabled(true);
                    chkPrintBill.setEnabled(true);
                    chkPrintOrder.setEnabled(true);
                    Cursor c = db.getBillPrinter();
                    if (c.moveToFirst()) {
                        etPrinterIP.setText(c.getString(0));
                    }
                } else {
                    swBillPrint.setChecked(false);
                    tvEnDisBillPrint.setText("Disabled");
                    etPrinterIP.setEnabled(false);
                    btnOK.setEnabled(false);
                    etPrinterIP.setText("");
                    chkPrintOrder.setEnabled(false);
                    chkPrintBill.setEnabled(false);
                }
            } else if (name.equals("Barcode")) {
                if (allow == 1) {
                    swBarcode.setChecked(true);
                    tvEnDisBarcode.setText("Enabled");
                } else {
                    swBarcode.setChecked(false);
                    tvEnDisBarcode.setText("Disabled");
                }
            } else if (name.equals("ItemSub")) {
                if (allow == 1) {
                    swItemSub.setChecked(true);
                    btnAddItemSub.setEnabled(true);
                } else {
                    swItemSub.setChecked(false);
                    btnAddItemSub.setEnabled(false);
                }
            } else if (name.equals(AppConstant.StartTime)) {
                if (allow == 1) {
                    swStartTime.setChecked(true);
                } else {
                    swStartTime.setChecked(false);
                }
            } else if (name.equals("Use Full Layout")) {
                if (allow == 1) {
                    chkFullLayout.setChecked(true);
                } else {
                    chkFullLayout.setChecked(false);
                }
            } else if (name.equals("Open Order Waiter")) {
                if (allow == 1) {
                    chkOpenOrderWaiter.setChecked(true);
                } else {
                    chkOpenOrderWaiter.setChecked(false);
                }
            } else if (name.equals("Open Order Kitchen")) {
                if (allow == 1) {
                    chkOpenOrderKitchen.setChecked(true);
                } else {
                    chkOpenOrderKitchen.setChecked(false);
                }
            } else if (name.equals("Change Table")) {
                if (allow == 1) {
                    chkChangeTable.setChecked(true);
                } else {
                    chkChangeTable.setChecked(false);
                }
            } else if (name.equals("Advanced Tax")) {
                if (allow == 1) {
                    chkAdvancedTax.setChecked(true);
                } else {
                    chkAdvancedTax.setChecked(false);
                }
            } else if (name.equals("Print Order")) {
                if (allow == 1) {
                    chkPrintOrder.setChecked(true);
                } else {
                    chkPrintOrder.setChecked(false);
                }
            } else if (name.equals("Print Bill")) {
                if (allow == 1) {
                    chkPrintBill.setChecked(true);
                } else {
                    chkPrintBill.setChecked(false);
                }
            } else if (name.equals("Hide SalePrice")) {
                if (allow == 1) {
                    chkHidePrice.setChecked(true);
                } else {
                    chkHidePrice.setChecked(false);
                }
            }
        }
    }

    private void getFeature() {
        Cursor cur = db.getFeature();
        lstFeatureSettingData = new ArrayList<>();
        if (cur.getCount() != 0) {
            while (cur.moveToNext()) {
                FeatureSettingData data = new FeatureSettingData();
                data.setFeatureID(cur.getInt(0));
                data.setFeatureName(cur.getString(1));
                data.setIsAllow(cur.getInt(2));
                lstFeatureSettingData.add(data);
            }
        }
        featureListAdapter = new FeatureListAdapter(this, lstFeatureSettingData);
        int totalHeight = 0;
        int adapterCount = featureListAdapter.getCount();
        for (int size = 0; size < adapterCount; size++) {
            View listItem = featureListAdapter.getView(size, null, lvFeature);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = lvFeature.getLayoutParams();
        params.height = totalHeight + (lvFeature.getDividerHeight() * (adapterCount - 1)) + 30;
        lvFeature.setLayoutParams(params);
        lvFeature.setAdapter(featureListAdapter);
        featureListAdapter.setOnCheckedListener(this);
    }

    private void showManageMenuDialog() {
        LayoutInflater reg = LayoutInflater.from(context);
        View vi = reg.inflate(R.layout.dialog_manage_menu, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setView(vi);

        final TextView tvLabelChooseMainMenu = (TextView) vi.findViewById(R.id.tvLabelChooseMainMenu);
        lvMainMenu = (ListView) vi.findViewById(R.id.lvMainMenu);
        final Button btnAllow = (Button) vi.findViewById(R.id.btnAllow);

        setMainMenuDataToAdapter();

        dialog.setCancelable(true);
        final AlertDialog dialog1 = dialog.create();
        dialog1.show();

        btnAllow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (lstCheckedMainMenu.size() == 0) {
                    showMessage(info_message, "Choose Main Menu!");
                    return;
                }
                db.resetMainMenu();

                for (int i = 0; i < lstCheckedMainMenu.size(); i++) {
                    db.updateAllowedMainMenu(lstCheckedMainMenu.get(i));
                }
                lstCheckedMainMenu = new ArrayList<>();
                showMessage(success_message, "Success!");
                dialog1.dismiss();
            }
        });
    }

    private void showDevPasswordDialog() {
        LayoutInflater reg = LayoutInflater.from(context);
        View passwordView = reg.inflate(R.layout.dialog_password, null);
        android.app.AlertDialog.Builder passwordDialog = new android.app.AlertDialog.Builder(context);
        passwordDialog.setView(passwordView);

        final EditText etPassword = (EditText) passwordView.findViewById(R.id.etPassword);
        final Button btnCancel = (Button) passwordView.findViewById(R.id.btnCancel);
        final Button btnOK = (Button) passwordView.findViewById(R.id.btnOK);

        passwordDialog.setCancelable(true);
        final android.app.AlertDialog passwordRequireDialog = passwordDialog.create();
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
                String inputPassword = etPassword.getText().toString();
                if (inputPassword.length() == 0) {
                    showMessage(info_message, "Enter Password!");
                    return;
                }
                if (inputPassword.equals("111")) {
                    Intent i = new Intent(getApplicationContext(), FeatureActivity.class);
                    startActivity(i);
                    passwordRequireDialog.dismiss();
                } else {
                    showMessage(error_message, "Invalid Password!");
                }
            }
        });
    }

    private void setMainMenuDataToAdapter() {
        lstMainMenuData = new ArrayList<>();
        Cursor cur = db.getAllMainMenu();
        while (cur.moveToNext()) {
            MainMenuData data = new MainMenuData();
            data.setMainMenuID(cur.getInt(0));
            data.setMainMenuName(cur.getString(1));
            data.setIsAllow(cur.getInt(3));
            lstMainMenuData.add(data);
        }
        dialogManageMenuListAdapter = new DialogManageMenuListAdapter(this, lstMainMenuData);
        lvMainMenu.setAdapter(dialogManageMenuListAdapter);
        dialogManageMenuListAdapter.setOnCheckedListener(this);
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

    public class AllowFeature extends AsyncTask<String, String, String> {
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
                    String sql_query = "";
                    if (allowType == isAllow)
                        sql_query = "update FeatureSetting set isAllow=1 where FeatureID=" + updateFeatureID;
                    else if (allowType == isNotAllow)
                        sql_query = "update FeatureSetting set isAllow=0 where FeatureID=" + updateFeatureID;
                    Statement st = con.createStatement();
                    st.execute(sql_query);
                    isSuccess = true;
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
            if (isSuccess) {
                if (allowType == isAllow) db.updateAllowFeature(updateFeatureID, 1);
                else if (allowType == isNotAllow) db.updateAllowFeature(updateFeatureID, 0);
            } else {
                showMessage(msg_type, r);
            }
        }
    }
}
