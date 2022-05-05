package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
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
	private EquipmentEJB equipmentEJB;

	@GET
	@Path("/eqp/eqpreplace/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			return ok(equipmentEJB.getEquipmentSearchResults(code, null, authenticationTools.getInforContext()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}