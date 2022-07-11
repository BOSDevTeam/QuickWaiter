package com.bosictsolution.quickwaiter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andprn.jpos.command.ESCPOSConst;
import com.andprn.jpos.printer.ESCPOSPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.PrintOrderListAdapter;
import common.DBHelper;
import data.PrintOrderData;
import data.TransactionData;

public class PrintOrderActivity extends AppCompatActivity {

    LinearLayout layoutPrint;
    TextView tvPrinting;
    Button btnPrint;
    PrintOrderListAdapter printOrderListAdapter;
    DBHelper db;
    private Calendar cCalendar;
    String tableName,userName;
    private ESCPOSPrinter posPtr;
    final Context context = this;
    int tabletSize,tableId;
    SimpleDateFormat dateTimeFormat;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_order);

        db = new DBHelper(this);
        cCalendar= Calendar.getInstance();
        dateTimeFormat =new SimpleDateFormat(LoginActivity.DATE_TIME_FORMAT);
        dateFormat=new SimpleDateFormat(LoginActivity.MM_DATE_FORMAT);
        timeFormat=new SimpleDateFormat(LoginActivity.ORDER_PRINT_TIME_FORMAT);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        tableId=intent.getIntExtra("TableId",0);
        tableName=intent.getStringExtra("TableName");
        userName=intent.getStringExtra("UserName");

        layoutPrint=(LinearLayout) findViewById(R.id.layoutPrint);
        tvPrinting=(TextView)findViewById(R.id.tvPrinting);
        btnPrint=(Button)findViewById(R.id.btnPrint);

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp <= 600) {
            tabletSize = 8;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 640;
        } else {
            tabletSize = 10;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutPrint.getLayoutParams();
            params.width = 640;
        }

        createPrintData(MainActivity.lstOrder);

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

    private void changePrintLayoutToBitmap() {
        for (int i = 0; i < printOrderListAdapter.getCount() ; i++) {
            View v = layoutPrint.getChildAt(i);
            if (v != null) {
                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                v.draw(canvas);
                savePrintLayoutToWaiterOneDB(bitmap);
                try {
                    print(context, "Printed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String savePrintLayoutToWaiterOneDB(Bitmap bitmapImage){
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/QuickWaiterDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File logoPath=new File(directory,"print_order.png");

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

    public void print(Context context,String msg) throws IOException
    {
        posPtr = new ESCPOSPrinter();
        File directory = new File(Environment.getExternalStorageDirectory().getPath(), "/QuickWaiterDB");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File filePath=new File(directory,"print_order.png");
        String receiptPath=filePath.toString();
        posPtr.printBitmap(receiptPath, ESCPOSConst.ALIGNMENT_CENTER);
        posPtr.lineFeed(4);
        posPtr.cutPaper();
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void createPrintData(List<TransactionData> lstOrder) {
        List<PrintOrderData> lstData = new ArrayList<>();

        List<TransactionData> lstTran = new ArrayList<>();
        for (int j = 0; j < lstOrder.size(); j++) {
            TransactionData data = lstOrder.get(j);
            TransactionData curData = new TransactionData();
            curData.setItemName(data.getItemName());
            curData.setStringQty(data.getStringQty());
            curData.setIntegerQty(data.getIntegerQty());
            curData.setFloatQty(data.getFloatQty());
            curData.setTaste(data.getTaste());
            curData.setTasteMulti(data.getTasteMulti());
            lstTran.add(curData);
        }
        if (lstTran.size() != 0) {
            PrintOrderData data = new PrintOrderData();
            data.setLstTran(lstTran);
            data.setTableName(tableName);
            data.setUserName(userName);
            data.setDateTime(dateTimeFormat.format(cCalendar.getTime()));
            data.setDate(dateFormat.format(cCalendar.getTime()));
            data.setTime(timeFormat.format(cCalendar.getTime()));

            lstData.add(data);
        }

        printOrderListAdapter = new PrintOrderListAdapter(context, lstData);
        final int adapterCount = printOrderListAdapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = printOrderListAdapter.getView(i, null, null);
            layoutPrint.addView(item);
        }
    }
}
