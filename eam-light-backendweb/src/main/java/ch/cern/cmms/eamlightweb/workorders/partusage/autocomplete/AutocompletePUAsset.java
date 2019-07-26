/**
 * 
 */
package ch.cern.cmms.eamlightweb.workorders.partusage.autocomplete;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.cmms.eamlightweb.tools.autocomplete.SimpleGridInput;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/autocomplete")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompletePUAsset extends Autocomplete {

	private static final long serialVersionUID = 2103801217084141204L;
	@Inject
	private ApplicationData applicationData;
	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/partusage/asset/{issuereturn}/{store}/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("issuereturn") String issuereturn, @PathParam("store") String store,
			@PathParam("code") String code) {
		try {
			GridRequest gridRequest = new GridRequest("OSOBJA");
			gridRequest.getGridRequestFilters().add(new GridRequestFilter("equipmentno", code.toUpperCase(), "CONTAINS", GridRequestFilter.JOINER.AND));

			if (issuereturn.startsWith("I")) // ISSUE
				gridRequest.getGridRequestFilters().add(new GridRequestFilter("store", store, "="));
			else { // RETURN
				gridRequest.getGridRequestFilters().add(new GridRequestFilter("store", "", "IS EMPTY"));
			}

			return ok(inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
					Pair.generateGridPairMap("247", "383"),
					inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}


	@GET
	@Path("/partusage/asset/complete/{workOrder}/{issuereturn}/{store}/{assetCode}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response completeData(@PathParam("workOrder") String workOrder, @PathParam("issuereturn") String issuereturn,
			@PathParam("store") String store, @PathParam("assetCode") String assetCode) {
		try {
			// Response map
			Map<String, String> respMap = new HashMap<>();
			SimpleGridInput input = prepareInputForPartForAsset();
			input.getGridFilters().add(new GridRequestFilter("equipmentno", assetCode.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.AND));

			if (issuereturn.startsWith("I")) { // ISSUE
				input.getGridFilters().add(new GridRequestFilter("store", store.toUpperCase(), "EQUALS"));

			} else { // RETURN
				input.getGridFilters().add(new GridRequestFilter("store", store.toUpperCase(), "IS_EMPTY"));
			}
			input.setFields(Arrays.asList("247", "383", "401", "399", "400"));  // 247=equipmentno,
																				// 383=equipmentdesc,
																				// 401=part,
																				// 399=bin,
																				// 400=lot
			List<String> dscs = getGridSingleRowResult(input);

			boolean validAsset = dscs != null;

			if (validAsset) {
				respMap.put("assetCode", dscs.get(0));
				respMap.put("assetDesc", dscs.get(1));
				respMap.put("partCode", dscs.get(2).toUpperCase());
				respMap.put("binCode", dscs.get(3));
				respMap.put("lot", dscs.get(4));

				// get the part description
				input = prepareInputForPart(store, workOrder);
				input.getGridFilters().add(new GridRequestFilter("partcode", dscs.get(2).toUpperCase(), "EQUALS"));
				input.setFields(Arrays.asList("140", "103", "141")); // 140=partcode,
																		// 103=partdescription,
																		// 141=partorganization
				dscs = getGridSingleRowResult(input);
				if (dscs != null) {
					respMap.put("partDesc", dscs.get(1));
					respMap.put("partOrg", dscs.get(2));
				}
			}
			return ok(respMap);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Prepare the input to load asset list
	 * 
	 * @return Input to load asset list
	 */
	private SimpleGridInput prepareInputForPartForAsset() {
		SimpleGridInput input = new SimpleGridInput("84", "OSOBJA", "85");
		input.setGridType("LIST");
		input.setFields(Arrays.asList("247", "383", "386", "13058")); // 247=equipmentno,
		// 383=equipmentdesc
		// 386=serialnumber
		// 13058=alias
		return input;
	}

	/**
	 * Prepare the input to load the part list
	 * 
	 * @return Input to load the part list
	 */
	private SimpleGridInput prepareInputForPart(String store, String workOrder) throws InforException {
		SimpleGridInput input = new SimpleGridInput("221", "LVIRPART", "224");
		input.getInforParams().put("control.org", authenticationTools.getInforContext().getOrganizationCode());
		input.getInforParams().put("multiequipwo", "false");
		input.getInforParams().put("store_code", store);
		input.getInforParams().put("relatedworkordernum", workOrder);
		input.setFields(Arrays.asList("140", "103")); // 140=partcode,
														// 103=partdescription
		return input;
	}

}