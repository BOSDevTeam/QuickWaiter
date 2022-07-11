package data;

/**
 * Created by User on 6/21/2017.
 */
public class TasteMultiData {

    int tid,groupid,tasteid,tasteSort;
    String tasteName,tasteShort;
    double price;

    public int getTid() {
        return tid;
    }

    public int getGroupid() {
        return groupid;
    }

    public int getTasteid() {
        return tasteid;
    }

    public int getTasteSort() {
        return tasteSort;
    }

    public String getTasteName() {
        return tasteName;
    }

    public String getTasteShort() {
        return tasteShort;
    }

    public double getPrice() {
        return price;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public void setTasteid(int tasteid) {
        this.tasteid = tasteid;
    }

    public void setTasteSort(int tasteSort) {
        this.tasteSort = tasteSort;
    }

    public void setTasteName(String tasteName) {
        this.tasteName = tasteName;
    }

    public void setTasteShort(String tasteShort) {
        this.tasteShort = tasteShort;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
