package ch.cern.cmms.eamlightweb.meter;

import java.util.*;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.GridTools.convertGridResultToObject;
import ch.cern.eam.wshub.core.services.workorders.entities.MeterReading;
import static ch.cern.eam.wshub.core.tools.GridTools.getCellContent;

/**
 * Controller for the meter readings in WO.
 *
 */
@Path("/meters")
@Interceptors({ RESTLoggingInterceptor.class })
public class MeterRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/read/eqp/{equipment}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readByEquipment(@PathParam("equipment") String equipment) {
		try {
			return ok(getMeterEquipmentMeterReading(equipment, null));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/read/meter/{meter}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readByMeter(@PathParam("meter") String meterCode) {
		try {
			GridRequest gridRequest = new GridRequest("OSMETE", GridRequest.GRIDTYPE.LIST, 1);
			gridRequest.setUserFunctionName("OSMETE");
			gridRequest.addFilter("metercode", meterCode, "=");
			String equipment = getCellContent("equipment", inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest).getRows()[0]);
			return ok(getMeterEquipmentMeterReading(equipment, meterCode));
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

	private List<MeterReadingWrap> getMeterEquipmentMeterReading(String equipmentCode, String meterCode) throws InforException {
		GridRequest gridRequest = new GridRequest("OSEQMR", GridRequest.GRIDTYPE.LIST);
		gridRequest.addParam("parameter.object", equipmentCode);

		if (meterCode != null) {
			gridRequest.addFilter("physicalmeter", meterCode, "=");
		}

		List<MeterReadingWrap> result = convertGridResultToObject(MeterReadingWrap.class,
				null,
				inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));

		result.forEach(meter -> meter.setEquipmentCode(equipmentCode));

		return result;
	}

}
