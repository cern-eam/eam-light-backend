package ch.cern.cmms.eamlightweb.tools;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.application.ApplicationService;
import ch.cern.cmms.eamlightweb.user.UserService;
import ch.cern.cmms.plugins.LDAPPlugin;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.workorders.entities.Employee;
import ch.cern.eam.wshub.core.tools.DataTypeTools;
import ch.cern.eam.wshub.core.tools.InforException;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;

import static ch.cern.eam.wshub.core.tools.DataTypeTools.isEmpty;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

@RequestScoped
public class AuthenticationTools {
    public enum Mode {
        ALL,
        PERSON
    }

    @Inject
    private HttpServletRequest request;
    @Inject
    private InforClient inforClient;
    @Inject
    private ApplicationData applicationData;
    @Inject
    private OpenIdTools openIdTools;
    @Inject
    private ApplicationService applicationService;
    @Inject
    private UserService userService;
    @Inject
    private LDAPPlugin ldapPlugin;

    public InforContext getInforContext() throws InforException
    {
        String user = null;
        String password = null;
        String organization = null;
        String tenant = null;
        String sessionid = null;
        String authenticationMode = applicationData.getAuthenticationMode();
        String localizeResults = request.getHeader("INFOR_LOCALIZE_RESULTS");
        if ("LOCAL".equalsIgnoreCase(authenticationMode)) {
            user = applicationData.getDefaultUser();
            if (user == null) {
                user = System.getProperty("DEFAULT_USER");
            }
            user = getUnderlyingUser(user);
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else if ("SSO".equalsIgnoreCase(authenticationMode)) {
            user = getUnderlyingUser(request.getHeader("ADFS_LOGIN"));
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else if ("OPENID".equalsIgnoreCase(authenticationMode)) {
            String header = request.getHeader("Authorization");
            user = getUnderlyingUser(openIdTools.getUserName(header));
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else if ("KEYCLOAK".equalsIgnoreCase(authenticationMode)) {
            try {
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) request.getUserPrincipal();
                String username = kp.getKeycloakSecurityContext().getToken().getPreferredUsername();
                user = getUnderlyingUser(username.toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
            password = applicationData.getAdminPassword();
            tenant = applicationData.getTenant();
            organization = applicationData.getDefaultOrganization();
        } else {
            user = request.getHeader("INFOR_USER");
            password = request.getHeader("INFOR_PASSWORD");
            tenant = request.getHeader("INFOR_TENANT");
            if (isEmpty(tenant)) {
                tenant = applicationData.getTenant();
            }
            organization = request.getHeader("INFOR_ORGANIZATION");
            if (isEmpty(organization)) {
                organization = applicationData.getDefaultOrganization();
            }
            sessionid = request.getHeader("INFOR_SESSIONID");
        }

        InforContext inforContext = new InforContext();

        if (isEmpty(localizeResults)) {
            inforContext.setLocalizeResults(false);
        } else {
            inforContext.setLocalizeResults("true".equalsIgnoreCase(localizeResults));
        }
        // Organization
        if (isEmpty(organization)) {
            throw inforClient.getTools().generateFault("Organization is required.");
        }
        inforContext.setOrganizationCode(organization);

        // Tenant
        if (isEmpty(tenant)) {
            throw inforClient.getTools().generateFault("Tenant is required.");
        }
        inforContext.setTenant(tenant);

        // Credentials, Session ID
        if (isNotEmpty(user)) {
            Credentials credentials = new Credentials();
            credentials.setUsername(user.toUpperCase());
            credentials.setPassword(password);
            inforContext.setCredentials(credentials);
        }

        if (isNotEmpty(sessionid)) {
            inforContext.setSessionID(sessionid);
            inforContext.setKeepSession(true);
        }

        if (isEmpty(sessionid) && (isEmpty(user) || isEmpty(password))) {
            throw inforClient.getTools().generateFault("Credentials or Session ID is required.");
        }

        return inforContext;
    }

    private  String getUnderlyingUser(String authenticatedUser) throws InforException {
        return getFinalUser(authenticatedUser, request.getHeader("INFOR_USER"));
    }

    private  String getFinalUser(String authenticatedUser, String impersonatedUser) throws InforException {
        String authenticationMode = applicationData.getAuthenticationMode();
        String tenant = request.getHeader("INFOR_TENANT");
        if (isEmpty(tenant)) {
            tenant = applicationData.getTenant();
        }
        boolean allowImpersonation = isNotEmpty(impersonatedUser)
                && Arrays.asList("SSO", "LOCAL", "OPENID", "KEYCLOAK").contains(authenticationMode)
                && applicationService.getServiceAccounts(tenant).containsKey(authenticatedUser)
                && userIsAllowed(authenticatedUser, impersonatedUser, tenant)
                ;
        return allowImpersonation ? impersonatedUser : authenticatedUser;
    }

    private boolean userIsAllowed(String authenticatedUser, String impersonatedUser, String tenant) throws InforException {
        try {
            Set<String> egroupMembers =
                    ldapPlugin.readEgroupMembers(applicationService.getServiceAccounts(tenant).get(authenticatedUser));
            return egroupMembers.contains(impersonatedUser);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getEmployee(String code, Mode mode) throws InforException {
        GridRequest gridRequest = new GridRequest("42", "LVPERS", "42");
        gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
        gridRequest.setRowCount(10);
        gridRequest.addParam("parameter.per_type", null);
        gridRequest.addParam("param.bypassdeptsecurity", "true");
        gridRequest.addParam("param.sessionid", null);
        gridRequest.addParam("parameter.noemployees", null);
        gridRequest.addParam("param.shift", null);

        if (mode == Mode.ALL) {
            gridRequest.addFilter("personcode", code, "EQUALS", GridRequestFilter.JOINER.OR);
        }
        gridRequest.addFilter("udfnum02", code, "EQUALS");

        GridRequestResult gridRequestResult = inforClient.getGridsService().executeQuery(getInforContext(), gridRequest);

        if (gridRequestResult.getRows().length == 0) {
            throw new InforException("No employee matched the code " + code, null, null);
        } else if (gridRequestResult.getRows().length > 1) {
            throw new InforException("Multiple employees matched the code " + code, null, null);
        }
        String content = Arrays.stream(gridRequestResult.getRows()[0].getCell())
                .filter(row -> "personcode".equals(row.getTag()))
                .filter(row -> row.getContent() != null)
                .findAny()
                .orElseThrow(() -> new InforException("Multiple employees matched the code " + code, null, null))
                .getContent();
        return content;
    }

    private void checkCanImpersonateUser(String userCode) throws InforException {
        final InforContext inforContext = getInforContext();
        String username = inforContext.getCredentials().getUsername();
        if (!userIsAllowed(username, userCode, inforContext.getTenant())) {
            throw new InforException("" + username + " is not allowed to impersonate " + userCode + ".", null, null);
        }
    }

    public EAMUser getUserToImpersonate(String userId, Mode mode) throws InforException {
        String code = userId;
        boolean isNumber = userId != null && userId.matches("^[0-9]*$");
        if (isNumber) {
            code = getEmployee(userId, mode);
            Employee employee = userService.getEmployee(getInforContext(), code);
            code = employee.getUserCode();
        } else if (mode == Mode.PERSON) {
            throw new InforException("You must use Person ID for this feature.", null, null);
        }
        if (DataTypeTools.isEmpty(code)) {
            throw new InforException("Employee " + userId + " does not have associated a user account.", null, null);
        }
        EAMUser eamUser = userService.readUserSetup(getInforContext(), code);
        checkCanImpersonateUser(eamUser.getUserCode());
        return eamUser;
    }

    public InforContext getR5InforContext() throws InforException {
        InforContext inforContext = new InforContext();
        inforContext.setCredentials(new Credentials());
        // Username
        if (isEmpty(applicationData.getAdminUser())) {
            inforContext.getCredentials().setUsername(request.getHeader("INFOR_USER"));
        } else {
            inforContext.getCredentials().setUsername(applicationData.getAdminUser());
        }

        // Password
        if (isEmpty(applicationData.getAdminPassword())) {
            inforContext.getCredentials().setPassword(request.getHeader("INFOR_PASSWORD"));
        } else {
            inforContext.getCredentials().setPassword(applicationData.getAdminPassword());
        }

        // Organization
        if (isEmpty(request.getHeader("INFOR_ORGANIZATION"))) {
            inforContext.setOrganizationCode(applicationData.getDefaultOrganization());
        } else {
            inforContext.setOrganizationCode(request.getHeader("INFOR_ORGANIZATION"));
        }

        // Sessionid
        if (!isEmpty(request.getHeader("INFOR_SESSIONID"))) {
            inforContext.setSessionID(request.getHeader("INFOR_SESSIONID"));
        }
        return inforContext;
    }

    public String getOrganizationCode() {
        try {
            return inforClient.getTools().getOrganizationCode(getInforContext());
        } catch (Exception exception) {
            return null;
        }
    }

}
