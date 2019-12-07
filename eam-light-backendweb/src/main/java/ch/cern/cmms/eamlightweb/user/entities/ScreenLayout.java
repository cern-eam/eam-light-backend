package ch.cern.cmms.eamlightweb.user.entities;

import java.util.HashMap;
import java.util.Map;

public class ScreenLayout {

	private Map<String, ElementInfo> fields;
	private Map<String, Map<String, ElementInfo>> tabs;

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

}
