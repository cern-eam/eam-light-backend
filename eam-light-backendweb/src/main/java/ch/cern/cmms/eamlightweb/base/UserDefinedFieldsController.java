package ch.cern.cmms.eamlightweb.base;


import ch.cern.cmms.eamlightweb.base.entities.CodeDescItem;
import ch.cern.cmms.eamlightweb.base.entities.RentityItem;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * To obtain the values of the UDFs when they are of type: CODE CODEDESC and
 * RENT
 *
 */
@ApplicationScoped
@Path("/userdefinedfields")
@Interceptors({ RESTLoggingInterceptor.class })
public class UserDefinedFieldsController extends EAMLightController {

	/**
	 *
	 */
	private static final long serialVersionUID = -3100913226296174854L;

	@Inject
	private AuthenticationTools authenticationTools;

	public static final Map<Pair, List<RentityItem>> rentityAutocompleteCache = new ConcurrentHashMap<>();

	@GET
	@Path("/complete/{rentity}/{filter}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response entityAutocomplete(@PathParam("rentity") String rentity, @PathParam("filter") String filter) {
		try {
			Pair query = new Pair(rentity, filter);
			if(rentityAutocompleteCache.containsKey(query)){
				return ok(rentityAutocompleteCache.get(query));
			}
			GridRequest gridRequest = new GridRequest("LVUDFE");
			gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
			gridRequest.setRowCount(1000);
			gridRequest.getParams().put("param.rentity", rentity);
			gridRequest.getParams().put("control.org", "*");
			List<GridRequestFilter> filters = new ArrayList<>();
			filters.add(new GridRequestFilter("userdefinedfieldvalue", filter, "BEGINS", GridRequestFilter.JOINER.OR));
			filters.add(new GridRequestFilter("description", filter, "BEGINS"));
			gridRequest.setGridRequestFilters(filters);
			List<RentityItem> response = inforClient.getTools().getGridTools()
					.convertGridResultToObject(RentityItem.class, null, inforClient.getGridsService().executeQuery(
							authenticationTools.getInforContext(), gridRequest));
			rentityAutocompleteCache.put(query, response);
			return ok(response);
		}
		catch (Exception e){
			return serverError(e);
		}
	}

	@GET
	@Path("/codedesc/{entity}/{fieldId}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getUDFCodeDesc(@PathParam("entity") String entity, @PathParam("fieldId") String fieldId) {
		try {
			return ok(getUDFCodeDescList(entity, fieldId));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/code/{entity}/{fieldId}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getUDFCodes(@PathParam("entity") String entity, @PathParam("fieldId") String fieldId) {
		try {
			// List of response
			return ok(getCodeList(entity, fieldId));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Prepare GridInput and use it to get result from the grid.
	 *
	 * @param rentity
	 * @param field
	 *
	 * @return
	 * @throws InforException
	 */
	private List<CodeDescItem> getUDFCodeDescList(String rentity, String field) throws InforException {

		GridRequest gridRequest = new GridRequest("BSUDLV_HDR");
		gridRequest.getParams().put("param.fieldid", field);
		gridRequest.getParams().put("param.rentity", rentity);
		List<CodeDescItem> response = inforClient.getTools().getGridTools().convertGridResultToObject(
				CodeDescItem.class, null,
				inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));
		return response;
	}

	private List<RentityItem> getCodeList(String rentity, String field) throws InforException {
		GridRequest gridRequest = new GridRequest("LVUDFC");
		gridRequest.getParams().put("param.field", field);
		gridRequest.getParams().put("param.fieldid", field);
		gridRequest.getParams().put("param.rentity", rentity);
		gridRequest.getParams().put("param.associatedrentity", rentity);
		List<RentityItem> response = inforClient.getTools().getGridTools().convertGridResultToObject(
				RentityItem.class, null,
				inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));
		return response;
	}
}
