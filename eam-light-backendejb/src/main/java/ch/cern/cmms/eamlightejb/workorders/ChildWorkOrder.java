package ch.cern.cmms.eamlightejb.workorders;

/**
 * To represent a child work order of the parent work order
 *
 */
public class ChildWorkOrder {

	/**
	 * Number
	 */
	private String number;

	/**
	 * Description
	 */
	private String description;

	/**
	 * Status
	 */
	private String status;

	/**
	 * Type
	 */
	private String type;

	/**
	 * Equipment
	 */
	private String equipment;

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the equipment
	 */
	public String getEquipment() {
		return equipment;
	}

	/**
	 * @param equipment
	 *            the equipment to set
	 */
	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChildWorkOrder [" + (number != null ? "number=" + number + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (status != null ? "status=" + status + ", " : "") + (type != null ? "type=" + type + ", " : "")
				+ (equipment != null ? "equipment=" + equipment : "") + "]";
	}
}
