package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;


@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentReplacement extends EAMLightController {

	@Inject
	private EquipmentEJB equipmentEJB;

	@GET
	@Path("/eqp/eqpreplace/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code, @QueryParam("filterL") Boolean excludeLocations) {
		try {
			return ok(equipmentEJB.getEquipmentSearchResults(code, excludeLocations ? Arrays.asList("A", "P", "S") : null, authenticationTools.getInforContext()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}