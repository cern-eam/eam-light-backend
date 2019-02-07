package ch.cern.cmms.eamlightweb.workorders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightejb.workorders.ChildWorkOrder;
import ch.cern.cmms.eamlightejb.workorders.WorkOrdersEJB;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.autocomplete.GridUtils;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestCell;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestRow;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/workordersmisc")
@Interceptors({ RESTLoggingInterceptor.class })
public class WorkOrderMisc extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;
	@EJB
	private WorkOrdersEJB wosEJB;
	@Inject
	protected GridUtils gridUtils;

	@GET
	@Path("/eqpmecwo/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getWorkOrderEquipment(@PathParam("workorder") String workorder) {
		try {
			return ok(wosEJB.getWorkOrderEquipment(workorder));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/childrenwo/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getChildrenWorkOrders(@PathParam("workorder") String workorder) throws InforException {
		// Init the list of children
		List<ChildWorkOrder> childrenWOs = new ArrayList<>();
		try {
			// 1 childworkordernum
			// 819 dependent
			// 2 description
			// 5 equipment
			// 449 equipmentorg
			// 448 equipmenttype
			// 9579 hasnodeptsecright
			// 451 parentworkordernum
			// 16 status
			// 12 type
			// Creates simple grid input
			SimpleGridInput input = new SimpleGridInput("176", "WSJOBS_CWO", "180");
			// GridController Type
			input.setGridType("LIST");
			// Fields to be retrieved
			input.setFields(Arrays.asList("1", "2", "5", "16", "12"));
			// Rows to print
			input.setRowCount("1000");
			Map<String, String> inforParams = new HashMap<>();
			inforParams.put("workordernum", workorder);
			inforParams.put("jobnum", workorder);
			inforParams.put("organization", authenticationTools.getInforContext().getOrganizationCode());
			inforParams.put("workorderrtype", "*");
			// Add infor params
			inforParams.forEach((k, v) -> {
				input.getInforParams().put(k, v);
			});
			// Execute grid
			GridRequestResult res = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());
			// Process result
			Arrays.stream(res.getRows()).forEach(row -> childrenWOs.add(processRow(row, input.getFields())));
			return ok(childrenWOs);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Process result row to create a new Child Work order and add it to the result
	 * list
	 * 
	 * @param row
	 *            Rows to be processed
	 * @param fields
	 *            List of the fields to be included in the object
	 * @return Indicator of row processed
	 */
	private ChildWorkOrder processRow(GridRequestRow row, List<String> fields) {
		// Create new Work Order part usage
		ChildWorkOrder childWO = new ChildWorkOrder();
		Arrays.stream(row.getCell()).filter(cell -> fields.contains(cell.getCol()))
				.forEach(cell -> addPropertyToChildWorkOrder(childWO, cell));
		// Add element to the list
		return childWO;
	}

	/**
	 * Add a property to the child Work order object
	 * 
	 * @param childWO
	 *            Child Work Order object to add the property
	 * @param cell
	 *            cell containing the property to be added
	 * @return Indicator of property added
	 */
	private boolean addPropertyToChildWorkOrder(ChildWorkOrder childWO, GridRequestCell cell) {
		// Check property
		switch (cell.getCol()) {
		case "1":/* childworkordernum */
			childWO.setNumber(cell.getContent());
			break;
		case "2":/* description */
			childWO.setDescription(cell.getContent());
			break;
		case "5":/* equipment */
			childWO.setEquipment(cell.getContent());
			break;
		case "16":/* status */
			childWO.setStatus(cell.getContent());
			break;
		case "12": /* Type */
			childWO.setType(cell.getContent());
			break;

		}
		// Property added
		return true;
	}
}
