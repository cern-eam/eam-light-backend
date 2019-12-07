package ch.cern.cmms.eamlightweb.workorders.autocomplete;

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
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteWOCostCode extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/wo/costcode/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest("38", "LOV", "38");
			gridRequest.getParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
			gridRequest.getParams().put("userfunction", "WSJOBS");
			gridRequest.setRowCount(10);
			gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("costcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("des_text", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR ));

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("costcode", "des_text"),
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
