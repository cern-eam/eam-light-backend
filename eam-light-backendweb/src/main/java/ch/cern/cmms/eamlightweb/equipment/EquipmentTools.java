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
}
