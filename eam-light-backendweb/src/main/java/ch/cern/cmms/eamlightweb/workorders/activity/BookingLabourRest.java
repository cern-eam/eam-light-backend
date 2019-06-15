package ch.cern.cmms.eamlightweb.workorders.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.services.workorders.entities.LaborBooking;

@Path("/bookinglabour")
@Interceptors({ RESTLoggingInterceptor.class })
public class BookingLabourRest extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readBookingLabours(@PathParam("workorder") String workorder) {
		try {
			List<LaborBooking> labors = inforClient.getLaborBookingService().readLaborBookings(authenticationTools.getR5InforContext(), workorder);
			Collections.sort(labors);
			return ok(labors);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createBookingLabour(LaborBooking bookingLabour) {
		try {
			// TODO Keep in mind that it must receive the TRADE field also, comming from the activity parent
			return ok(inforClient.getLaborBookingService().createLaborBooking(authenticationTools.getInforContext(), bookingLabour));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/init/{workorder}/{department}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initBookingLabour(@PathParam("workorder") String number,
			@PathParam("department") String department) {
		try {
		LaborBooking bookingLabor = new LaborBooking();
		//
		bookingLabor.setDateWorked(new Date());
		bookingLabor.setDepartmentCode(department);
		bookingLabor.setWorkOrderNumber(number);
		bookingLabor.setTypeOfHours("N");
		// Employee
		//TODO bookingLabor.setEmployeeCode(userData.getEamAccount().getCernId());
		return ok(bookingLabor);
	} catch(Exception e) {
		return serverError(e);
	}
}

}
