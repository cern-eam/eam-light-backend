package ch.cern.cmms.eamlightejb.meter;

import ch.cern.eam.wshub.core.adapters.DateAdapter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@NamedNativeQueries({
	@NamedNativeQuery(name = MeterReadingEntity.GET_METER_READINGS,
			query=
			  "select e.evt_object equipment, r5o7.o7get_desc('EN','OBJ', e.evt_object||'#'||e.evt_object_org,'', '') description,"
			+ " d.des_code, d.des_text, OUD_METER, OUD_LASTREADDATE,rea_date,rea_reading"
            + " from  r5events e"
            + " join r5objects o on o.obj_code = e.evt_object and o.obj_org = e.evt_object_org and o.obj_rstatus not in ('A','D')"
            + " join r5objusagedefs on oud_object = obj_code and oud_object_org = obj_org"
            + " join r5descriptions d on d.des_code = oud_uom and d.des_rentity = 'UOM' and d.des_lang = 'EN'"
            + " left join r5readings on rea_object = oud_object and rea_calcuom = OUD_UOM and rea_date = OUD_LASTREADDATE"
            + " where (e.evt_code =  :workOrderCode  or e.evt_routeparent =  :workOrderCode ) "
            + " order by e.evt_object, OUD_METER",
			resultClass = MeterReadingEntity.class
	),
	@NamedNativeQuery(name = MeterReadingEntity.GET_METER_READINGS_BY_EQ_ID,
			query=
			  "select o.obj_code equipment, r5o7.o7get_desc('EN','OBJ', o.obj_code||'#'||o.obj_org,'', '') description,"
			+ " d.des_code, d.des_text, OUD_METER, OUD_LASTREADDATE, rea_reading"
			+ " from r5objects o, r5descriptions d, r5objusagedefs"
			+ " left join r5readings on (rea_calcuom = OUD_UOM and rea_object = oud_object)"
			+ " where o.OBJ_CODE = :eqCode"
			+ " and o.obj_rstatus not in ('A','D')"
			+ " and oud_object = o.obj_code"
			+ " and oud_object_org = o.obj_org"
			+ " and d.des_code = oud_uom and d.des_rentity = 'UOM' and d.des_lang = 'EN'"
			+ " and rea_date = OUD_LASTREADDATE "
			+ " order by o.obj_code, OUD_METER",
			resultClass = MeterReadingEntity.class
	),
	@NamedNativeQuery(name = MeterReadingEntity.GET_METER_READING_BY_METER_ID,
			query = 
			  "select o.obj_code equipment, r5o7.o7get_desc('EN','OBJ', o.obj_code||'#'||o.obj_org,'', '') description,"
			+ " d.des_code, d.des_text, OUD_METER, OUD_LASTREADDATE, rea_reading"
			+ " from r5objects o, r5descriptions d, r5objusagedefs"
			+ " left join r5readings on (rea_calcuom = OUD_UOM and rea_object = oud_object)"
			+ " where OUD_METER LIKE :meterCode"
			+ " and o.obj_rstatus not in ('A','D')"
			+ " and oud_object = o.obj_code"
			+ " and oud_object_org = o.obj_org"
			+ " and d.des_code = oud_uom and d.des_rentity = 'UOM' and d.des_lang = 'EN'"
			+ " and rea_date = OUD_LASTREADDATE "
			+ " order by o.obj_code, OUD_METER",
			resultClass = MeterReadingEntity.class
	)
})
@IdClass(MeterReadingId.class)
public class MeterReadingEntity  implements Serializable{

	public static final String GET_METER_READINGS = "MeterReadingEntity.GET_METER_READINGS";
	public static final String GET_METER_READING_BY_METER_ID = "MeterReadingEntity.GET_METER_READING_BY_METER_ID";
	public static final String GET_METER_READINGS_BY_EQ_ID = "MeterReadingEntity.GET_METER_READINGS_BY_EQ_ID";

	@Id
	@Column(name = "equipment")
	private String equipmentCode;
	
	@Column(name = "description")
	private String equipmentDesc;
	
	@Id
	@Column(name = "des_code")
	private String uomCode;
	
	@Column(name = "des_text")
	private String uomDesc;
	
	@Column(name = "oud_meter")
	private String meterName;
	
	@Column(name = "oud_lastreaddate")
	private Date lastReading;
	
	@Column(name = "rea_reading")
	private Integer reading;

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
	 * @return the equipmentDesc
	 */
	public String getEquipmentDesc() {
		return equipmentDesc;
	}

	/**
	 * @param equipmentDesc the equipmentDesc to set
	 */
	public void setEquipmentDesc(String equipmentDesc) {
		this.equipmentDesc = equipmentDesc;
	}

	/**
	 * @return the uomCode
	 */
	public String getUomCode() {
		return uomCode;
	}

	/**
	 * @param uomCode the uomCode to set
	 */
	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	/**
	 * @return the uomDesc
	 */
	public String getUomDesc() {
		return uomDesc;
	}

	/**
	 * @param uomDesc the uomDesc to set
	 */
	public void setUomDesc(String uomDesc) {
		this.uomDesc = uomDesc;
	}

	/**
	 * @return the meterName
	 */
	public String getMeterName() {
		return meterName;
	}

	/**
	 * @param meterName the meterName to set
	 */
	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	/**
	 * @return the lastReading
	 */
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getLastReading() {
		return lastReading;
	}

	/**
	 * @param lastReading the lastReading to set
	 */
	public void setLastReading(Date lastReading) {
		this.lastReading = lastReading;
	}

	/**
	 * @return the reading
	 */
	public Integer getReading() {
		return reading;
	}

	/**
	 * @param reading the reading to set
	 */
	public void setReading(Integer reading) {
		this.reading = reading;
	}

	/**
	 * @return the getMeterReadings
	 */
	public static String getGetMeterReadings() {
		return GET_METER_READINGS;
	}
	
}

class MeterReadingId  implements Serializable{
	private String equipmentCode;
	private String uomCode;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((equipmentCode == null) ? 0 : equipmentCode.hashCode());
		result = prime * result + ((uomCode == null) ? 0 : uomCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeterReadingId other = (MeterReadingId) obj;
		if (equipmentCode == null) {
			if (other.equipmentCode != null)
				return false;
		} else if (!equipmentCode.equals(other.equipmentCode))
			return false;
		if (uomCode == null) {
			if (other.uomCode != null)
				return false;
		} else if (!uomCode.equals(other.uomCode))
			return false;
		return true;
	}
}
