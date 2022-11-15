package ch.cern.cmms.eamlightweb.equipment.structure;

import ch.cern.cmms.eamlightejb.equipment.tools.GraphNode;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;

import java.util.LinkedList;
import java.util.List;

@Path("/eqstructure")
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentStructureController extends EAMLightController {



    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;
    @Inject EquipmentStructure equipmentStructure;

	@GET
	@Path("/tree")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readEquipmentTree(@QueryParam("eqid") String eqID, @QueryParam("org") String org, @QueryParam("type") String type) {
		try {
			return ok(equipmentStructure.readEquipmentTree(eqID, org, type));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
    @Path("/attach")
    @Produces("application/json")
    @Consumes("application/json")
    public Response attachEquipment(ch.cern.eam.wshub.core.services.equipment.entities.EquipmentStructure equipmentStructure){
        try{
            return ok(inforClient.getEquipmentStructureService().addEquipmentToStructure(authenticationTools.getInforContext(), equipmentStructure));
        }catch (InforException ie){
            return serverError(ie);
        }
    }

    @POST
    @Path("/detach")
    @Produces("application/json")
    @Consumes("application/json")
    public Response detachEquipment(ch.cern.eam.wshub.core.services.equipment.entities.EquipmentStructure equipmentStructure){
        try{
            return ok(inforClient.getEquipmentStructureService().removeEquipmentFromStructure(authenticationTools.getInforContext(), equipmentStructure));
        }catch (InforException ie){
            return serverError(ie);
        }
    }

}
