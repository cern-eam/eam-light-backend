package ch.cern.cmms.eamlightweb.tools.autocomplete;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.utilities.AutocompleteEntityFilter;
import ch.cern.cmms.eamlightweb.utilities.AutocompleteEntityResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({RESTLoggingInterceptor.class})
public class AutocompleteEntity extends EAMLightController {

    @Inject
    private AutocompleteEntityResolver autocompleteEntityResolver;

    @GET
    @Path("/entity")
    @Produces("application/json")
    @Consumes("application/json")
    public Response complete(@QueryParam("s") String code, @QueryParam("entityType") String entityType, @QueryParam("entityClass") String entityClass) {
        try {
            return ok(autocompleteEntityResolver.autocomplete(entityType, new AutocompleteEntityFilter(code, entityClass), authenticationTools.getInforContext()));
        } catch (Exception e) {
            return serverError(e);
        }
    }

}
