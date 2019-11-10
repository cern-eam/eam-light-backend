package ch.cern.cmms.eamlightweb.workorders.partusage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.equipment.EquipmentHistory;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrder;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.autocomplete.GridUtils;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.services.entities.WorkOrderPart;
import ch.cern.eam.wshub.core.services.grids.entities.*;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransaction;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionLine;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionType;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.Activity;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;

@Path("/partusage")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class PartUsageRest extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private GridUtils gridUtils;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/stores")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadStoreList() {
		try {
			GridRequest input = new GridRequest("LVIRSTOR");
			input.setUserFunctionName("SSISSU");
			input.setRowCount(1000);
			input.getParams().put("param.storefield", "IR");
			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("682", "133"),
					inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), input)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/bins")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadBinList(@QueryParam("transaction") String transaction, @QueryParam("bin") String bin,
								@QueryParam("part") String part, @QueryParam("store") String store) {
		try {
			GridRequest gridRequest;
			if (transaction.startsWith("I")) {
				// ISSUE
				gridRequest = new GridRequest("LVISSUEBIN");
				if (bin != null && !bin.isEmpty()) {
					gridRequest.getGridRequestFilters().add(new GridRequestFilter("bincode", bin, "BEGINS"));
				}
			} else {
				// RETURN
				gridRequest = new GridRequest("LVRETURNBIN");
			}
			gridRequest.getParams().put("part_code", part);
			gridRequest.getParams().put("part_org", authenticationTools.getInforContext().getOrganizationCode());
			gridRequest.getParams().put("store_code", store);

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("830", "824"),
					inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}


	/**
	 * Loads the default bin
	 *
	 * @return Default bin
	 * @throws InforException
	 *             Error loading the default bin
	 */
	private String loadDefaultBin(String part, String store, String transaction) throws InforException {
		String result = null;
		SimpleGridInput input = new SimpleGridInput("110", "SSPART_STO", "133");
		input.setGridType(GridRequest.GRIDTYPE.LIST);
		input.getInforParams().put("partcode", part);
		input.getInforParams().put("partorg", authenticationTools.getInforContext().getOrganizationCode());
		input.getInforParams().put("userfunction", "SSPART");
		input.getGridFilters().add(new GridRequestFilter("storecode", store, "EQUALS" ));

		if (transaction.startsWith("I")) // ISSUE
			input.setFields(Arrays.asList("661")); // 661=defaultbin
		else
			input.setFields(Arrays.asList("12651"));// 12651=defaultreturnbin

		GridRequestResult res = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());

		List<List<String>> gridRow = Arrays.stream(res.getRows())
				.map(row -> Arrays.stream(row.getCell()).filter(cell -> input.getFields().contains(cell.getCol()))
						.sorted((cell1, cell2) -> input.getFields().indexOf(cell1.getCol())
								- input.getFields().indexOf(cell2.getCol()))
						.map(cell -> cell.getContent()).collect(Collectors.toList()))
				.collect(Collectors.toList());

		try {
			if (gridRow != null && !gridRow.isEmpty() && gridRow.get(0) != null && !gridRow.get(0).isEmpty())
				result = gridRow.get(0).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result == null ? "" : result;
	}

	@POST
	@Path("/transaction")
	@Produces("application/json")
	@Consumes("application/json")
	public Response createPartUsage(IssueReturnPartTransaction transaction) {
		try {
			transaction.setTransactionOn(IssueReturnPartTransactionType.WORKORDER);
			return ok(inforClient.getPartMiscService().createIssueReturnTransaction(authenticationTools.getInforContext(), transaction));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}


	@POST
	@Path("/init")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initPartUsage(WorkOrder workOrder) {
		try {
			// Create the issue/return transacction for the workOrder
			IssueReturnPartTransaction transaction = createTransaction(workOrder);
			// Create the transaction line and add it to the transaction
			IssueReturnPartTransactionLine transLine = createTransactionLine();
			// Add the line to the transaction
			transaction.getTransactionlines().add(transLine);
			return ok(transaction);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}


	/**
	 * Creates the transaction line that is going to be added to the transaction
	 *
	 * @return The transaction line to be created
	 */
	private IssueReturnPartTransactionLine createTransactionLine() {
		// Init the line
		IssueReturnPartTransactionLine line = new IssueReturnPartTransactionLine();
		/* Assign attributes */
		// Part Code
		line.setPartCode(null);
		// Part Org
		line.setPartOrg("*");
		// Bin
		line.setBin(null);
		// Lot
		line.setLot(null);
		// Quantity
		line.setTransactionQty("1");
		// Asset code
		line.setAssetIDCode(null);
		// Returns line created
		return line;
	}

	/**
	 * Creates the transaction
	 *
	 * @return
	 * @throws InforException
	 */
	private IssueReturnPartTransaction createTransaction(WorkOrder workOrder) throws InforException {
		// Create the transaction
		IssueReturnPartTransaction transaction = new IssueReturnPartTransaction();
		/* Asign attributes */
		// Transaction Type
		transaction.setTransactionOn(IssueReturnPartTransactionType.WORKORDER);
		// Work order number
		transaction.setWorkOrderNumber(workOrder.getNumber());
		// Equipment
		transaction.setEquipmentCode(workOrder.getEquipmentCode());
		// Activity
		// Read activities
		Activity[] activities = inforClient.getLaborBookingService().readActivities(authenticationTools.getInforContext(), workOrder.getNumber(), false);
		if (activities != null && activities.length == 1) {
			transaction.setActivityCode(activities[0].getActivityCode());
		} else {
			transaction.setActivityCode(null);
		}
		// Store
		transaction.setStoreCode(null);
		// Department
		transaction.setDepartmentCode(workOrder.getDepartmentCode());
		// Transaction type
		transaction.setTransactionType("ISSUE");
		// Init lines
		transaction.setTransactionlines(new LinkedList<IssueReturnPartTransactionLine>());
		// Returns the transaction created
		return transaction;
	}

	@GET
	@Path("/transactions/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")

	public Response loadPartUsageList(@PathParam("workorder") String workorder) {
		try {
			// Init the list
			List<WorkOrderPart> partUsageList = new ArrayList<>();
			// Just execute if there is work order
			if (workorder != null) {
				Map<String, String> map = new HashMap<>();
				map.put("partcode", "partCode");
				//map.put("357", "partUom");
				map.put("partdescription", "partDesc");
				map.put("activity_display", "activityDesc");
				map.put("storecode", "storeCode");
				map.put("usedqty", "usedQty");
				map.put("plannedqty", "plannedQty");

				// Creates simple grid input
				GridRequest gridRequest = new GridRequest("226", "WSJOBS_PAR", "237");
				gridRequest.getParams().put("param.workordernum", workorder);
				gridRequest.getParams().put("param.headeractivity", "0");
				gridRequest.getParams().put("param.headerjob", "0");

				partUsageList = inforClient.getTools().getGridTools().converGridResultToObject(WorkOrderPart.class,
						map,
						inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

				partUsageList.stream().forEach(partUsage -> setPartUsageTransType(partUsage));
			}
			return ok(partUsageList);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	private void setPartUsageTransType(WorkOrderPart workOrderPartUsage) {
		try {
			if (inforClient.getTools().getDataTypeTools().isNotEmpty(workOrderPartUsage.getPlannedQty())) {
				workOrderPartUsage.setTransType("Planned");
				workOrderPartUsage.setQuantity(workOrderPartUsage.getPlannedQty());
			} else if (Integer.valueOf(workOrderPartUsage.getUsedQty()) < 0) {
				workOrderPartUsage.setTransType("Return");
				workOrderPartUsage.setQuantity("" + (-1 * Integer.valueOf(workOrderPartUsage.getUsedQty())));
			} else {
				workOrderPartUsage.setTransType("Issue");
				workOrderPartUsage.setQuantity(workOrderPartUsage.getUsedQty());
			}
		} catch (Exception e) {
			workOrderPartUsage.setTransType("Issue");
		}
	}

}
