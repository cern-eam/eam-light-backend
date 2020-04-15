package ch.cern.cmms.eamlightweb.workorders.activity;

import java.util.HashMap;
import java.util.Map;

import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

@Path("/boolists")
@Interceptors({ RESTLoggingInterceptor.class })
public class BookingLabourLists extends EAMLightController {

	@GET
	@Path("/typehours")
	@Produces("application/json")
	public Response readTradeCodes() {
		GridRequest gridRequest = new GridRequest("LVOCTP", GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam("date", null);
		gridRequest.addParam("trade", "20");
		gridRequest.addParam("dept", null);
		gridRequest.addParam("event", null);
		gridRequest.addParam("employee", "");
		return getPairListResponse(gridRequest, "101", "103");
	}

}
