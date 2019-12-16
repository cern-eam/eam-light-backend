package ch.cern.cmms.eamlightweb.tools.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
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

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteClass extends EAMLightController {

	@GET
	@Path("/class/{entity}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("entity") String entity, @PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest( "LVCLAS", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.addParam("parameter.rentity", entity);
		gridRequest.addParam("parameter.r5role", "");
		gridRequest.addParam("parameter.bypassorg", "true");
		gridRequest.addFilter("class", code.toUpperCase(), "BEGINS");
		gridRequest.sortBy("class");

		return getPairListResponse(gridRequest, "class", "des_text");
	}

}
