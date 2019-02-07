package ch.cern.cmms.eamlightejb.layout;

import javax.persistence.*;
import java.util.Map;

@NamedNativeQueries({
		@NamedNativeQuery(name = TabInfo.FETCH_TAB_INFO, query = "SELECT TRP_FUNCTION, TRP_TAB, TRP_GROUP, "
				+ "DECODE(TRP_VISIBLE,'+',1,'-',0,0) TRP_VISIBLE, DECODE(TRP_SELECT,'?',1,0) TRP_SELECT, "
				+ "DECODE(TRP_UPDATE,'*',1,0) TRP_UPDATE, DECODE(TRP_INSERT,'+',1,'-',0,0) TRP_INSERT, "
				+ "DECODE(TRP_DELETE,'X',1,0) TRP_DELETE, DECODE(TRP_SYSREQUIRED,'+',1,'-',0,0) TRP_SYSREQUIRED, "
				+ "TRP_SECURITYDDSPYID FROM R5TABPERMISSIONS WHERE TRP_FUNCTION = :function AND TRP_TAB IN :tabnames "
				+ "AND TRP_GROUP= :usergroup", resultClass = TabInfo.class) })
@Entity
public class TabInfo {

	public static final String FETCH_TAB_INFO = "TabInfo.FETCH_TAB_INFO";

	
	@Column(name = "TRP_FUNCTION")
	private String function;

	@Id
	@Column(name = "TRP_TAB")
	private String tab;

	@Column(name = "TRP_GROUP")
	private String group;

	@Column(name = "TRP_VISIBLE")
	private boolean visible;

	@Column(name = "TRP_SELECT")
	private boolean selectAllowed;

	@Column(name = "TRP_UPDATE")
	private boolean updateAllowed;

	@Column(name = "TRP_INSERT")
	private boolean insertAllowed;

	@Column(name = "TRP_DELETE")
	private boolean deleteAllowed;

	@Column(name = "TRP_SYSREQUIRED")
	private boolean alwaysAvailable;

	@Column(name = "TRP_SECURITYDDSPYID")
	private String securityDataSpy;
	
	@Transient
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
