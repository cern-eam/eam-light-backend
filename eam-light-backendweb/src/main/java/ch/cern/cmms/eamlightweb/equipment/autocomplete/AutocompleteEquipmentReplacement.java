package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import ch.cern.cmms.eamlightejb.equipment.tools.EquipmentSearch;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentReplacement extends EAMLightController {

	@Inject
	private EquipmentSearch equipmentSearch;

	@GET
	@Path("/eqp/eqpreplace/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
//		GridRequest gridRequest = new GridRequest( "OSOBJA", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
//		gridRequest.addFilter("equipmentno", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
//		gridRequest.addFilter("alias", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
//		gridRequest.addFilter("serialnumber", code.toUpperCase(), "BEGINS");
//		return getPairListResponse(gridRequest, "equipmentno", "equipmentdesc");
		try {
			return ok(equipmentSearch.getEquipmentSearchResults(code, authenticationTools.getInforContext()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}