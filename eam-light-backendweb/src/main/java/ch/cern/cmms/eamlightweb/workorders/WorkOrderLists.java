package ch.cern.cmms.eamlightweb.workorders;

import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/wolists")
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderLists extends EAMLightController {

	@GET
	@Path("/problemcodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readProblemCodes(@QueryParam("woclass") String woclass, @QueryParam("objclass") String objclass) {
		GridRequest gridRequest = new GridRequest("LVRECO", GridRequest.GRIDTYPE.LOV);
		//
		gridRequest.addParam("param.objclass", objclass);
		gridRequest.addParam("param.objclassorg", null);
		gridRequest.addParam("param.clgroup", null);
		gridRequest.addParam("parameter.equipmentorg", null);
		gridRequest.addParam("parameter.equipment", null);
		return getPairListResponse(gridRequest, "101", "103");
	}

	@GET
	@Path("/statuscodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readStatusCodes(@QueryParam("wostatus") String wostatus, @QueryParam("wotype") String wotype,
			@QueryParam("newwo") Boolean newwo) throws InforException {
		GridRequest gridRequest = new GridRequest("LVWRSTDRP", GridRequest.GRIDTYPE.LOV);
		// Definition of parameters
		if (newwo) {
			gridRequest.addParam("param.poldstat", "-");
			gridRequest.addParam("param.pexcclause", "C");
		} else {
			gridRequest.addParam("param.poldstat", wostatus);
			gridRequest.addParam("param.pexcclause", "A");
		}
		gridRequest.addParam("param.pfunrentity", "EVNT");
		return getPairListResponse(gridRequest, "118", "629");
	}

	@GET
	@Path("/typecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readTypeCodes(@QueryParam("userGroup") String userGroup) throws InforException {
		GridRequest gridRequest = new GridRequest("LVGROUPWOTYPE", GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam("parameter.pagemode", null);
		gridRequest.addParam("parameter.usergroup", userGroup);
		return getPairListResponse(gridRequest, "101", "103");
	}

	@GET
	@Path("/prioritycodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readPriorityCodes() {
		GridRequest gridRequest = new GridRequest("LVJBPR", GridRequest.GRIDTYPE.LOV);
		gridRequest.addFilter("description", "Tou", "NOTCONTAINS");
		return getPairListResponse(gridRequest, "priority", "description");
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
		GridRequest gridRequest = new GridRequest("LVFAILURE", GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam("param.objclass", objclass);
		gridRequest.addParam("param.problemcod", problemCode);
		gridRequest.addParam("param.objclassorg", null);
		gridRequest.addParam("param.clgroup", null);
		gridRequest.addParam("userfunction", "WSJOBS");
		gridRequest.addParam("parameter.equipmentorg", null);
		gridRequest.addParam("parameter.equipment", null);
		return getPairListResponse(gridRequest, "101", "103");
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
			GridRequest gridRequest = new GridRequest("LVCAUSE", GridRequest.GRIDTYPE.LOV);
			// objclassorg, clgroup, failurecode, problemcode, objclass
			gridRequest.addParam("param.objclass", objclass);
			gridRequest.addParam("param.failurecode", failurecode);
			gridRequest.addParam("param.problemcode", problemcode);
			gridRequest.addParam("param.objclassorg", null);
			gridRequest.addParam("param.clgroup", null);
			gridRequest.addParam("parameter.equipmentorg", null);
			gridRequest.addParam("parameter.equipment", null);

			return getPairListResponse(gridRequest, "101", "103");
	}

	@GET
	@Path("/actioncodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readActionCodes(@QueryParam("objclass") String objclass,
			@QueryParam("failurecode") String failurecode, @QueryParam("problemcode") String problemcode,
			@QueryParam("causecode") String causecode) {
			GridRequest gridRequest = new GridRequest("LVACTION", GridRequest.GRIDTYPE.LOV);

			gridRequest.addParam("param.objclass", objclass);
			gridRequest.addParam("param.failurecode", failurecode);
			gridRequest.addParam("param.problemcode", problemcode);
			gridRequest.addParam("param.causecode", causecode);
			gridRequest.addParam("param.objclassorg", null);
			gridRequest.addParam("param.clgroup", null);
			gridRequest.addParam("parameter.equipmentorg", null);
			gridRequest.addParam("parameter.equipment", null);

			return getPairListResponse(gridRequest, "101", "103");
	}
}
