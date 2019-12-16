package ch.cern.cmms.eamlightweb.workorders.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
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
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteLocation extends EAMLightController {

	@GET
	@Path("/location")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code) {
		GridRequest gridRequest = new GridRequest( "LVOBJL_LOC", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);

		gridRequest.addParam("param.bypassdeptsecurity", null);
		gridRequest.addParam("parameter.filterutilitybill", null);
		gridRequest.addParam("control.org", null);
		gridRequest.addParam("param.cctrspcvalidation", "D");
		gridRequest.addParam("param.department", null);
		gridRequest.addParam("param.loantodept", null);

		gridRequest.addFilter("equipmentcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
		gridRequest.addFilter("variable5", code.toUpperCase(), "BEGINS");
		gridRequest.sortBy("equipmentcode");

		return getPairListResponse(gridRequest, "equipmentcode", "equipmentdesc");
	}

}
