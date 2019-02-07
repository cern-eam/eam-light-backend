package ch.cern.cmms.eamlightweb.workorders.autocomplete;

import java.util.Arrays;
import java.util.List;

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
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter;
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter.JOINER;
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter.OPERATOR;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteWOCostCode extends Autocomplete {

	@Inject
	private AuthenticationTools authenticationTools;

	private SimpleGridInput prepareInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("38", "LOV", "38");
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.getInforParams().put("userfunction", "WSJOBS");
		in.setGridType("LOV");
		in.setFields(Arrays.asList("101", "103")); // 101=code, 103=des_text
		return in;
	}

	@GET
	@Path("/wo/costcode/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		try {
			// Input
			SimpleGridInput in = prepareInput();
			in.getWhereParams().put("costcode",
					new WhereParameter(OPERATOR.STARTS_WITH, code.toUpperCase(), JOINER.OR));
			in.getWhereParams().put("des_text", new WhereParameter(OPERATOR.STARTS_WITH, code.toUpperCase()));
			// Result
			List<Pair> resultList = getGridResults(in);
			return ok(resultList);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}
}
