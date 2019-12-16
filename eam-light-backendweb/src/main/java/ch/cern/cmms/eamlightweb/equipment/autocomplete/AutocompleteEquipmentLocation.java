package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentLocation extends EAMLightController {

	@GET
	@Path("/eqp/location")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code) {
		GridRequest gridRequest = new GridRequest( "LVOBJL_EQ_LOC", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);

		gridRequest.addParam("param.bypassdeptsecurity", null);
		gridRequest.addParam("param.objectcode", null);
		gridRequest.addParam("param.objectorg", null);
		gridRequest.addParam("control.org", null);

		gridRequest.addFilter("equipmentcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
		gridRequest.sortBy("equipmentcode");

		return getPairListResponse(gridRequest, "equipmentcode", "equipmentdesc");
	}

}
