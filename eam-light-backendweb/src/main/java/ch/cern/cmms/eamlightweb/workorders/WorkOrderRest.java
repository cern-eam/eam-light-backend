package ch.cern.cmms.eamlightweb.workorders;

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
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
import ch.cern.eam.wshub.core.tools.Tools;

import static ch.cern.cmms.eamlightweb.tools.OrganizationTools.assumeEquipmentMonoOrg;

@Path("/workorders")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readWorkOrder(@PathParam("workorder") String number) {
		try {
			WorkOrder workOrder = inforClient.getWorkOrderService().readWorkOrder(authenticationTools.getInforContext(), number);

			if ("IS".equals(workOrder.getTypeCode())) {
				throw Tools.generateFault("Invalid work order");
			}

			return ok(workOrder);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createWorkOrder(WorkOrder workOrder) {

		assumeEquipmentMonoOrg(workOrder);
		String woNumber = null;
		try {
			woNumber = inforClient.getWorkOrderService().createWorkOrder(authenticationTools.getInforContext(), workOrder);
			// Read again the work order
			return ok(inforClient.getWorkOrderService().readWorkOrder(authenticationTools.getInforContext(), woNumber));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateWorkOrder(WorkOrder workOrder) {
		try {
			assumeEquipmentMonoOrg(workOrder);
			inforClient.getWorkOrderService().updateWorkOrder(authenticationTools.getInforContext(), workOrder);
			// Read again the work order
			return ok(inforClient.getWorkOrderService().readWorkOrder(authenticationTools.getInforContext(), workOrder.getNumber()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@DELETE
	@Path("/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response deleteWorkOrder(@PathParam("workorder") String number) {
		try {
			return ok(inforClient.getWorkOrderService().deleteWorkOrder(authenticationTools.getInforContext(), number));
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
	public Response initWorkOrder() {
		try {
			WorkOrder workOrder = inforClient.getWorkOrderService()
					.readWorkOrderDefault(authenticationTools.getInforContext(), "");
			workOrder.setUserDefinedFields(new UserDefinedFields());
			workOrder.setCustomFields(inforClient.getTools()
					.getCustomFieldsTools()
					.getWSHubCustomFields(authenticationTools.getInforContext(), "EVNT", "*"));
			return ok(workOrder);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
