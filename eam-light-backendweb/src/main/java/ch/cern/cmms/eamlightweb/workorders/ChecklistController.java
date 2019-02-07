package ch.cern.cmms.eamlightweb.workorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrderActivityCheckList;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/checklists")
@Interceptors({ RESTLoggingInterceptor.class })
public class ChecklistController extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateChecklist(WorkOrderActivityCheckList checklist) {
		try {
			return ok(inforClient.getChecklistService().updateWorkOrderChecklist(authenticationTools.getInforContext(), checklist));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
