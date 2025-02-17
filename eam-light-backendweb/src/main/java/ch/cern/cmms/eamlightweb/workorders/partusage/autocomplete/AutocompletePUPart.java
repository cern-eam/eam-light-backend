package ch.cern.cmms.eamlightweb.workorders.partusage.autocomplete;

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
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import java.util.List;
import java.util.Map;

/**
 * Autocomplete class to select the part or asset in the part usage of work orders
 */
@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePUPart extends EAMLightController {

	@GET
	@Path("/partusage/part/{workorder}/{store}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("workorder") String workorder, @PathParam("store") String store,
							 @PathParam("code") String code) throws InforException{
		GridRequest gridRequest = new GridRequest( "LVIRPART", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
		gridRequest.addParam("control.org", authenticationTools.getInforContext().getOrganizationCode());
		gridRequest.addParam("multiequipwo", "false");
		gridRequest.addParam("store_code", store);
		gridRequest.addParam("parameter.excludeparentpart", "false");
		gridRequest.addParam("relatedworkordernum", workorder);

		gridRequest.addFilter("partcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR);

		if (code.length() > 3) {
			gridRequest.addFilter("partdescription", code, "CONTAINS", GridRequestFilter.JOINER.OR);
		}
		gridRequest.addFilter("udfchar01", code, "BEGINS", GridRequestFilter.JOINER.OR);
		// CDD Drawing Reference
		gridRequest.addFilter("udfchar03", code, "BEGINS", GridRequestFilter.JOINER.OR);
		// EDMS Item ID Reference
		gridRequest.addFilter("udfchar11", code.toUpperCase(), "BEGINS");

		final GridRequestResult gridRequestResult =
				inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest);

		final List<Map<String, String>> partList = GridTools.convertGridResultToMapList(gridRequestResult);
		partList.forEach(part -> {part.put("code", part.get("partcode")); part.put(
				"desc",
				part.get("partdescription"));});
		return ok(partList);
	}

}
