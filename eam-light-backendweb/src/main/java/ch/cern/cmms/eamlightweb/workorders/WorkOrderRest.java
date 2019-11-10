package ch.cern.cmms.eamlightweb.workorders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Tools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.UserTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;

@Path("/workorders")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderRest extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private ApplicationData applicationData;
	@Inject
	private Tools tools;
	@EJB
	private UserTools userTools;

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
	@Path("/init/{entity}/{systemFunction}/{userFunction}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initWorkOrder(@PathParam("entity") String entity,
			@PathParam("systemFunction") String systemFunction, @PathParam("userFunction") String userFunction,
			@Context UriInfo info) {
		try {
			WorkOrder workOrder = inforClient.getWorkOrderService().readWorkOrderDefault(authenticationTools.getInforContext(), "");

			// Default values from request
			Map<String, List<String>> queryParams = info.getQueryParameters();
			Map<String, String> parameters = new HashMap<>();
			queryParams.forEach((k, v) -> parameters.put(k, v != null ? v.get(0) : null));
			// Check if there is screen parameter
			userFunction = parameters.get("screen") != null ? parameters.get("screen") : userFunction;

			// User defined fields
			workOrder.setUserDefinedFields(new UserDefinedFields());

			tools.pupulateBusinessObject(workOrder, parameters);

			// If there is a standard Work Order, then read the fields
			if (workOrder.getStandardWO() != null && !workOrder.getStandardWO().trim().equals("")) {
				// Obtain the loaded work order
				workOrder = inforClient.getWorkOrderService().readStandardWorkOrder(authenticationTools.getInforContext(), workOrder);
			}

			// Class
			String woclass = workOrder.getClassCode() != null && !"".equals(workOrder.getClassCode())
					? workOrder.getClassCode()
					: "*";
			// Custom Fields (Loaded with the default class, or the preloaded one)
			try {
				workOrder.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), "EVNT", woclass));
			} catch (Exception e) {

			}
			// Populate custom fields if they are not null
			if (workOrder.getCustomFields() != null) {
				tools.populateCustomFields(workOrder.getCustomFields(), parameters);
			}

			return ok(workOrder);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
