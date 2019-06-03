package ch.cern.cmms.eamlightweb.base;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class CustomFieldsController extends Autocomplete {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	public List<Pair> cfChar(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("461", "code");

		GridRequest gridRequest = new GridRequest("104", "LVCFV", "105");
		return getData("param.propcode", property, gridRequest, map);
	}

	public List<Pair> cfNum(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("462", "code");

		GridRequest gridRequest = new GridRequest("105", "LVCFN", "106");
		return getData("param.propcode", property, gridRequest, map);
	}

	public List<Pair> cfDate(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("463", "code");

		GridRequest gridRequest = new GridRequest("106", "LVCFD", "107");
		return getData("param.propcode", property, gridRequest, map);
	}

	public List<Pair> cfDateTime(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("464", "code");

		GridRequest gridRequest = new GridRequest("107", "LVCFD", "108");
		return getData("param.propcode", property, gridRequest, map);
	}

	public List<Pair> cfCodeDesc(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("461", "code");
		map.put("465", "desc");

		GridRequest gridRequest = new GridRequest("108", "LVCFCD", "109");
		return getData("param.propcode", property, gridRequest, map);
	}

	public List<Pair> cfEntity(String entity, String filter) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("101", "code");
		map.put("103", "desc");

		GridRequest gridRequest = new GridRequest("109", "LVCFE", "110");
		gridRequest.setRowCount("10");
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("customfieldvalue", filter, "BEGINS", GridRequestFilter.JOINER.OR));
		gridRequest.getGridRequestFilters().add(new GridRequestFilter("description", filter, "BEGINS"));

		return getData("parameter.propentity", entity, gridRequest, map);
	}


	public List<Pair> getData(String paramName, String paramValue, GridRequest gridRequest, Map<String, String> map) throws InforException {
		gridRequest.setGridType("LOV");
		gridRequest.getParams().put(paramName, paramValue);

		List<Pair> customFieldLookupValues = inforClient.getTools().getGridTools().converGridResultToObject(Pair.class,
				map,
				inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));

		return customFieldLookupValues;
	}

}
