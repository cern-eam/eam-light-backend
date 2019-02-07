/**
 * 
 */
package ch.cern.cmms.eamlightejb.workorders;

/**
 * Part usage of a work order
 *
 */
public class WorkOrderPartUsage {

	/**
	 * Activity
	 */
	private String activity;

	/**
	 * Store
	 */
	private String storeCode;

	/**
	 * Store description
	 */
	private String storeDesc;

	/**
	 * Part code
	 */
	private String partCode;

	/**
	 * Part description
	 */
	private String partDesc;

	/**
	 * Part UOM
	 */
	private String partUom;

	/**
	 * Transaction type
	 */
	private String transType;

	/**
	 * Bin
	 */
	private String binCode;

	/**
	 * Quantity
	 */
	private String quantity;

	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}

	/**
	 * @param activity
	 *            the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}

	/**
	 * @return the storeCode
	 */
	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * @param storeCode
	 *            the storeCode to set
	 */
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * @return the storeDesc
	 */
	public String getStoreDesc() {
		return storeDesc;
	}

	/**
	 * @param storeDesc
	 *            the storeDesc to set
	 */
	public void setStoreDesc(String storeDesc) {
		this.storeDesc = storeDesc;
	}

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
	 * @return the partUom
	 */
	public String getPartUom() {
		return partUom;
	}

	/**
	 * @param partUom
	 *            the partUom to set
	 */
	public void setPartUom(String partUom) {
		this.partUom = partUom;
	}

	/**
	 * @return the binCode
	 */
	public String getBinCode() {
		return binCode;
	}

	/**
	 * @param binCode
	 *            the binCode to set
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WorkOrderPartUsage [" + (activity != null ? "activity=" + activity + ", " : "")
				+ (storeCode != null ? "storeCode=" + storeCode + ", " : "")
				+ (storeDesc != null ? "storeDesc=" + storeDesc + ", " : "")
				+ (partCode != null ? "partCode=" + partCode + ", " : "")
				+ (partDesc != null ? "partDesc=" + partDesc + ", " : "")
				+ (partUom != null ? "partUom=" + partUom + ", " : "")
				+ (binCode != null ? "binCode=" + binCode + ", " : "")
				+ (quantity != null ? "quantity=" + quantity : "") + "]";
	}

	/**
	 * @return the transType
	 */
	public String getTransType() {
		return transType;
	}

	/**
	 * @param transType
	 *            the transType to set
	 */
	public void setTransType(String transType) {
		this.transType = transType;
	}

}
