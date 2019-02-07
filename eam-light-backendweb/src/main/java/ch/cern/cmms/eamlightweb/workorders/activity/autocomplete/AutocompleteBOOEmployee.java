package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
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
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteBOOEmployee extends Autocomplete {

	private SimpleGridInput prepareInput() {
		SimpleGridInput in = new SimpleGridInput("146", "LVEMP", "151");
		in.setGridType("LOV");
		in.setFields(Arrays.asList("112", "113")); // 112=personcode, 113=description
		in.getInforParams().put("date", null);
		in.getInforParams().put("per_type", null);
		in.getInforParams().put("trade", "KPL");
		in.getInforParams().put("act", "5");
		in.getInforParams().put("booplan", "true");
		in.getInforParams().put("event", "555555");
		in.getInforParams().put("octype", "N");
		return in;
	}

	@GET
	@Path("/boo/employee/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		SimpleGridInput in = prepareInput();
		in.getWhereParams().put("personcode", new WhereParameter(OPERATOR.STARTS_WITH, code.toUpperCase(), JOINER.OR));
		in.getWhereParams().put("description", new WhereParameter(OPERATOR.CONTAINS, code.toUpperCase()));
		in.getSortParams().put("description", true);
		try {
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
