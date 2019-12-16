package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteBOOEmployee extends EAMLightController {

	@GET
	@Path("/boo/employee/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest("146", "LVEMP", "151");
		gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam("param.date", null);
		gridRequest.addParam("parameter.per_type", null);
		gridRequest.addParam("param.trade", "KPL");
		gridRequest.addParam("param.act", "5");
		gridRequest.addParam("param.booplan", "true");
		gridRequest.addParam("param.event", "555555");
		gridRequest.addParam("param.octype", "N");

		gridRequest.getGridRequestFilters().add(new GridRequestFilter("personcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("description", code.toUpperCase(), "CONTAINS" ));

		gridRequest.sortBy("description");

		return getPairListResponse(gridRequest, "personcode", "description");
	}

}
