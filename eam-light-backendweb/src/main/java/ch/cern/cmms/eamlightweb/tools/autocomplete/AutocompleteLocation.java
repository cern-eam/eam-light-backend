package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteLocation extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("3660", "LVOBJL_LOC", "3715");
		//TODO
		//in.getInforParams().put("loantodept", applicationData.getLoadToDept());
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.setGridType("LIST");
		in.setFields(Arrays.asList("247", "249")); // 247=equipmentcode, 249=equipmentdesc
		in.getInforParams().put("cctrspcvalidation", "D");
		in.getInforParams().put("department", "");
		in.getInforParams().put("filterutilitybill", null);
		return in;
	}

	@GET
	@Path("/location")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code) {
		try {
			// Input
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("equipmentcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR));
			in.getGridFilters().add(new GridRequestFilter("variable5", code.toUpperCase(), "BEGINS"));
			in.getSortParams().put("equipmentcode", true); // true=ASC, false=DESC
			// Result
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
