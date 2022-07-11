package data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class ItemSubGroupData implements Serializable {

    int pkId,isSingleCheck;
    String subGroupName,subTitle,checkType;
    boolean isSelected;
    List<ItemSubData> lstItemSubData;

    public int getPkId() {
        return pkId;
    }

    public void setPkId(int pkId) {
        this.pkId = pkId;
    }

    public String getSubGroupName() {
        return subGroupName;
    }

    public void setSubGroupName(String subGroupName) {
        this.subGroupName = subGroupName;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int isSingleCheck() {
        return isSingleCheck;
    }

    public void setSingleCheck(int singleCheck) {
        isSingleCheck = singleCheck;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<ItemSubData> getLstItemSubData() {
        return lstItemSubData;
    }

    public void setLstItemSubData(List<ItemSubData> lstItemSubData) {
        this.lstItemSubData = lstItemSubData;
    }
}
