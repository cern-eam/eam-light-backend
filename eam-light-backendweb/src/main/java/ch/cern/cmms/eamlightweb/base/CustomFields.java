package ch.cern.cmms.eamlightweb.base;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.plugins.LDAPPlugin;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

@Path("/customfields")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class CustomFields extends EAMLightController {

	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;
	@Inject
	private CustomFieldsController customFieldsController;
	@Inject
	private LDAPPlugin ldapPlugin;

	@GET
	@Path("/data")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readCustomFields(@QueryParam("entity") String entity, @QueryParam("inforClass") String inforClass) {
		try {
			return ok(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), entity, inforClass));
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
			@QueryParam("inforClass") String inforClass) throws InforException {
		try {
			return ok(customFieldsController.readCustomFieldsLookupValues(entity, inforClass));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/autocomplete/{rentity}/{code}/{filter}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("rentity") String rentity, @PathParam("code") String code, @PathParam("filter") String filter) throws InforException {
		List<Pair> result = new LinkedList<>();
		if (code.equals("0003") && filter.length() > 3) {
			result.addAll(ldapPlugin.readEgroups(filter));
		}
		result.addAll(customFieldsController.cfEntity(rentity, filter));

		return ok(result);
	}

}
