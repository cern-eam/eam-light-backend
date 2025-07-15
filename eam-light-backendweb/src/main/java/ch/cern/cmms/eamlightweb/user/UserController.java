package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.entities.UserData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.administration.entities.ElementInfo;
import ch.cern.eam.wshub.core.services.administration.entities.ScreenLayout;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/users")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class UserController extends EAMLightController {

	@Inject
	private UserService userService;
	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/")
	@Produces("application/json")
	public Response readUserData(@QueryParam("currentScreen") String currentScreen,
								 @QueryParam("screenCode") String screenCode) {
		try {
			final UserData userData = userService.getUserData(currentScreen, screenCode);
			return ok(userData);
		} catch (InforException e){
			return forbidden(e);
		} catch (Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/screenlayout/{userGroup}/{entity}/{systemFunction}/{userFunction}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readScreenLayout(@PathParam("userGroup") String userGroup,
									 @PathParam("entity") String entity,
									 @PathParam("systemFunction") String systemFunction,
									 @PathParam("userFunction") String userFunction,
									 @QueryParam("lang") String language,
									 @QueryParam("tabname") List<String> tabs) throws InforException {
		try {
            if (language == null) {
				final InforContext inforContext = authenticationTools.getInforContext();
				final EAMUser eamUser = userService.readUserSetup(inforContext, inforContext.getCredentials().getUsername());
				language = eamUser.getLanguage();
			}

			final InforContext r5InforContext = authenticationTools.getR5InforContext();
			r5InforContext.setLanguage(language);
			ScreenLayout screenLayout = inforClient.getScreenLayoutService().readScreenLayout(r5InforContext, systemFunction, userFunction, tabs, userGroup, entity);
			for (ElementInfo elementInfo : screenLayout.getFields().values()) {
				String xpath = elementInfo.getXpath();
				if (xpath != null && xpath.startsWith("EAMID_")) {
					// Remove "EAMID_" and the next segment
					String[] parts = xpath.split("_", 3); // split into 3 parts: ["EAMID", "xxx", "rest"]
					if (parts.length == 3) {
						String transformed = parts[2].replace("_", ".");
						elementInfo.setXpath(transformed); // update xpath
					}
				}
			}
			return ok(screenLayout);
		} catch(Exception e) {
			e.printStackTrace();
			return serverError(e);
		}
	}

	@GET
	@Path("/impersonate")
	@Produces("application/json")
	public Response readUserToImpersonate(@QueryParam("userId") String userId, @QueryParam("mode") AuthenticationTools.Mode mode) {
		try {
			EAMUser userToImpersonate = authenticationTools.getUserToImpersonate(userId, mode);
			return ok(userToImpersonate);
		} catch (InforException e) {
			return serverError(e);
		}
	}

}
