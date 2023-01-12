package ch.cern.cmms.eamlightweb.workorders.partusage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
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

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.plugins.SharedPlugin;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.entities.WorkOrderPart;
import ch.cern.eam.wshub.core.services.grids.entities.*;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransaction;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionLine;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionType;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.Activity;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;

@Path("/partusage")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class PartUsageRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private SharedPlugin sharedPlugin;

	@GET
	@Path("/stores")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadStoreList() {
		try {
			GridRequest gridRequest = new GridRequest("LVIRSTOR", GridRequest.GRIDTYPE.LOV, 1000);
			gridRequest.setUserFunctionName("SSISSU");
			gridRequest.getParams().put("param.storefield", "IR");
			gridRequest.getParams().put("parameter.r5role", "");
			return ok(inforClient.getTools().getGridTools().convertGridResultToObject(Pair.class,
					Pair.generateGridPairMap("682", "133"),
					inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
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
				gridRequest = new GridRequest("LVISSUEBIN", GridRequest.GRIDTYPE.LOV);
				if (bin != null && !bin.isEmpty()) {
					gridRequest.addFilter("bincode", bin, "BEGINS");
				}
			} else {
				// RETURN
				gridRequest = new GridRequest("LVRETURNBIN");
			}
			gridRequest.addParam("part_code", part);
			gridRequest.addParam("part_org", authenticationTools.getInforContext().getOrganizationCode());
			gridRequest.addParam("store_code", store);

			return ok(inforClient.getTools().getGridTools().convertGridResultToObject(Pair.class,
					Pair.generateGridPairMap("830", "824"),
					inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/lots/issue")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadLotListIssue(@QueryParam("lot") String lot, @QueryParam("bin") String bin,
									 @QueryParam("part") String part, @QueryParam("store") String store,
									 @QueryParam("requireAvailableQty") boolean requireAvailableQty) {
		try {
			GridRequest gridRequest;
			InforContext context = authenticationTools.getInforContext();

			gridRequest = new GridRequest("LVIRLOT", GridRequest.GRIDTYPE.LOV);

			gridRequest.addParam("bin_code", bin);
			gridRequest.addParam("part_code", part);
			gridRequest.addParam("part_org", context.getOrganizationCode());
			gridRequest.addParam("store_code", store);

			if (requireAvailableQty) {
				gridRequest.addFilter("availableqty", "0", ">", GridRequestFilter.JOINER.AND);
			}

			if (lot != null && !lot.isEmpty()) {
				gridRequest.addFilter("lotcode", lot, "=");
			}

			return ok(GridTools.convertGridResultToObject(Pair.class,
					  Pair.generateGridPairMap("825", "2175"),
					  inforClient.getGridsService().executeQuery(context, gridRequest)));

		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/lots/return")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadLotListReturn(@QueryParam("lot") String lot, @QueryParam("part") String part) {
		try {
			GridRequest gridRequest;
			InforContext context = authenticationTools.getInforContext();

			List<Pair> udsLots = sharedPlugin.getUdsLots(part, inforClient, context);

			// Check whether there are user defined lots, otherwise return all lots
			if (udsLots != null && udsLots.size() > 0) {
				return ok(udsLots);
			} else {
				gridRequest = new GridRequest("LVLOT", GridRequest.GRIDTYPE.LOV);
				gridRequest.setRowCount(10000);
			}

			if (lot != null && !lot.isEmpty()) {
				gridRequest.addFilter("lotcode", lot, "=");
			}

			return ok(GridTools.convertGridResultToObject(Pair.class,
					  Pair.generateGridPairMap("2174", "2175"),
					  inforClient.getGridsService().executeQuery(context, gridRequest)));

		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
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
		line.setTransactionQty(new BigDecimal(1));
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
			transaction.setActivityCode(activities[0].getActivityCode().toString());
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
				// Creates simple grid input
				GridRequest gridRequest = new GridRequest("WSJOBS_PAR");
				gridRequest.addParam("param.workordernum", workorder);
				gridRequest.addParam("param.headeractivity", "0");
				gridRequest.addParam("param.headerjob", "0");

				partUsageList = inforClient.getTools().getGridTools().convertGridResultToObject(WorkOrderPart.class,
						null,
						inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

				partUsageList = transformPartUsageList(partUsageList);
			}
			return ok(partUsageList);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	private List<WorkOrderPart> transformPartUsageList(List<WorkOrderPart> list) {
		List<WorkOrderPart> rows = new ArrayList<>();

		Function<WorkOrderPart, WorkOrderPart> cloneWoPart = woPart -> {
			try {
				return (WorkOrderPart) woPart.clone();
			} catch(CloneNotSupportedException e) {
				throw new RuntimeException("Failed to clone WorkOrderPart");
			}
		};

		list.stream().forEach(woPart -> {
			if (woPart.getPlannedQty() != null && woPart.getPlannedQty().compareTo(BigDecimal.ZERO) == 1) {
				WorkOrderPart clone = cloneWoPart.apply(woPart);
				clone.setTransType("Planned");
				clone.setQuantity(clone.getPlannedQty());
				rows.add(clone);
			}

			if (woPart.getUsedQty() != null) {
				int usedQtyCompareToZero = woPart.getUsedQty().compareTo(BigDecimal.ZERO);
				if (usedQtyCompareToZero != 0) {
					WorkOrderPart clone = cloneWoPart.apply(woPart);
					boolean issue = usedQtyCompareToZero > 0;
					clone.setTransType(issue ? "Issue" : "Return");
					clone.setQuantity(clone.getUsedQty().abs());
					rows.add(clone);
				}
			}
		});

		return rows;
	}

}
