package ch.cern.cmms.eamlightweb.location;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightNativeRestController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.UserDefinedFields;
import ch.cern.eam.wshub.core.services.equipment.entities.Location;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.Tools.extractEntityCode;
import static ch.cern.eam.wshub.core.tools.Tools.extractOrganizationCode;
import net.datastream.schemas.mp_fields.LOCATIONID_Type;
import net.datastream.schemas.mp_fields.ORGANIZATIONID_Type;
import net.datastream.schemas.mp_functions.mp0318_001.MP0318_GetLocation_001;
import net.datastream.schemas.mp_functions.mp0319_001.MP0319_SyncLocation_001;
import net.datastream.schemas.mp_results.mp0318_001.MP0318_GetLocation_001_Result;
import net.datastream.schemas.mp_results.mp0319_001.MP0319_SyncLocation_001_Result;

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
import javax.xml.ws.soap.SOAPFaultException;

@Path("/locations")
@ApplicationScoped
@Interceptors({RESTLoggingInterceptor.class})
public class LocationRest extends EAMLightNativeRestController {

    @Inject
    private AuthenticationTools authenticationTools;

    @Inject
    private InforClient inforClient;

    @GET
    @Path("/{location}")
    @Produces("application/json")
    public Response readLocation(@PathParam("location") String location) {
        try {
            MP0318_GetLocation_001 getLocation = new MP0318_GetLocation_001();
            getLocation.setLOCATIONID(new LOCATIONID_Type());
            getLocation.getLOCATIONID().setORGANIZATIONID(new ORGANIZATIONID_Type());
            getLocation.getLOCATIONID().getORGANIZATIONID().setORGANIZATIONCODE(extractOrganizationCode(location));
            getLocation.getLOCATIONID().setLOCATIONCODE( extractEntityCode(location) );

            MP0318_GetLocation_001_Result getLocationResult = inforClient.getTools().performInforOperation(authenticationTools.getInforContext(), inforClient.getInforWebServicesToolkitClient()::getLocationOp , getLocation);

            return ok(getLocationResult);
        } catch (SOAPFaultException e) {
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
            return ok(inforClient.getLocationService().createLocation(authenticationTools.getInforContext(), location));
        } catch (SOAPFaultException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @PUT
    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateLocation(net.datastream.schemas.mp_entities.location_001.Location location) {
        try {
            MP0319_SyncLocation_001 syncLocation = new MP0319_SyncLocation_001();
            syncLocation.setLocation(location);
            MP0319_SyncLocation_001_Result result =  inforClient.getTools().performInforOperation(authenticationTools.getInforContext(), inforClient.getInforWebServicesToolkitClient()::syncLocationOp , syncLocation);
            return ok(result);
        } catch (InforException e) {
            return badRequest(e);
        } catch (Exception e) {
            return serverError(e);
        }
    }

    @DELETE
    @Path("/{locationCode : .+}")
    @Produces("application/json")
    public Response deleteLocation(@PathParam("locationCode") String locationCode) {
        try {
            return ok(inforClient.getLocationService().deleteLocation(authenticationTools.getInforContext(), locationCode));
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
