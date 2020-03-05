package ch.cern.cmms.eamlightweb.location;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.equipment.entities.Location;
import ch.cern.eam.wshub.core.tools.InforException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/locations")
@ApplicationScoped
@Interceptors({RESTLoggingInterceptor.class})
public class LocationRest extends EAMLightController {

    @Inject
    private AuthenticationTools authenticationTools;

    @Inject
    private InforClient inforClient;

    @GET
    @Path("/{locationCode}")
    @Produces("application/json")
    public Response readLocation(@PathParam("locationCode") String locationCode) {
        try {
            return ok(
                inforClient.getLocationService().readLocation(authenticationTools.getInforContext(), locationCode));
        } catch (InforException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @POST
    @Path("/")
    @Produces("application/json")
    public Response createLocation(Location location) {
        try {
            String locationCode = inforClient.getLocationService().createLocation(authenticationTools.getInforContext(),
                location);
            return ok(
                inforClient.getLocationService().readLocation(authenticationTools.getInforContext(), locationCode));
        } catch (InforException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PUT
    @Path("/{locationCode}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateLocation(@PathParam("locationCode") String locationCode, Location location) {
        try {
            inforClient.getLocationService().updateLocation(authenticationTools.getInforContext(), location);
            return ok(inforClient.getLocationService().readLocation(authenticationTools.getInforContext(),
                locationCode));
        } catch (InforException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @DELETE
    @Path("/{locationCode}")
    @Produces("application/json")
    public Response deleteLocation(@PathParam("locationCode") String locationCode) {
        try {
            return ok(inforClient.getLocationService().deleteLocation(authenticationTools.getInforContext(),
                locationCode));
        } catch (InforException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @GET
    @Path("/init")
    @Produces("application/json")
    public Response initLocation() {
        try {
            Location location = new Location();
            location.setUserDefinedFields(new UserDefinedFields());
            location.setCustomFields(inforClient.getTools().getCustomFieldsTools()
                .getWSHubCustomFields(authenticationTools.getInforContext(), "LOC", "*"));
            return ok(location);
        } catch (Exception e) {
            return serverError(e);
        }
    }
}
