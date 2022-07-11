package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ItemData {

    String itemid,itemName,subMenuName,mainMenuName;
    int sysid;
    int subMenuID;
    int mainMenuID;
    int stype;
    int outOfOrder;
    int counterID;
    int incomeid;
    double price;
    String ingredients;
    int noDis;
    int itemDis;
    int pNumber;

    public int getpNumber() {
        return pNumber;
    }

    public void setpNumber(int pNumber) {
        this.pNumber = pNumber;
    }

    public int getItemDis() {
        return itemDis;
    }

    public void setItemDis(int itemDis) {
        this.itemDis = itemDis;
    }

    public int getNoDis() {
        return noDis;
    }

    public void setNoDis(int noDis) {
        this.noDis = noDis;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getIncomeid() {
        return incomeid;
    }

    public void setIncomeid(int incomeid) {
        this.incomeid = incomeid;
    }

    public int getCounterID() {
        return counterID;
    }

    public void setCounterID(int counterID) {
        this.counterID = counterID;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setSubMenuName(String subMenuName) {
        this.subMenuName = subMenuName;
    }

    public void setMainMenuName(String mainMenuName) {
        this.mainMenuName = mainMenuName;
    }

    public void setSysid(int sysid) {
        this.sysid = sysid;
    }

    public void setSubMenuID(int subMenuID) {
        this.subMenuID = subMenuID;
    }

    public void setMainMenuID(int mainMenuID) {
        this.mainMenuID = mainMenuID;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    public void setOutOfOrder(int outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemid() {
        return itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public String getSubMenuName() {
        return subMenuName;
    }

    public String getMainMenuName() {
        return mainMenuName;
    }

    public int getSysid() {
        return sysid;
    }

    public int getSubMenuID() {
        return subMenuID;
    }

    public int getMainMenuID() {
        return mainMenuID;
    }

    public int getStype() {
        return stype;
    }

    public int getOutOfOrder() {
        return outOfOrder;
    }

    public double getPrice() {
        return price;
    }
}
