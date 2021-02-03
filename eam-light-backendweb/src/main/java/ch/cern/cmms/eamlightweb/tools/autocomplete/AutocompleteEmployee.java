package ch.cern.cmms.eamlightweb.tools.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

import java.util.Arrays;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEmployee extends EAMLightController {

	@GET
	@Path("/employee/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		GridRequest gridRequest = new GridRequest("LVPERS", GridRequest.GRIDTYPE.LOV, 10);
		gridRequest.addParam("parameter.per_type", null);
		gridRequest.addParam("param.bypassdeptsecurity", "true");
		gridRequest.addParam("param.sessionid", null);
		gridRequest.addParam("parameter.noemployees", null);
		gridRequest.addParam("param.shift", null);

		String uppercasedCode = code.toUpperCase();

		gridRequest.addFilter("personcode", uppercasedCode, "BEGINS", GridRequestFilter.JOINER.OR);

		Arrays.stream(uppercasedCode.split(" ")).forEach(name -> {
			gridRequest.addFilter("description", " " + name, "CONTAINS",
					GridRequestFilter.JOINER.OR, true, false);

			gridRequest.addFilter("description", name, "BEGINS",
					GridRequestFilter.JOINER.AND, false, true);
		});

		gridRequest.sortBy("description");

		return getPairListResponse(gridRequest,"personcode", "description");
	}
}