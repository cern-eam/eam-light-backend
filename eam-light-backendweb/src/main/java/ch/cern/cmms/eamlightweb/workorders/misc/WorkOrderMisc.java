package ch.cern.cmms.eamlightweb.workorders.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;
import net.datastream.schemas.mp_results.mp7336_001.AdditionalWOEquipDetails;

@Path("/workordersmisc")
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderMisc extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/eqpmecwo/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getWorkOrderEquipment(@PathParam("workorder") String workorder) {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("247", "equipmentCode");
			map.put("249", "equipmentDesc");
			map.put("1872", "equipmentType");
			map.put("448", "equipmentTypeDesc");

			GridRequest gridRequest = new GridRequest("1631", "WSJOBS_MEC", "1618");
			gridRequest.addParam("param.workordernum", workorder);

			List<WorkOrderEquipment> childrenWOs = inforClient.getTools().getGridTools().convertGridResultToObject(WorkOrderEquipment.class,
					map,
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
			return ok(childrenWOs);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/childrenwo/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getChildrenWorkOrders(@PathParam("workorder") String workorder) throws InforException {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("1", "number");
			map.put("2", "description");
			map.put("5", "equipment");
			map.put("16", "status");
			map.put("12", "type");

			GridRequest gridRequest = new GridRequest("176", "WSJOBS_CWO", "180");
			gridRequest.addParam("param.jobnum", workorder);

			List<ChildWorkOrder> childrenWOs = inforClient.getTools().getGridTools().convertGridResultToObject(ChildWorkOrder.class,
														map,
														inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
			return ok(childrenWOs);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/equipment/{eqCode}/details")
	@Produces("application/json")
	public Response getWOEquipLinearDetails(@PathParam("eqCode") String eqCode) throws InforException {
		try {
			final AdditionalWOEquipDetails woEquipLinearDetails = inforClient.getWorkOrderMiscService().getEquipLinearDetails(authenticationTools.getR5InforContext(), eqCode);
			return ok(woEquipLinearDetails);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}