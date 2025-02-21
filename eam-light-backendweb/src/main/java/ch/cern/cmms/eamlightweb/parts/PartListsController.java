package ch.cern.cmms.eamlightweb.parts;

import java.util.*;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightejb.parts.PartAssociation;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/partlists")
@Interceptors({ RESTLoggingInterceptor.class })
public class PartListsController extends EAMLightController {

	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/trackMethods")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readTrackingMethodCodes() throws InforException {
		GridRequest gridRequest = new GridRequest("LVTRACK", GridRequest.GRIDTYPE.LOV);
		return ok(inforClient.getTools().getGridTools().convertGridResultToObject(Pair.class,
				Pair.generateGridPairMap("101", "103"),
				inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
	}

	@GET
	@Path("/assets/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadAssets(@PathParam("part") String part) {
		try {
			GridRequest gridRequest = new GridRequest("OSOBJA", 1000);
			gridRequest.setUserFunctionName("OSOBJA");
			gridRequest.addFilter("part", part, "EQUALS");

			String[] fields = new String[] {"equipmentno", "equipmentdesc", "assetstatus", "assetstatus_display", "location", "department"};

			return ok(inforClient.getTools().getGridTools().convertGridResultToMapList(inforClient.getGridsService()
					.executeQuery(authenticationTools.getInforContext(), gridRequest), Arrays.asList(fields)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
