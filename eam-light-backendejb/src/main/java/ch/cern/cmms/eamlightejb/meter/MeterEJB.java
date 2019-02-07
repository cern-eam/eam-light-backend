package ch.cern.cmms.eamlightejb.meter;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class MeterEJB {

	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Default constructor.
	 */
	public MeterEJB() {
	}
	
	public List<MeterReadingEntity> getMeterReadings(String woCode) {
		return em.createNamedQuery(MeterReadingEntity.GET_METER_READINGS, MeterReadingEntity.class)
			   .setParameter("workOrderCode", woCode)
			   .getResultList();
	}
	
	public List<MeterReadingEntity> getMeterReadingsByMeterCode(String meterCode){
		try {
			return em.createNamedQuery(MeterReadingEntity.GET_METER_READING_BY_METER_ID, MeterReadingEntity.class)
				   .setParameter("meterCode", meterCode)
				   .getResultList();
		} catch(javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public List<MeterReadingEntity> getMeterReadingsByMeterCode(String meterCode, int maxResults){
		try {
			return em.createNamedQuery(MeterReadingEntity.GET_METER_READING_BY_METER_ID, MeterReadingEntity.class)
				   .setParameter("meterCode", meterCode)
				   .setMaxResults(maxResults)
				   .getResultList();
		} catch(javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public List<MeterReadingEntity> getMeterReadingsByEquipmentCode(String eqCode) {
		return em.createNamedQuery(MeterReadingEntity.GET_METER_READINGS_BY_EQ_ID, MeterReadingEntity.class)
			   .setParameter("eqCode", eqCode)
			   .getResultList();
	}

	/**
	 * Check if last value is smaller for normal meter (not up/down meter) having a 'Maximum Value' defined
	 * @param eqCode
	 * @param uom
	 * @param value
	 * @return true if the value is less than the Last Reading, which indicates the meter has rolled over. 
	 */
	public boolean checkValueRollover(String eqCode, String uom, String value) {
		// Check if last value is smaller for normal meter (not up/down meter) having a 'Maximum Value' defined
		try {
			String query = "select met_last from R5METERS left join r5objusagedefs on oud_meter = met_code where oud_updownmeter='-' and oud_object = :eqCode and oud_uom = :uom and met_max is not null";
			Number lastReading = (Number) em.createNativeQuery(query)
				   .setParameter("eqCode", eqCode)
				   .setParameter("uom", uom)
				   .getSingleResult();
			 
			if(lastReading != null && lastReading.doubleValue() > Double.parseDouble(value))
				return true;
		} catch(NoResultException e) {
			return false;
		} 
		return false;
	}
	
}
