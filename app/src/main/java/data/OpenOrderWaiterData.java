package data;

/**
 * Created by User on 8/31/2017.
 */
public class OpenOrderWaiterData {

    int tranid;
    String date;
    String time;
    String table;
    int guest;
    int waiterid;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTranid() {
        return tranid;
    }

    public void setTranid(int tranid) {
        this.tranid = tranid;
    }

    public int getWaiterid() {
        return waiterid;
    }

    public void setWaiterid(int waiterid) {
        this.waiterid = waiterid;
    }

    public String getDate() {
        return date;
    }

    public String getTable() {
        return table;
    }

    public int getGuest() {
        return guest;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setGuest(int guest) {
        this.guest = guest;
    }
}
