package ch.cern.cmms.eamlightweb.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import ch.cern.cmms.eamlightejb.UserTools;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Tools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightejb.equipment.PartAssociated;
import ch.cern.cmms.eamlightweb.tools.autocomplete.GridUtils;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightejb.layout.ElementInfo;
import ch.cern.cmms.eamlightejb.layout.LayoutBean;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightejb.workorders.WorkOrdersEJB;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.equipment.entities.EquipmentReplacement;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestCell;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestRow;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/equipment")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentRest extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private ApplicationData applicationData;
	@EJB
	private LayoutBean layoutBean;
	@Inject
	private WorkOrdersEJB wosEJB;
	@Inject
	private EquipmentEJB equipmentEJB;
	@Inject
	private GridUtils gridUtils;
	@Inject
	private EquipmentReplacementService equipmentReplacementService;
	@Inject
    private AuthenticationTools authenticationTools;
	@Inject
	private Tools tools;
	@EJB
	private UserTools userTools;

	@GET
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readEquipment(@QueryParam("c") String equipment) {
		try {
			return ok(inforClient.getEquipmentFacadeService().readEquipment(authenticationTools.getInforContext(), equipment));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createEquipment(Equipment equipment) {
		Equipment response = null;
		try {
			inforClient.getEquipmentFacadeService().createEquipment(authenticationTools.getInforContext(), equipment);
			// Read again the equipment
			return ok(inforClient.getEquipmentFacadeService().readEquipment(authenticationTools.getInforContext(), equipment.getCode()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateEquipment(Equipment equipment) {
		try {
			inforClient.getEquipmentFacadeService().updateEquipment(authenticationTools.getInforContext(), equipment);
			// Read again the equipment
			return ok(inforClient.getEquipmentFacadeService().readEquipment(authenticationTools.getInforContext(), equipment.getCode()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@DELETE
	@Path("/{equipment}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response deleteEquipment(@PathParam("equipment") String equipment) {
		try {
			inforClient.getEquipmentFacadeService().deleteEquipment(authenticationTools.getInforContext(), equipment);
			return noConent();
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Path("/replace")
	@Produces("application/json")
	@Consumes("application/json")
	public Response replaceEquipment(EquipmentReplacement eqpReplacement) {
		try {
			return ok(equipmentReplacementService.replaceEquipment(authenticationTools.getInforContext(), eqpReplacement));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/history")
	@Produces("application/json")
	public Response getEquipmentHistory(@QueryParam("c") String equipmentCode) {
		try {
			return ok(wosEJB.getObjectHistory(equipmentCode));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/workorders")
	@Produces("application/json")
	public Response getEquipmentWorkOrders(@QueryParam("c") String equipmentCode) {
		try {
			return ok(wosEJB.getObjectWorkOrders(equipmentCode));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/init/{entity}/{eqpType}/{systemFunction}/{userFunction}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initEquipment(@PathParam("entity") String entity, @PathParam("eqpType") String eqpType,
			@PathParam("systemFunction") String systemFunction, @PathParam("userFunction") String userFunction,
			@Context UriInfo info) {
		try {
			// Default values from request
			Map<String, List<String>> queryParams = info.getQueryParameters();
			Map<String, String> parameters = new HashMap<>();
			queryParams.forEach((k, v) -> parameters.put(k, v != null ? v.get(0) : null));
			// Check if there is screen parameter
			userFunction = parameters.get("screen") != null ? parameters.get("screen") : userFunction;

			Equipment equipment = new Equipment();
			// User defined fields
			equipment.setUserDefinedFields(new UserDefinedFields());
			equipment.setTypeCode(eqpType);

			// Default values from screen
			Map<String, ElementInfo> screenFields = layoutBean.getRecordViewElements(systemFunction, userFunction,
					entity, userTools.getUserGroup(authenticationTools.getInforContext()));

			// Assign default values
			EquipmentTools.assignDefaultValues(equipment, screenFields, applicationData);
			// Populate Object
			tools.pupulateBusinessObject(equipment, parameters);

			// Custom fields
			equipment.setCustomFields(
					inforClient.getTools().getCustomFieldsTools().getMTCustomFields(authenticationTools.getInforContext(), entity, equipment.getClassCode() != null ? equipment.getClassCode() : "*"));
			// Populate custom fields if they are not null
			if (equipment.getCustomFields() != null) {
				tools.populateCustomFields(equipment.getCustomFields(), parameters);
			}
			// Commision date
			if (equipment.getComissionDate() == null)
				equipment.setComissionDate(new Date());
			return ok(equipment);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/children/{equipment}")
	@Produces("application/json")
	public Response getEquipmentChildren(@PathParam("equipment") String equipment) {
		try {
			return ok(equipmentEJB.getEquipmentChildren(equipment));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/partsassociated/{parentScreen}/{equipment}")
	@Produces("application/json")
	public Response getPartsAssociated(@PathParam("parentScreen") String parentScreen,
			@PathParam("equipment") String equipment) {
		try {
			List<PartAssociated> partsAssociated = new ArrayList<>();
			// Creates simple grid input
			SimpleGridInput input = new SimpleGridInput("402", "BSPARA", "414");
			// GridController Type
			input.setGridType("LIST");
			// Fields to be retrieved
			// 1019 papartcode
			// 1022 description
			// 1020 quantity
			// 2207 partuom
			input.setFields(Arrays.asList("1019", "1022", "1020", "2207"));
			// Rows to print
			input.setRowCount("1000");

			Map<String, String> inforParams = new HashMap<>();
			inforParams.put("organization", authenticationTools.getInforContext().getOrganizationCode());
			inforParams.put("userfunction", parentScreen);
			inforParams.put("entity", "OBJ");
			inforParams.put("valuecode", equipment + "#" + authenticationTools.getInforContext().getOrganizationCode());
			// Add infor params
			inforParams.forEach((k, v) -> {
				input.getInforParams().put(k, v);
			});
			// Execute grid
			GridRequestResult res = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());
			// Process result
			Arrays.stream(res.getRows()).forEach(row -> partsAssociated.add(processRow(row, input.getFields())));
			// Final response
			return ok(partsAssociated);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Process result row to create a new Part Association and add it to the result
	 * list
	 * 
	 * @param row
	 *            Rows to be processed
	 * @param fields
	 *            List of the fields to be included in the object
	 * @return Indicator of row processed
	 */
	private PartAssociated processRow(GridRequestRow row, List<String> fields) {
		// Create new Part Associated
		PartAssociated part = new PartAssociated();
		Arrays.stream(row.getCell()).filter(cell -> fields.contains(cell.getCol()))
				.forEach(cell -> addPropertyToPartAssociations(part, cell));
		return part;
	}

	/**
	 * Add a property to the Part object
	 * 
	 * @param part
	 *            Part object to add the property
	 * @param cell
	 *            cell containing the property to be added
	 * @return Indicator of property added
	 */
	private boolean addPropertyToPartAssociations(PartAssociated part, GridRequestCell cell) {
		// 1019 papartcode
		// 1022 description
		// 1020 quantity
		// 2207 partuom
		// Check property
		switch (cell.getCol()) {
		case "1019":/* papartcode */
			part.setPartCode(cell.getContent());
			break;
		case "1022":/* description */
			part.setPartDesc(cell.getContent());
			break;
		case "1020":/* quantity */
			part.setQuantity(cell.getContent());
			break;
		case "2207":/* partuom */
			part.setUom(cell.getContent());
			break;
		}
		// Property added
		return true;
	}

}
