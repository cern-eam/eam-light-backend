/**
 * 
 */
package ch.cern.cmms.eamlightweb.equipment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import ch.cern.cmms.eamlightejb.layout.ElementInfo;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;

/**
 * Tools to do operation with the equipment Objects
 *
 */
@Named
@ApplicationScoped
public class EquipmentTools {

	public static final List<String> D_STATUS_LIST = Arrays
			.asList(new String[] { "DP", "DCR", "DX", "DPE", "EO", "DPD", "K", "D" });

	public static void postReadEquipment(Equipment equipment) {
		// Check commisionDate
		if (new Date(0).equals(equipment.getComissionDate())) {
			equipment.setComissionDate(null);
		}
	}

	public static void assignDefaultValues(Equipment equipment, Map<String, ElementInfo> fields,
			ApplicationData applicationData) {
		// Iterate over the list of fields
		for (String key : fields.keySet()) {
			// ElementInfo
			ElementInfo element = fields.get(key);
			// Just check if default value is not null
			if (element.getDefaultValue() == null) {
				continue;
			}
			// Get default value
			String defaultValue = element.getDefaultValue().trim();
			// Switch according to the key
			switch (key) {
			case "equipmentno":
				equipment.setCode(defaultValue);
				break;
			case "equipmentdesc":
				equipment.setDescription(defaultValue);
				break;
			case "assetstatus":
				equipment.setStatusCode(defaultValue);
				break;
			case "class":
				equipment.setClassCode(defaultValue);
				break;
			case "category":
				equipment.setCategoryCode(defaultValue);
				break;
			case "department":
				equipment.setDepartmentCode(defaultValue);
				break;
			//TODO
				/*
			case "commissiondate":
				try {
					equipment.setComissionDate(
							new SimpleDateFormat(applicationData.getDateTimeDbFormat()).parse(defaultValue));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
				*/
			case "assignedto":
				equipment.setAssignedTo(defaultValue);
				break;
			case "criticality":
				equipment.setCriticality(defaultValue);
				break;
			case "manufacturer":
				equipment.setManufacturerCode(defaultValue);
				break;
			case "serialnumber":
				equipment.setSerialNumber(defaultValue);
				break;
			case "model":
				equipment.setModel(defaultValue);
				break;
			case "part":
				equipment.setPartCode(defaultValue);
				break;
			case "udfchar11":
				equipment.getUserDefinedFields().setUdfchar11(defaultValue);
				break;
			case "location":
				// equipment.setHierarchyLocationCode(defaultValue);
				break;
			case "position":
				// equipment.setHierarchyPositionCode(defaultValue);
				break;
			case "parentasset":
				// equipment.setHierarchyAssetCode(defaultValue);
				break;
			case "udfchar04":
				equipment.getUserDefinedFields().setUdfchar04(defaultValue);
				break;
			case "udfchar05":
				equipment.getUserDefinedFields().setUdfchar04(defaultValue);
				break;
			case "udfchkbox02":
				equipment.getUserDefinedFields().setUdfchkbox02(defaultValue);
				break;
			}
		}
	}

}
