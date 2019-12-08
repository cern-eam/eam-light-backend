/**
 * 
 */
package ch.cern.cmms.eamlightweb.workorders.partusage.autocomplete;

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

import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

/**
 * Autocomplete class to select the part or asset in the part usage of work orders
 */
@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePUPart extends Autocomplete {

	@GET
	@Path("/partusage/part/{workorder}/{store}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("workorder") String workorder, @PathParam("store") String store,
			@PathParam("code") String code) throws InforException{
		// Input
		SimpleGridInput input = new SimpleGridInput("221", "LVIRPART", "224");
		input.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		input.getInforParams().put("multiequipwo", "false");
		input.getInforParams().put("store_code", store);
		input.getInforParams().put("parameter.excludeparentpart", "false");
		input.getInforParams().put("relatedworkordernum", workorder);
		input.setFields(Arrays.asList("140", "103")); // 140=partcode,
														// 103=partdescription
		// Trim the code
		code = code.trim().replaceAll("%", "");
		try {
			input.getGridFilters().add(new GridRequestFilter("partcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR));
			if (code.length() > 3)
				input.getGridFilters().add(new GridRequestFilter("partdescription", code, "CONTAINS", GridRequestFilter.JOINER.OR));

			input.getGridFilters().add(new GridRequestFilter("udfchar01", code, "BEGINS", GridRequestFilter.JOINER.OR));

			// Code
			input.getGridFilters().add(new GridRequestFilter("udfchar03", code, "BEGINS", GridRequestFilter.JOINER.OR));// CDD
																										// Drawing
																										// Reference
			input.getGridFilters().add(new GridRequestFilter("udfchar11", code.toUpperCase(), "BEGINS"));// EDMS:
																								// "Item
																								// ID"
																								// (References
																								// t_master_dat.PART_ID)
			input.setUseNative(true);
			input.setQueryTimeout(5500);

			return ok(getGridResults(input));

		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
