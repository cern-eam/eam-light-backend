package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentParent extends EAMLightController {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/eqp/parent/{type}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("type") String type, @QueryParam("code") String code) {
			GridRequest gridRequest = new GridRequest( "LVOBJL_EQ", GridRequest.GRIDTYPE.LIST, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);
			gridRequest.setUseNative(false);

			gridRequest.addParam("param.objectrtype", type);
			gridRequest.addParam("param.bypassdeptsecurity", null);
			gridRequest.addParam("param.objectcode", "");
			gridRequest.addParam("param.objectorg", authenticationTools.getOrganizationCode());
			gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());

			gridRequest.addFilter("equipmentcode", code.toUpperCase(), "BEGINS");
			gridRequest.sortBy("equipmentcode");

			return getPairListResponse(gridRequest, "equipmentcode", "equipmentdesc");
	}

}