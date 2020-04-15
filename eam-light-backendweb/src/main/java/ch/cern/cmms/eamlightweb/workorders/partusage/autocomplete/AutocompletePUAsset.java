/**
 * 
 */
package ch.cern.cmms.eamlightweb.workorders.partusage.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
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
	@Path("/partusage/asset/{issuereturn}/{store}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("issuereturn") String issuereturn, @PathParam("store") String store, @PathParam("code") String code) {

		GridRequest gridRequest = new GridRequest("OSOBJA", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.addFilter("equipmentno", code.toUpperCase(), "CONTAINS", GridRequestFilter.JOINER.AND);

		if (issuereturn.startsWith("I")) // ISSUE
			gridRequest.addFilter("store", store, "=");
		else { // RETURN
			gridRequest.addFilter("store", "", "IS EMPTY");
		}

		return getPairListResponse(gridRequest, "equipmentno", "equipmentdesc");
	}

	@GET
	@Path("/partusage/asset/complete/{workOrder}/{issuereturn}/{store}/{assetCode}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response completeData(@PathParam("workOrder") String workOrder, @PathParam("issuereturn") String issuereturn,
								 @PathParam("store") String store, @PathParam("assetCode") String assetCode) {
		try {
			GridRequest gridRequest = new GridRequest( "OSOBJA", GridRequest.GRIDTYPE.LIST, 1);
			gridRequest.addFilter("equipmentno", assetCode.toUpperCase(), "=", GridRequestFilter.JOINER.AND);

			if (issuereturn.startsWith("I")) {
				// ISSUE
				gridRequest.addFilter("store", store.toUpperCase(), "EQUALS");
			} else {
				// RETURN
				gridRequest.addFilter("store", store.toUpperCase(), "IS EMPTY");
			}

			String[] fields = {"equipmentno", "equipmentdesc", "part", "bin", "lot"};
			return ok(inforClient.getTools().getGridTools().convertGridResultToMapList(Arrays.asList(fields),
					inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}