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
	public Response readProblemCodes(
			@QueryParam("woclass") String woclass,
			@QueryParam("objclass") String objclass,
			@QueryParam("equipment") String equipment) {
		GridRequest gridRequest = new GridRequest("LVRECO", GridRequest.GRIDTYPE.LOV);
		gridRequest.setUserFunctionName("WSJOBS");
		//
		gridRequest.addParam("param.objclass", objclass);
		gridRequest.addParam("param.objclassorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.clgroup", null);
		gridRequest.addParam("parameter.equipmentorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("parameter.equipment", equipment);
		return getCodesResponse(gridRequest);
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

		// This is the only exception where the numeric ids differ (See WorkOrderLists::getCodesResponse)
		// 118=code
		// 629=description
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
		return getCodesResponse(gridRequest);
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
	public Response readFailureCodes(
			@QueryParam("objclass") String objclass,
			@QueryParam("problemcode") String problemCode,
			@QueryParam("equipment") String equipment) {
		GridRequest gridRequest = new GridRequest("LVFAILURE", GridRequest.GRIDTYPE.LOV);
		gridRequest.setUserFunctionName("WSJOBS");
		gridRequest.addParam("param.objclass", objclass);
		gridRequest.addParam("param.problemcod", problemCode);
		gridRequest.addParam("param.objclassorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.clgroup", null);
		gridRequest.addParam("parameter.equipmentorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("parameter.equipment", equipment);
		return getCodesResponse(gridRequest);
	}

	/**
	 * Loads the cause codes lists
	 */
	@GET
	@Path("/causecodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCauseCodes(
			@QueryParam("objclass") String objclass,
			@QueryParam("failurecode") String failurecode,
			@QueryParam("problemcode") String problemcode,
			@QueryParam("equipment") String equipment) {
		GridRequest gridRequest = new GridRequest("LVCAUSE", GridRequest.GRIDTYPE.LOV);
		gridRequest.setUserFunctionName("WSJOBS");
		// objclassorg, clgroup, failurecode, problemcode, objclass
		gridRequest.addParam("param.objclass", objclass);
		gridRequest.addParam("param.objclassorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.failurecode", failurecode);
		gridRequest.addParam("param.problemcode", problemcode);
		gridRequest.addParam("param.clgroup", null);
		gridRequest.addParam("parameter.equipmentorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("parameter.equipment", equipment);
		return getCodesResponse(gridRequest);
	}

	@GET
	@Path("/actioncodes")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readActionCodes(@QueryParam("objclass") String objclass,
			@QueryParam("failurecode") String failurecode,
			@QueryParam("problemcode") String problemcode,
			@QueryParam("causecode") String causecode,
			@QueryParam("equipment") String equipment) {
		GridRequest gridRequest = new GridRequest("LVACTION", GridRequest.GRIDTYPE.LOV);

		gridRequest.addParam("param.objclass", objclass);
		gridRequest.addParam("param.objclassorg", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.failurecode", failurecode);
		gridRequest.addParam("param.problemcode", problemcode);
		gridRequest.addParam("param.causecode", causecode);
		gridRequest.addParam("param.clgroup", null);
		gridRequest.addParam("parameter.equipment", equipment);
		gridRequest.addParam("parameter.equipmentorg", authenticationTools.getOrganizationCode());
		return getCodesResponse(gridRequest);
	}

	private Response getCodesResponse(GridRequest gridRequest) {
		// For the codes used in the work order, Infor EAM commonly uses:
		// Column id 101 for the code cell
		// Column id 103 for the description cell
		// In the GridRequestResult, these are named, and this name could also be used
		// However, since 101 and 103 are used in most requests, whereas the name differs per request,
		// we have opted to use these numeric ids here
		return getPairListResponse(gridRequest, "101", "103");
	}
}
