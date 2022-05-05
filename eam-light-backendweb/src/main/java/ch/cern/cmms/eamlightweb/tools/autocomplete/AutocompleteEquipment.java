package ch.cern.cmms.eamlightweb.tools.autocomplete;

import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipment extends EAMLightController {

	@Inject
	private EquipmentEJB equipmentEJB;

	private GridRequest prepareGridRequest(GridRequest.GRIDTYPE gridType)  {
		GridRequest gridRequest = new GridRequest("67", "LVOBJL", "59");
		gridRequest.setGridType(gridType);
		gridRequest.setRowCount(10);
		gridRequest.addParam("param.objectrtype", null);
		gridRequest.addParam("param.loantodept", "true");
		gridRequest.addParam("param.bypassdeptsecurity", "false");
		gridRequest.addParam("parameter.filterutilitybill", null);
		gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.cctrspcvalidation", "D");
		gridRequest.addParam("param.department", null);

		return gridRequest;
	}

	@GET
	@Path("/eqp")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code) {
		try {
			return ok(equipmentEJB.getEquipmentSearchResults(code, null, authenticationTools.getInforContext()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/eqp/selected")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getValuesSelectedEquipment(@QueryParam("code") String code) {
		try {

			GridRequest gridRequest = prepareGridRequest(GridRequest.GRIDTYPE.LIST);
			gridRequest.addFilter("equipmentcode", code.trim(), "EQUALS");

			String[] fields = new String[] {"equipmentcode", "equipmentdesc", "department",
					                        "departmentdisc", "parentlocation", "locationdesc", "equipcostcode"};

			return ok(inforClient.getTools().getGridTools().convertGridResultToMapList(inforClient.getGridsService()
					.executeQuery(authenticationTools.getInforContext(), gridRequest), Arrays.asList(fields)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
