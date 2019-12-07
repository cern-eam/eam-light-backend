package ch.cern.cmms.eamlightweb.tools;

import ch.cern.eam.wshub.core.services.entities.CustomField;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;

import javax.enterprise.context.RequestScoped;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Map;

@RequestScoped
public class Tools {

	public static boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	/**
	 * Populates a business object according to the parameters received in the url
	 *
	 * @param object
	 *            The object to be populated
	 * @param request
	 *            the Servlet request
	 */
	public static void pupulateBusinessObject(Object object, Map<String, String> requestValues) {
		// Iterate over the declared fields of the object
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			// Name of the field non capital
			String fieldName = field.getName().toLowerCase();
			// If the field is the userdefinedscreens, execute the process for
			// this
			if (fieldName.equals("userdefinedfields")) {
				try {
					// Set the field accessible
					field.setAccessible(true);
					// Get the user defined fields
					UserDefinedFields udfs = (UserDefinedFields) field.get(object);
					// Call recursive method
					pupulateBusinessObject(udfs, requestValues);
				} catch (Exception e) {/* Ignore */
					System.out.println(e.getMessage());
				}
			}
			// Check if exists in the request
			String fieldValue = requestValues.get(fieldName);
			if (fieldValue != null) {
				// Set the field accessible
				field.setAccessible(true);
				// Set the value for the field
				try {
					// Type of field
					if ("Date".equals(field.getType().getSimpleName())) {
						// Try to assign the date. Format dd-MMM-yyyy
						field.set(object, new SimpleDateFormat("dd-MMM-yyyy").parse(fieldValue));
					} else {/* Assumes String */
						field.set(object, fieldValue);
					}
				} catch (Exception e) {/* Ignore */
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * Populates the custom fields according to the parameters received in the
	 * request
	 *
	 * @param customFields
	 *            Custom fields to be populated
	 * @param request
	 *            The request
	 */
	public static void populateCustomFields(CustomField[] customFields, Map<String, String> request) {
		// Iterate over the custom fields
		for (CustomField customField : customFields) {
			// Check if a value is received for the custom field
			String value = request.get(customField.getCode());
			if (value != null) {
				// Assign the value to the custom field
				customField.setValue(value);
			}
		}
	}



}
