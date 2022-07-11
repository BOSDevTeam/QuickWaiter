package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class CustomerData {

    public int getMan() {
        return man;
    }

    public int getWomen() {
        return women;
    }

    public int getChild() {
        return child;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTableName() {
        return tableName;
    }

    public void setMan(int man) {
        this.man = man;
    }

    public void setWomen(int women) {
        this.women = women;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setCustomerInfoId(int customerInfoId) {
        this.customerInfoId = customerInfoId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public void setTranId(int tranId) {
        this.tranId = tranId;
    }

    int man;
    int women;
    int child;

    public void setTotal(int total) {
        this.total = total;
    }

    int total;

    public int getTotal() {
        return total;
    }

    public int getCustomerInfoId() {
        return customerInfoId;
    }

    public int getTableId() {
        return tableId;
    }

    public int getTranId() {
        return tranId;
    }

    int customerInfoId;
    int tableId;
    int tranId;
    String date,time,tableName;

}
