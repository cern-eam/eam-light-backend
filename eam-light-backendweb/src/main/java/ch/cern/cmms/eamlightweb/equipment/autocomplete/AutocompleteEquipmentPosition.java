package ch.cern.cmms.eamlightweb.equipment.autocomplete;

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
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentPosition extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("2085", "LVOBJL_EQ", "2055");
		in.getInforParams().put("objectorg", authenticationTools.getInforContext().getOrganizationCode());
		in.setGridType("LIST");
		in.setFields(Arrays.asList("247", "249")); // 247=equipmentcode,
													// 249=equipmentdesc
		in.getInforParams().put("objectrtype", "P");
		in.getInforParams().put("objectcode", "");
		return in;
	}

	@GET
	@Path("/eqp/position/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareInput();
			in.getGridFilters().add(new GridRequestFilter("equipmentcode", code.toUpperCase(), "BEGINS"));
			in.getSortParams().put("equipmentcode", true); // true=ASC, false=DESC
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}