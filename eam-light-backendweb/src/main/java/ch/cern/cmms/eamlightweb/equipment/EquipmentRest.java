package ch.cern.cmms.eamlightweb.equipment;

import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightweb.codegenerator.CodeGeneratorService;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrders;
import ch.cern.cmms.standardworkorders.MTFWorkOrderServiceImpl;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.equipment.entities.EquipmentReplacement;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.material.entities.PartAssociation;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/equipment")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private EquipmentEJB equipmentEJB;
	@Inject
	private EquipmentReplacementService equipmentReplacementService;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private MyWorkOrders myWorkOrders;
	@Inject
	private CodeGeneratorService codeGeneratorService;
	@Inject
	private MTFWorkOrderServiceImpl mtfStandardWorkOrderService;

	@GET
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readEquipment(@QueryParam(value = "c") String equipment) {
		try {
			return ok(inforClient.getEquipmentFacadeService().readEquipment(authenticationTools.getInforContext(), equipment));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/type")
	@Produces("application/json")
	public Response readEquipmentType(@QueryParam("c") String equipmentCode) {
		try {
			return ok(inforClient.getEquipmentFacadeService().readEquipmentType(authenticationTools.getInforContext(), equipmentCode));
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
		try {
			// Generate new numeric code if the requested code starts with @
			if (equipment.getCode()!=null && codeGeneratorService.isCodePrefix(equipment.getCode())) {
				String newCode = codeGeneratorService.getNextEquipmentCode(equipment.getCode(),
					authenticationTools.getInforContext(), equipment.getTypeCode());
				equipment.setCode(newCode);
			}

			return ok(inforClient.getEquipmentFacadeService().createEquipment(authenticationTools.getInforContext(), equipment));
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
			InforContext context = authenticationTools.getInforContext();

			if (equipment.getStatusCode().equals("D")) {
				equipment.setStatusCode("I");
				equipment = EquipmentTools.clearHierarchy(context, inforClient, equipment);
				equipment.setStatusCode("D");
			}

			return ok(inforClient.getEquipmentFacadeService().updateEquipment(context, equipment));
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
	@Path("/collectdetachables/{oldEquipment}")
	@Produces("application/json")
	public Response collectDetachableEquipment(@PathParam("oldEquipment") String oldEquipmentCode) {
		try {
			return ok(equipmentReplacementService.collectDetachableEquipment(authenticationTools.getInforContext(), oldEquipmentCode));
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
			GridRequest gridRequest = new GridRequest("EUMLWH");
			gridRequest.setRowCount(2000);
			gridRequest.addFilter("woobject", equipmentCode, "=", GridRequestFilter.JOINER.AND);
			gridRequest.sortBy("wocompleted", "DESC");
			return ok(GridTools.convertGridResultToObject(EquipmentHistory.class,
					  null,
					  inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/workorders")
	@Produces("application/json")
	public Response getEquipmentWorkOrders(@QueryParam("c") String equipmentCode) {
		try {
			return ok(myWorkOrders.getObjectWorkOrders(equipmentCode));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/events")
	@Produces("application/json")
	public Response getEquipmentEvents(@QueryParam("c") String equipmentCode, @QueryParam("t") String equipmentType) {
		try {
			return ok(myWorkOrders.getObjectEvents(equipmentCode, equipmentType));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/init/{eqpType}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initEquipment(@PathParam("eqpType") String eqpType) {
		try {
			Equipment equipment = new Equipment();

			/*
			//TODO: Uncomment once Infor fixes the web services used below
			switch (eqpType) {
				case "A":
					equipment = inforClient.getAssetService().readAssetDefault(authenticationTools.getInforContext(), "");
					break;
				case "P":
					equipment = inforClient.getPositionService().readPositionDefault(authenticationTools.getInforContext(), "");
					break;
				case "S":
					equipment = inforClient.getSystemService().readSystemDefault(authenticationTools.getInforContext(), "");
					break;
				default:
					throw generateFault("Equipment type not supported.");
			}
			*/

			equipment.setTypeCode(eqpType);
			equipment.setStateCode("GOOD");
			equipment.setStatusCode("I");
			equipment.setComissionDate(new Date());
			equipment.setUserDefinedFields(new UserDefinedFields());
			equipment.setCustomFields(inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), "OBJ", "*"));

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
			GridRequest gridRequest = new GridRequest("402", "BSPARA", "414");

			gridRequest.addParam("param.entity", "OBJ");
			gridRequest.addParam("param.valuecode", equipment + "#" + authenticationTools.getInforContext().getOrganizationCode());

			List<PartAssociated> parts = inforClient.getTools().getGridTools().convertGridResultToObject(PartAssociated.class,
											null,
											inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

			return ok(parts);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/{eqCode}/mtfsteps/maxstep")
	@Produces("application/json")
	public Response getEquipmentStandardWOMaxStep(@PathParam("eqCode") String eqCode, @QueryParam("swo") String swo) {
		try {
			return ok(mtfStandardWorkOrderService.getEquipmentStandardWOMaxStep(eqCode, swo));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
       @Path("/partsassociated")
       @Produces("application/json")
       public Response createPartAssociation(PartAssociation partAssociation) {
	   try {
		   return ok(inforClient.getPartMiscService().createPartAssociation(authenticationTools.getInforContext(), partAssociation));
	   } catch(Exception e) {
		   return serverError(e);
	   }
	}

}
