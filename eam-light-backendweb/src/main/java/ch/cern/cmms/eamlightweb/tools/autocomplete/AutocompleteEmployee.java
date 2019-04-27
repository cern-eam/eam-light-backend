package ch.cern.cmms.eamlightweb.tools.autocomplete;

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
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEmployee extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("42", "LVPERS", "42");
		in.getInforParams().put("loantodept", "TRUE");
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.setGridType("LOV");
		in.setFields(Arrays.asList("112", "113")); // 112=personcode, 113=description
		in.getInforParams().put("noemployees", null);
		in.getInforParams().put("shift", null);
		in.getInforParams().put("sessionid", null);
		in.getInforParams().put("per_type", null);
		return in;
	}

	@GET
	@Path("/employee/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			// Input
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("personcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR));

			try {
				Integer.valueOf(code.toUpperCase());
				in.getGridFilters().add(new GridRequestFilter("udfnum02", code.toUpperCase(), "EQUALS", GridRequestFilter.JOINER.OR));
			} catch (Exception e) {
			}

			in.getGridFilters().add(new GridRequestFilter("description", code.toUpperCase(), "CONTAINS"));
			//Sort
			in.getSortParams().put("description", true); // true=ASC, false=DESC
			// Result
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}