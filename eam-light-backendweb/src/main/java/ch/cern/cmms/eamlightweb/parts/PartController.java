package ch.cern.cmms.eamlightweb.parts;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.material.entities.Part;
import ch.cern.eam.wshub.core.services.material.entities.PartStock;
import ch.cern.eam.wshub.core.tools.InforException;

import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

@Path("/parts")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class PartController extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private PartService partService;

	@GET
	@Path("/partstock/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readPartStock(@PathParam("part") String partCode) {
		try {
			GridRequest gridRequest = new GridRequest("SSPART_BIS", GridRequest.GRIDTYPE.LOV);
			gridRequest.setUserFunctionName("SSPART");
			gridRequest.addParam("partorg", authenticationTools.getInforContext().getOrganizationCode());
			gridRequest.addParam("partcode", partCode);

			return ok(inforClient.getTools().getGridTools().convertGridResultToObject(PartStock.class,
					null,
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
				Optional<String> newCode = partService.getNextAvailablePartCode(prefix,
					authenticationTools.getInforContext());
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
	@Path("/init/{entity}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initPart(@PathParam("entity") String entity,
							 @DefaultValue("") @QueryParam("newCode") String newCode,
							 @DefaultValue("") @QueryParam("classcode") String classCode) {
		try {
			Part part = inforClient.getPartService().readPartDefault(authenticationTools.getInforContext(), "");

			part.setUserDefinedFields(new UserDefinedFields());

			if (isNotEmpty(newCode)) {
				part.setCode(newCode);
			}

			// Custom Fields
			String partClass = isNotEmpty(classCode) ? classCode : "*";
			part.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getR5InforContext(), "PART", partClass));

			return ok(part);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
