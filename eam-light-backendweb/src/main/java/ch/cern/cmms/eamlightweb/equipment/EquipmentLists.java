package ch.cern.cmms.eamlightweb.equipment;

import java.util.*;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrders;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/eqplists")
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentLists extends EAMLightController {

	@Inject
	private MyWorkOrders myWorkOrders;

	@GET
	@Path("/eqpwos/{eqpcode}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readEqpWorkOrders(@PathParam("eqpcode") String eqpcode) {
		try {
			return ok(myWorkOrders.getObjectWorkOrders(eqpcode));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
