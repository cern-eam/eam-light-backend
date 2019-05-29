package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;

/**
 * Rest services for the left panel containing "My Work Orders" and my team's
 * work orders
 *
 */
@Path("/myworkorders")
@Interceptors({ RESTLoggingInterceptor.class })
public class MyWorkOrdersController extends WSHubController {

	@Inject
	private MyWorkOrders myWorkOrders;

	@GET
	@Path("/my")
	@Produces("application/json")
	@Consumes("application/json")
	public Response read1() {
		try {
			return ok(myWorkOrders.getMyOpenWorkOrders());
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/myteam")
	@Produces("application/json")
	@Consumes("application/json")
	public Response read2() {
		try {
			return ok(myWorkOrders.getMyTeamsWorkOrders());
		} catch(Exception e) {
			return serverError(e);
		}
	}

}