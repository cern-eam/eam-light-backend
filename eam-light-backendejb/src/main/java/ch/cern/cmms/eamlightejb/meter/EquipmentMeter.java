package ch.cern.cmms.eamlightejb.meter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EquipmentMeter  implements Serializable {

	private String equipmentCode;
	
	private List<MeterReadingWrap> meterReadings = new ArrayList<MeterReadingWrap>();
	
	public EquipmentMeter() {
	}
	
	/**
	 * @return the equipmentCode
	 */
	public String getEquipmentCode() {
		return equipmentCode;
	}

	/**
	 * @param equipmentCode the equipmentCode to set
	 */
	public void setEquipmentCode(String equipmentCode) {
		this.equipmentCode = equipmentCode;
	}

	/**
	 * @return the meterReadings
	 */
	public List<MeterReadingWrap> getMeterReadings() {
		return meterReadings;
	}

	/**
	 * @param meterReadings the meterReadings to set
	 */
	public void setMeterReadings(List<MeterReadingWrap> meterReadings) {
		this.meterReadings = meterReadings;
	}

}
