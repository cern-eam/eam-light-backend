package ch.cern.cmms.eamlightweb.user.entities;

import ch.cern.eam.wshub.core.annotations.GridField;

public class ScreenInfo {

	@GridField(name="functionname")
	private String screenCode;
	private String startupAction;
	private String parentScreen;
	@GridField(name="screenname")
	private String screenDesc;
	@GridField(name="queryallowed")
	private boolean readAllowed;
	@GridField(name="insertallowed")
	private boolean creationAllowed;
	@GridField(name="deleteallowed")
	private boolean deleteAllowed;
	@GridField(name="updateallowed")
	private boolean updateAllowed;
	@GridField(name="tab")
	private String tab;
	@GridField(name="tabalwaysdisplayed")
	private boolean tabAlwaysDisplayed;
	@GridField(name="tabavailable")
	private boolean tabAvailable;



	public String getScreenCode() {
		return screenCode;
	}
	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}
	public String getScreenDesc() {
		return screenDesc;
	}
	public void setScreenDesc(String screenDesc) {
		this.screenDesc = screenDesc;
	}
	public boolean isCreationAllowed() {
		return creationAllowed;
	}
	public void setCreationAllowed(boolean creationAllowed) {
		this.creationAllowed = creationAllowed;
	}
	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}
	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}
	public boolean isUpdateAllowed() {
		return updateAllowed;
	}
	public void setUpdateAllowed(boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}

	public boolean isReadAllowed() {
		return readAllowed;
	}
	public void setReadAllowed(boolean readAllowed) {
		this.readAllowed = readAllowed;
	}
	
	public String getParentScreen() {
		return parentScreen;
	}
	public void setParentScreen(String parentScreen) {
		this.parentScreen = parentScreen;
	}

	public boolean isTabAlwaysDisplayed() { return tabAlwaysDisplayed; }
	public void setTabAlwaysDisplayed(boolean tabAlwaysDisplayed) { this.tabAlwaysDisplayed = tabAlwaysDisplayed; }

	public boolean isTabAvailable() { return tabAvailable; }
	public void setTabAvailable(boolean tabAvailable) { this.tabAvailable = tabAvailable; }

	public String getTab() { return tab; }
	public void setTab(String tab) { this.tab = tab; }

	public String getStartupAction() {
		return startupAction;
	}

	public void setStartupAction(String startupAction) {
		this.startupAction = startupAction;
	}

	@Override
	public String toString() {
		return "ScreenInfo [screenCode=" + screenCode + ", parentScreen=" + parentScreen + ", screenDesc=" + screenDesc
				+ ", readAllowed=" + readAllowed + ", creationAllowed=" + creationAllowed + ", deleteAllowed="
				+ deleteAllowed + ", updateAllowed=" + updateAllowed + "]";
	}
	
}
