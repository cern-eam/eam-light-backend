/**
 * 
 */
package ch.cern.cmms.eamlightweb.equipment;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.tools.InforException;

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

	public static Equipment clearHierarchy(InforContext context, InforClient inforClient, Equipment equipment) throws InforException {
		equipment.setHierarchyAssetCode("");
		equipment.setHierarchyAssetDesc("");
		equipment.setHierarchyAssetDependent(false);
		equipment.setHierarchyLocationCode("");
		equipment.setHierarchyLocationDesc("");
		equipment.setHierarchyPositionCode("");
		equipment.setHierarchyPositionDesc("");
		equipment.setHierarchyPositionDependent(false);
		equipment.setHierarchySystemCode("");
		equipment.setHierarchySystemDesc("");
		equipment.setHierarchyPositionDependent(false);
		inforClient.getEquipmentFacadeService().updateEquipment(context, equipment);
		return inforClient.getEquipmentFacadeService().readEquipment(context, equipment.getCode());
	}
}
