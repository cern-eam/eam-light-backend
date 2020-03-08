package ch.cern.cmms.eamlightweb.workorders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.workorders.entities.StandardWorkOrder;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isEmpty;

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
			return ok(inforClient.getWorkOrderService().readWorkOrder(authenticationTools.getInforContext(), number));
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
	@Path("/init/{entity}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initWorkOrder(@PathParam("entity") String entity,
								  @DefaultValue("") @QueryParam("classcode") String classCode,
								  @DefaultValue("") @QueryParam("standardwo") String standardWO) {
		try {
			WorkOrder workOrder = inforClient.getWorkOrderService().readWorkOrderDefault(authenticationTools.getInforContext(), "");
			// User defined fields
			workOrder.setUserDefinedFields(new UserDefinedFields());
			// Class
			String woclass = isNotEmpty(classCode) ? classCode : "*";
			// Custom Fields (Loaded with the default class, or the preloaded one)
			workOrder.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), "EVNT", woclass));
			return ok(workOrder);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
