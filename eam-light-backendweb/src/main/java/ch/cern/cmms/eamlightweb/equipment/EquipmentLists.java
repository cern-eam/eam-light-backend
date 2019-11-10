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

import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.cmms.eamlightweb.user.UserTools;
import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrders;
import ch.cern.eam.wshub.core.services.grids.entities.GridDataspy;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.GridTools.getCellContent;

@Path("/eqplists")
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentLists extends DropdownValues {

	@Inject
	private MyWorkOrders myWorkOrders;
	@Inject
	private UserTools userTools;

	@GET
	@Path("/statuscodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readStatusCodes(@QueryParam("neweqp") Boolean neweqp, @QueryParam("oldStatusCode") String oldStatusCode) throws InforException {
		GridRequest gridRequest = new GridRequest("BSAUTH_HDR", GridRequest.GRIDTYPE.LOV);

		gridRequest.getGridRequestFilters().add(new GridRequestFilter("usergroupcode", userTools.getUserGroup(authenticationTools.getInforContext()), "=", GridRequestFilter.JOINER.OR, true, null));
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("usercode", authenticationTools.getInforContext().getCredentials().getUsername(), "=", GridRequestFilter.JOINER.AND, null, true));
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("entity", "OBJ", "=", GridRequestFilter.JOINER.AND));
		if (neweqp) {
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("fromstatus", "-", "="));
		} else {
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("fromstatus", oldStatusCode, "="));
		}

		GridRequestResult gridRequestResult = inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest);
		List<Pair> result = inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
				Pair.generateGridPairMap("3580", "3581"),
				inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

		if (gridRequestResult.getRows().length > 0) {
			result.add(new Pair(getCellContent("3578", gridRequestResult.getRows()[0]), getCellContent("3579", gridRequestResult.getRows()[0])));
		}
		return ok(result);
	}

	@GET
	@Path("/criticalitycodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCriticalityCodes() {
		try {
			return ok(loadDropdown("2385", "LVMULTICRITIC", "2359", GridRequest.GRIDTYPE.LIST,
					Arrays.asList("101", "103"), new HashMap<>()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
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
}
