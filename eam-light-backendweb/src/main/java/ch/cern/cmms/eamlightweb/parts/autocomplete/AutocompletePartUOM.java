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

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePartUOM extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("33", "LVUOMS", "33");
		in.setGridType(GridRequest.GRIDTYPE.LOV);
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.getInforParams().put("aspect", null);
		in.setFields(Arrays.asList("666", "103")); // 666=uomcode, 103=description
		return in;
	}

	@GET
	@Path("/part/uom/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("uomcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR));
			in.getGridFilters().add(new GridRequestFilter("description", code.toUpperCase(), "BEGINS"));
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
