package ch.cern.cmms.eamlightweb.workorders.activity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
@Path("/actlists")
@Interceptors({ RESTLoggingInterceptor.class })
public class ActivitiesLists extends EAMLightController {

	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/trades")
	@Produces("application/json")
	public Response readTradeCodes() {
		GridRequest gridRequest = new GridRequest("LVTRADE", GridRequest.GRIDTYPE.LOV);
		return getPairListResponse(gridRequest, "trade", "tradedesc");
	}

	@GET
	@Path("/task")
	@Produces("application/json")
	public Response readTasks() {
		GridRequest gridRequest = new GridRequest("LVWTSK", GridRequest.GRIDTYPE.LOV);
		gridRequest.setParams(produceInforParamsForTaskDropdown());
		// Load the dropdown
		return getPairListResponse(gridRequest, "task", "taskdesc");
	}

	@GET
	@Path("/matlist")
	@Produces("application/json")
	public Response readMaterialList() {
		GridRequest gridRequest = new GridRequest("LVMATL", GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());
		return getPairListResponse(gridRequest, "matlist", "matlistdesc");
	}

	/**
	 * Initialize Infor parameters for dropdown list with tasks.
	 * 
	 * @return Map with Infor parameters.
	 */
	private Map<String, Object> produceInforParamsForTaskDropdown() {
		Map<String, Object> inforParams = new HashMap<String, Object>();
		inforParams.put("eventno", null);
		inforParams.put("act", "20");
		inforParams.put("personsreq", null);
		inforParams.put("techpartfailure", null);
		inforParams.put("esthrs", "");
		inforParams.put("reasonforrepair", null);
		inforParams.put("manufacturer", null);
		inforParams.put("syslevel", null);
		inforParams.put("excludenoteeplanning", null);
		inforParams.put("taskuom", "");
		inforParams.put("excludeenhanceplanning", null);
		inforParams.put("trade", null);
		inforParams.put("isolationmethod", null);
		inforParams.put("excludemultipletrades", null);
		inforParams.put("workaccomplished", null);
		inforParams.put("asslevel", null);
		inforParams.put("excludejobplanning", null);
		inforParams.put("complevel", null);
		inforParams.put("control.org", authenticationTools.getOrganizationCode());
		return inforParams;
	}

}
