package data;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class FeatureSettingData {

    String featureName;
    int isAllow,featureID;
    boolean selected;

    public int getFeatureID() {
        return featureID;
    }

    public void setFeatureID(int featureID) {
        this.featureID = featureID;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getFeatureName() {
        return featureName;
    }

    public int getIsAllow() {
        return isAllow;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setIsAllow(int isAllow) {
        this.isAllow = isAllow;
    }
}
