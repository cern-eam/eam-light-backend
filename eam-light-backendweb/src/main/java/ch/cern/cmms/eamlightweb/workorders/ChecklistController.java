package ch.cern.cmms.eamlightweb.workorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.workorders.entities.Activity;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrderActivityCheckList;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrderActivityCheckListSignature;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/checklists")
@Interceptors({ RESTLoggingInterceptor.class })
public class ChecklistController extends EAMLightController {

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
		} catch (Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Path("/workorders")
	@Produces("application/json")
	@Consumes("application/json")
	public Response createFollowUpWorkOrders(Activity activity) {
		try {
			return ok(inforClient.getChecklistService().createFollowUpWorkOrders(authenticationTools.getInforContext(), activity));
		} catch (InforException e) {
			return badRequest(e);
		} catch (Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Path("/esign")
	@Produces("application/json")
	@Consumes("application/json")
	public Response eSignWorkOrderActivityChecklist(WorkOrderActivityCheckListSignature workOrderActivityCheckListSignature) {
		try {
			return ok(inforClient.getChecklistService().eSignWorkOrderActivityChecklist(authenticationTools.getInforContext(), workOrderActivityCheckListSignature));
		} catch (InforException e) {
			return badRequest(e);
		} catch (Exception e) {
			return serverError(e);
		}
	}
}
