package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

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
public class AutocompleteACTTask extends EAMLightController {

	@GET
	@Path("/act/task/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest("LVWTSK", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);

		gridRequest.addParam("param.isolationmethod", null);
		gridRequest.addParam("param.excludemultipletrades", null);
		gridRequest.addParam("param.excludejobplanning", null);
		gridRequest.addParam("param.excludenoteeplanning", null);
		gridRequest.addParam("parameter.excludeenhanceplanning", null);
		gridRequest.addParam("param.asslevel", null);
		gridRequest.addParam("param.complevel", null);
		gridRequest.addParam("param.eventno", null);
		gridRequest.addParam("param.act", null);
		gridRequest.addParam("param.esthrs", null);
		gridRequest.addParam("param.manufacturer", null);
		gridRequest.addParam("param.personsreq", null);
		gridRequest.addParam("param.reasonforrepair", null);
		gridRequest.addParam("param.syslevel", null);
		gridRequest.addParam("param.taskuom", null);
		gridRequest.addParam("param.techpartfailure", null);
		gridRequest.addParam("param.trade", null);
		gridRequest.addParam("param.workaccomplished", null);

		gridRequest.addFilter("task", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR );
		gridRequest.addFilter("taskdesc", code.toUpperCase(), "BEGINS" );

		return getPairListResponse(gridRequest, "task", "taskdesc");
	}

}
