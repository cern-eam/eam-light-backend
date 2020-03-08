package ch.cern.cmms.eamlightweb.workorders;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/stdworkorders")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class StandardWorkOrderRest extends EAMLightController {

    @GET
    @Path("/{stdworkorder}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response readWorkOrder(@PathParam("stdworkorder") String number) {
        try {
            return ok(inforClient.getStandardWorkOrderService().readStandardWorkOrder(authenticationTools.getInforContext(), number));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

}
