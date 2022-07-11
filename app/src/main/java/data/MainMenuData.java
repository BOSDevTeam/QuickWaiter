package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class MainMenuData {

    public String mainMenuName;
    public int mainMenuID,isAllow,counterid;
    boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getMainMenuName() {
        return mainMenuName;
    }

    public int getMainMenuID() {
        return mainMenuID;
    }

    public int getIsAllow() {
        return isAllow;
    }

    public int getCounterid() {
        return counterid;
    }

    public void setMainMenuName(String mainMenuName) {
        this.mainMenuName = mainMenuName;
    }

    public void setMainMenuID(int mainMenuID) {
        this.mainMenuID = mainMenuID;
    }

    public void setIsAllow(int isAllow) {
        this.isAllow = isAllow;
    }

    public void setCounterid(int counterid) {
        this.counterid = counterid;
    }
}
