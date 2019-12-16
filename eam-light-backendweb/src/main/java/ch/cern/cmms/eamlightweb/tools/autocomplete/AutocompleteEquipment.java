package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipment extends EAMLightController {

	private GridRequest prepareGridRequest()  {
		GridRequest gridRequest = new GridRequest("67", "LVOBJL", "59");
		gridRequest.setGridType(GridRequest.GRIDTYPE.LIST);
		gridRequest.setRowCount(10);
		gridRequest.addParam("param.objectrtype", null);
		gridRequest.addParam("param.loantodept", true);
		gridRequest.addParam("param.bypassdeptsecurity", false);
		gridRequest.addParam("parameter.filterutilitybill", null);
		gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.cctrspcvalidation", "D");
		gridRequest.addParam("param.department", null);

		return gridRequest;
	}

	@GET
	@Path("/wo/eqp")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code) {
		GridRequest gridRequest = prepareGridRequest();
		gridRequest.addFilter("equipmentcode", code.toUpperCase(), "BEGINS");
		return getPairListResponse(gridRequest, "equipmentcode", "equipmentdesc");
	}

	@GET
	@Path("/wo/eqp/selected")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getValuesSelectedEquipment(@QueryParam("code") String code) {
		try {

			GridRequest gridRequest = prepareGridRequest();
			gridRequest.addFilter("equipmentcode", code.trim(), "EQUALS");

			String[] fields = new String[] {"equipmentcode", "equipmentdesc", "department",
					                        "departmentdisc", "parentlocation", "locationdesc", "equipcostcode"};

			return ok(inforClient.getTools().getGridTools().convertGridResultToMapList(Arrays.asList(fields),
					inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
