package data;

/**
 * Created by User on 9/8/2017.
 */
public class OpenOrderKitchenData {

    String tableName;
    String waiterName;
    String datetime;
    String itemName;
    String taste;
    String stringQty;
    int tranid;
    int tableid;

    public int getTableid() {
        return tableid;
    }

    public void setTableid(int tableid) {
        this.tableid = tableid;
    }

    public String getStringQty() {
        return stringQty;
    }

    public void setStringQty(String stringQty) {
        this.stringQty = stringQty;
    }

    public String getTableName() {
        return tableName;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getItemName() {
        return itemName;
    }

    public String getTaste() {
        return taste;
    }

    public int getTranid() {
        return tranid;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setTaste(String taste) {
        this.taste = taste;
    }

    public void setTranid(int tranid) {
        this.tranid = tranid;
    }
}
