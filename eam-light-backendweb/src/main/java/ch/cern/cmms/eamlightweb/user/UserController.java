package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class UserController extends EAMLightController {

	@Inject
	private UserService userService;
	@Inject
	private ScreenLayoutService screenLayoutService;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/")
	@Produces("application/json")
	public Response readUserData(@QueryParam("currentScreen") String currentScreen,
								 @QueryParam("screenCode") String screenCode) {
		try {
			return ok(userService.getUserData(currentScreen, screenCode));
		} catch(Exception e) {
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
			return ok(screenLayoutService.getScreenLayout(authenticationTools.getR5InforContext(), systemFunction, userFunction, tabs, userGroup));
		} catch(Exception e) {
			e.printStackTrace();
			return serverError(e);
		}
	}

}
