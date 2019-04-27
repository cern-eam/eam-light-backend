package ch.cern.cmms.eamlightweb.equipment.autocomplete;

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
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentCategory extends Autocomplete {

	private SimpleGridInput prepareInput() {
		SimpleGridInput in = new SimpleGridInput("121", "LVCAT", "119");
		in.setGridType("LOV");
		in.getInforParams().put("onlymatchclass", "");
		in.getInforParams().put("class", "");
		return in;
	}

	@GET
	@Path("/eqp/category/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		SimpleGridInput in = prepareInput();
		in.setFields(Arrays.asList("101", "103")); // 101=category,
													// 103=categorydesc
		in.getGridFilters().add(new GridRequestFilter("category", code.toUpperCase(), "BEGINS"));

		in.getSortParams().put("category", true); // true=ASC, false=DESC

		try {
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/eqp/categorydata/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getCategoryData(@PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareInput();
			in.setFields(Arrays.asList("101", "103", "389", "484", "515")); // 101=category,
																			// 103=categorydesc,
																			// 389=categoryclass,
																			// 484=categoryclassdesc,
																			// 515=manufacturer
			in.getGridFilters().add(new GridRequestFilter("category", code, "EQUALS"));
			return ok(getGridSingleRowResult(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}