package ch.cern.cmms.eamlightweb.user.entities;

import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;

import java.util.Map;

public class UserData {

	private EAMUser eamAccount;
	private Map<String, ScreenInfo> screens;
	private String assetScreen;
	private String positionScreen;
	private String systemScreen;
	private String workOrderScreen;
	private String partScreen;

	public EAMUser getEamAccount() {
		return eamAccount;
	}

	public void setEamAccount(EAMUser eamAccount) {
		this.eamAccount = eamAccount;
	}

	public Map<String, ScreenInfo> getScreens() {
		return screens;
	}

	public void setScreens(Map<String, ScreenInfo> screens) {
		this.screens = screens;
	}

	public String getAssetScreen() {
		return assetScreen;
	}

	public void setAssetScreen(String assetScreen) {
		this.assetScreen = assetScreen;
	}

	public String getPositionScreen() {
		return positionScreen;
	}

	public void setPositionScreen(String positionScreen) {
		this.positionScreen = positionScreen;
	}

	public String getSystemScreen() {
		return systemScreen;
	}

	public void setSystemScreen(String systemScreen) {
		this.systemScreen = systemScreen;
	}

	public String getWorkOrderScreen() {
		return workOrderScreen;
	}

	public void setWorkOrderScreen(String workOrderScreen) {
		this.workOrderScreen = workOrderScreen;
	}

	public String getPartScreen() {
		return partScreen;
	}

	public void setPartScreen(String partScreen) {
		this.partScreen = partScreen;
	}

	public String getLanguage() {
		return "EN";
	}


	@Override
	public String toString() {
		return "UserData [" + (eamAccount != null ? "eamAccount=" + eamAccount + ", " : "")
				+ (screens != null ? "screens=" + screens + ", " : "")
				+ (assetScreen != null ? "assetScreen=" + assetScreen + ", " : "")
				+ (positionScreen != null ? "positionScreen=" + positionScreen + ", " : "")
				+ (systemScreen != null ? "systemScreen=" + systemScreen + ", " : "")
				+ (workOrderScreen != null ? "workOrderScreen=" + workOrderScreen + ", " : "")
				+ (partScreen != null ? "partScreen=" + partScreen : "") + "]";
	}

}
