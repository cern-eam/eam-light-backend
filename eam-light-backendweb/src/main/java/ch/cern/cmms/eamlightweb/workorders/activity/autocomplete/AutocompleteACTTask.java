package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

import java.util.Arrays;

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
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteACTTask extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;


	@GET
	@Path("/act/task/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest("LVWTSK");
			gridRequest.setGridType("LOV");

			gridRequest.getParams().put("param.isolationmethod", null);
			gridRequest.getParams().put("param.excludemultipletrades", null);
			gridRequest.getParams().put("param.excludejobplanning", null);
			gridRequest.getParams().put("param.excludenoteeplanning", null);
			gridRequest.getParams().put("parameter.excludeenhanceplanning", null);
			gridRequest.getParams().put("param.asslevel", null);
			gridRequest.getParams().put("param.complevel", null);
			gridRequest.getParams().put("param.eventno", null);
			gridRequest.getParams().put("param.act", null);
			gridRequest.getParams().put("param.esthrs", null);
			gridRequest.getParams().put("param.manufacturer", null);
			gridRequest.getParams().put("param.personsreq", null);
			gridRequest.getParams().put("param.reasonforrepair", null);
			gridRequest.getParams().put("param.syslevel", null);
			gridRequest.getParams().put("param.taskuom", null);
			gridRequest.getParams().put("param.techpartfailure", null);
			gridRequest.getParams().put("param.trade", null);
			gridRequest.getParams().put("param.workaccomplished", null);

			gridRequest.getGridRequestFilters().add(new GridRequestFilter("task", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("taskdesc", code.toUpperCase(), "BEGINS" ));

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("task", "taskdesc"),
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
