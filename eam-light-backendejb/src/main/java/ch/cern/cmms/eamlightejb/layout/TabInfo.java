package ch.cern.cmms.eamlightejb.layout;
import java.util.Map;

public class TabInfo {

	public TabInfo() {

	}

	public TabInfo(Map<String, ElementInfo> fields) {
		this.fields = fields;
	}

	private String function;

	private String tab;

	private String group;

	private boolean visible;

	private boolean selectAllowed;

	private boolean updateAllowed;

	private boolean insertAllowed;

	private boolean deleteAllowed;

	private boolean alwaysAvailable;

	private String securityDataSpy;
	
	private Map<String, ElementInfo> fields;

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param function
	 *            the function to set
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the tab
	 */
	public String getTab() {
		return tab;
	}

	/**
	 * @param tab
	 *            the tab to set
	 */
	public void setTab(String tab) {
		this.tab = tab;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the selectAllowed
	 */
	public boolean isSelectAllowed() {
		return selectAllowed;
	}

	/**
	 * @param selectAllowed
	 *            the selectAllowed to set
	 */
	public void setSelectAllowed(boolean selectAllowed) {
		this.selectAllowed = selectAllowed;
	}

	/**
	 * @return the updateAllowed
	 */
	public boolean isUpdateAllowed() {
		return updateAllowed;
	}

	/**
	 * @param updateAllowed
	 *            the updateAllowed to set
	 */
	public void setUpdateAllowed(boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}

	/**
	 * @return the insertAllowed
	 */
	public boolean isInsertAllowed() {
		return insertAllowed;
	}

	/**
	 * @param insertAllowed
	 *            the insertAllowed to set
	 */
	public void setInsertAllowed(boolean insertAllowed) {
		this.insertAllowed = insertAllowed;
	}

	/**
	 * @return the deleteAllowed
	 */
	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}

	/**
	 * @param deleteAllowed
	 *            the deleteAllowed to set
	 */
	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}

	/**
	 * @return the alwaysAvailable
	 */
	public boolean isAlwaysAvailable() {
		return alwaysAvailable;
	}

	/**
	 * @param alwaysAvailable
	 *            the alwaysAvailable to set
	 */
	public void setAlwaysAvailable(boolean alwaysAvailable) {
		this.alwaysAvailable = alwaysAvailable;
	}

	/**
	 * @return the securityDataSpy
	 */
	public String getSecurityDataSpy() {
		return securityDataSpy;
	}

	/**
	 * @param securityDataSpy
	 *            the securityDataSpy to set
	 */
	public void setSecurityDataSpy(String securityDataSpy) {
		this.securityDataSpy = securityDataSpy;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TabInfo [" + (function != null ? "function=" + function + ", " : "")
				+ (tab != null ? "tab=" + tab + ", " : "") + (group != null ? "group=" + group + ", " : "") + "visible="
				+ visible + ", selectAllowed=" + selectAllowed + ", updateAllowed=" + updateAllowed + ", insertAllowed="
				+ insertAllowed + ", deleteAllowed=" + deleteAllowed + ", alwaysAvailable=" + alwaysAvailable + ", "
				+ (securityDataSpy != null ? "securityDataSpy=" + securityDataSpy : "") + "]";
	}

	public Map<String, ElementInfo> getFields() {
		return fields;
	}

	public void setFields(Map<String, ElementInfo> fields) {
		this.fields = fields;
	}

}
