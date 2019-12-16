/**
 * 
 */
package ch.cern.cmms.eamlightweb.equipment;

import ch.cern.eam.wshub.core.annotations.GridField;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a Part associated to the Equipment. Used to display the
 * information of the region "Parts Associated" in all the screens of equipment
 *
 */
public class PartAssociated  implements Serializable {

	@GridField(name="papartcode")
	private String partCode;
	@GridField(name="description")
	private String partDesc;
	@GridField(name="quantity")
	private BigDecimal quantity;
	@GridField(name="partuom")
	private String uom;

	public String getPartCode() {
		return partCode;
	}

	public void setPartCode(String partCode) {
		this.partCode = partCode;
	}

	public String getPartDesc() {
		return partDesc;
	}

	public void setPartDesc(String partDesc) {
		this.partDesc = partDesc;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	@Override
	public String toString() {
		return "PartAssociated [" + (partCode != null ? "partCode=" + partCode + ", " : "")
				+ (partDesc != null ? "partDesc=" + partDesc + ", " : "")
				+ (quantity != null ? "quantity=" + quantity + ", " : "") + (uom != null ? "uom=" + uom : "") + "]";
	}

}
