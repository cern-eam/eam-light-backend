package ch.cern.cmms.eamlightweb.equipment;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.UserTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrders;
import ch.cern.eam.wshub.core.services.grids.entities.GridDataspy;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

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
		GridDataspy dataspy = getDefaultDataSpy("813", "LOV");
		List<GridRequestFilter> gridFilters = new LinkedList<>();
		gridFilters.add(new GridRequestFilter("usergroupcode", userTools.getUserGroup(authenticationTools.getInforContext()), "=", GridRequestFilter.JOINER.OR, "true", null));
		gridFilters.add(new GridRequestFilter("usercode", authenticationTools.getInforContext().getCredentials().getUsername(), "=", GridRequestFilter.JOINER.AND, null, "true"));
		gridFilters.add(new GridRequestFilter("entity", "OBJ", "=", GridRequestFilter.JOINER.AND));
		if (neweqp) {
			gridFilters.add(new GridRequestFilter("fromstatus", "-", "="));
		} else {
			gridFilters.add(new GridRequestFilter("fromstatus", oldStatusCode, "="));
		}
		GridRequestResult gridRequestResult = loadGridRequestResult("813", "BSAUTH_HDR", dataspy.getCode(), "LOV", Arrays.asList("3580", "3581"), null, gridFilters, true);
		List<Pair> result = convertToPairs(Arrays.asList("3580", "3581"), gridRequestResult);
		// Extract current status
		if (gridRequestResult.getRows().length > 0) {
			//TODO: do not assume the order from the grid ws response
			result.add(new Pair(gridRequestResult.getRows()[0].getCell()[2].getContent(), gridRequestResult.getRows()[0].getCell()[3].getContent()));
		}
		return ok(result);
	}

	@GET
	@Path("/criticalitycodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCriticalityCodes() {
		try {
			return ok(loadDropdown("2385", "LVMULTICRITIC", "2359", "LIST",
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
