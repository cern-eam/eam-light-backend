package ch.cern.cmms.eamlightweb.meter;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.MeterReading;

@Path("/meters")
@Interceptors({ RESTLoggingInterceptor.class })
public class MeterRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createReading(MeterReading meterReading) {
		try {
			return ok(inforClient.getWorkOrderMiscService().createMeterReading(authenticationTools.getInforContext(), meterReading));
		} catch (InforException e) {
			return badRequest(e);
		} catch (Exception e) {
			return serverError(e);
		}
	}
}

