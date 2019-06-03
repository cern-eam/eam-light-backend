package ch.cern.cmms.eamlightweb.parts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.cmms.eamlightejb.parts.PartAssociation;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/partlists")
@Interceptors({ RESTLoggingInterceptor.class })
public class PartListsController extends DropdownValues {

	@GET
	@Path("/trackMethods")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readTrackingMethodCodes() {
		List<Pair> methods = new LinkedList<Pair>();
		methods.add(new Pair("NOST", "Non stocke, non suivi, depenses prevues"));
		methods.add(new Pair("TRPQ", "Stocke, qte suivie"));
		methods.add(new Pair("TRQ", "Stocke, quantite suivie, montant non suivi"));
		return ok(methods);
	}

	@GET
	@Path("/partsassociated/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadPartAssociation(@PathParam("part") String part) {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("7448", "entity");
			map.put("3747", "code");
			map.put("8463", "description");
			map.put("3750", "quantity");
			map.put("15932", "type");

			GridRequest gridRequest = new GridRequest("817", "SSPART_EPA", "800");
			gridRequest.getParams().put("partcode", part);
			gridRequest.getParams().put("partorg", authenticationTools.getInforContext().getOrganizationCode());

			List<PartAssociation> partAssociations = inforClient.getTools().getGridTools().converGridResultToObject(PartAssociation.class,
									map,
									inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

			return ok(partAssociations);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
