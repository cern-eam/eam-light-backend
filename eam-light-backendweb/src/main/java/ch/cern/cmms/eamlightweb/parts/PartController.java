package ch.cern.cmms.eamlightweb.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Tools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightejb.parts.PartsEJB;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.UserTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightejb.layout.ElementInfo;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.material.entities.Part;
import ch.cern.eam.wshub.core.services.material.entities.PartStock;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/parts")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class PartController extends WSHubController {

	@Inject
	private InforClient inforClient;
	@EJB
	private PartsEJB partsEJB;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private Tools tools;
	@EJB
	private UserTools userTools;

	@GET
	@Path("/partstock/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readPartStock(@PathParam("part") String partCode) {
		try {
			GridRequest gridRequest = new GridRequest("SSPART_BIS", GridRequest.GRIDTYPE.LOV);
			gridRequest.setUserFunctionName("SSPART");
			gridRequest.getParams().put("partorg", authenticationTools.getInforContext().getOrganizationCode());
			gridRequest.getParams().put("partcode", partCode);

			Map<String, String> map = new HashMap<>();
			map.put("bisstore", "storeCode");
			map.put("storedesc", "storeDesc");
			map.put("bisbin", "bin");
			map.put("bislot", "lot");
			map.put("bisqty", "qtyOnHand");
			map.put("repairquantity", "repairQuantity");
			map.put("bisassetid", "assetCode");

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(PartStock.class,
					map,
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readPart(@PathParam("part") String number) {
		try {
			return ok(inforClient.getPartService().readPart(authenticationTools.getInforContext(), number));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createPart(Part part) {
		Part response = null;
		try {
			// Generate new numeric code if the requested code starts with @
			if (part.getCode()!=null && part.getCode().startsWith("@")) {
				String prefix = part.getCode().substring(1,part.getCode().length());
				Optional<String> newCode = partsEJB.getNextAvailablePartCode(prefix);
				if (newCode.isPresent()) {
					part.setCode(newCode.get());
				} else {
					return badRequest(new Exception("Wrong code provided after '@'"));
				}
			}			
			// create part
			inforClient.getPartService().createPart(authenticationTools.getInforContext(), part);
			// Read again the part
			return ok(inforClient.getPartService().readPart(authenticationTools.getInforContext(), part.getCode()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updatePart(Part part) {
		try {
			inforClient.getPartService().updatePart(authenticationTools.getInforContext(), part);
			// Read again the part
			return ok(inforClient.getPartService().readPart(authenticationTools.getInforContext(), part.getCode()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@DELETE
	@Path("/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response deletePart(@PathParam("part") String partCode) {
		try {
			return ok(inforClient.getPartService().deletePart(authenticationTools.getInforContext(), partCode));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/init/{entity}/{systemFunction}/{userFunction}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initPart(@PathParam("entity") String entity, @PathParam("systemFunction") String systemFunction,
			@PathParam("userFunction") String userFunction, @Context UriInfo info) {
		try {
			// Default values from request
			Map<String, List<String>> queryParams = info.getQueryParameters();
			Map<String, String> parameters = new HashMap<>();
			queryParams.forEach((k, v) -> parameters.put(k, v != null ? v.get(0) : null));
			// Check if there is screen parameter
			userFunction = parameters.get("screen") != null ? parameters.get("screen") : userFunction;

			Part part = new Part();
			// User defined fields
			part.setUserDefinedFields(new UserDefinedFields());

			// Populate Object
			tools.pupulateBusinessObject(part, parameters);

			// Custom Fields
			part.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), "PART",
					part.getClassCode() != null ? part.getClassCode() : "*"));

			// Populate custom fields if they are not null
			if (part.getCustomFields() != null) {
				tools.populateCustomFields(part.getCustomFields(), parameters);
			}
			// Final response
			return ok(part);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
