package ch.cern.cmms.eamlightweb.workorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.workorders.entities.Activity;
import ch.cern.eam.wshub.core.services.workorders.entities.TaskPlan;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrderActivityChecklistItem;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrderActivityChecklistSignature;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigInteger;

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
	public Response updateChecklist(
			WorkOrderActivityChecklistItem checklistItem,
			@QueryParam("taskPlanCode") String taskPlanCode
	) {
		try {
			TaskPlan taskPlan = null;
			if (taskPlanCode != null && !taskPlanCode.isEmpty()) {
				taskPlan = new TaskPlan();
				taskPlan.setCode(taskPlanCode);
				taskPlan.setTaskRevision(BigInteger.ZERO);
			}
			return ok(inforClient.getChecklistService().updateWorkOrderChecklistItem(
					authenticationTools.getInforContext(),
					checklistItem,
					taskPlan
			));
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
	public Response eSignWorkOrderActivityChecklist(WorkOrderActivityChecklistSignature workOrderActivityCheckListSignature) {
		try {
			return ok(inforClient.getChecklistService().eSignWorkOrderActivityChecklist(authenticationTools.getInforContext(), workOrderActivityCheckListSignature));
		} catch (InforException e) {
			return badRequest(e);
		} catch (Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/definition/{taskplanid}/{id}")
	@Produces("application/json")
	public Response getChecklistDefinition(@PathParam("taskplanid") String taskPlanCode, @PathParam("id") String id) {
		try {
			TaskPlan taskPlan = new TaskPlan();
			taskPlan.setCode(taskPlanCode);
			taskPlan.setTaskRevision(BigInteger.ZERO);
			return ok(inforClient.getChecklistService().getChecklistDefinition(authenticationTools.getInforContext(), taskPlan, id));
		} catch (InforException e) {
			return badRequest(e);
		} catch (Exception e) {
			return serverError(e);
		}
	}
}
