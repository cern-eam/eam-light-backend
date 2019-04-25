package ch.cern.cmms.eamlightweb.base;


import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.GridUtils;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.autocomplete.WhereParameter;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * To obtain the values of the UDFs when they are of type: CODE CODEDESC and
 * RENT
 *
 */
@RequestScoped
@Path("/userdefinedscreens")
@Interceptors({ RESTLoggingInterceptor.class })
public class UserDefinedFieldsController extends Autocomplete {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3100913226296174854L;

	@Inject
	private AuthenticationTools authenticationTools;

	@Inject
	private GridUtils gridUtils;

	/**
	 * Fields to be retrieved (CODE, CODEDESC)
	 */
	private List<String> fields;

	@PostConstruct
	private void init() {
		fields = new ArrayList<String>();
		fields.add("7797"); // 7797=userdefinedfieldvalue
		fields.add("629"); // 629=description
	}

	private SimpleGridInput prepareAutoCompleteInput() throws InforException {
		SimpleGridInput in = new SimpleGridInput("2297", "LVUDFE", "2240");
		in.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		in.setGridType("LOV");
		in.setFields(Arrays.asList("101", "103")); // 101=code, 103=description
		return in;
	}

	@GET
	@Path("/complete/{rentity}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response entityAutocomplete(@PathParam("rentity") String rentity, @PathParam("code") String code) {
		try {
			SimpleGridInput in = prepareAutoCompleteInput();
			// Rentity
			in.getInforParams().put("rentity", rentity);
			in.getWhereParams().put("userdefinedfieldvalue",
					new WhereParameter(WhereParameter.OPERATOR.STARTS_WITH, code.toUpperCase(), WhereParameter.JOINER.OR));
			in.getWhereParams().put("description", new WhereParameter(WhereParameter.OPERATOR.STARTS_WITH, code.toUpperCase()));
			return ok(getGridResults(in));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/codedesc/{entity}/{fieldId}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getUDFCodeDesc(@PathParam("entity") String entity, @PathParam("fieldId") String fieldId) {
		try {
			// List of response
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
			return ok(getUDFCodeDescList(entity, fieldId, false));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	public List<Pair> getUDFCodeDescList(String rentity, String field) throws InforException {
		return getUDFCodeDescList(rentity, field, true);
	}

	/**
	 * Prepare GridInput and use it to get result from the grid.
	 * 
	 * @param rentity
	 * @param field
	 * @param isPairWithDesc
	 *            Flag to specify type of the lookup (only code=false,
	 *            codeWithDesc=true)
	 * @return
	 * @throws InforException
	 */
	private List<Pair> getUDFCodeDescList(String rentity, String field, boolean isCodeWithDesc) throws InforException {
		SimpleGridInput input = null;
		List<Pair> results = new LinkedList<>();
		// Creates simple grid input
		if (isCodeWithDesc) { // code and desc
			input = new SimpleGridInput("2295", "LVUDFCD", "2238");
		} else { // just code
			input = new SimpleGridInput("2296", "LVUDFC", "2239");
		}
		// GridController Type
		input.setGridType("LOV");
		input.setFields(fields);
		// Max rows to be shown
		input.setRowCount("1000");
		// Add infor params
		input.getInforParams().put("rentity", rentity);
		input.getInforParams().put("field", field);
		// Execute grid
		GridRequestResult res = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());
		// Put the results in the list
		Arrays.stream(res.getRows())
				.map(row -> Arrays.stream(row.getCell()).filter(cell -> fields.contains(cell.getCol()))
						.sorted((cell1, cell2) -> fields.indexOf(cell1.getCol()) - fields.indexOf(cell2.getCol()))
						.map(cell -> cell.getContent()).collect(Collectors.joining("#")))
				.collect(Collectors.toList()).forEach(element -> results
						.add(new Pair(element.split("#")[0], element.split("#")[isCodeWithDesc ? 1 : 0])));

		// Return result
		return results;
	}

}
