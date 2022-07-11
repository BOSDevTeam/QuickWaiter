package data;

/**
 * Created by NweYiAung on 21-02-2017.
 */
public class BookingData {

    String guestName,phone,date,time,purpose,remark,waiterName, bookingTableName;
    int totalPeople,waiterid,bookingTableid,bookingid;

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getRemark() {
        return remark;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getBookingTableName() {
        return bookingTableName;
    }

    public String getPhone() {
        return phone;
    }

    public int getTotalPeople() {
        return totalPeople;
    }

    public int getWaiterid() {
        return waiterid;
    }

    public int getBookingTableid() {
        return bookingTableid;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public void setBookingTableName(String bookingTableName) {
        this.bookingTableName = bookingTableName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTotalPeople(int totalPeople) {
        this.totalPeople = totalPeople;
    }

    public void setWaiterid(int waiterid) {
        this.waiterid = waiterid;
    }

    public void setBookingTableid(int bookingTableid) {
        this.bookingTableid = bookingTableid;
    }
}
