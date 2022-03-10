package ch.cern.cmms.eamlightweb.tools.autocomplete;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.watchers.WatcherInfo;
import ch.cern.cmms.eamlightejb.watchers.WatchersService;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteUser extends EAMLightController {

    @Inject
    private WatchersService watchersService;

    @GET
    @Path("/users/{code}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response complete(@PathParam("code") String code) throws InforException {
        return ok(watchersService.getAutocompleteOptions(authenticationTools.getR5InforContext(), code));
    }

    @GET
    @Path("/workorders/{wo}/users/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response completeFilteredByWOAccess(@QueryParam("hint") String hint, @PathParam("wo") String woCode) {
        final List<WatcherInfo> filteredWatcherInfo = watchersService.getFilteredWatcherInfo(woCode, hint);
        return ok(filteredWatcherInfo);
    }

    @GET
    @Path("/workorders/{wo}/users/search")
    @Produces("application/json")
    @Consumes("application/json")
    public Response completeFilteredByWOAccessSearch(@QueryParam("hint") String hint,
                                                      @PathParam("wo") String woCode) {
        final List<WatcherInfo> filteredWatcherInfo = watchersService.getFilteredWatcherInfo(woCode, hint);
        return ok(filteredWatcherInfo);
    }
}