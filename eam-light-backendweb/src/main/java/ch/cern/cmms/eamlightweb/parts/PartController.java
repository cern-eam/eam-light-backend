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

import ch.cern.cmms.eamlightejb.UserTools;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Tools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightejb.parts.PartsEJB;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightejb.layout.ElementInfo;
import ch.cern.cmms.eamlightejb.layout.LayoutBean;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.material.entities.Part;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/parts")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class PartController extends WSHubController {

	@Inject
	private InforClient inforClient;
	@EJB
	private PartsEJB partsEJB;
	@EJB
	private LayoutBean layoutBean;
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
			return ok(partsEJB.getPartStock(partCode, authenticationTools.getInforContext().getCredentials().getUsername()));
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

			// Default values from screen
			Map<String, ElementInfo> screenFields = layoutBean.getRecordViewElements(systemFunction, userFunction,
					entity, userTools.getUserGroup(authenticationTools.getInforContext()));

			// Assign default values
			assignDefaultValues(part, screenFields);
			// Populate Object
			tools.pupulateBusinessObject(part, parameters);

			// Custom Fields
			part.setCustomFields(inforClient.getTools().getCustomFieldsTools().getMTCustomFields(authenticationTools.getInforContext(), "PART",
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

	/**
	 * Assign the default values to the object being created
	 */
	private void assignDefaultValues(Part part, Map<String, ElementInfo> fields) {
		// Iterate over the list of fields
		for (String key : fields.keySet()) {
			// ElementInfo
			ElementInfo element = fields.get(key);
			// Just check if default value is not null
			if (element.getDefaultValue() == null)
				continue;/* Continue with next record */
			// Get default value
			String defaultValue = element.getDefaultValue().trim();
			// Switch according to the key
			switch (key) {
			case "partcode":
				part.setCode(defaultValue);
				break;
			case "description":
				part.setDescription(defaultValue);
				break;
			case "class":
				part.setClassCode(defaultValue);
				break;
			case "category":
				part.setCategoryCode(defaultValue);
				break;
			case "uom":
				part.setUOM(defaultValue);
				break;
			case "trackingtype":
				part.setTrackingMethod(defaultValue);
				break;
			case "commoditycode":
				part.setCommodityCode(defaultValue);
				break;
			case "trackbyasset":
				part.setTrackByAsset(defaultValue);
				break;
			case "udfchar01":
				part.getUserDefinedFields().setUdfchar01(defaultValue);
				break;
			case "udfchar02":
				part.getUserDefinedFields().setUdfchar02(defaultValue);
				break;
			case "udfchar03":
				part.getUserDefinedFields().setUdfchar03(defaultValue);
				break;
			case "udfchar04":
				part.getUserDefinedFields().setUdfchar04(defaultValue);
				break;
			case "udfchar05":
				part.getUserDefinedFields().setUdfchar05(defaultValue);
				break;
			case "udfchar11":
				part.getUserDefinedFields().setUdfchar11(defaultValue);
				break;
			case "udfchar12":
				part.getUserDefinedFields().setUdfchar12(defaultValue);
				break;
			}
		}
	}
}
