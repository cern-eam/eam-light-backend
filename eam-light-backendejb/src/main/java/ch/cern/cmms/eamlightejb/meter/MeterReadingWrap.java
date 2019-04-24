package ch.cern.cmms.eamlightejb.meter;

import ch.cern.eam.wshub.core.adapters.DateAdapter;
import ch.cern.eam.wshub.core.services.workorders.entities.MeterReading;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

public class MeterReadingWrap {

	/**
	 * Last update date of the value for the UOM
	 */
	private Date lastUpdateDate;
	
	/**
	 * Last value read for the UOM 
	 */
	private String lastValue;
	
	/**
	 * UOM description
	 */
	private String uomDesc;
	
	/**
	 * Meter reading name
	 */
	private String meterName;
	
	private MeterReading meterReading;
	
	public MeterReadingWrap() {
	}
	
	public MeterReadingWrap(Date lastUpdateDate, String lastValue, String uomDesc, String meterName, MeterReading meterReading) {
		super();
		this.lastUpdateDate = lastUpdateDate;
		this.lastValue = lastValue;
		this.uomDesc = uomDesc;
		this.meterName = meterName;
		this.meterReading = meterReading;
	}

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

	/**
	 * @return the lastValue
	 */
	public String getLastValue() {
		return lastValue;
	}

	/**
	 * @param lastValue the lastValue to set
	 */
	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	/**
	 * @return the meterReading
	 */

	public MeterReading getMeterReading() {
		return meterReading;
	}

	/**
	 * @param meterReading the meterReading to set
	 */
	public void setMeterReading(MeterReading meterReading) {
		this.meterReading = meterReading;
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

}
