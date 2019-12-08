package ch.cern.cmms.eamlightweb.equipment.autocomplete;

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
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentPrimarySystem extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/eqp/primsystem/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest("2085", "LVOBJL_EQ", "2055");
			gridRequest.getParams().put("param.objectorg", authenticationTools.getInforContext().getOrganizationCode());
			gridRequest.getParams().put("param.objectrtype", "S");
			gridRequest.getParams().put("param.objectcode", "");
			gridRequest.sortBy("equipmentcode");
			gridRequest.setRowCount(10);
			gridRequest.setGridType(GridRequest.GRIDTYPE.LIST);
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("equipmentcode", code.toUpperCase(), "BEGINS"));

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("equipmentcode", "equipmentdesc"),
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}