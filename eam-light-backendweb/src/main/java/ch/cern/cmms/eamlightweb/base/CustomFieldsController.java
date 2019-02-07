package ch.cern.cmms.eamlightweb.base;


import ch.cern.cmms.eamlightejb.customfields.CustomFieldLookupValue;
import ch.cern.cmms.eamlightejb.customfields.CustomFieldsBean;
import ch.cern.cmms.eamlightweb.tools.autocomplete.Autocomplete;
import ch.cern.eam.wshub.core.services.entities.CustomField;

import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Dependent
public class CustomFieldsController extends Autocomplete {

	@EJB
	private CustomFieldsBean customFieldsBean;

	public List<CustomFieldLookupValue> cfCodeDesc(String property, String lookupType, String entity,
												   String classCode) {
		return cfCodeDesc(property, lookupType, entity, classCode, "EN");
	}

	public List<CustomFieldLookupValue> cfCodeDesc(String property, String lookupType, String entity, String classCode,
			String language) {
		List<CustomFieldLookupValue> types = customFieldsBean.getCFCodeDescValues(property, lookupType, entity,
				classCode, language);
		if (types == null)/* Check null list */
			types = new ArrayList<>();
		// Remove null values
		types.removeAll(Collections.singleton(null));
		return types;
	}

	public List<CustomFieldLookupValue> cfChar(String property, String lookupType, String entity, String classCode) {
		List<CustomFieldLookupValue> types = customFieldsBean.getCFCharValues(property, lookupType, entity, classCode);
		if (types == null)/* Check null list */
			return new ArrayList<>();
		// Remove null values
		types.removeAll(Collections.singleton(null));
		return types;
	}

	public List<CustomFieldLookupValue> cfDate(String property, String lookupType, String entity, String classCode) {
		List<CustomFieldLookupValue> types = customFieldsBean.getCFDateValues(property, lookupType, entity, classCode);
		if (types == null)/* Check null list */
			return new ArrayList<>();
		// Remove null values
		types.removeAll(Collections.singleton(null));
		return types;
	}

	public List<CustomFieldLookupValue> cfDateTime(String property, String lookupType, String entity,
			String classCode) {
		List<CustomFieldLookupValue> types = customFieldsBean.getCFDateTimeValues(property, lookupType, entity,
				classCode);
		if (types == null)/* Check null list */
			return new ArrayList<>();
		// Remove null values
		types.removeAll(Collections.singleton(null));
		return types;
	}

	public List<CustomFieldLookupValue> cfNum(String property, String lookupType, String entity, String classCode) {
		List<CustomFieldLookupValue> types = customFieldsBean.getCFNumValues(property, lookupType, entity, classCode);
		if (types == null)/* Check null list */
			return new ArrayList<>();
		// Remove null values
		types.removeAll(Collections.singleton(null));
		return types;
	}

}
