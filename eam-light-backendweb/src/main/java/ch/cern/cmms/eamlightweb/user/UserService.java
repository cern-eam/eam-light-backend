package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.user.entities.ScreenInfo;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.user.entities.UserData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class UserService {

    @Inject
    private ScreenService screenService;
    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;

    public UserData getUserData(String currentScreen, String screenCode) throws InforException {
        UserData userData = new UserData();

        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        userData.setEamAccount(inforClient.getUserSetupService().readUserSetup(authenticationTools.getInforContext(), userCode));
        userData.setScreens(screenService.getScreens(authenticationTools.getR5InforContext(), userData.getEamAccount().getUserGroup()));

        userData.setAssetScreen(getScreenCode("OSOBJA", "asset", currentScreen, screenCode, userData));
        userData.setPositionScreen(getScreenCode("OSOBJP", "position", currentScreen, screenCode, userData));
        userData.setSystemScreen(getScreenCode("OSOBJS", "system", currentScreen, screenCode, userData));
        userData.setWorkOrderScreen(getScreenCode("WSJOBS", "workorder", currentScreen, screenCode, userData));
        userData.setPartScreen(getScreenCode("SSPART", "part", currentScreen, screenCode, userData));

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
                screenCode = userData.getEamAccount().getUdfchar05();
                break;
            case "SSPART":/* UDF06 - Part */
                screenCode = userData.getEamAccount().getUdfchar06();
                break;
            case "OSOBJA":/* UDF07 - Asset */
                screenCode = userData.getEamAccount().getUdfchar07();
                break;
            case "OSOBJP":/* UDF08 - Position */
                screenCode = userData.getEamAccount().getUdfchar08();
                break;
            case "OSOBJS":/* UDF09 - System */
                screenCode = userData.getEamAccount().getUdfchar09();
                break;
        }

        // Check now if the screen code has any value
        final String defaultScreenCode = screenCode;
        if (isNotEmpty(defaultScreenCode)
                && userData.getScreens().values().stream().anyMatch(s -> defaultScreenCode.equals(s.getScreenCode()))) {
            return defaultScreenCode;
        }

        // 3. Checking access to the default screen
        String stream = userData.getScreens().values().stream()
                                     .filter(screenInfo -> functionCode.equals(screenInfo.getParentScreen()))
                                     .map(ScreenInfo::getScreenCode)
                                     .findFirst()
                                     .orElse(null);

        return stream;
    }

}
