package ch.cern.cmms.eamlightweb.workorders.activity;

import java.util.Arrays;
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
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/actlists")
@Interceptors({ RESTLoggingInterceptor.class })
public class ActivitiesLists extends DropdownValues {

	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/trades")
	@Produces("application/json")
	public Response readTradeCodes() {
		try {
			// Load the dropdown
			return ok(loadDropdown("85", "LVTRADE", "86", GridRequest.GRIDTYPE.LOV, Arrays.asList("101", "103"),
					new HashMap<String, String>()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/task")
	@Produces("application/json")
	public Response readTasks() {
		try {
			// Load the dropdown
			return ok(loadDropdown("1181", "LVWTSK", "1147", GridRequest.GRIDTYPE.LOV, Arrays.asList("1978", "2023"),
					produceInforParamsForTaskDropdown()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/matlist")
	@Produces("application/json")
	public Response readMaterialList() {
		try {
			Map<String, String> inforParams = new HashMap<String, String>();
			inforParams.put("control.org", authenticationTools.getInforContext().getOrganizationCode());
			// Load the dropdown
			return ok(loadDropdown("90", "LVMATL", "91", GridRequest.GRIDTYPE.LOV, Arrays.asList("101", "103"), inforParams));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Initialize Infor parameters for dropdown list with tasks.
	 * 
	 * @return Map with Infor parameters.
	 */
	private Map<String, String> produceInforParamsForTaskDropdown() throws InforException {
		Map<String, String> inforParams = new HashMap<String, String>();
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
		inforParams.put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		return inforParams;
	}

}
