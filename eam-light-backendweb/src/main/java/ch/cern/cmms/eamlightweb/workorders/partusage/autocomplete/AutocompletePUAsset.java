/**
 * 
 */
package ch.cern.cmms.eamlightweb.workorders.partusage.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePUAsset extends EAMLightController {

	@GET
	@Path("/partusage/asset")
	@Produces("application/json")
	public Response complete(@QueryParam("transaction") String transaction, @QueryParam("store") String store,
							 @QueryParam("part") String part, @QueryParam("code") String code) {

		GridRequest gridRequest = new GridRequest("OSOBJA", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.setUserFunctionName("OSOBJA");
		gridRequest.addFilter("equipmentno", code.toUpperCase(), "CONTAINS", GridRequestFilter.JOINER.AND);

		if (!part.isEmpty()) {
			gridRequest.addFilter("part", part, "=", GridRequestFilter.JOINER.AND);
		}

		if (transaction.equals("ISSUE")) {
			gridRequest.addFilter("store", store, "=");
		} else if (transaction.equals("RETURN")) {
			gridRequest.addFilter("store", "", "IS EMPTY");
		}

		return getPairListResponse(gridRequest, "equipmentno", "equipmentdesc");
	}

}