package ch.cern.cmms.eamlightweb.workorders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.workorders.WorkOrderStatus;
import ch.cern.cmms.eamlightejb.workorders.WorkOrderType;
import ch.cern.cmms.eamlightejb.workorders.WorkOrdersEJB;
import ch.cern.eam.wshub.core.services.grids.entities.GridDataspy;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/wolists")
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderLists extends DropdownValues {

	@EJB
	private WorkOrdersEJB wosEJB;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/problemcodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readProblemCodes(@QueryParam("woclass") String woclass, @QueryParam("objclass") String objclass) {
		try {
			// Map of parameters to send to the query
			Map<String, String> parameters = new HashMap<>();
			// Get the default data spy
			GridDataspy dataspy = getDefaultDataSpy("29", "LOV");
			// Definition of parameters
			parameters.clear();
			//
			parameters.put("woclass", woclass);
			parameters.put("objclass", objclass);
			parameters.put("objclassorg", null);
			parameters.put("clgroup", null);
			parameters.put("userfunction", "WSJOBS");
			parameters.put("equipmentorg", null);
			parameters.put("equipment", null);
			// Load the dropdown
			return ok(loadDropdown("29", "LVRECO", dataspy.getCode(), "LOV", Arrays.asList("101", "103"), parameters));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/statuscodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readStatusCodes(@QueryParam("wostatus") String wostatus, @QueryParam("wotype") String wotype,
			@QueryParam("newwo") Boolean newwo) throws InforException {
		List<WorkOrderStatus> statuses = wosEJB.getWorkOrderStatuses(authenticationTools.getInforContext(), wostatus, wotype, newwo);
		return ok(statuses.stream().map(status -> new Pair(status.getCode(), status.getDesc())).collect(Collectors.toList()));
	}

	@GET
	@Path("/typecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readTypeCodes(@QueryParam("wostatus") String wostatus, @QueryParam("wotype") String wotype,
			@QueryParam("newwo") Boolean newwo, @QueryParam("ppmwo") Boolean ppmwo) throws InforException {
		List<WorkOrderType> types = wosEJB.getWorkOrderTypes(authenticationTools.getInforContext(), wostatus, wotype,
				newwo, ppmwo);
		return ok(types.stream().map(type -> new Pair(type.getCode(), type.getDesc())).collect(Collectors.toList()));
	}

	@GET
	@Path("/prioritycodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readPriorityCodes() {
		List<Pair> woPriorities = new LinkedList<Pair>();
		woPriorities.add(new Pair("L", "Basse"));
		woPriorities.add(new Pair("H", "Haute"));
		woPriorities.add(new Pair("M", "Moyenne"));
		woPriorities.add(new Pair("*", "Toutes Priorités"));
		return ok(woPriorities);
	}

	/**
	 * Loads the Failure codes lists
	 * 
	 * @param objclass
	 *            The class of the equipment
	 * @param problemCode
	 *            The problem code of the work Order
	 */
	@GET
	@Path("/failurecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readFailureCodes(@QueryParam("objclass") String objclass,
			@QueryParam("problemcode") String problemCode) {
		try {
			// Map of parameters to send to the query
			Map<String, String> parameters = new HashMap<>();
			// Get the default data spy
			GridDataspy dataspy = getDefaultDataSpy("101", "LOV");
			// Definition of parameters
			parameters.clear();
			parameters.put("objclass", objclass);
			parameters.put("problemcode", problemCode);
			parameters.put("objclassorg", null);
			parameters.put("clgroup", null);
			parameters.put("userfunction", "WSJOBS");
			parameters.put("equipmentorg", null);
			parameters.put("equipment", null);
			// Load the dropdown
			return ok(loadDropdown("101", "LVFAILURE", dataspy.getCode(), "LOV", Arrays.asList(new String[] { "101", "103" }), parameters));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Loads the cause codes lists
	 */
	@GET
	@Path("/causecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCauseCodes(@QueryParam("objclass") String objclass,
			@QueryParam("failurecode") String failurecode, @QueryParam("problemcode") String problemcode) {
		try {
			// Map of parameters to send to the query
			Map<String, String> parameters = new HashMap<>();
			// Get the default data spy
			GridDataspy dataspy = getDefaultDataSpy("102", "LOV");
			// Definition of parameters
			parameters.clear();
			// objclassorg, clgroup, failurecode, problemcode, objclass
			parameters.put("objclass", objclass);
			parameters.put("failurecode", failurecode);
			parameters.put("problemcode", problemcode);
			parameters.put("objclassorg", null);
			parameters.put("clgroup", null);
			parameters.put("userfunction", "WSJOBS");
			parameters.put("equipmentorg", null);
			parameters.put("equipment", null);
			// Load the dropdown
			return ok(loadDropdown("102", "LVCAUSE", dataspy.getCode(), "LOV",
					Arrays.asList(new String[] { "101", "103" }), parameters));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/actioncodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readActionCodes(@QueryParam("objclass") String objclass,
			@QueryParam("failurecode") String failurecode, @QueryParam("problemcode") String problemcode,
			@QueryParam("causecode") String causecode) {
		try {
			// Map of parameters to send to the query
			Map<String, String> parameters = new HashMap<>();
			// Get the default data spy
			GridDataspy dataspy = getDefaultDataSpy("103", "LOV");
			// Definition of parameters
			parameters.clear();
			parameters.put("objclass", objclass);
			parameters.put("failurecode", failurecode);
			parameters.put("problemcode", problemcode);
			parameters.put("causecode", causecode);
			parameters.put("objclassorg", null);
			parameters.put("clgroup", null);
			parameters.put("userfunction", "WSJOBS");
			parameters.put("equipmentorg", null);
			parameters.put("equipment", null);
			// Load the dropdown
			return ok(loadDropdown("103", "LVACTION", dataspy.getCode(), "LOV", Arrays.asList(new String[] { "101", "103" }), parameters));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
