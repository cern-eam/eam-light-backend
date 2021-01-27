package ch.cern.cmms.eamlightweb.workorders.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteWOCostCode extends EAMLightController {

	@GET
	@Path("/wo/costcode/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest("38", "LOV", "38");
		gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());
		gridRequest.addParam("userfunction", "WSJOBS");
		gridRequest.setRowCount(10);
		gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("costcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("des_text", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));

		return getPairListResponse(gridRequest, "costcode", "des_text");
	}

	@GET
	@Path("/equipment/costcode/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response completeEquipmentCostCode(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest("LVOBJCOST", GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());
		gridRequest.addParam("userfunction", "OSOBJA");
		gridRequest.setRowCount(10);
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("costcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("costcodedescription", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
		return getPairListResponse(gridRequest, "costcode", "costcodedescription");
	}
}
