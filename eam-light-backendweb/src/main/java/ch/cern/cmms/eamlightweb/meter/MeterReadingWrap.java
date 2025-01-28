package ch.cern.cmms.eamlightweb.meter;

import ch.cern.eam.wshub.core.adapters.DateAdapter;
import ch.cern.eam.wshub.core.annotations.GridField;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

public class MeterReadingWrap {

	@GridField(name="lastreadingdate")
	private Date lastUpdateDate;
	
	@GridField(name="lastvalue")
	private BigDecimal lastValue;

	@GridField(name="meterrollover")
	private BigDecimal rolloverValue;
	
	@GridField(name="uomDesc")
	private String uomDesc;

	@GridField(name="meterunit")
	private String uom;
	
	@GridField(name="metercode")
	private String meterName;

	private String equipmentCode;

	/**
	 * @return the lastUpdateDate
	 */
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * @param lastUpdateDate the lastUpdateDate to set
	 */
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getUomDesc() {
		return uomDesc;
	}

	public void setUomDesc(String uomDesc) {
		this.uomDesc = uomDesc;
	}

	public String getMeterName() {
		return meterName;
	}

	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	public String getEquipmentCode() {
		return equipmentCode;
	}

	public void setEquipmentCode(String equipmentCode) {
		this.equipmentCode = equipmentCode;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public BigDecimal getLastValue() {
		return lastValue;
	}

	public void setLastValue(BigDecimal lastValue) {
		this.lastValue = lastValue;
	}

	public BigDecimal getRolloverValue() {
		return rolloverValue;
	}

	public void setRolloverValue(BigDecimal rolloverValue) {
		this.rolloverValue = rolloverValue;
	}
}
