package ch.cern.cmms.eamlightweb.equipment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
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
import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightejb.equipment.EquipmentStatus;
import ch.cern.cmms.eamlightejb.workorders.WorkOrdersEJB;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/eqplists")
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentLists extends DropdownValues {

	@EJB
	private EquipmentEJB equipmentEJB;
	@EJB
	private WorkOrdersEJB wosEJB;

	@GET
	@Path("/statuscodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readStatusCodes(@QueryParam("neweqp") Boolean neweqp) {
		try {
			List<EquipmentStatus> statuses = equipmentEJB.getEquipmentStatuses(authenticationTools.getInforContext(), "-","EN", "OBJ",
					neweqp);
			return ok(statuses.stream().map(status -> new Pair(status.getCode(), status.getDesc()))
					.collect(Collectors.toList()));
		} catch(Exception e) {
			return serverError(e);
		}
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
			return ok(wosEJB.getObjectWorkOrders(eqpcode));
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
