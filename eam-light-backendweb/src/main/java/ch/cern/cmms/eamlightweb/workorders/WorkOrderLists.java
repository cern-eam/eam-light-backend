package ch.cern.cmms.eamlightweb.workorders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import ch.cern.cmms.eamlightweb.user.UserTools;
import ch.cern.eam.wshub.core.services.grids.entities.GridDataspy;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/wolists")
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderLists extends DropdownValues {

	@Inject
	private UserTools userTools;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/problemcodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readProblemCodes(@QueryParam("woclass") String woclass, @QueryParam("objclass") String objclass) {
		try {
			GridRequest gridRequest = new GridRequest("LVRECO");
			gridRequest.setGridType("LOV");
			//
			gridRequest.getParams().put("param.objclass", objclass);
			gridRequest.getParams().put("param.objclassorg", null);
			gridRequest.getParams().put("param.clgroup", null);
			gridRequest.getParams().put("parameter.equipmentorg", null);
			gridRequest.getParams().put("parameter.equipment", null);

			return ok(loadDropdown(gridRequest, "101", "103"));
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
		GridRequest gridRequest = new GridRequest("LVWRSTDRP");
		gridRequest.setGridType("LOV");
		// Definition of parameters
		if (newwo) {
			gridRequest.getParams().put("param.poldstat", "-");
			gridRequest.getParams().put("param.pexcclause", "C");
		} else {
			gridRequest.getParams().put("param.poldstat", wostatus);
			gridRequest.getParams().put("param.pexcclause", "A");
		}
		gridRequest.getParams().put("param.pfunrentity", "EVNT");
		return ok(loadDropdown(gridRequest, "118", "629"));
	}

	@GET
	@Path("/typecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readTypeCodes(@QueryParam("wostatus") String wostatus, @QueryParam("wotype") String wotype,
			@QueryParam("newwo") Boolean newwo, @QueryParam("ppmwo") Boolean ppmwo) throws InforException {
		GridRequest gridRequest = new GridRequest("LVGROUPWOTYPE");
		gridRequest.setGridType("LOV");
		gridRequest.getParams().put("parameter.pagemode", null);
		gridRequest.getParams().put("parameter.usergroup", userTools.getUserGroup(authenticationTools.getInforContext()));
		return ok(loadDropdown(gridRequest, "101", "103"));
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
			GridRequest gridRequest = new GridRequest("LVFAILURE");
			gridRequest.setGridType("LOV");
			gridRequest.getParams().put("param.objclass", objclass);
			gridRequest.getParams().put("param.problemcod", problemCode);
			gridRequest.getParams().put("param.objclassorg", null);
			gridRequest.getParams().put("param.clgroup", null);
			gridRequest.getParams().put("userfunction", "WSJOBS");
			gridRequest.getParams().put("parameter.equipmentorg", null);
			gridRequest.getParams().put("parameter.equipment", null);
			return ok(loadDropdown(gridRequest, "101", "103"));
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
			GridRequest gridRequest = new GridRequest("LVCAUSE");
			gridRequest.setGridType("LOV");
			// objclassorg, clgroup, failurecode, problemcode, objclass
			gridRequest.getParams().put("param.objclass", objclass);
			gridRequest.getParams().put("param.failurecode", failurecode);
			gridRequest.getParams().put("param.problemcode", problemcode);
			gridRequest.getParams().put("param.objclassorg", null);
			gridRequest.getParams().put("param.clgroup", null);
			gridRequest.getParams().put("parameter.equipmentorg", null);
			gridRequest.getParams().put("parameter.equipment", null);

			return ok(loadDropdown(gridRequest, "101", "103"));
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
			GridRequest gridRequest = new GridRequest("LVACTION");
			gridRequest.setGridType("LOV");

			gridRequest.getParams().put("param.objclass", objclass);
			gridRequest.getParams().put("param.failurecode", failurecode);
			gridRequest.getParams().put("param.problemcode", problemcode);
			gridRequest.getParams().put("param.causecode", causecode);
			gridRequest.getParams().put("param.objclassorg", null);
			gridRequest.getParams().put("param.clgroup", null);
			gridRequest.getParams().put("parameter.equipmentorg", null);
			gridRequest.getParams().put("parameter.equipment", null);

			return ok(loadDropdown(gridRequest, "101", "103"));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
