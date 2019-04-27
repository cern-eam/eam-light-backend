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
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteACTMaterialList extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("90", "LVMATL", "91");
		in.setGridType("LOV");
		in.setFields(Arrays.asList("101", "103"));
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		return in;
	}

	@GET
	@Path("/act/matlist/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("matlist", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
			in.getGridFilters().add(new GridRequestFilter("matlistdesc", code.toUpperCase(), "BEGINS" ));

			in.getSortParams().put("matlist", true); // true=ASC, false=DESC
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
