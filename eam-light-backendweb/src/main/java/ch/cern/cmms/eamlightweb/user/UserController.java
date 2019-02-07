package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/users")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class UserController extends WSHubController {

	@Inject
	private UserData userData;

	@GET
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readUserData(@QueryParam("currentScreen") String currentScreen,
			@QueryParam("screenCode") String screenCode) {
		try {
			return ok(userData.copy(currentScreen, screenCode));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
