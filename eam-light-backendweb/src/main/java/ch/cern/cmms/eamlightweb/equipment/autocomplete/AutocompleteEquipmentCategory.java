package ch.cern.cmms.eamlightweb.equipment.autocomplete;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteEquipmentCategory extends EAMLightController {

	@GET
	@Path("/eqp/category/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code, @QueryParam("class") @DefaultValue("") String eqpClass)  {
		GridRequest gridRequest = new GridRequest( "LVCAT", GridRequest.GRIDTYPE.LOV, 10);

		gridRequest.addParam("parameter.class", eqpClass);
		gridRequest.addParam("parameter.onlymatchclass", "");

		gridRequest.sortBy("category");
		gridRequest.addFilter("category", code.toUpperCase(), "BEGINS");

		return getPairListResponse(gridRequest, "category", "categorydesc");
	}

	@GET
	@Path("/eqp/categorydata/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getCategoryData(@PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest( "LVCAT", GridRequest.GRIDTYPE.LOV, 10);
			gridRequest.addParam("parameter.class", "");
			gridRequest.addParam("parameter.onlymatchclass", "");
			gridRequest.addFilter("category", code.toUpperCase(), "EQUALS");

			String[] fields = new String[] {"category", "categorydesc", "categoryclass",
					"categoryclassdesc", "manufacturer"};

			return ok(inforClient.getTools().getGridTools().convertGridResultToMapList(inforClient.getGridsService()
					.executeQuery(authenticationTools.getInforContext(), gridRequest), Arrays.asList(fields)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}