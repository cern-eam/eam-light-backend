package ch.cern.cmms.eamlightweb.tools.autocomplete;

import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipment extends EAMLightController {

	@Inject
	private EquipmentEJB equipmentEJB;

	private GridRequest prepareGridRequest(GridRequest.GRIDTYPE gridType)  {
		// Dataspy changed from 59 to nothing, to select default dataspy. To be reviewed after next PROD-to-TEST
		GridRequest gridRequest = new GridRequest("LVOBJL");
		gridRequest.setGridType(gridType);
		gridRequest.setRowCount(10);
		gridRequest.addParam("param.objectrtype", null);
		gridRequest.addParam("param.loantodept", "true");
		gridRequest.addParam("param.bypassdeptsecurity", "false");
		gridRequest.addParam("parameter.filterutilitybill", null);
		gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());
		gridRequest.addParam("param.cctrspcvalidation", "D");
		gridRequest.addParam("param.department", null);

		return gridRequest;
	}

	@GET
	@Path("/eqp")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code, @QueryParam("filterL") Boolean excludeLocations) {
		try {
			return ok(equipmentEJB.getEquipmentSearchResults(code, excludeLocations ? Arrays.asList("A", "P", "S") : null, authenticationTools.getInforContext()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
