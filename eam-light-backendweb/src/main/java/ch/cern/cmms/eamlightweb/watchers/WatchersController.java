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

@Path("/watchers")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class WatchersController extends EAMLightController {

    @Inject
    private WatchersService watchersService;

    @GET
    @Path("/{woCode}")
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

    @PUT
    @Path("/{woCode}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addWatchersToWorkOrder(@PathParam("woCode") String woCode, List<String> users) {
        try {
            return ok(watchersService.addWatchersToWorkOrder(authenticationTools.getInforContext(), users, woCode));
        } catch (InforException e){
            return forbidden(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PUT
    @Path("/remove/{woCode}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response removeWatchersFromWorkOrder(@PathParam("woCode") String woCode, List<String> users) {
        try {
            return ok(watchersService.removeWatchersFromWorkOrder(authenticationTools.getInforContext(), users, woCode));
        } catch (InforException e){
            return forbidden(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

}
