package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import org.jboss.logging.Logger.Level;

import ch.cern.eam.wshub.core.services.grids.entities.GridDataspy;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

public abstract class DropdownValues extends WSHubController {

	@Inject
	protected AuthenticationTools authenticationTools;
	@Inject
	protected InforClient inforClient;
	@Inject
	protected LoggingService logging;
	@Inject
	protected GridUtils gridUtils;


	protected List<Pair> loadDropdown(String gridId, String gridName, String gridDataSpy, String gridType,
									  List<String> fields, Map<String, String> inforParams) throws InforException {
	 	return loadDropdown(gridId, gridName,  gridDataSpy, gridType, fields, inforParams, null, false);
	}

	protected List<Pair> loadDropdown(String gridId, String gridName, String gridDataSpy, String gridType,
									  List<String> fields, Map<String, String> inforParams, List<GridRequestFilter> gridFilters, Boolean useNative) throws InforException {
		GridRequestResult result = loadGridRequestResult(gridId, gridName, gridDataSpy, gridType, fields, inforParams, gridFilters, useNative);
		return convertToPairs(fields, result);
	}

	protected GridDataspy getDefaultDataSpy(String gridId, String type) {
		try {
			return inforClient.getGridsService().getDefaultDataspy(authenticationTools.getInforContext(), gridId, type);
		} catch (InforException e) {
			logging.log(Level.ERROR, e.getMessage());
		}
		return null;
	}


	protected GridRequestResult loadGridRequestResult(String gridId, String gridName, String gridDataSpy, String gridType,
													  List<String> fields, Map<String, String> inforParams, List<GridRequestFilter> gridFilters, Boolean useNative) throws InforException
	{
		// Creates simple grid input
		SimpleGridInput input = new SimpleGridInput(gridId, gridName, gridDataSpy);
		// GridController Type
		input.setGridType(gridType);
		// Fields to be retrieved
		input.setFields(fields);
		// Rows to print
		input.setRowCount("1000");
		// Use native
		input.setUseNative(useNative);
		// Parameter map
		input.setGridFilters(gridFilters);

		// Add infor params
		if (inforParams != null) {
			inforParams.forEach((k, v) -> {
				input.getInforParams().put(k, v);
			});
		}
		return gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());
	}

	protected List<Pair> convertToPairs(List<String> fields, GridRequestResult res) {
		return Arrays.stream(res.getRows())
				.map(row -> Arrays.stream(row.getCell()).filter(cell -> fields.contains(cell.getCol()))
						.sorted(Comparator.comparing(cell -> fields.indexOf(cell.getCol())))
						.map(cell -> cell.getContent()).collect(Collectors.toList()))
				.map(list -> new Pair(list.get(0), list.get(1)))
				.collect(Collectors.toList());
	}

}
