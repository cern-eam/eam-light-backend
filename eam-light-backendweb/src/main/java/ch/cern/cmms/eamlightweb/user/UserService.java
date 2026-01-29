package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightejb.cache.CacheUtils;
import ch.cern.cmms.eamlightejb.cache.Cacheable;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.user.entities.ScreenInfo;
import ch.cern.cmms.eamlightweb.user.entities.UserData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.workorders.entities.Employee;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.tools.Tools;
import com.github.benmanes.caffeine.cache.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

@ApplicationScoped
public class UserService implements Cacheable {

    private final Cache<String, EAMUser> userCache = CacheUtils.buildDefaultCache();

    @Inject
    private ScreenService screenService;
    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;

    @Override
    public void clearCache() {
        userCache.invalidateAll();
    }

    @Override
    public void setExpiresAfter(long l, TimeUnit timeUnit) {
        CacheUtils.updateCacheTimeout(userCache, l, timeUnit);
    }

    public UserData getUserData(String currentScreen, String screenCode) throws InforException {
        UserData userData = new UserData();

        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        final EAMUser eamAccount = readUserSetup(authenticationTools.getInforContext(), userCode);
        userData.setEamAccount(eamAccount);
        final InforContext r5InforContext = authenticationTools.getR5InforContext();
        r5InforContext.setLanguage(eamAccount.getLanguage());
        userData.setScreens(screenService.getScreens(r5InforContext, userData.getEamAccount().getUserGroup()));
        userData.setAssetScreen(getScreenCode("OSOBJA", "asset", currentScreen, screenCode, userData));
        userData.setPositionScreen(getScreenCode("OSOBJP", "position", currentScreen, screenCode, userData));
        userData.setSystemScreen(getScreenCode("OSOBJS", "system", currentScreen, screenCode, userData));
        userData.setWorkOrderScreen(getScreenCode("WSJOBS", "workorder", currentScreen, screenCode, userData));
        userData.setPartScreen(getScreenCode("SSPART", "part", currentScreen, screenCode, userData));
        userData.setLocationScreen(getScreenCode("OSOBJL", "location", currentScreen, screenCode, userData));
        userData.setNcrScreen(getScreenCode("OSNCHD", "ncr", currentScreen, screenCode, userData));
        userData.setNcrWorkOrderScreen("OSJOBS");

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

        // 3. Try to set the default to the master screen, if none is specified
        screenCode = screenCode != null ? screenCode : functionCode;

        // 4. Check if the screen code has any value and if the user has access to the screen with that screen code
        if (isNotEmpty(screenCode) && userData.getScreens().containsKey(screenCode)) {
            return screenCode;
        }

        // 5. Checking access to the default screen
        return userData.getScreens().values().stream()
                                     .filter(screenInfo -> functionCode.equals(screenInfo.getParentScreen()))
                                     .map(ScreenInfo::getScreenCode)
                                     .findFirst()
                                     .orElse(null);
    }

    public EAMUser readUserSetup(InforContext inforContext, String userCode) throws InforException {
        try {
            String userCacheKey = Tools.getCacheKey(inforContext, userCode);
            return userCache.get(userCacheKey, key -> loadUserSetup(inforContext, userCode));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InforException) {
                throw (InforException) e.getCause();
            }
            throw new InforException("Failed to read user setup", null, null);
        }
    }

    private EAMUser loadUserSetup(InforContext inforContext, String userCode) {
        try {
            return inforClient.getUserSetupService().readUserSetup(inforContext, userCode);
        } catch (InforException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee getEmployee(InforContext inforContext, String employeeCode) throws InforException {
        return inforClient.getEmployeeService().readEmployee(inforContext, employeeCode);
    }
}
