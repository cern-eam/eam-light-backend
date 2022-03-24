package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;


@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentReplacement extends EAMLightController {

	@GET
	@Path("/eqp/eqpreplace/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest( "OSOBJA", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.addFilter("equipmentno", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
		gridRequest.addFilter("alias", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
		gridRequest.addFilter("serialnumber", code.toUpperCase(), "BEGINS");
		return getPairListResponse(gridRequest, "equipmentno", "equipmentdesc");
	}

}