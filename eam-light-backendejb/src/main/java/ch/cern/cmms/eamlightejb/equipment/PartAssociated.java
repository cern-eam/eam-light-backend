/**
 * 
 */
package ch.cern.cmms.eamlightejb.equipment;

import java.io.Serializable;

/**
 * Represents a Part associated to the Equipment. Used to display the
 * information of the region "Parts Associated" in all the screens of equipment
 *
 */
public class PartAssociated  implements Serializable {

	/**
	 * Part code
	 */
	private String partCode;

	/**
	 * Part Description
	 */
	private String partDesc;

	/**
	 * Quantity
	 */
	private String quantity;

	/**
	 * UOM
	 */
	private String uom;

	/**
	 * @return the partCode
	 */
	public String getPartCode() {
		return partCode;
	}

	/**
	 * @param partCode
	 *            the partCode to set
	 */
	public void setPartCode(String partCode) {
		this.partCode = partCode;
	}

	/**
	 * @return the partDesc
	 */
	public String getPartDesc() {
		return partDesc;
	}

	/**
	 * @param partDesc
	 *            the partDesc to set
	 */
	public void setPartDesc(String partDesc) {
		this.partDesc = partDesc;
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
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom
	 *            the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PartAssociated [" + (partCode != null ? "partCode=" + partCode + ", " : "")
				+ (partDesc != null ? "partDesc=" + partDesc + ", " : "")
				+ (quantity != null ? "quantity=" + quantity + ", " : "") + (uom != null ? "uom=" + uom : "") + "]";
	}

}
