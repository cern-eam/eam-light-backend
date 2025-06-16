package ch.cern.cmms.eamlightweb.workorders.partusage;

import ch.cern.cmms.eamlightweb.application.ApplicationService;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.plugins.SharedPlugin;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransaction;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionLine;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionType;
import ch.cern.eam.wshub.core.services.workorders.entities.Activity;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ch.cern.eam.wshub.core.tools.Tools.extractEntityCode;
import static ch.cern.eam.wshub.core.tools.Tools.extractOrganizationCode;

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
	@Inject
	private ApplicationService applicationService;

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
			gridRequest.addParam("part_code", extractEntityCode(part));
			gridRequest.addParam("part_org", inforClient.getTools().getOrganizationCode(authenticationTools.getInforContext(), extractOrganizationCode(part)));
			gridRequest.addParam("store_code", store);

			return ok(GridTools.convertGridResultToObject(Pair.class,
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
			System.out.println("part: " +  part);
			gridRequest.addParam("bin_code", bin);
			gridRequest.addParam("part_code", extractEntityCode(part));
			gridRequest.addParam("part_org", inforClient.getTools().getOrganizationCode(authenticationTools.getInforContext(), extractOrganizationCode(part)));
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
			Map<String, String> applicationData = applicationService.getParams();

			List<Pair> udsLots = sharedPlugin.getUdsLots(extractEntityCode(part), inforClient, context, applicationData);

			// Check whether there are user defined lots, otherwise return all lots
			if (udsLots != null && !udsLots.isEmpty()) {
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
		/* Assign attributes */
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

}
