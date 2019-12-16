package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentBin extends EAMLightController {

	@GET
	@Path("/eqp/bin")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("code") String code, @QueryParam("store") String store) {
		GridRequest gridRequest = new GridRequest( "LVSTRBIN", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.sortBy("bincode");
		gridRequest.addParam("bincodetohide", null);
		gridRequest.addParam("bisstore", store);
		gridRequest.addFilter("bincode", code.toUpperCase(), "BEGINS");
		return getPairListResponse(gridRequest, "bincode", "bindescription");
	}

}