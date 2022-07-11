package data;

import java.util.List;

public class PrintOrderData {
    public List<TransactionData> getLstTran() {
        return lstTran;
    }

    public void setLstTran(List<TransactionData> lstTran) {
        this.lstTran = lstTran;
    }

    List<TransactionData> lstTran;
    public int getsTypeId() {
        return sTypeId;
    }

    public int getTableId() {
        return tableId;
    }

    public int getUserId() {
        return userId;
    }

    public String getsTypeName() {
        return sTypeName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getUserName() {
        return userName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setsTypeId(int sTypeId) {
        this.sTypeId = sTypeId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setsTypeName(String sTypeName) {
        this.sTypeName = sTypeName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    int sTypeId,tableId,userId;
    String sTypeName;
    String tableName;
    String userName;
    String dateTime;

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String date;
    String time;
}
