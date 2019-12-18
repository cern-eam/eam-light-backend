package ch.cern.cmms.eamlightweb.application;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.base.CustomFieldsController;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.ScreenLayoutService;
import ch.cern.cmms.eamlightweb.user.ScreenService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.impl.GridsServiceImpl;
import ch.cern.eam.wshub.core.services.grids.impl.InforGrids;

@Path("/application")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class ApplicationController extends EAMLightController {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;
	@Inject
	private ApplicationData applicationData;


	@GET
	@Path("/applicationdata")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readApplicationData() {
		try {
			GridRequest gridRequest = new GridRequest("BSINST");
			gridRequest.addFilter("installcode", "EL_", "BEGINS");
			return ok(inforClient.getTools().getGridTools().convertGridResultToMap("installcode", "value",
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/refreshCache")
	@Produces("application/json")
	@Consumes("application/json")
	public Response cleanCache() {
		ScreenLayoutService.screenLayoutCache.clear();
		ScreenLayoutService.screenLayoutLabelCache.clear();
		ScreenService.screenCache.clear();
		InforGrids.gridFieldCache.clear();
		GridsServiceImpl.gridIdCache.clear();
		CustomFieldsController.customFieldsLookupValuesCache.clear();
		return ok("EAM Light cache has been successfully refreshed.");
	}

	@GET
	@Path("/version")
	@Produces("application/json")
	public Response readVersion() {
		String version = applicationData.getVersion();
		return ok(version);
	}
}