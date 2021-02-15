package ch.cern.cmms.eamlightweb.equipment.structure;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.OrganizationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
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

@Path("/eqstructure")
@Interceptors({ RESTLoggingInterceptor.class })
public class EquipmentStructure extends EAMLightController {

	@EJB
	private EquipmentEJB equipmentEJB;
    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;

	@GET
	@Path("/tree")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readEquipmentTree(@QueryParam("eqid") String eqID) {
		try {
			return ok(equipmentEJB.getEquipmentStructureTree(eqID));
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
            InforContext context = authenticationTools.getInforContext();
            OrganizationTools.assumeMonoOrg(context);
            return ok(inforClient.getEquipmentStructureService().addEquipmentToStructure(context, equipmentStructure));
        }catch (InforException ie){
            return serverError(ie);
        }
    }

}
