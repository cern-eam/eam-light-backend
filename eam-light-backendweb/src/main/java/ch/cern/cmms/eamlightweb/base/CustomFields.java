package ch.cern.cmms.eamlightweb.base;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.CustomField;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/customfields")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class CustomFields extends WSHubController {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;
	@Inject
	private CustomFieldsController customFieldsController;

	@GET
	@Path("/data")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCustomFields(@QueryParam("entity") String entity, @QueryParam("inforClass") String inforClass) {
		try {
			return ok(inforClient.getTools().getCustomFieldsTools().getMTCustomFields(authenticationTools.getInforContext(), entity, inforClass));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/lookupvalues")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCustomFieldsLookupValues(@QueryParam("entity") String entity,
			@QueryParam("inforClass") String inforClass) {
		Map<String, List<Pair>> customFieldLookupValues = new HashMap<>();
		try {
			CustomField[] customFields = inforClient.getTools().getCustomFieldsTools().getMTCustomFields(authenticationTools.getInforContext(), entity, inforClass);
			for (CustomField customField : customFields) {
				if (customField.getLovType().equals("C")
						|| customField.getLovType().equals("E")
						|| customField.getLovType().equals("P")) {
					switch (customField.getType()) {
					case "CODE":
						customFieldLookupValues.put(customField.getCode(), customFieldsController.cfCodeDesc(customField.getCode()));
						break;
					case "NUM":
						customFieldLookupValues.put(customField.getCode(), customFieldsController.cfNum(customField.getCode()));
						break;
					case "DATI":
						customFieldLookupValues.put(customField.getCode(), customFieldsController.cfDateTime(customField.getCode()));
						break;
					case "DATE":
						customFieldLookupValues.put(customField.getCode(), customFieldsController.cfDate(customField.getCode()));
						break;
					case "CHAR":
						customFieldLookupValues.put(customField.getCode(), customFieldsController.cfChar(customField.getCode()));
						break;
					}
				}
			}
			return ok(customFieldLookupValues);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/autocomplete/{rentity}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("rentity") String rentity, @PathParam("code") String code) throws InforException {
		return ok(customFieldsController.cfEntity(rentity, code));
	}

}
