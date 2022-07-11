package common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import data.ItemAndSubData;
import data.ItemSubData;
import data.ItemSubGroupData;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class DBHelper extends SQLiteOpenHelper  {

    public static final String DATABASE_NAME="QuickWaiter";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Waiter "+"(WaiterID integer,WaiterName text,Password text)");
        db.execSQL("CREATE TABLE TableType "+"(TableTypeID integer,TableTypeName text)");
        db.execSQL("CREATE TABLE tblTable "+"(TableID integer,TableName text,TableTypeID integer,Active integer)");
        db.execSQL("CREATE TABLE MainMenu "+"(MainMenuID integer,MainMenuName text,isAllow integer,CounterID integer)");
        db.execSQL("CREATE TABLE SubMenu "+"(SubMenuID integer,SubMenuName text,MainMenuID integer,SortCode text,IncomeID integer)");
        db.execSQL("CREATE TABLE Item "+"(ItemID text,ItemName text,SubMenuID integer,Price real,Stype int,OutofOrder int,Ingredients text,Barcode text,NoDis int,ItemDis int)");
        db.execSQL("CREATE TABLE SystemItem "+"(SysID integer,ItemID text)");
        db.execSQL("CREATE TABLE SType "+"(STypeID integer,STypeName text)");
        db.execSQL("CREATE TABLE Taste "+"(TasteID integer,TasteName text)");
        db.execSQL("CREATE TABLE TasteMulti "+"(TID integer,GroupID integer,TasteID integer,TasteName text,TasteShort text,TasteSort integer,Price real)");
        db.execSQL("CREATE TABLE SlipFormat "+"(title1 text,title2 text,title3 text,title4 text,Message1 text,Message2 text)");
        db.execSQL("CREATE TABLE SystemSetting "+"(Tax integer,Service integer,AdminPassword text,Title text,UserPassword text)");
        db.execSQL("CREATE TABLE IPSetting "+"(IPAddress text,User text,Password text,Database text)");
        db.execSQL("CREATE TABLE FeatureSetting "+"(FeatureID integer,FeatureName text,isAllow integer)");
        db.execSQL("CREATE TABLE PrinterSetting "+"(SType integer,PrinterIP text)");
        db.execSQL("CREATE TABLE Setting "+"(SettingID integer primary key,SettingName text,isAllow integer)");
        db.execSQL("CREATE TABLE Register "+"(RID integer primary key,MacAddress text,Key text)");
        db.execSQL("CREATE TABLE BillPrinter "+"(PrinterIP text)");

        db.execSQL("CREATE TABLE WaiterTemp "+"(WaiterID integer,WaiterName text,Password text)");
        db.execSQL("CREATE TABLE TableTypeTemp "+"(TableTypeID integer,TableTypeName text)");
        db.execSQL("CREATE TABLE tblTableTemp "+"(TableID integer,TableName text,TableTypeID integer)");
        db.execSQL("CREATE TABLE MainMenuTemp "+"(MainMenuID integer,MainMenuName text,isAllow integer,CounterID integer)");
        db.execSQL("CREATE TABLE SubMenuTemp "+"(SubMenuID integer,SubMenuName text,MainMenuID integer,SortCode text,IncomeID integer)");
        db.execSQL("CREATE TABLE TasteTemp "+"(TasteID integer,TasteName text)");
        db.execSQL("CREATE TABLE TasteMultiTemp "+"(TID integer,GroupID integer,TasteID integer,TasteName text,TasteShort text,TasteSort integer,Price real)");

        db.execSQL("CREATE TABLE ItemSubGroup "+"(PKID integer primary key,GroupName text,SubTitle text,IsSingleCheck integer)");
        db.execSQL("CREATE TABLE ItemSub "+"(PKID integer primary key,SubGroupID integer,SubName text,Price integer)");
        db.execSQL("CREATE TABLE ItemAndSub "+"(PKID integer primary key,ItemID text,SubGroupID integer,LevelNo integer)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS Waiter");
        db.execSQL("DROP TABLE IF EXISTS TableType");
        db.execSQL("DROP TABLE IF EXISTS tblTable");
        db.execSQL("DROP TABLE IF EXISTS MainMenu");
        db.execSQL("DROP TABLE IF EXISTS SubMenu");
        db.execSQL("DROP TABLE IF EXISTS Item");
        db.execSQL("DROP TABLE IF EXISTS SystemItem");
        db.execSQL("DROP TABLE IF EXISTS SType");
        db.execSQL("DROP TABLE IF EXISTS Taste");
        db.execSQL("DROP TABLE IF EXISTS TasteMulti");
        db.execSQL("DROP TABLE IF EXISTS SlipFormat");
        db.execSQL("DROP TABLE IF EXISTS SystemSetting");
        db.execSQL("DROP TABLE IF EXISTS IPSetting");
        db.execSQL("DROP TABLE IF EXISTS FeatureSetting");
        db.execSQL("DROP TABLE IF EXISTS PrinterSetting");
        db.execSQL("DROP TABLE IF EXISTS Setting");
        db.execSQL("DROP TABLE IF EXISTS Register");
        db.execSQL("DROP TABLE IF EXISTS BillPrinter");
        db.execSQL("DROP TABLE IF EXISTS ItemSubGroup");
        db.execSQL("DROP TABLE IF EXISTS ItemSub");
        db.execSQL("DROP TABLE IF EXISTS ItemAndSub");

        onCreate(db);
    }
    public void truncateAllTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Waiter");
        db.execSQL("delete from TableType");
        db.execSQL("delete from tblTable");
        db.execSQL("delete from MainMenu");
        db.execSQL("delete from SubMenu");
        db.execSQL("delete from Item");
        db.execSQL("delete from SystemItem");
        db.execSQL("delete from SType");
        db.execSQL("delete from Taste");
        db.execSQL("delete from TasteMulti");
        db.execSQL("delete from SystemSetting");
        db.execSQL("delete from FeatureSetting");
        db.execSQL("delete from PrinterSetting");
        db.execSQL("delete from Setting");
        db.execSQL("delete from Register");
        db.execSQL("delete from BillPrinter");
        db.execSQL("delete from SlipFormat");
        db.execSQL("delete from ItemSubGroup");
        db.execSQL("delete from ItemSub");
        db.execSQL("delete from ItemAndSub");

        db.execSQL("delete from WaiterTemp");
        db.execSQL("delete from TableTypeTemp");
        db.execSQL("delete from tblTableTemp");
        db.execSQL("delete from MainMenuTemp");
        db.execSQL("delete from SubMenuTemp");
        db.execSQL("delete from TasteTemp");
        db.execSQL("delete from TasteMultiTemp");
    }
    //IP Setting
    public boolean insertIPSetting(String ip,String user,String password,String database){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("IPAddress", ip);
        value.put("User", user);
        value.put("Password", password);
        value.put("Database", database);
        long i= db.insert("IPSetting", null, value);
        if(i == -1)return false;
        else return true;
    }
    public void deleteIPSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from IPSetting");
    }
    public Cursor getIPSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select IPAddress,User,Password,Database from IPSetting",null);
        return cur;
    }
    //Register
    public void deleteRegister(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from Register");
    }
    public boolean insertRegister(String macAddress,String registerkey){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MacAddress", macAddress);
        value.put("Key", registerkey);
        db.insert("Register", null, value);
        return true;
    }
    //Setting
    public boolean insertSetting(String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SettingName", name);
        value.put("isAllow",0);
        db.insert("Setting", null, value);
        return true;
    }
    public boolean updateSettingByName(String name,int allow){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("isAllow",allow);
        db.execSQL("update Setting set isAllow="+allow+" where SettingName='"+name+"'");
        return true;
    }
    public boolean resetTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update tblTable set Active=0");
        return true;
    }
    public Cursor getSetting(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SettingName,isAllow from Setting",null);
        return cur;
    }
    public Cursor getManageTableSetting(){
        String name="Manage Table";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getBillPrintSetting(){
        String name="Bill Printing";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getBarcodeSetting(){
        String name="Barcode";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public boolean allowStartTimeSetting() {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select isAllow from Setting where SettingName='" + AppConstant.StartTime + "'", null);
        if (cur.moveToFirst()) {
            if (cur.getInt(0) == 1) result = true;
            else result = false;
        }
        return result;
    }
    public Cursor getFullLayoutSetting(){
        String name="Use Full Layout";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getOpenOrderWaiterSetting(){
        String name="Open Order Waiter";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getOpenOrderKitchenSetting(){
        String name="Open Order Kitchen";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getChangeTableSetting(){
        String name="Change Table";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getAdvancedTaxSetting(){
        String name="Advanced Tax";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getPrintOrderSetting(){
        String name="Print Order";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getPrintBillSetting(){
        String name="Print Bill";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    public Cursor getHideSalePriceSetting(){
        String name="Hide SalePrice";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from Setting where SettingName='"+name+"'",null);
        return cur;
    }
    //Bill Print
    public boolean insertBillPrinter(String printerIP){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from BillPrinter");
        ContentValues value=new ContentValues();
        value.put("PrinterIP", printerIP);
        db.insert("BillPrinter", null, value);
        return true;
    }
    public Cursor getBillPrinter(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PrinterIP from BillPrinter",null);
        return cur;
    }
    //Manage Menu
    public boolean resetMainMenu(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update MainMenu set isAllow=0");
        return true;
    }
    public boolean updateAllowedMainMenu(int mainMenuID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update MainMenu set isAllow=1 where MainMenuID="+mainMenuID);
        return true;
    }
    //Manage Table
    public boolean activeTableByTableID(int tableid){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update tblTable set Active="+1+" where TableID="+tableid);
        return true;
    }
    public boolean activeTableByTableTypeID(int tableTypeID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update tblTable set Active="+1+" where TableTypeID="+tableTypeID);
        return true;
    }
    public boolean inActiveTableByTableTypeID(int tableTypeID){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("update tblTable set Active="+0+" where TableTypeID="+tableTypeID);
        return true;
    }

    /**
     * Access Table Data
     */
    //Slip Format
    public boolean insertSlipFormat(String title1,String title2,String title3,String title4,String message1,String message2){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("title1", title1);
        value.put("title2", title2);
        value.put("title3", title3);
        value.put("title4", title4);
        value.put("Message1", message1);
        value.put("Message2", message2);
        db.insert("SlipFormat", null, value);
        return true;
    }
    public Cursor getSlipFormat(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select title1,title2,title3,title4,Message1,Message2 from SlipFormat",null);
        return cur;
    }
    //Feature Setting
    public boolean insertFeature(int featureid,String featureName,int isAllow){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("FeatureID",featureid);
        value.put("FeatureName", featureName);
        value.put("isAllow", isAllow);
        db.insert("FeatureSetting", null, value);
        return true;
    }
    public void truncateFeatureSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from FeatureSetting");
    }
    public Cursor getFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select FeatureID,FeatureName,isAllow from FeatureSetting",null);
        return cur;
    }
    public boolean updateAllowFeature(int featureid,int isAllow){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("isAllow", isAllow);
        db.update("FeatureSetting", value, "FeatureID=?", new String[]{Integer.toString(featureid)});
        return true;
    }
    public Cursor getAutoTasteFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.AutoTaste+"'",null);
        return cur;
    }
    public Cursor getOrderTimeFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.OrderTime+"'",null);
        return cur;
    }
    public Cursor getCustomerInfoFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.CustomerInfo+"'",null);
        return cur;
    }
    public Cursor getSetAllCustomerFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.SetAllCustomer+"'",null);
        return cur;
    }
    public Cursor getBookingTableFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.BookingTable+"'",null);
        return cur;
    }
    public Cursor getOutOfOrderFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.OutOfOrder+"'",null);
        return cur;
    }
    public Cursor getNotPairItemNameAndTasteFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.NotPairItemNameTaste+"'",null);
        return cur;
    }
    public Cursor getUseTasteMultiFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.UseTasteMulti+"'",null);
        return cur;
    }
    public Cursor getItemDiscountFeature(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select isAllow from FeatureSetting where FeatureName='"+FeatureName.ItemDiscount+"'",null);
        return cur;
    }
    //Waiter
    public boolean insertWaiter(int id,String name,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WaiterID", id);
        value.put("WaiterName", name);
        value.put("Password", password);
        db.insert("Waiter", null, value);
        return true;
    }
    public Cursor getAllWaiter(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM Waiter", null);
        return cur;
    }
    //Table Type
    public boolean insertTableType(int id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableTypeID", id);
        value.put("TableTypeName", name);
        db.insert("TableType", null, value);
        return true;
    }
    public Cursor getAllTableType(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM TableType", null);
        return cur;
    }
    //Table
    public boolean insertTable(int id,String name,int tableType){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableID", id);
        value.put("TableName", name);
        value.put("TableTypeID", tableType);
        db.insert("tblTable", null, value);
        return true;
    }
    public Cursor getAllTable(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT TableID,TableName,tb.TableTypeID,TableTypeName FROM tblTable tb INNER JOIN TableType type ON tb.TableTypeID=type.TableTypeID", null);
        return cur;
    }
    public Cursor getTableByTableTypeID(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT TableID,TableName,Active FROM tblTable WHERE TableTypeID="+id, null);
        return cur;
    }
    public Cursor getOnlyActiveTable(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT TableID,TableName FROM tblTable WHERE Active=1 AND TableTypeID="+id, null);
        return cur;
    }
    //Main Menu
    public boolean insertMainMenu(int id,String name,int counterid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuID", id);
        value.put("MainMenuName", name);
        value.put("isAllow", 1);
        value.put("CounterID", counterid);
        db.insert("MainMenu", null, value);
        return true;
    }
    public Cursor getAllMainMenu(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT MainMenuID,MainMenuName,CounterID,isAllow FROM MainMenu", null);
        return cur;
    }
    public Cursor getAllowedMainMenu(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT MainMenuID,MainMenuName,CounterID FROM MainMenu WHERE isAllow=1", null);
        return cur;
    }
    //Sub Menu
    public boolean insertSubMenu(int id,String name,int mainMenuid,String sortcode,int incomeid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubMenuID", id);
        value.put("SubMenuName", name);
        value.put("MainMenuID", mainMenuid);
        value.put("SortCode", sortcode);
        value.put("IncomeID",incomeid);
        db.insert("SubMenu", null, value);
        return true;
    }
    public Cursor getAllSubMenu(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT SubMenuID,SubMenuName,sub.MainMenuID,MainMenuName,IncomeID FROM SubMenu sub INNER JOIN MainMenu main ON sub.MainMenuID=main.MainMenuID WHERE main.isAllow=1 ORDER BY sortcode", null);
        return cur;
    }
    //Item
    public boolean insertItem(String id,String name,int subMenuid,double price,int stype,int outoforder,String ingredients,String barcode,int noDis,int itemDis){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ItemID", id);
        value.put("ItemName", name);
        value.put("SubMenuID", subMenuid);
        value.put("Price", price);
        value.put("Stype", stype);
        value.put("OutofOrder", outoforder);
        value.put("Ingredients",ingredients);
        value.put("Barcode",barcode);
        value.put("NoDis",noDis);
        value.put("ItemDis",itemDis);
        db.insert("Item", null, value);
        return true;
    }
    public void updateOutOfOrderItem(String itemid,int outOfOrder){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("OutofOrder", outOfOrder);
        db.update("Item", value, "ItemID=?", new String[]{itemid});
    }
    public void updateDiscountItem(String itemid,int noDis){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("NoDis", noDis);
        db.update("Item", value, "ItemID=?", new String[]{itemid});
    }
    public boolean insertSysItem(int sysitemid,String itemid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SysID", sysitemid);
        value.put("ItemID", itemid);
        db.insert("SystemItem", null, value);
        return true;
    }
    public Cursor getItemBySubMenuID(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT item.ItemID,item.ItemName,item.Price,sys.SysID,main.CounterID,Stype,OutofOrder,sub.IncomeID,item.NoDis,item.ItemDis FROM Item item INNER JOIN SystemItem sys ON item.ItemID=sys.ItemID INNER JOIN SubMenu sub ON item.SubMenuID=sub.SubMenuID INNER JOIN MainMenu main ON sub.MainMenuID=main.MainMenuID WHERE item.SubMenuID="+id, null);
        return cur;
    }
    public Cursor getItemByBarcode(String barcode){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("SELECT item.ItemID,item.ItemName,item.Price,item.Ingredients,sys.SysID,main.CounterID,Stype,OutofOrder,sub.IncomeID,item.NoDis FROM Item item INNER JOIN SystemItem sys ON item.ItemID=sys.ItemID INNER JOIN SubMenu sub ON item.SubMenuID=sub.SubMenuID INNER JOIN MainMenu main ON sub.MainMenuID=main.MainMenuID WHERE item.Barcode='"+barcode+"'", null);
        return cur;
    }
    public Cursor getAllItem(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT ItemID,ItemName,SubMenuName FROM Item item INNER JOIN SubMenu sub ON item.SubMenuID=sub.SubMenuID", null);
        return cur;
    }
    //SType
    public boolean insertSType(int stypeid,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("STypeID", stypeid);
        value.put("STypeName", name);
        db.insert("SType", null, value);
        return true;
    }
    public Cursor getAllSType(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT STypeID,STypeName FROM SType", null);
        return cur;
    }
    //Taste
    public boolean insertTaste(int id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TasteID", id);
        value.put("TasteName", name);
        db.insert("Taste", null, value);
        return true;
    }
    public Cursor getAllTaste(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM Taste", null);
        return cur;
    }
    //Taste Multi
    public boolean insertTasteMulti(int tid,int groupid,int tasteid,String tastename,String tasteshort,int tastesort,double price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TID", tid);
        value.put("GroupID",groupid);
        value.put("TasteID",tasteid);
        value.put("TasteName",tastename);
        value.put("TasteShort",tasteshort);
        value.put("TasteSort", tastesort);
        value.put("Price",price);
        db.insert("TasteMulti", null, value);
        return true;
    }
    public Cursor getAllTasteMulti(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * FROM TasteMulti", null);
        return cur;
    }
    public Cursor getTasteMultiByGroupID(int groupid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT TID,GroupID,TasteID,TasteName,TasteShort,TasteSort,Price FROM TasteMulti WHERE GroupID="+groupid, null);
        return cur;
    }
    //System Setting
    public boolean insertSystemSetting(int tax,int service,String adminPassword,String title,String userPassword){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("Tax", tax);
        value.put("Service", service);
        value.put("AdminPassword", adminPassword);
        value.put("Title",title);
        value.put("UserPassword",userPassword);
        db.insert("SystemSetting", null, value);
        return true;
    }
    public Cursor getAdminPassword(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select AdminPassword from SystemSetting", null);
        return cur;
    }
    public Cursor getUserPassword(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select UserPassword from SystemSetting", null);
        return cur;
    }
    public Cursor getServiceTax(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select Tax,Service from SystemSetting", null);
        return cur;
    }
    public Cursor getShopName(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select Title from SystemSetting", null);
        return cur;
    }
    //Printer Setting
    public boolean insertPrinterSetting(int stype,String printerip){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SType", stype);
        value.put("PrinterIP", printerip);
        db.insert("PrinterSetting", null, value);
        return true;
    }
    public Cursor getPrinterIPBySType(int stype){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PrinterIP from PrinterSetting where SType="+stype, null);
        return cur;
    }

    /**
     * methods for update data
     */
    /* waiter methods for update data*/
    public Cursor isWaiterID(int waiterid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select WaiterID from Waiter where WaiterID="+waiterid,null);
        return cur;
    }
    public boolean updateWaiter(int id,String name,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WaiterName", name);
        value.put("Password", password);
        db.update("Waiter", value, "WaiterID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public void truncateWaiterTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from WaiterTemp");
    }
    public boolean insertWaiterTemp(int id,String name,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("WaiterID", id);
        value.put("WaiterName", name);
        value.put("Password", password);
        db.insert("WaiterTemp", null, value);
        return true;
    }
    public Cursor isWaiterIDinTemp(int waiterid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select WaiterID from WaiterTemp where WaiterID="+waiterid,null);
        return cur;
    }
    public Integer deleteWaiter(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete("Waiter", "WaiterID=?", new String[]{Integer.toString(id)});
    }

    /* table type methods for update data*/
    public Cursor isTableTypeID(int tabletypeid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableTypeID from TableType where TableTypeID="+tabletypeid,null);
        return cur;
    }
    public boolean updateTableType(int id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableTypeName", name);
        db.update("TableType", value, "TableTypeID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public void truncateTableTypeTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from TableTypeTemp");
    }

    public boolean insertTableTypeTemp(int id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableTypeID", id);
        value.put("TableTypeName", name);
        db.insert("TableTypeTemp", null, value);
        return true;
    }
    public Cursor isTableTypeIDinTemp(int tabletypeid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableTypeID from TableTypeTemp where TableTypeID="+tabletypeid,null);
        return cur;
    }
    public Integer deleteTableType(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        SQLiteDatabase db2=this.getReadableDatabase();
        Cursor cur=db2.rawQuery("SELECT TableID FROM tblTable WHERE TableTypeID="+id, null);
        if(cur.getCount()==0){
            db.delete("TableType", "TableTypeID=?", new String[]{Integer.toString(id)});
            return 1;
        }
        else{
            return 0;
        }
    }
    /* table methods for update data*/
    public Cursor isTableID(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableID from tblTable where TableID="+tableid,null);
        return cur;
    }
    public boolean updateTable(int id,String name,int tableType){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableName", name);
        value.put("TableTypeID", tableType);
        db.update("tblTable", value, "TableID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public void truncateTableTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from tblTableTemp");
    }
    public boolean insertTableTemp(int id,String name,int tableType){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TableID", id);
        value.put("TableName", name);
        value.put("TableTypeID", tableType);
        db.insert("tblTableTemp", null, value);
        return true;
    }
    public Cursor isTableIDinTemp(int tableid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TableID from tblTableTemp where TableID="+tableid,null);
        return cur;
    }
    public Integer deleteTable(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete("tblTable", "TableID=?", new String[]{Integer.toString(id)});
    }
    /* main menu methods for update data*/
    public Cursor isMainMenuID(int mainmenuid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MainMenuID from MainMenu where MainMenuID="+mainmenuid,null);
        return cur;
    }
    public boolean updateMainMenu(int id,String name,int counterid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuName", name);
        value.put("CounterID", counterid);
        db.update("MainMenu", value, "MainMenuID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public void truncateMainMenuTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from MainMenuTemp");
    }
    public boolean insertMainMenuTemp(int id,String name,int counterid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("MainMenuID", id);
        value.put("MainMenuName", name);
        value.put("CounterID", counterid);
        db.insert("MainMenuTemp", null, value);
        return true;
    }
    public Cursor isMainMenuIDinTemp(int mainmenuid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select MainMenuID from MainMenuTemp where MainMenuID="+mainmenuid,null);
        return cur;
    }
    public Integer deleteMainMenu(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        SQLiteDatabase db2=this.getReadableDatabase();
        Cursor cur=db2.rawQuery("SELECT SubMenuID FROM SubMenu WHERE MainMenuID="+id, null);
        if(cur.getCount()==0){
            db.delete("MainMenu", "MainMenuID=?", new String[]{Integer.toString(id)});
            return 1;
        }
        else{
            return 0;
        }
    }
    /* sub menu methods for update data*/
    public Cursor isSubMenuID(int submenuid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubMenuID from SubMenu where SubMenuID="+submenuid,null);
        return cur;
    }
    public boolean updateSubMenu(int id,String name,int mainMenuid,String sortcode,int incomeid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubMenuName", name);
        value.put("MainMenuID", mainMenuid);
        value.put("SortCode", sortcode);
        value.put("IncomeID",incomeid);
        db.update("SubMenu", value, "SubMenuID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public void truncateSubMenuTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SubMenuTemp");
    }
    public boolean insertSubMenuTemp(int id,String name,int mainMenuid,String sortcode,int incomeid){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubMenuID", id);
        value.put("SubMenuName", name);
        value.put("MainMenuID", mainMenuid);
        value.put("SortCode", sortcode);
        value.put("IncomeID",incomeid);
        db.insert("SubMenuTemp", null, value);
        return true;
    }
    public Cursor isSubMenuIDinTemp(int submenuid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubMenuID from SubMenuTemp where SubMenuID="+submenuid,null);
        return cur;
    }
    public Integer deleteSubMenu(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        SQLiteDatabase db2=this.getReadableDatabase();
        Cursor cur=db2.rawQuery("SELECT ItemID FROM Item WHERE SubMenuID="+id, null);
        if(cur.getCount()==0){
            db.delete("SubMenu", "SubMenuID=?", new String[]{Integer.toString(id)});
            return 1;
        }
        else{
            return 0;
        }
    }
    /* item method for update data*/
    public void truncateItem(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SystemItem");
        db.execSQL("delete from Item");
    }
    /* taste methods for update data*/
    public Cursor isTasteID(int tasteid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TasteID from Taste where TasteID="+tasteid,null);
        return cur;
    }
    public boolean updateTaste(int id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TasteName", name);
        db.update("Taste", value, "TasteID=?", new String[]{Integer.toString(id)});
        return true;
    }
    public void truncateTasteTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from TasteTemp");
    }
    public boolean insertTasteTemp(int id,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TasteID", id);
        value.put("TasteName", name);
        db.insert("TasteTemp", null, value);
        return true;
    }
    public Cursor isTasteIDinTemp(int tasteid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TasteID from TasteTemp where TasteID="+tasteid,null);
        return cur;
    }
    public Integer deleteTaste(int id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete("Taste", "TasteID=?", new String[]{Integer.toString(id)});
    }
    /* taste multi methods for update data*/
    public Cursor isTasteMultiID(int tid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TID from TasteMulti where TID="+tid,null);
        return cur;
    }
    public boolean updateTasteMulti(int tid,int groupid,int tasteid,String tastename,String tasteshort,int tastesort,double price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("GroupID",groupid);
        value.put("TasteID",tasteid);
        value.put("TasteName",tastename);
        value.put("TasteShort",tasteshort);
        value.put("TasteSort", tastesort);
        value.put("Price",price);
        db.update("TasteMulti", value, "TID=?", new String[]{Integer.toString(tid)});
        return true;
    }
    public void truncateTasteMultiTemp(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from TasteMultiTemp");
    }
    public boolean insertTasteMultiTemp(int tid,int groupid,int tasteid,String tastename,String tasteshort,int tastesort,double price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("TID", tid);
        value.put("GroupID",groupid);
        value.put("TasteID",tasteid);
        value.put("TasteName",tastename);
        value.put("TasteShort",tasteshort);
        value.put("TasteSort", tastesort);
        value.put("Price",price);
        db.insert("TasteMultiTemp", null, value);
        return true;
    }
    public Cursor isTIDinTemp(int tid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select TID from TasteMultiTemp where TID="+tid,null);
        return cur;
    }
    public Integer deleteTasteMulti(int tid){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete("TasteMulti", "TID=?", new String[]{Integer.toString(tid)});
    }
    /*system setting method for update data*/
    public void truncateSystemSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SystemSetting");
    }
    /*printer setting method for update data*/
    public void truncatePrinterSetting(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from PrinterSetting");
    }
    /*slip format method for update data*/
    public void truncateSlipFormat(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SlipFormat");
    }
    /*stype method for update data*/
    public void truncateSType(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from SType");
    }
    /**
     * end update methods
     */

    /**
     * Item Sub Group
     */
    public boolean insertItemSubGroup(String groupName,String subTitle,int isSingleCheck){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("GroupName", groupName);
        value.put("SubTitle", subTitle);
        value.put("IsSingleCheck", isSingleCheck);
        db.insert("ItemSubGroup", null, value);
        return true;
    }
  /*  public boolean insertItemSubGroup(int pkId,String groupName,String subTitle,int isSingleCheck){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PKID",pkId);
        value.put("GroupName", groupName);
        value.put("SubTitle", subTitle);
        value.put("IsSingleCheck", isSingleCheck);
        db.insert("ItemSubGroup", null, value);
        return true;
    }*/
    public boolean updateItemSubGroup(int pkId,String groupName,String subTitle,int isSingleCheck){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("GroupName", groupName);
        value.put("SubTitle", subTitle);
        value.put("IsSingleCheck", isSingleCheck);
        db.update("ItemSubGroup", value, "PKID=?", new String[]{Integer.toString(pkId)});
        return true;
    }
    public boolean deleteItemSubGroup(int pkId){
        SQLiteDatabase dbRead=this.getReadableDatabase();
        Cursor cur=dbRead.rawQuery("select PKID from ItemSub where SubGroupID="+pkId,null);
        if(cur.moveToFirst()){
            return false;
        }else {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from ItemSubGroup where PKID=" + pkId);
            return true;
        }
    }
    public Cursor getItemSubGroup(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PKID,GroupName,SubTitle,IsSingleCheck from ItemSubGroup",null);
        return cur;
    }
    public Cursor getItemSubGroupByFilter(String groupName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PKID,GroupName,SubTitle,IsSingleCheck from ItemSubGroup where GroupName Like '%"+groupName+"%'",null);
        return cur;
    }

    /**
     * Item And Sub
     */
    public boolean insertItemAndSub(String itemId,int subGroupId,int levelNo){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("ItemID", itemId);
        value.put("SubGroupID", subGroupId);
        value.put("LevelNo", levelNo);
        db.insert("ItemAndSub", null, value);
        return true;
    }
   /* public boolean insertItemAndSub(int pkId,String itemId,int subGroupId,int levelNo){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PKID",pkId);
        value.put("ItemID", itemId);
        value.put("SubGroupID", subGroupId);
        value.put("LevelNo", levelNo);
        db.insert("ItemAndSub", null, value);
        return true;
    }*/
    public List<ItemAndSubData> getItemAndSub(){
        SQLiteDatabase db=this.getReadableDatabase();
        List<ItemAndSubData> lstItemAndSubData=new ArrayList<>();
        Cursor cur=db.rawQuery("select PKID,ItemID,SubGroupID,LevelNo from ItemAndSub",null);
        while(cur.moveToNext()){
            ItemAndSubData data=new ItemAndSubData();
            data.setPkId(cur.getInt(0));
            data.setItemId(cur.getString(1));
            data.setSubGroupId(cur.getInt(2));
            data.setLevelNo(cur.getInt(3));
            lstItemAndSubData.add(data);
        }
        return lstItemAndSubData;
    }
    public boolean deleteItemAndSub(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from ItemAndSub where ItemID='" + itemId + "'");
        return true;
    }
    public List<ItemSubGroupData> getItemSubGroupByItemID(String itemId){
        SystemSetting systemSetting=new SystemSetting();
        List<ItemSubGroupData> lstItemSubGroupData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubGroupID from ItemAndSub where ItemID= '"+itemId+"'",null);
        while(cur.moveToNext()){
            Cursor nextCur=db.rawQuery("select PKID,GroupName,SubTitle,IsSingleCheck from ItemSubGroup where PKID= "+cur.getInt(0),null);
            while(nextCur.moveToNext()){
                ItemSubGroupData data=new ItemSubGroupData();
                data.setPkId(nextCur.getInt(0));
                data.setSubGroupName(nextCur.getString(1));
                data.setSubTitle(nextCur.getString(2));
                if(nextCur.getInt(3)==1){
                    data.setSingleCheck(1);
                    data.setCheckType(systemSetting.SINGLE_CHECK);
                }else{
                    data.setSingleCheck(0);
                    data.setCheckType(systemSetting.MULTI_CHECK);
                }
                lstItemSubGroupData.add(data);
            }
        }
        return lstItemSubGroupData;
    }

    public List<ItemSubGroupData> getItemSubByItemID(String itemId){
        List<ItemSubGroupData> lstItemSubGroupData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select SubGroupID,SubTitle,IsSingleCheck from ItemAndSub iAnds inner join ItemSubGroup gp on iAnds.SubGroupID=gp.PKID where ItemID= '"+itemId+"' order by LevelNo",null);
        while(cur.moveToNext()){
            ItemSubGroupData groupData=new ItemSubGroupData();
            groupData.setPkId(cur.getInt(0));
            groupData.setSubTitle(cur.getString(1));
            groupData.setSingleCheck(cur.getInt(2));

            List<ItemSubData> lstItemSubData=new ArrayList<>();
            Cursor nextCur=db.rawQuery("select PKID,SubName,Price from ItemSub where SubGroupID= "+cur.getInt(0),null);
            while(nextCur.moveToNext()){
                ItemSubData data=new ItemSubData();
                data.setPkId(nextCur.getInt(0));
                data.setSubName(nextCur.getString(1));
                data.setPrice(nextCur.getInt(2));
                lstItemSubData.add(data);
            }
            groupData.setLstItemSubData(lstItemSubData);
            lstItemSubGroupData.add(groupData);
        }
        return lstItemSubGroupData;
    }

    /**
     * Item Sub
     */
    public boolean insertItemSub(int subGroupId,String subName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubGroupID", subGroupId);
        value.put("SubName", subName);
        value.put("Price", price);
        db.insert("ItemSub", null, value);
        return true;
    }
  /*  public boolean insertItemSub(int pkId,int subGroupId,String subName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("PKID",pkId);
        value.put("SubGroupID", subGroupId);
        value.put("SubName", subName);
        value.put("Price", price);
        db.insert("ItemSub", null, value);
        return true;
    }*/
    public boolean updateItemSub(int pkId,int subGroupId,String subName,int price){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues value=new ContentValues();
        value.put("SubGroupID", subGroupId);
        value.put("SubName", subName);
        value.put("Price", price);
        db.update("ItemSub", value, "PKID=?", new String[]{Integer.toString(pkId)});
        return true;
    }
    public boolean deleteItemSub(int pkId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from ItemSub where PKID=" + pkId);
        return true;
    }
    public Cursor getItemSub(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select item.PKID,SubGroupID,SubName,Price,GroupName from ItemSub item inner join ItemSubGroup gp on item.SubGroupID=gp.PKID order by SubGroupID",null);
        return cur;
    }
    public List<ItemSubData> getItemSubByGroup(int subGroupId){
        List<ItemSubData> lstItemSubData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select PKID,SubName,Price from ItemSub where SubGroupID= "+subGroupId,null);
        while(cur.moveToNext()){
            ItemSubData data=new ItemSubData();
            data.setPkId(cur.getInt(0));
            data.setSubName(cur.getString(1));
            data.setPrice(cur.getInt(2));
            lstItemSubData.add(data);
        }
        return lstItemSubData;
    }
    public Cursor getItemSubByFilter(String subName){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("select item.PKID,SubGroupID,SubName,Price,GroupName from ItemSub item inner join ItemSubGroup gp on item.SubGroupID=gp.PKID where SubName Like '%" + subName + "%' order by SubGroupID",null);
        return cur;
    }
}
