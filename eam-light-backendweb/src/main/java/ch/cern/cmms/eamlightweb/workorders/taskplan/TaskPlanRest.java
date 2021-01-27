package ch.cern.cmms.eamlightweb.workorders.taskplan;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.workorders.entities.TaskPlan;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;

import javax.ws.rs.core.Response;

@Path("/taskplan")
@Interceptors({ RESTLoggingInterceptor.class })
public class TaskPlanRest extends EAMLightController {
    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;

    @GET
    @Path("/{taskcode}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response readTaskPlan (@PathParam("taskcode") String taskCode) {
        try {
            TaskPlan taskPlan = new TaskPlan();
            taskPlan.setCode(taskCode);
            return ok(inforClient.getTaskPlanService().getTaskPlan(authenticationTools.getInforContext(), taskPlan));
        } catch (InforException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

}
