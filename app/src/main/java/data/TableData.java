package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class TableData {

    int tableTypeID;
    int tableid;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    boolean selected;

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    int isActive;
    String tableTypeName,tableName;

    public int getTableTypeID() {
        return tableTypeID;
    }

    public int getTableid() {
        return tableid;
    }

    public String getTableTypeName() {
        return tableTypeName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableTypeID(int tableTypeID) {
        this.tableTypeID = tableTypeID;
    }

    public void setTableid(int tableid) {
        this.tableid = tableid;
    }

    public void setTableTypeName(String tableTypeName) {
        this.tableTypeName = tableTypeName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
