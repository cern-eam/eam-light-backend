package ch.cern.cmms.eamlightweb.workorders;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.workorders.WorkOrdersEJB;

/**
 * Rest services for the left panel containing "My Work Orders" and my team's
 * work orders
 *
 */
@Path("/myworkorders")
@Interceptors({ RESTLoggingInterceptor.class })
public class MyWorkOrders extends WSHubController {

	@EJB
	private WorkOrdersEJB wosEJB;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/my")
	@Produces("application/json")
	@Consumes("application/json")
	public Response read1() {
		try {
			return ok(wosEJB.getWOs(authenticationTools.getInforContext()));
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
			return ok(wosEJB.getTeamWOs(authenticationTools.getInforContext()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}