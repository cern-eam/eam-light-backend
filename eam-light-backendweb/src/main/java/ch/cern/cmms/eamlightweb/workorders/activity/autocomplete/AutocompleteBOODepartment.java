package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteBOODepartment extends EAMLightController {

	@GET
	@Path("/boo/department/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest( "LVMRCSANDRATE", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);

		gridRequest.addParam("param.bypassdeptsecurity", null);
		gridRequest.addParam("param.event", null);
		gridRequest.addParam("param.trade", null);
		gridRequest.addParam("param.octype", null);
		gridRequest.addParam("param.date", null);
		gridRequest.addParam("param.employee", null);

		gridRequest.addFilter("department", code.toUpperCase(), "BEGINS" );
		gridRequest.sortBy("department");

		return getPairListResponse(gridRequest, "department", "des_text");
	}

}
