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
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter;
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter.JOINER;
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter.OPERATOR;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.tools.InforException;

/**
 * Autocomplete class to select the part or asset in the part usage of work orders
 */
@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePUPart extends Autocomplete {

	@Inject
	private ApplicationData applicationData;

	@GET
	@Path("/partusage/part/{workorder}/{store}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("workorder") String workorder, @PathParam("store") String store,
			@PathParam("code") String code) {
		// Input
		SimpleGridInput input = new SimpleGridInput("221", "LVIRPART", "224");
		input.getInforParams().put("control.org", applicationData.getControlOrg());
		input.getInforParams().put("multiequipwo", applicationData.getMultiEquipmentWO());
		input.getInforParams().put("store_code", store);
		input.getInforParams().put("relatedworkordernum", workorder);
		input.setFields(Arrays.asList("140", "103")); // 140=partcode,
														// 103=partdescription
		// Trim the code
		code = code.trim().replaceAll("%", "");
		try {
			input.getWhereParams().put("partcode", new WhereParameter(code.toUpperCase(), JOINER.OR));
			if (code.length() > 3)
				input.getWhereParams().put("partdescription", new WhereParameter(OPERATOR.CONTAINS, code, JOINER.OR));

			input.getWhereParams().put("udfchar01", new WhereParameter(code.toUpperCase(), JOINER.OR)); // SCEM
																										// Code
			input.getWhereParams().put("udfchar03", new WhereParameter(code.toUpperCase(), JOINER.OR)); // CDD
																										// Drawing
																										// Reference
			input.getWhereParams().put("udfchar11", new WhereParameter(code.toUpperCase())); // EDMS:
																								// "Item
																								// ID"
																								// (References
																								// t_master_dat.PART_ID)

			input.setQueryTimeout(5500);

			return ok(getGridResults(input));

		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
