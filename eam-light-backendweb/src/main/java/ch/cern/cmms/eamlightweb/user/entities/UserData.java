package ch.cern.cmms.eamlightweb.user.entities;

import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;

import java.util.List;
import java.util.Map;

public class UserData {

	private EAMUser eamAccount;
	private Map<String, ScreenInfo> screens;
	private String assetScreen;
	private String positionScreen;
	private String systemScreen;
	private String workOrderScreen;
	private String partScreen;
	private String locationScreen;
	private Map<String, List<Map<String, String>>> reports;

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

	public String getLocationScreen() {
		return locationScreen;
	}

	public void setLocationScreen(String locationScreen) {
		this.locationScreen = locationScreen;
	}

	public String getLanguage() {
		return "EN";
	}

	public Map<String, List<Map<String, String>>> getReports() {
		return reports;
	}

	public void setReports(Map<String, List<Map<String, String>>> reports) {
		this.reports = reports;
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
