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
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteACTTask extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException{
		SimpleGridInput in = new SimpleGridInput("1181", "LVWTSK", "1147");
		in.setGridType("LOV");
		in.setFields(Arrays.asList("1978", "2023"));
		in.getInforParams().put("eventno", null);
		in.getInforParams().put("act", "20");
		in.getInforParams().put("personsreq", null);
		in.getInforParams().put("techpartfailure", null);
		in.getInforParams().put("esthrs", "");
		in.getInforParams().put("reasonforrepair", null);
		in.getInforParams().put("manufacturer", null);
		in.getInforParams().put("syslevel", null);
		in.getInforParams().put("excludenoteeplanning", null);
		in.getInforParams().put("taskuom", "");
		in.getInforParams().put("excludeenhanceplanning", null);
		in.getInforParams().put("trade", null);
		in.getInforParams().put("isolationmethod", null);
		in.getInforParams().put("excludemultipletrades", null);
		in.getInforParams().put("workaccomplished", null);
		in.getInforParams().put("asslevel", null);
		in.getInforParams().put("excludejobplanning", null);
		in.getInforParams().put("complevel", null);
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		return in;
	}

	@GET
	@Path("/act/task/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("task", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
			in.getGridFilters().add(new GridRequestFilter("taskdesc", code.toUpperCase(), "BEGINS" ));

			in.getSortParams().put("task", true); // true=ASC, false=DESC
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
