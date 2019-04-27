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
public class AutocompleteDepartment extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("36", "LVMRCS", "36");
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.getInforParams().put("showstardepartment", null);
		in.setGridType("LOV");
		in.setFields(Arrays.asList("101", "103")); // 101=department, 103=des_text
		return in;
	}

	@GET
	@Path("/department/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			// Input
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("department", code.toUpperCase(), "CONTAINS"));
			in.getSortParams().put("department", true); // true=ASC, false=DESC
			// Result
			return  ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
