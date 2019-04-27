package ch.cern.cmms.eamlightweb.parts.autocomplete;

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

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePartCategory extends Autocomplete {

	@Inject
	LoggingService logger;

	private SimpleGridInput prepareInput() {
		SimpleGridInput in = new SimpleGridInput("95", "LVPARTCAT", "96");
		in.setGridType("LOV");
		in.setFields(Arrays.asList("101", "103")); // 101=category, 103=description
		return in;
	}

	@GET
	@Path("/part/category/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		SimpleGridInput in = prepareInput();
		in.getGridFilters().add(new GridRequestFilter("category", code.toUpperCase(), "BEGINS"));
		in.getSortParams().put("category", true); // true=ASC, false=DESC
		try {
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
