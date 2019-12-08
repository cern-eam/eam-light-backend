package ch.cern.cmms.eamlightweb.workorders.meter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.meter.EquipmentMeter;
import ch.cern.cmms.eamlightejb.meter.MeterEJB;
import ch.cern.cmms.eamlightejb.meter.MeterReadingEntity;
import ch.cern.cmms.eamlightejb.meter.MeterReadingWrap;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.MeterReading;

/**
 * Controller for the meter readings in WO.
 *
 */
@Path("/meters")
@Interceptors({ RESTLoggingInterceptor.class })
public class MeterRest extends WSHubController {

	@EJB
	private MeterEJB meterEJB;

	@Inject
	private InforClient inforClient;

	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/read/wo/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readByWorkOrder(@PathParam("workorder") String number) {
		try {
			List<MeterReadingEntity> rawReadings = meterEJB.getMeterReadings(number);
			List<EquipmentMeter> eqReadings = new ArrayList<EquipmentMeter>();
			EquipmentMeter curReading = null;
			// readings per equipment
			for (MeterReadingEntity r : rawReadings) {
				if (curReading == null || !curReading.getEquipmentCode().equals(r.getEquipmentCode())) {
					curReading = new EquipmentMeter();
					curReading.setEquipmentCode(r.getEquipmentCode());
					eqReadings.add(curReading);
				}
				String lastReading = r.getReading() == null ? null : r.getReading().toString();
				Date lastReadingDate = r.getLastReading() == null ? null : r.getLastReading();
				MeterReadingWrap meterReadingWrap = new MeterReadingWrap(lastReadingDate, lastReading, r.getUomDesc(),
						r.getMeterName(), new MeterReading());
				curReading.getMeterReadings().add(meterReadingWrap);
				meterReadingWrap.getMeterReading().setEquipmentCode(curReading.getEquipmentCode());
				meterReadingWrap.getMeterReading().setWoNumber(number);
				meterReadingWrap.getMeterReading().setUOM(r.getUomCode());
			}
			// Final response
			return ok(eqReadings);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/read/eqp/{equipment}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readByEquipment(@PathParam("equipment") String equipment) {
		try {
			List<MeterReadingEntity> rawReadings = meterEJB.getMeterReadingsByEquipmentCode(equipment);
			List<MeterReadingWrap> meterReadings = new ArrayList<MeterReadingWrap>();

			for (MeterReadingEntity m : rawReadings) {
				String lastReading = m.getReading() == null ? null : m.getReading().toString();
				Date lastReadingDate = m.getLastReading() == null ? null : m.getLastReading();
				MeterReadingWrap meterReadingWrap = new MeterReadingWrap(lastReadingDate, lastReading, m.getUomDesc(),
						m.getMeterName(), new MeterReading());
				meterReadings.add(meterReadingWrap);

				// init MeterReading
				meterReadingWrap.getMeterReading().setEquipmentCode(equipment);
				meterReadingWrap.getMeterReading().setUOM(m.getUomCode());
			}
			// Final response
			return ok(meterReadings);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/read/meter/{meterCode}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readByMeter(@PathParam("meterCode") String meterCode) {
		try {
			List<MeterReadingEntity> rawReadingList = meterEJB.getMeterReadingsByMeterCode(meterCode);
			MeterReadingEntity rawReading = null;
			if (rawReadingList != null && !rawReadingList.isEmpty()) {
				rawReading = rawReadingList.get(0);
			}
			MeterReadingWrap meterReadingWrap = null;
			if (rawReading != null) {
				String lastReading = rawReading.getReading() == null ? null : rawReading.getReading().toString();
				meterReadingWrap = new MeterReadingWrap(rawReading.getLastReading(), lastReading,
						rawReading.getUomDesc(), rawReading.getMeterName(), new MeterReading());
				// init MeterReading
				meterReadingWrap.getMeterReading().setEquipmentCode(rawReading.getEquipmentCode());
				meterReadingWrap.getMeterReading().setUOM(rawReading.getUomCode());
			}
			// Final response
			return ok(meterReadingWrap);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/check")
	@Produces("application/json")
	@Consumes("application/json")
	public Response checkRollOver(@QueryParam("equipment") String equipment, @QueryParam("uom") String uom,
			@QueryParam("actualValue") String actualValue) {
		try {
			return ok(meterEJB.checkValueRollover(equipment, uom, actualValue));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	/**
	 * Creates a new reading for the given equipment.
	 * 
	 * @param meterReading
	 *            Meter reading for the equipment.
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createReading(MeterReading meterReading) {
		try {
			return ok(inforClient.getWorkOrderMiscService().createMeterReading(authenticationTools.getInforContext(), meterReading));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
