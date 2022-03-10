package ch.cern.cmms.eamlightweb.watchers;

import ch.cern.cmms.eamlightejb.watchers.WatchersService;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/workorders")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class WatchersController extends EAMLightController {

    @Inject
    private WatchersService watchersService;

    @GET
    @Path("/{woCode}/watchers")
    @Produces("application/json")
    public Response getWatchersForWorkOrder(@PathParam("woCode") String woCode) {
        try {
            return ok(watchersService.getWatchersForWorkOrder(authenticationTools.getInforContext(), woCode));
        } catch (InforException e){
            return forbidden(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @POST
    @Path("/{woCode}/watchers")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addWatchersToWorkOrder(@PathParam("woCode") String woCode, List<String> users) {
        try {
            return ok(watchersService.addWatchersToWorkOrder(authenticationTools.getInforContext(),
                    authenticationTools.getR5InforContext(), woCode, users));
        } catch (InforException e){
            return forbidden(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PUT
    @Path("/{woCode}/watchers/remove")
    @Consumes("application/json")
    @Produces("application/json")
    public Response removeWatchersFromWorkOrder(@PathParam("woCode") String woCode, List<String> users) {
        try {
            return ok(watchersService.removeWatchersFromWorkOrder(authenticationTools.getInforContext(), woCode, users));
        } catch (InforException e){
            return forbidden(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

}
