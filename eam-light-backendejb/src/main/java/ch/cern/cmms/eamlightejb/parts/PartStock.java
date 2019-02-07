/**
 * 
 */
package ch.cern.cmms.eamlightejb.parts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;

/**
 * Class used to show the stock of a Part in the Parts Screen
 *
 */
@Entity
@NamedNativeQuery(name = PartStock.STOCK_PARTSTOCK, query = "select rownum keyfield, bis_store bisstore, des_text storedesc, bis_bin "
		+ "bisbin, bis_lot bislot, CASE WHEN obj_code is null THEN NVL(bis_qty,0) ELSE CASE WHEN obj_rstatus='CRR' "
		+ "THEN 0 ELSE 1 END END bisqty, CASE WHEN obj_code is null THEN NVL(bis_repairqty,0) ELSE CASE WHEN obj_rstatus='CRR' "
		+ "THEN 1 ELSE 0 END END repairquantity, obj_code bisassetid FROM r5binstock, r5objects, r5descriptions, r5stores, r5userorganization "
		+ "WHERE   bis_part =  :part_code AND bis_part_org =  '*' AND obj_part (+) = bis_part "
		+ "AND obj_part_org (+) = bis_part_org AND obj_store (+) = bis_store AND obj_bin (+) = bis_bin "
		+ "AND obj_lot (+) = bis_lot AND ( obj_rstatus IS NULL OR obj_rstatus IN ('B', 'C', 'CRR') ) "
		+ "AND des_code = bis_store AND des_rentity = 'STOR' AND des_lang = 'EN' "
		+ "AND str_code = bis_store AND uog_user = :eamUser "
		+ "AND uog_org = str_org order by bis_store ASC, bis_bin ASC, bis_lot ASC", resultClass = PartStock.class)
public class PartStock {

	public static final String STOCK_PARTSTOCK = "PartStock.STOCK_PARTSTOCK";

	/**
	 * ID Field
	 */
	@Id
	@Column(name = "keyfield")
	private String rowNum;

	/**
	 * Code of the store
	 */
	@Column(name = "bisstore")
	private String storeCode;

	/**
	 * Description of the store
	 */
	@Column(name = "storedesc")
	private String storeDesc;

	/**
	 * Bin
	 */
	@Column(name = "bisbin")
	private String bin;

	/**
	 * Lot
	 */
	@Column(name = "bislot")
	private String lot;

	/**
	 * Quantity
	 */
	@Column(name = "bisqty")
	private String quantity;

	/**
	 * Repair Quantity
	 */
	@Column(name = "repairquantity")
	private String repairQuantity;

	/**
	 * Asset Code
	 */
	@Column(name = "bisassetid")
	private String assetCode;

	/**
	 * 
	 */
	public PartStock() {
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
	 * @return the bin
	 */
	public String getBin() {
		return bin;
	}

	/**
	 * @param bin
	 *            the bin to set
	 */
	public void setBin(String bin) {
		this.bin = bin;
	}

	/**
	 * @return the lot
	 */
	public String getLot() {
		return lot;
	}

	/**
	 * @param lot
	 *            the lot to set
	 */
	public void setLot(String lot) {
		this.lot = lot;
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
	 * @return the repairQuantity
	 */
	public String getRepairQuantity() {
		return repairQuantity;
	}

	/**
	 * @param repairQuantity
	 *            the repairQuantity to set
	 */
	public void setRepairQuantity(String repairQuantity) {
		this.repairQuantity = repairQuantity;
	}

	/**
	 * @return the assetCode
	 */
	public String getAssetCode() {
		return assetCode;
	}

	/**
	 * @param assetCode
	 *            the assetCode to set
	 */
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PartStock [" + (storeCode != null ? "storeCode=" + storeCode + ", " : "")
				+ (storeDesc != null ? "storeDesc=" + storeDesc + ", " : "") + (bin != null ? "bin=" + bin + ", " : "")
				+ (lot != null ? "lot=" + lot + ", " : "") + (quantity != null ? "quantity=" + quantity + ", " : "")
				+ (repairQuantity != null ? "repairQuantity=" + repairQuantity + ", " : "")
				+ (assetCode != null ? "assetCode=" + assetCode : "") + "]";
	}

	/**
	 * @return the rowNum
	 */
	public String getRowNum() {
		return rowNum;
	}

	/**
	 * @param rowNum
	 *            the rowNum to set
	 */
	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

}
