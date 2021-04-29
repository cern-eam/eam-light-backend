package ch.cern.cmms.eamlightweb.user;

import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

import ch.cern.cmms.eamlightejb.equipment.EquipmentTreeNode;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.user.entities.ScreenInfo;
import ch.cern.cmms.eamlightweb.user.entities.UserData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.workorders.entities.Employee;
import ch.cern.eam.wshub.core.tools.InforException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserService {

    @Inject
    private ScreenService screenService;
    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;
    public static final Map<String, EAMUser> userCache = new ConcurrentHashMap<>();

    public UserData getUserData(String currentScreen, String screenCode) throws InforException {
        UserData userData = new UserData();

        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        userData.setEamAccount(readUserSetup(authenticationTools.getInforContext(), userCode));
        userData.setScreens(screenService.getScreens(authenticationTools.getR5InforContext(), userData.getEamAccount().getUserGroup()));

        userData.setAssetScreen(getScreenCode("OSOBJA", "asset", currentScreen, screenCode, userData));
        userData.setPositionScreen(getScreenCode("OSOBJP", "position", currentScreen, screenCode, userData));
        userData.setSystemScreen(getScreenCode("OSOBJS", "system", currentScreen, screenCode, userData));
        userData.setWorkOrderScreen(getScreenCode("WSJOBS", "workorder", currentScreen, screenCode, userData));
        userData.setPartScreen(getScreenCode("SSPART", "part", currentScreen, screenCode, userData));
        userData.setLocationScreen(getScreenCode("OSOBJL", "location", currentScreen, screenCode, userData));


        userData.setReports(screenService.getReports(authenticationTools.getInforContext(), userData.getEamAccount().getUserGroup()));

        return userData;
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
    private String getScreenCode(String functionCode, String screen, String currentScreen, String requestScreen, UserData userData) {

        // 1. Checking screen code provided in URL
        if (screen.equals(currentScreen) && isNotEmpty(requestScreen)) {
            if (userData.getScreens().get(requestScreen) != null && userData.getScreens().get(requestScreen).getParentScreen().equals(functionCode)) {
                return requestScreen;
            } else {
                return null;
            }
        }

        // 2. Checking in the UDFs according to the function code
        String screenCode = null;
        switch (functionCode) {
            case "WSJOBS":/* UDF05 - Work Order */
                screenCode = userData.getEamAccount().getUserDefinedFields().getUdfchar05();
                break;
            case "SSPART":/* UDF06 - Part */
                screenCode = userData.getEamAccount().getUserDefinedFields().getUdfchar06();
                break;
            case "OSOBJA":/* UDF07 - Asset */
                screenCode = userData.getEamAccount().getUserDefinedFields().getUdfchar07();
                break;
            case "OSOBJP":/* UDF08 - Position */
                screenCode = userData.getEamAccount().getUserDefinedFields().getUdfchar08();
                break;
            case "OSOBJS":/* UDF09 - System */
                screenCode = userData.getEamAccount().getUserDefinedFields().getUdfchar09();
                break;
        }

        // Check if the screen code has any value and if the user has access to the screen with that screen code
        if (isNotEmpty(screenCode) && userData.getScreens().containsKey(screenCode)) {
            return screenCode;
        }

        // 3. Checking access to the default screen
        String stream = userData.getScreens().values().stream()
                                     .filter(screenInfo -> functionCode.equals(screenInfo.getParentScreen()))
                                     .map(ScreenInfo::getScreenCode)
                                     .findFirst()
                                     .orElse(null);

        return stream;
    }

    public EAMUser readUserSetup(InforContext inforContext, String userCode) throws InforException {
        if (!userCache.containsKey(userCode)) {
            userCache.put(userCode, inforClient.getUserSetupService().readUserSetup(inforContext, userCode));
        }
        return userCache.get(userCode);
    }

    public Employee getEmployee(InforContext inforContext, String employeeCode) throws InforException {
        return inforClient.getEmployeeService().readEmployee(inforContext, employeeCode);
    }



}
