package ch.cern.cmms.eamlightweb.workorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.workorders.entities.Activity;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Path("/activities")
@Interceptors({ RESTLoggingInterceptor.class })
public class ActivitiesRest extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/read")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readActivities(@QueryParam("workorder") String number, @DefaultValue("true") @QueryParam("includeChecklists") Boolean includeChecklists) {
		try {
			return ok(inforClient.getLaborBookingService().readActivities(authenticationTools.getInforContext(), number, includeChecklists));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createActivity(Activity activity) {
		try {
			return ok(inforClient.getLaborBookingService().createActivity(authenticationTools.getInforContext(),activity));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateActivity(Activity activity) {
		try {
			return ok(inforClient.getLaborBookingService().updateActivity(authenticationTools.getInforContext(),activity));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/init/{workorder}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response initActivity(@PathParam("workorder") String number) {
		try {
			Activity activity = new Activity();
			activity.setWorkOrderNumber(number);
			activity.setActivityCode(getDefaultActivityId(number));
			activity.setStartDate(new Date());
			activity.setEndDate(new Date());
			activity.setPeopleRequired(BigInteger.ONE);
			activity.setEstimatedHours(BigDecimal.ONE);
			return ok(activity);
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	private String getDefaultActivityId(String workOrder) throws InforException {
		Activity[] activities = inforClient.getLaborBookingService().readActivities(authenticationTools.getInforContext(), workOrder, false);
		if (activities != null && activities.length > 0) {
			return Integer.toString(Integer.parseInt(activities[activities.length - 1].getActivityCode()) + 5);
		} else {
			return "5";
		}
	}

}
