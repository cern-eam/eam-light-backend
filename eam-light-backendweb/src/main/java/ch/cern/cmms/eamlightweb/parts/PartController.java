package ch.cern.cmms.eamlightweb.parts;

import ch.cern.cmms.eamlightweb.codegenerator.CodeGeneratorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.material.entities.Part;
import ch.cern.eam.wshub.core.services.material.entities.PartStock;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.tools.Tools;

@Path("/parts")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class PartController extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private CodeGeneratorService codeGeneratorService;

	@GET
	@Path("/partstock/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readPartStock(@PathParam("part") String partCode) {
		try {
			GridRequest gridRequest = new GridRequest("SSPART_BIS", GridRequest.GRIDTYPE.LOV);
			gridRequest.setUserFunctionName("SSPART");
			String org = Tools.extractOrganizationCode(partCode);
			if (org == null) {
				//org = authenticationTools.getInforContext().getOrganizationCode();
				org = "*";
			}
			gridRequest.addParam("partorg", org);
			gridRequest.addParam("partcode", Tools.extractEntityCode(partCode));

			final GridRequestResult gridRequestResult = inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest);

			return ok(GridTools.convertGridResultToObject(PartStock.class,null, gridRequestResult));
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
			final Part part = inforClient.getPartService().readPart(authenticationTools.getInforContext(), number +
					"#*");
			return ok(part);
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
		try {
			// Generate new numeric code if the requested code starts with @
			InforContext context = authenticationTools.getInforContext();
			if (part.getCode()!=null && codeGeneratorService.isCodePrefix(part.getCode())) {
				String newCode = codeGeneratorService.getNextPartCode(part.getCode(),
					context);
					part.setCode(newCode);
			}
			// create part
			return ok(inforClient.getPartService().createPart(context, part));
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
			return ok(inforClient.getPartService().updatePart(authenticationTools.getInforContext(), part));
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
	@Path("/init")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initPart() {
		try {
			Part part = inforClient.getPartService().readPartDefault(authenticationTools.getInforContext(), "");
			part.setUserDefinedFields(new UserDefinedFields());
			part.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getR5InforContext(), "PART", "*"));
			return ok(part);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
