package ch.cern.cmms.eamlightweb.equipment;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.equipment.entities.NonConformity;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/ncrs")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class NonConformitiesRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readNonConformity(@PathParam(value = "code") String code) {
		try {
			return ok(inforClient.getNonconformityService().readNonconformity(authenticationTools.getInforContext(), code));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createNonConformity(NonConformity nonConformity) {
		try {
			return ok(inforClient.getNonconformityService().createNonconformity(authenticationTools.getInforContext(), nonConformity));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateNonConformity(NonConformity nonConformity) {
		try {
			return ok(inforClient.getNonconformityService().updateNonconformity(authenticationTools.getInforContext(), nonConformity));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@DELETE
	@Path("/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response deleteNonConformity(@PathParam("code") String code) {
		try {
			inforClient.getNonconformityService().deleteNonconformity(authenticationTools.getInforContext(), code);
			return noConent();
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
	public Response initNonConformity() {
			try {
				NonConformity nonConformity = inforClient.getNonconformityService().readNonconformityDefault(authenticationTools.getInforContext(), "");
				nonConformity.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), "NOCF", "*"));
				nonConformity.setUserDefinedFields(new UserDefinedFields());
				return ok(nonConformity);
			} catch (InforException e) {
				return badRequest(e);
			} catch (Exception e) {
				return serverError(e);
			}
		}


	@GET
	@Path("/equipment/{asset}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getEquipmentNonConformities(@PathParam("asset") String asset) {
		try {
			List<Map<String, String>> assetNonConformities = new ArrayList<>();
			if (asset != null) {
				GridRequest gridRequest = new GridRequest("OSNCHD");
				gridRequest.setUserFunctionName("OSNCHD");
				gridRequest.addFilter("equipment", asset, "=");
				assetNonConformities = inforClient.getTools().getGridTools().convertGridResultToMapList(
						inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)
				);
			}
			return ok(assetNonConformities);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
