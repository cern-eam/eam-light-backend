package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteACTTrade extends Autocomplete {

	private SimpleGridInput prepareInput() {
		SimpleGridInput in = new SimpleGridInput("85", "LVTRADE", "86");
		in.setGridType(GridRequest.GRIDTYPE.LOV);
		in.setFields(Arrays.asList("101", "103"));
		return in;
	}

	@GET
	@Path("/act/trade/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		SimpleGridInput in = prepareInput();
		in.getGridFilters().add(new GridRequestFilter("trade", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
		in.getGridFilters().add(new GridRequestFilter("tradedesc", code.toUpperCase(), "BEGINS" ));

		in.getSortParams().put("trade", true); // true=ASC, false=DESC
		try {
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
