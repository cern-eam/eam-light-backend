package ch.cern.cmms.eamlightweb.workorders.misc;

/**
 * To represent a child work order of the parent work order
 *
 */
public class ChildWorkOrder {

	private String number;
	private String description;
	private String status;
	private String type;
	private String equipment;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEquipment() {
		return equipment;
	}

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
