package ch.cern.cmms.eamlightweb.user.entities;

import java.util.HashMap;
import java.util.Map;

public class Tab {
    Map<String, ElementInfo> fields;
    Boolean alwaysDisplayed;
    Boolean tabAvailable;
    String tabDescription;

    public Tab() {
        fields = new HashMap<>();
        alwaysDisplayed = false;
        tabAvailable = false;
    }

    public Map<String, ElementInfo> getFields() {
        return fields;
    }

    public void setFields(Map<String, ElementInfo> fields) {
        this.fields = fields;
    }

    public Boolean getAlwaysDisplayed() {
        return alwaysDisplayed;
    }

    public void setAlwaysDisplayed(Boolean alwaysDisplayed) {
        this.alwaysDisplayed = alwaysDisplayed;
    }

    public Boolean getTabAvailable() {
        return tabAvailable;
    }

    public void setTabAvailable(Boolean tabAvailable) {
        this.tabAvailable = tabAvailable;
    }

    public String getTabDescription() {
        return tabDescription;
    }

    public void setTabDescription(String tabDescription) {
        this.tabDescription = tabDescription;
    }
}
