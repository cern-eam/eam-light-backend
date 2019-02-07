package ch.cern.cmms.eamlightweb.workorders.activity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/boolists")
@Interceptors({ RESTLoggingInterceptor.class })
public class BookingLabourLists extends DropdownValues {

	@GET
	@Path("/typehours")
	@Produces("application/json")
	public Response readTradeCodes() {
		try {
			// Load the dropdown
			return ok(loadDropdown("143", "LVOCTP", "148", "LOV",
					Arrays.asList("101", "103"),
					produceInforParamsForTypeOfHoursDropdown()));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	private Map<String, String> produceInforParamsForTypeOfHoursDropdown() {
		Map<String, String> inforParams = new HashMap<String, String>();
		inforParams.put("date", null);
		inforParams.put("trade", "20");
		inforParams.put("dept", null);
		inforParams.put("event", null);
		inforParams.put("employee", "");
		return inforParams;
	}

}
