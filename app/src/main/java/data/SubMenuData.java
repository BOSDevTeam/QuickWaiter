package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class SubMenuData {

    public String subMenuName,mainMenuName,sortCode;
    public int subMenuID;
    public int mainMenuID;
    public int incomeid;

    public int getIncomeid() {
        return incomeid;
    }

    public void setIncomeid(int incomeid) {
        this.incomeid = incomeid;
    }

    public String getSubMenuName() {
        return subMenuName;
    }

    public String getMainMenuName() {
        return mainMenuName;
    }

    public String getSortCode() {
        return sortCode;
    }

    public int getSubMenuID() {
        return subMenuID;
    }

    public int getMainMenuID() {
        return mainMenuID;
    }

    public void setSubMenuName(String subMenuName) {
        this.subMenuName = subMenuName;
    }

    public void setMainMenuName(String mainMenuName) {
        this.mainMenuName = mainMenuName;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public void setSubMenuID(int subMenuID) {
        this.subMenuID = subMenuID;
    }

    public void setMainMenuID(int mainMenuID) {
        this.mainMenuID = mainMenuID;
    }
}
