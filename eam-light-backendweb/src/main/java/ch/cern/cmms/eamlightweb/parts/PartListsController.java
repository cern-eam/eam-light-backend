package ch.cern.cmms.eamlightweb.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.tools.autocomplete.DropdownValues;
import ch.cern.cmms.eamlightejb.parts.PartAssociation;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestCell;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestRow;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/partlists")
@Interceptors({ RESTLoggingInterceptor.class })
public class PartListsController extends DropdownValues {

	@GET
	@Path("/trackMethods")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readTrackingMethodCodes() {
		List<Pair> methods = new LinkedList<Pair>();
		methods.add(new Pair("NOST", "Non stocke, non suivi, depenses prevues"));
		methods.add(new Pair("TRPQ", "Stocke, qte suivie"));
		methods.add(new Pair("TRQ", "Stocke, quantite suivie, montant non suivi"));
		return ok(methods);
	}

	@GET
	@Path("/partsassociated/{part}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response loadPartAssociation(@PathParam("part") String part) {
		// Init the list of part associations
		List<PartAssociation> partAssociations = new ArrayList<>();
		try {
			// Creates simple grid input
			SimpleGridInput input = new SimpleGridInput("817", "SSPART_EPA", "800");
			// GridController Type
			input.setGridType("LIST");
			// Fields to be retrieved
			input.setFields(Arrays.asList("7448", "3747", "8463", "3750", "15932"));
			// Rows to print
			input.setRowCount("1000");
			Map<String, String> inforParams = new HashMap<>();
			inforParams.put("partcode", part);
			inforParams.put("partorg", authenticationTools.getInforContext().getOrganizationCode());
			inforParams.put("userfunction", "SSPART");
			// Add infor params
			inforParams.forEach((k, v) -> {
				input.getInforParams().put(k, v);
			});
			// Execute grid
			GridRequestResult res = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());
			// Process result
			Arrays.stream(res.getRows()).forEach(row -> partAssociations.add(processRow(row, input.getFields())));
			// Response final
			return ok(partAssociations);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Process result row to create a new Part Association and add it to the result
	 * list
	 * 
	 * @param row
	 *            Rows to be processed
	 * @param fields
	 *            List of the fields to be included in the object
	 * @return Indicator of row processed
	 */
	private PartAssociation processRow(GridRequestRow row, List<String> fields) {
		// Create new Part Association
		PartAssociation partAssociation = new PartAssociation();
		Arrays.stream(row.getCell()).filter(cell -> fields.contains(cell.getCol()))
				.forEach(cell -> addPropertyToPartAssociations(partAssociation, cell));
		// Add element to the list
		return partAssociation;
	}

	/**
	 * Add a property to the Part Association object
	 * 
	 * @param partAssociation
	 *            Part Association object to add the property
	 * @param cell
	 *            cell containing the property to be added
	 * @return Indicator of property added
	 */
	private boolean addPropertyToPartAssociations(PartAssociation partAssociation, GridRequestCell cell) {
		// 8462 epaentity, 3747 code, 15708 vmrsdesc, 3750 quantity, 12527
		// partassociatedpk, 8461 epartype
		// Check property
		switch (cell.getCol()) {
		case "7448":/* Entity */
			partAssociation.setEntity(cell.getContent());
			break;
		case "3747":/* code */
			partAssociation.setCode(cell.getContent());
			break;
		case "8463":/* Description */
			partAssociation.setDescription(cell.getContent());
			break;
		case "3750":/* quantity */
			partAssociation.setQuantity(cell.getContent());
			break;
		case "15932": /* Type */
			partAssociation.setType(cell.getContent());
			break;

		}
		// Property added
		return true;
	}

}
