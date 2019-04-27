package ch.cern.cmms.eamlightweb.workorders.autocomplete;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.sound.sampled.Line;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteWOEquipment extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("67", "LVOBJL", "59");
		in.getInforParams().put("equipmentlookup", "true");
		in.getInforParams().put("loantodept", "TRUE");
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.setGridType("LIST");
		in.setQueryTimeout(7000);
		in.getInforParams().put("cctrspcvalidation", "D");
		in.getInforParams().put("department", "");
		in.getInforParams().put("objectrtype", null);
		in.getInforParams().put("filterutilitybill", null);
		in.getInforParams().put("filternonconformity", null);
		return in;
	}

	@GET
	@Path("/wo/eqp")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@QueryParam("s") String code) {
		try {
			// Input
			SimpleGridInput in = prepareInput();
			in.setFields(Arrays.asList("247", "249")); // 247=equipmentcode,
														// 249=equipmentdesc
			in.getGridFilters().add(new GridRequestFilter("equipmentcode", code.toUpperCase(), "BEGINS"));
			// Result
			List<Pair> resultList = getGridResults(in);
			return ok(resultList);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/wo/eqp/selected")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getValuesSelectedEquipment(@QueryParam("code") String code) {
		try {
			// Input
			SimpleGridInput input = prepareInput();
			input.getGridFilters().add(new GridRequestFilter("equipmentcode", code.trim(), "EQUALS"));
			// 247=equipmentcode, 249=equipmentdesc, 9439=departmentCode,
			// 254=departmentDesc, 250=eqLocationCode, 252=eqLocationDesc,
			// 397=equipcostcode
			input.setFields(Arrays.asList("247", "249", "9439", "254", "250", "252", "397"));
			// Result
			return ok(getGridSingleRowResult(input));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
