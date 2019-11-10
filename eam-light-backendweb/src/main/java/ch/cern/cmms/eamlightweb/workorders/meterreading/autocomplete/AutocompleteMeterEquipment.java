package ch.cern.cmms.eamlightweb.workorders.meterreading.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteMeterEquipment extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("67", "LVOBJL", "59");
		in.getInforParams().put("equipmentlookup", "true");
		in.getInforParams().put("loantodept", "TRUE");
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.setGridType(GridRequest.GRIDTYPE.LIST);
		in.getInforParams().put("cctrspcvalidation", "D");
		in.getInforParams().put("department", "");
		in.getInforParams().put("objectrtype", null);
		in.getInforParams().put("filterutilitybill", null);
		// 247=equipmentcode, 249=equipmentdesc
		in.setFields(Arrays.asList("247", "249"));
		return in;
	}

	@GET
	@Path("/meters/equipment/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("equipmentcode", code.toUpperCase(), "BEGINS"));
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}