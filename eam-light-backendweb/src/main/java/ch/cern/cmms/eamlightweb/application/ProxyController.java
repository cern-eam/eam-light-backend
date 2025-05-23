package ch.cern.cmms.eamlightweb.application;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightNativeRestController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.interceptors.InforInterceptor;
import ch.cern.eam.wshub.core.interceptors.beans.InforErrorData;
import ch.cern.eam.wshub.core.interceptors.beans.InforExtractedData;
import ch.cern.eam.wshub.core.interceptors.beans.InforRequestData;
import ch.cern.eam.wshub.core.interceptors.beans.InforResponseData;
import ch.cern.eam.wshub.core.services.INFOR_OPERATION;
import ch.cern.eam.wshub.core.tools.InforException;
import net.datastream.schemas.mp_fields.CATEGORYID;
import net.datastream.schemas.mp_fields.EQUIPMENTID_Type;
import net.datastream.schemas.mp_fields.ORGANIZATIONID_Type;
import net.datastream.schemas.mp_functions.mp0324_001.MP0324_GetEquipmentCategory_001;
import net.datastream.schemas.mp_functions.mp0328_002.MP0328_GetPositionParentHierarchy_002;
import net.datastream.schemas.mp_results.mp0324_001.MP0324_GetEquipmentCategory_001_Result;
import net.datastream.schemas.mp_results.mp0328_002.MP0328_GetPositionParentHierarchy_002_Result;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.spi.CDI;
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

    private InforInterceptor inforInterceptor;

    @PostConstruct
    public void init() {
        try {
            inforInterceptor = CDI.current().select(InforInterceptor.class).get();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

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
    public Response proxy(String body, @Context Request request, @Context UriInfo uriInfo) {
        try {
            String path = uriInfo.getPath(false).replace("/proxy", "");
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(URI.create(applicationData.getRESTURL() + path));
            Invocation.Builder builder = target.request();

            InforContext inforContext = authenticationTools.getInforContext();

            if (inforContext.getCredentials() != null) {
                String credentials = inforContext.getCredentials().getUsername() + ":" + inforContext.getCredentials().getPassword();
                builder.header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes()));
            }

            if (inforContext.getSessionID() != null) {
                builder.header("sessionid", inforContext.getSessionID());
                builder.header("keepsession", "true");
            }

            builder.header("tenant", authenticationTools.getInforContext().getTenant());
            builder.header("organization", authenticationTools.getOrganizationCode());
            builder.header("accept", "application/json");
            String method = request.getMethod();
            Entity<?> entity = (method.equals("GET") || method.equals("HEAD") || method.equals("OPTIONS")) ? null : Entity.json(body);

            Response originalResponse = builder.method(method, entity);

            // Build new response from the original one and filter out caching headers
            Response.ResponseBuilder responseBuilder = Response.status(originalResponse.getStatus()).entity(originalResponse.getEntity());

            originalResponse.getHeaders().forEach((key, values) -> {
                if (!key.equalsIgnoreCase("Cache-Control") && !key.equalsIgnoreCase("Pragma") && !key.equalsIgnoreCase("Expires")) {
                    values.forEach(value -> responseBuilder.header(key, value));
                }
            });

            Response response =  responseBuilder
                        .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .build();

            log(method, path, body, response);

            return response;

        } catch (Exception e) {
            return serverError(e);
        }

    }

    private void log(String method, String path, String requestBody, Response response) {
        Logger.getLogger("wshublogger").log(Logger.Level.DEBUG, requestBody);
        // TODO save response as well

        if (inforInterceptor == null) {
            return;
        }

        try {
            InforRequestData inforRequestData =  new InforRequestData.Builder()
                    .withInforContext(authenticationTools.getInforContext())
                    .withInput(requestBody)
                    .build();

            InforResponseData inforResponseData = new InforResponseData.Builder()
                    .withResponse("RESPONSE")
                            .withResponseTime(10000l)
                                    .build();

            InforExtractedData inforExtractedData = new InforExtractedData.Builder()
                    .withDataReference1(path).build();

            InforErrorData inforErrorData = new InforErrorData.Builder()
                    .withException(new Exception("ERROR"))
                            .build();

            if (response.getStatus() == 200) {
                inforInterceptor.afterSuccess(convert(method, path), inforRequestData, inforResponseData, inforExtractedData);
            }

            if (response.getStatus() == 400) {
                inforInterceptor.afterError(convert(method, path), inforRequestData, inforErrorData, inforExtractedData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage());
        }

    }

    private INFOR_OPERATION convert(String method, String path) {
        //TODO
        return INFOR_OPERATION.OTHER;
    }
}