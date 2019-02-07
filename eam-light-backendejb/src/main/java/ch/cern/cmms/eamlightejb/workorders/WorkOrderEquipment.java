/**
 * 
 */
package ch.cern.cmms.eamlightejb.workorders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import java.io.Serializable;

/**
 * Representation of an Equipment that belongs to a Multi Equipment Work Order
 *
 */
@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = WorkOrderEquipment.GET_WO_EQUIPMENT, query = "SELECT OBJ_CODE, OBJ_DESC, OBJ_OBTYPE,"
				+ " UCO_DESC FROM R5EVENTS, R5OBJECTS, R5UCODES WHERE EVT_OBJECT = OBJ_CODE AND UCO_RENTITY='OBTP'"
				+ " AND UCO_CODE = OBJ_OBTYPE AND EVT_PARENT = :wonumber AND EVT_JOBTYPE = 'MEC'", resultClass = WorkOrderEquipment.class) })
public class WorkOrderEquipment implements Serializable {

	public static final String GET_WO_EQUIPMENT = "WorkOrderEquipment.GET_WO_EQUIPMENT";

	@Id
	@Column(name = "OBJ_CODE")
	private String equipmentCode;

	@Column(name = "OBJ_DESC")
	private String equipmentDesc;

	@Column(name = "OBJ_OBTYPE")
	private String equipmentType;

	@Column(name = "UCO_DESC")
	private String equipmentTypeDesc;

	/**
	 * @return the equipmentCode
	 */
	public String getEquipmentCode() {
		return equipmentCode;
	}

	/**
	 * @param equipmentCode
	 *            the equipmentCode to set
	 */
	public void setEquipmentCode(String equipmentCode) {
		this.equipmentCode = equipmentCode;
	}

	/**
	 * @return the equipmentDesc
	 */
	public String getEquipmentDesc() {
		return equipmentDesc;
	}

	/**
	 * @param equipmentDesc
	 *            the equipmentDesc to set
	 */
	public void setEquipmentDesc(String equipmentDesc) {
		this.equipmentDesc = equipmentDesc;
	}

	/**
	 * @return the equipmentType
	 */
	public String getEquipmentType() {
		return equipmentType;
	}

	/**
	 * @param equipmentType
	 *            the equipmentType to set
	 */
	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	/**
	 * @return the equipmentTypeDesc
	 */
	public String getEquipmentTypeDesc() {
		return equipmentTypeDesc;
	}

	/**
	 * @param equipmentTypeDesc
	 *            the equipmentTypeDesc to set
	 */
	public void setEquipmentTypeDesc(String equipmentTypeDesc) {
		this.equipmentTypeDesc = equipmentTypeDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WorkOrderEquipment [" + (equipmentCode != null ? "equipmentCode=" + equipmentCode + ", " : "")
				+ (equipmentDesc != null ? "equipmentDesc=" + equipmentDesc + ", " : "")
				+ (equipmentType != null ? "equipmentType=" + equipmentType + ", " : "")
				+ (equipmentTypeDesc != null ? "equipmentTypeDesc=" + equipmentTypeDesc : "") + "]";
	}

}
