package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipment extends EAMLightController {

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
	public Response complete(@QueryParam("s") String code, @QueryParam("a") String alias, @QueryParam("n") String serialNo) {
		GridRequest gridRequest = prepareGridRequest(GridRequest.GRIDTYPE.LOV);

		List<Pair> gridFilters = new ArrayList<>(Collections.singletonList(new Pair("equipmentcode", code)));
		if (alias != null && alias.trim().length() > 0) {
			gridFilters.add(new Pair("alias", alias));
		}
		if (serialNo != null && serialNo.trim().length() > 0) {
			gridFilters.add(new Pair("serialno", serialNo));
		}

		for (Pair column: gridFilters) {
			boolean first = column.getCode().equals(gridFilters.get(0).getCode());
			boolean last = column.getCode().equals(gridFilters.get(gridFilters.size() - 1).getCode());

			gridRequest.addFilter(column.getCode(), column.getDesc().toUpperCase(), "BEGINS",
					last ? GridRequestFilter.JOINER.AND : GridRequestFilter.JOINER.OR,
					first, last);
		}

		return getPairListResponse(gridRequest, "equipmentcode", "equipmentdesc");
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
