package ch.cern.cmms.eamlightweb.tools.autocomplete;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteClass extends Autocomplete {

	@Inject
	private InforClient inforClient;

	@GET
	@Path("/class/{entity}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("entity") String entity, @PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest("44", "LVCLAS", "44");
			gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
			gridRequest.setRowCount(10);
			gridRequest.getParams().put("parameter.rentity", entity);
			gridRequest.getParams().put("parameter.r5role", "");
			gridRequest.getParams().put("parameter.bypassorg", true);
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("class", code.toUpperCase(), "BEGINS"));
			gridRequest.sortyBy("class");

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("class", "des_text"),
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
