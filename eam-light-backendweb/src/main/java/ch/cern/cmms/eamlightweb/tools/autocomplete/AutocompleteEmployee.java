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

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEmployee extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/employee/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest("42", "LVPERS", "42");
			gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
			gridRequest.setRowCount(10);
			gridRequest.getParams().put("parameter.per_type", null);
			gridRequest.getParams().put("param.bypassdeptsecurity", true);
			gridRequest.getParams().put("param.sessionid", null);
			gridRequest.getParams().put("parameter.noemployees", null);
			gridRequest.getParams().put("param.shift", null);

			gridRequest.addFilter("personcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);
			gridRequest.addFilter("description", code.toUpperCase(), "CONTAINS");
			gridRequest.sortBy("description");

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("personcode", "description"),
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}