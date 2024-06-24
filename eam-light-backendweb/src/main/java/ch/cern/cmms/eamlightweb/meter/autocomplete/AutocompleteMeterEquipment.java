package ch.cern.cmms.eamlightweb.meter.autocomplete;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteMeterEquipment extends EAMLightController {

	@GET
	@Path("/meters/equipment/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		// Result
		GridRequest gridRequest = new GridRequest("OSMETE", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.setUserFunctionName("OSMETE");
		gridRequest.setUseNative(true);
		gridRequest.addFilter("equipment", code, "BEGINS");
		return getPairListResponse(gridRequest, "equipment", "meterunit");
	}

}