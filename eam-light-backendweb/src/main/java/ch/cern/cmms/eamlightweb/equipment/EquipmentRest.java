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
