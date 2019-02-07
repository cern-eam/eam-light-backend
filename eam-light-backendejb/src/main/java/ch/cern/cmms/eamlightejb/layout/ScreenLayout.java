package ch.cern.cmms.eamlightejb.layout;

import java.util.Map;

public class ScreenLayout {

	private Map<String, ElementInfo> fields;
	private Map<String, TabInfo> tabs;
	public Map<String, ElementInfo> getFields() {
		return fields;
	}
	public void setFields(Map<String, ElementInfo> fields) {
		this.fields = fields;
	}
	public Map<String, TabInfo> getTabs() {
		return tabs;
	}
	public void setTabs(Map<String, TabInfo> tabs) {
		this.tabs = tabs;
	}

	

}
