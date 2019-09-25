package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteClass extends Autocomplete {

	private SimpleGridInput prepareInput() {
		SimpleGridInput in = new SimpleGridInput("44", "LVCLAS", "44");
		in.setGridType(GridRequest.GRIDTYPE.LOV);
		in.setFields(Arrays.asList("681", "120")); // 681=class , 629=des_text
		return in;
	}

	@GET
	@Path("/class/{entity}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("entity") String entity, @PathParam("code") String code) {
		try {
			// Input
			SimpleGridInput in = prepareInput();
			in.getInforParams().put("parameter.rentity", entity);
			in.getGridFilters().add(new GridRequestFilter("class", code.toUpperCase(), "BEGINS"));
			in.getSortParams().put("class", true); // true=ASC, false=DESC
			// Result
			List<Pair> resultList = getGridResults(in);
			return ok(resultList);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
