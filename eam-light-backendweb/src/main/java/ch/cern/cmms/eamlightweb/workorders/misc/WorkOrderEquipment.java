/**
 * 
 */
package ch.cern.cmms.eamlightweb.workorders.misc;

import java.io.Serializable;

/**
 * Representation of an Equipment that belongs to a Multi Equipment Work Order
 *
 */
public class WorkOrderEquipment implements Serializable {

	private String equipmentCode;
	private String equipmentDesc;
	private String equipmentType;
	private String equipmentTypeDesc;

	public String getEquipmentCode() {
		return equipmentCode;
	}

	public void setEquipmentCode(String equipmentCode) {
		this.equipmentCode = equipmentCode;
	}

	public String getEquipmentDesc() {
		return equipmentDesc;
	}

	public void setEquipmentDesc(String equipmentDesc) {
		this.equipmentDesc = equipmentDesc;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getEquipmentTypeDesc() {
		return equipmentTypeDesc;
	}

	public void setEquipmentTypeDesc(String equipmentTypeDesc) {
		this.equipmentTypeDesc = equipmentTypeDesc;
	}

	@Override
	public String toString() {
		return "WorkOrderEquipment [" + (equipmentCode != null ? "equipmentCode=" + equipmentCode + ", " : "")
				+ (equipmentDesc != null ? "equipmentDesc=" + equipmentDesc + ", " : "")
				+ (equipmentType != null ? "equipmentType=" + equipmentType + ", " : "")
				+ (equipmentTypeDesc != null ? "equipmentTypeDesc=" + equipmentTypeDesc : "") + "]";
	}

}
