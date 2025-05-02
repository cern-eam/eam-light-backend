package ch.cern.cmms.eamlightweb.application;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.EAMLightNativeRestController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.tools.InforException;
import net.datastream.schemas.mp_fields.CATEGORYID;
import net.datastream.schemas.mp_fields.EQUIPMENTID_Type;
import net.datastream.schemas.mp_fields.LOCATIONID_Type;
import net.datastream.schemas.mp_fields.ORGANIZATIONID_Type;
import net.datastream.schemas.mp_functions.mp0318_001.MP0318_GetLocation_001;
import net.datastream.schemas.mp_functions.mp0324_001.MP0324_GetEquipmentCategory_001;
import net.datastream.schemas.mp_functions.mp0328_002.MP0328_GetPositionParentHierarchy_002;
import net.datastream.schemas.mp_results.mp0318_001.MP0318_GetLocation_001_Result;
import net.datastream.schemas.mp_results.mp0324_001.MP0324_GetEquipmentCategory_001_Result;
import net.datastream.schemas.mp_results.mp0328_002.MP0328_GetPositionParentHierarchy_002_Result;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.ws.soap.SOAPFaultException;
import java.net.URI;


@ApplicationScoped
@Path("proxy")
public class ProxyController extends EAMLightNativeRestController {

    @Inject
    private AuthenticationTools authenticationTools;

    @Inject
    private InforClient inforClient;

    @Inject
    private ApplicationData applicationData;

    @GET
    @Path("/category/{categoryCode}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response readCustomFields(@PathParam("categoryCode") String categoryCode) {
        try {
            MP0324_GetEquipmentCategory_001 getEquipmentCategory = new MP0324_GetEquipmentCategory_001();
            getEquipmentCategory.setCATEGORYID(new CATEGORYID());
            getEquipmentCategory.getCATEGORYID().setCATEGORYCODE(categoryCode);
            MP0324_GetEquipmentCategory_001_Result result = inforClient.getTools().performInforOperation(authenticationTools.getInforContext(), inforClient.getInforWebServicesToolkitClient()::getEquipmentCategoryOp, getEquipmentCategory);
            return ok(result);
        } catch (SOAPFaultException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

    @GET
    @Path("/customfields")
    @Produces("application/json")
    @Consumes("application/json")
    public Response readCustomFields(@QueryParam("entityCode") String entityCode, @QueryParam("classCode") String classCode) {
        try {
            return ok(inforClient.getTools().getCustomFieldsTools().getInforCustomFields(authenticationTools.getInforContext(), entityCode, classCode));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

    @GET
    @Path("/positionparenthierarchy")
    @Produces("application/json")
    @Consumes("application/json")
    public Response readPositionHierarchy(@QueryParam("code") String code, @QueryParam("org") String org) {
        try {
            MP0328_GetPositionParentHierarchy_002 getpositionph = new MP0328_GetPositionParentHierarchy_002();
            getpositionph.setPOSITIONID(new EQUIPMENTID_Type());
            getpositionph.getPOSITIONID().setORGANIZATIONID(new ORGANIZATIONID_Type());
            getpositionph.getPOSITIONID().getORGANIZATIONID().setORGANIZATIONCODE(org);
            getpositionph.getPOSITIONID().setEQUIPMENTCODE(code);

            MP0328_GetPositionParentHierarchy_002_Result result =
                    inforClient.getTools().performInforOperation(authenticationTools.getInforContext(), inforClient.getInforWebServicesToolkitClient()::getPositionParentHierarchyOp, getpositionph);

           return ok(result);
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

    @Path("{path: .*}")
    @GET
    @POST
    @PUT
    @DELETE
    @HEAD
    @OPTIONS
    public Response proxy(@PathParam("path") String path, String body, @Context Request request) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URI.create(applicationData.getRESTURL() + "/" + path.replace("#", "%23")));
        Invocation.Builder builder = target.request();

        // Only send explicitly defined headers
        String credentials = null;
        try {
            credentials = authenticationTools.getInforContext().getCredentials().getUsername() + ":" + authenticationTools.getInforContext().getCredentials().getPassword();
        } catch (Exception e) {

        }

        builder.header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes()));
        builder.header("tenant", "infor");
        builder.header("organization", authenticationTools.getOrganizationCode());
        builder.header("accept", "application/json");
        String method = request.getMethod();
        Entity<?> entity = (method.equals("GET") || method.equals("HEAD") || method.equals("OPTIONS")) ? null : Entity.json(body);

        Response originalResponse = builder.method(method, entity);

        // Create a copy of the response in order not to pass any caching headers
        Response.ResponseBuilder responseBuilder = Response.status(originalResponse.getStatus()).entity(originalResponse.getEntity());

        originalResponse.getHeaders().forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Cache-Control") &&
                    !key.equalsIgnoreCase("Pragma") &&
                    !key.equalsIgnoreCase("Expires")) {
                values.forEach(value -> responseBuilder.header(key, value));
            }
        });

        return responseBuilder
                .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build();

    }
}