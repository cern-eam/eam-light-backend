package ch.cern.cmms.eamlightweb.user.entities;

import ch.cern.cmms.eamlightweb.user.ScreenLayoutService;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class ScreenLayout {


    private Map<String, ElementInfo> fields;
    private Map<String, Map<String, ElementInfo>> tabs;
    private Map<String, ScreenInfo> tabPermissions;

    public Map<String, ElementInfo> getFields() {
        return fields;
    }

    public void setFields(Map<String, ElementInfo> fields) {
        this.fields = fields;
    }

    public Map<String, Map<String, ElementInfo>> getTabs() {
        if (tabs == null) {
            tabs = new HashMap<>();
        }
        return tabs;
    }

    public void setTabs(Map<String, Map<String, ElementInfo>> tabs) {
        this.tabs = tabs;
    }

    public Map<String, ScreenInfo> getTabPermissions() {
        return tabPermissions;
    }

    public void setTabPermissions(Map<String, ScreenInfo> screenInfoForTabs) {
        this.tabPermissions = screenInfoForTabs;
    }

}
