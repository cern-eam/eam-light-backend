package ch.cern.cmms.eamlightweb.application;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.UserTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/application")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class ApplicationController extends WSHubController {

	@EJB
	private UserTools userTools;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;
	@Inject
	private ScreenLayoutService screenLayoutService;

	@GET
	@Path("/applicationdata")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readApplicationData() {
		try {
			GridRequest gridRequest = new GridRequest("BSINST");
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("installcode", "EL_", "BEGINS"));
			return ok(inforClient.getTools().getGridTools().convertGridResultToMap("installcode", "value", inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/screenlayout/{entity}/{systemFunction}/{userFunction}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readScreenLayout(@PathParam("entity") String entity,
			@PathParam("systemFunction") String systemFunction, @PathParam("userFunction") String userFunction,
			@QueryParam("lang") String language, @QueryParam("tabname") List<String> tabs) throws InforException {
		try {
			String userGroup = userTools.getUserGroup(authenticationTools.getInforContext());
				return ok(screenLayoutService.getScreenLayout(authenticationTools.getR5InforContext(), systemFunction, userFunction, tabs, userGroup));

		} catch(Exception e) {
			e.printStackTrace();
			return serverError(e);
		}
	}

}
