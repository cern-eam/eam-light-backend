package ch.cern.cmms.eamlightweb.equipment.structure;

import javax.ejb.EJB;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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


}
