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
	@Path("/statuscodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readStatusCodes(@QueryParam("userGroup") String userGroup, @QueryParam("neweqp") Boolean neweqp, @QueryParam("oldStatusCode") String oldStatusCode) throws InforException {
		GridRequest gridRequest = new GridRequest("BSAUTH_HDR", GridRequest.GRIDTYPE.LOV);

		gridRequest.addFilter("usergroupcode", userGroup, "=", GridRequestFilter.JOINER.OR, true, null);
		gridRequest.addFilter("usercode", authenticationTools.getInforContext().getCredentials().getUsername(), "=", GridRequestFilter.JOINER.AND, null, true);
		gridRequest.addFilter("entity", "OBJ", "=", GridRequestFilter.JOINER.AND);
		if (neweqp) {
			gridRequest.addFilter("fromstatus", "-", "=");
		} else {
			gridRequest.addFilter("fromstatus", oldStatusCode, "=");
		}

		GridRequestResult gridRequestResult = inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest);
		List<Pair> result = inforClient.getTools().getGridTools().convertGridResultToObject(Pair.class,
				Pair.generateGridPairMap("3580", "3581"),
				inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

		if (!neweqp && !result.stream().anyMatch(pair -> pair.getCode().equals(oldStatusCode))) {
			String oldStatusDesc = inforClient.getTools().getFieldDescriptionsTools().readUserCodeDesc(authenticationTools.getR5InforContext(), "OBST", oldStatusCode);
			result.add(new Pair(oldStatusCode, oldStatusDesc));
		}

		return ok(result);
	}

	@GET
	@Path("/criticalitycodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCriticalityCodes() {
		GridRequest gridRequest = new GridRequest("BSUCOD_HDR", GridRequest.GRIDTYPE.LIST);
		gridRequest.addParam("param.entitycode", "OBCR");
		return getPairListResponse(gridRequest, "usercode", "usercodedescription");
	}

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

	@GET
	@Path("/statecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readEquipmentStateCodes() {
		GridRequest gridRequest = new GridRequest("BSUCOD_HDR", 20);
		gridRequest.addParam("param.entitycode", "OBSA");
		return getPairListResponse(gridRequest, "usercode", "usercodedescription");

	}

}
