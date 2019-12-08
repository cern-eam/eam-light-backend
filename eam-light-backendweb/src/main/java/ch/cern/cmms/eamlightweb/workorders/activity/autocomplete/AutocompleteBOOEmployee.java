package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

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
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteBOOEmployee extends Autocomplete {

	@Inject
	private InforClient inforClient;

	@GET
	@Path("/boo/employee/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {

		try {
			GridRequest gridRequest = new GridRequest("146", "LVEMP", "151");
			gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
			gridRequest.getParams().put("param.date", null);
			gridRequest.getParams().put("parameter.per_type", null);
			gridRequest.getParams().put("param.trade", "KPL");
			gridRequest.getParams().put("param.act", "5");
			gridRequest.getParams().put("param.booplan", "true");
			gridRequest.getParams().put("param.event", "555555");
			gridRequest.getParams().put("param.octype", "N");

			gridRequest.getGridRequestFilters().add(new GridRequestFilter("personcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("description", code.toUpperCase(), "CONTAINS" ));

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
