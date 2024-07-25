package ch.cern.cmms.eamlightweb.base;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.CustomField;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Dependent
public class CustomFieldsController extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;
	public static final Map<String, Map<String, List<Pair>>> customFieldsLookupValuesCache = new ConcurrentHashMap<>();

	public Map<String, List<Pair>> readCustomFieldsLookupValues(String entity, String inforClass) throws InforException {
		String lookupValuesCacheKey = entity + "_" + inforClass;
		if (customFieldsLookupValuesCache.containsKey(lookupValuesCacheKey)) {
			return customFieldsLookupValuesCache.get(lookupValuesCacheKey);
		}

		Map<String, List<Pair>> customFieldLookupValues = new HashMap<>();

			CustomField[] customFields = inforClient.getTools().getCustomFieldsTools().getWSHubCustomFields(authenticationTools.getInforContext(), entity, inforClass);
			for (CustomField customField : customFields) {
				if (customField.getLovType().equals("C")
						|| customField.getLovType().equals("E")
						|| customField.getLovType().equals("P")) {
					switch (customField.getType()) {
						case "CODE":
							customFieldLookupValues.put(customField.getCode(), cfCodeDesc(customField.getCode()));
							break;
						case "NUM":
							customFieldLookupValues.put(customField.getCode(), cfNum(customField.getCode()));
							break;
						case "DATI":
							customFieldLookupValues.put(customField.getCode(), cfDateTime(customField.getCode()));
							break;
						case "DATE":
							customFieldLookupValues.put(customField.getCode(), cfDate(customField.getCode()));
							break;
						case "CHAR":
							customFieldLookupValues.put(customField.getCode(), cfChar(customField.getCode()));
							break;
					}
				}
			}

			// Store in the cache before returning
			customFieldsLookupValuesCache.put(lookupValuesCacheKey, customFieldLookupValues);
			return customFieldLookupValues;
	}

	private String treatPropcode(String propertyCode) {
		if (propertyCode == null) {
			return null;
		}
		return propertyCode.replaceAll("-", "-0045"); //The LOV grid requests (such as LVCFCD) do not return the list
		// of values for Custom Fields containing a dash (-) due to an EAM bug, so the 4 digit asci code needs to be
		// appended (0045)
	}

	public List<Pair> cfChar(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("461", "code");

		GridRequest gridRequest = new GridRequest("LVCFV");
		gridRequest.setRowCount(1000);
		return getData("param.propcode", treatPropcode(property), gridRequest, map);
	}

	public List<Pair> cfNum(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("462", "code");

		GridRequest gridRequest = new GridRequest("LVCFN");
		gridRequest.setRowCount(1000);
		return getData("param.propcode", treatPropcode(property), gridRequest, map);
	}

	public List<Pair> cfDate(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("463", "code");

		GridRequest gridRequest = new GridRequest("LVCFD");
		gridRequest.setRowCount(1000);
		return getData("param.propcode", treatPropcode(property), gridRequest, map);
	}

	public List<Pair> cfDateTime(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("464", "code");

		GridRequest gridRequest = new GridRequest("LVCFD");
		gridRequest.setRowCount(1000);
		return getData("param.propcode", treatPropcode(property), gridRequest, map);
	}

	public List<Pair> cfCodeDesc(String property) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("461", "code");
		map.put("465", "desc");

		GridRequest gridRequest = new GridRequest("LVCFCD");
		gridRequest.setRowCount(1000);

		return getData("param.propcode", treatPropcode(property), gridRequest, map);
	}

	public List<Pair> cfEntity(String entityCode, String rentCodeValue, String cfCode, String filter) throws InforException {
		Map<String, String> map = new HashMap<>();
		map.put("101", "code");
		map.put("103", "desc");

		GridRequest gridRequest = new GridRequest("LVCFE");
		gridRequest.setRowCount(10);
		gridRequest.addFilter("customfieldvalue", filter, "BEGINS", GridRequestFilter.JOINER.OR);
		gridRequest.addFilter("description", filter, "BEGINS");
		gridRequest.addParam("param.fieldid", cfCode);
		gridRequest.addParam("param.associatedrentity", entityCode);
		gridRequest.addParam("param.lookuprentity", rentCodeValue);

		return getData("parameter.propentity", rentCodeValue, gridRequest, map);
	}


	public List<Pair> getData(String paramName, String paramValue, GridRequest gridRequest, Map<String, String> map) throws InforException {
		gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
		gridRequest.addParam(paramName, paramValue);

		List<Pair> customFieldLookupValues = inforClient.getTools().getGridTools().convertGridResultToObject(Pair.class,
				map,
				inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));

		return customFieldLookupValues;
	}

}
