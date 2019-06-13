package ch.cern.cmms.eamlightweb.user;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import com.fasterxml.jackson.annotation.JsonInclude;

import ch.cern.cmms.eamlightejb.layout.LayoutBean;
import ch.cern.cmms.eamlightejb.layout.ScreenInfo;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.entities.EAMUser;
import org.jboss.logging.Logger;

@RequestScoped
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

	@EJB
	private LayoutBean layoutBean;
	@Inject
	private ApplicationData applicationData;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;
	@Inject
	private LoggingService loggingService;

	@PostConstruct
	private void init() {
		//
		//
		//
		try {
			String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
			eamAccount = inforClient.getUserSetupService().readUserSetup(authenticationTools.getInforContext(), userCode);
			// Invalid account?
			if (getEamAccount() == null || "*INA".equals(getEamAccount().getClassCode())) {
				return;
			}
		} catch (Exception e) {
			loggingService.log(Logger.Level.FATAL, "Couldn't read eam user " + e.getMessage());
		}
		//
		// USER SCREENS
		//
		ArrayList<String> functionCodes = new ArrayList<String>();
		functionCodes.add("WSJOBS");
		functionCodes.add("OSOBJA");
		functionCodes.add("OSOBJP");
		functionCodes.add("OSOBJS");
		functionCodes.add("SSPART");
		screens = layoutBean.getUserScreens(functionCodes, getEamAccount().getUserCode());

		//
		// CREDENTIALS
		//
		credentials = new Credentials();
		credentials.setUsername(getEamAccount().getUserCode());
		credentials.setPassword(applicationData.getAdminPassword());

	}

	private Credentials credentials;
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

	public Map<String, ScreenInfo> getScreens() {
		return screens;
	}

	//
	// Default screens
	//
	public String getAssetScreen(String requestScreen) {
		if (assetScreen == null) {
			assetScreen = getScreenCode("OSOBJA", requestScreen);
		}
		return assetScreen;
	}

	public String getPositionScreen(String requestScreen) {
		if (positionScreen == null) {
			positionScreen = getScreenCode("OSOBJP", requestScreen);
		}
		return positionScreen;
	}

	public String getSystemScreen(String requestScreen) {
		if (systemScreen == null) {
			systemScreen = getScreenCode("OSOBJS", requestScreen);
		}
		return systemScreen;
	}

	public String getWorkOrderScreen(String requestScreen) {
		if (workOrderScreen == null) {
			workOrderScreen = getScreenCode("WSJOBS", requestScreen);
		}
		return workOrderScreen;
	}

	public String getPartScreen(String requestScreen) {
		if (partScreen == null) {
			partScreen = getScreenCode("SSPART", requestScreen);
		}
		return partScreen;
	}

	/**
	 * Gets the screen code according to the function code
	 * 
	 * @param functionCode
	 *            Function code
	 * @return The code of the screen. The priority is the following:<br/>
	 *         1. Check the screen code received in the request<br/>
	 *         2. Check the screen code in the user defined fields<br/>
	 *         3. Check the default screen for the user<br/>
	 */
	@XmlTransient
	private String getScreenCode(String functionCode, String requestScreen) {
		String screenCode = null;

		// 1. Checking screen code provided in URL
		if (requestScreen != null) {
			if (screens.get(requestScreen) != null
					&& screens.get(requestScreen).getParentScreen().equals(functionCode)) {
				return requestScreen;
			} else {
				return null;
			}
		}

		// 2. Checking in the UDFs according to the function code
		switch (functionCode) {
		case "WSJOBS":/* Work Order */
			// Check UDF05
			screenCode = getEamAccount().getUdfchar05();
			break;
		case "SSPART":/* Part */
			// Check UDF06
			screenCode = getEamAccount().getUdfchar06();
			break;
		case "OSOBJA":/* Asset */
			// Check UDF07
			screenCode = getEamAccount().getUdfchar07();
			break;
		case "OSOBJP":/* Position */
			// Check UDF08
			screenCode = getEamAccount().getUdfchar08();
			break;
		case "OSOBJS":/* System */
			// Check UDF09
			screenCode = getEamAccount().getUdfchar09();
			break;
		}
		// Check now if the screen code has any value
		if (screenCode != null && !"".equals(screenCode.trim()))
			return screenCode;

		// 3. Checking access to the default screen
		ScreenInfo screenInfo = layoutBean.getUserDefaultScreen(functionCode, getEamAccount().getUserCode());
		if (screenInfo != null) {
			return screenInfo.getScreenCode();
		}
		// Otherwise user has no screen visible for this function
		return null;
	}

	/**
	 * Obtains the default language to display the application
	 * 
	 * @return The default language of the application
	 */
	public String getLanguage() {
		return "EN";
	}

	public UserData copy(String currentScreen, String screenCode) {
		if (screenCode.equals("undefined")) {
			screenCode = "";
		}
		UserData userData = new UserData();
		userData.eamAccount = eamAccount;
		userData.assetScreen = getAssetScreen(
				"asset".equals(currentScreen) && !"".equals(screenCode) ? screenCode : null);
		userData.positionScreen = getPositionScreen(
				"position".equals(currentScreen) && !"".equals(screenCode) ? screenCode : null);
		userData.systemScreen = getSystemScreen(
				"system".equals(currentScreen) && !"".equals(screenCode) ? screenCode : null);
		userData.workOrderScreen = getWorkOrderScreen(
				"workorder".equals(currentScreen) && !"".equals(screenCode) ? screenCode : null);
		userData.partScreen = getPartScreen("part".equals(currentScreen) && !"".equals(screenCode) ? screenCode : null);
		userData.screens = screens;
		return userData;
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
