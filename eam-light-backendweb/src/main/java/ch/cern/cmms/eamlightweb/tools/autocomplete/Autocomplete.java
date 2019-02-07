package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;


public abstract class Autocomplete extends WSHubController {

	@Inject
	private GridUtils gridUtils;

	@Inject
	protected AuthenticationTools authenticationTools;

	public List<Pair> getGridResults(SimpleGridInput input) throws InforException {
		GridRequestResult res = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());

		// For each row
		// Filter cells with the ids from the fields list
		// Sort cells using its index in the fields list
		// Convert the stream of rows to the List
		return Arrays.stream(res.getRows()).map(row -> Arrays.stream(row.getCell())
				.filter(cell -> input.getFields().contains(cell.getCol()) || input.getFields().contains(cell.getTag()))
				.sorted((cell1, cell2) -> input.getFields().indexOf(cell1.getCol())
						- input.getFields().indexOf(cell2.getCol()))
				.map(cell -> cell.getContent()).collect(Collectors.toList()))
				.map(item -> new Pair(item.get(0), item.get(1))).collect(Collectors.toList());
	}

	public List<String> getGridSingleRowResult(SimpleGridInput input) throws InforException {
		GridRequestResult result = gridUtils.getGridRequestResult(input, authenticationTools.getInforContext());

		Optional<List<String>> gridRow = Arrays.stream(result.getRows())
				.map(row -> Arrays.stream(row.getCell()).filter(cell -> input.getFields().contains(cell.getCol()))
						.sorted((cell1, cell2) -> input.getFields().indexOf(cell1.getCol())
								- input.getFields().indexOf(cell2.getCol()))
						.map(cell -> cell.getContent()).collect(Collectors.toList()))
				.findFirst();

		if (gridRow.isPresent()) {
			return gridRow.get();
		} else {
			return null;
		}
	}

	public String getGridSingleStringResult(SimpleGridInput input) throws InforException {
		List<String> result = getGridSingleRowResult(input);
		if (result != null) {
			return result.get(0);
		} else {
			return null;
		}
	}

}
