/**
 * 
 */
package ch.cern.cmms.eamlightejb.parts;

/**
 * Class that represents a part associations for the "Where Used" region on the
 * screen
 *
 */
public class PartAssociation {

	/**
	 * Entity
	 */
	private String entity;

	/**
	 * Code
	 */
	private String code;

	/**
	 * Description
	 */
	private String description;

	/**
	 * Quantity
	 */
	private String quantity;

	/**
	 * Type
	 */
	private String type;

	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
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

	public String getLink() {
		switch (entity) {
		case "Equipment":
			return "equipment/" + code;
		case "Part":
			return "part/" + code;
		case "Work Order":
			return "workorder/" + code;
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PartAssociation [" + (entity != null ? "entity=" + entity + ", " : "")
				+ (code != null ? "code=" + code + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (quantity != null ? "quantity=" + quantity + ", " : "") + (type != null ? "type=" + type : "") + "]";
	}
}
