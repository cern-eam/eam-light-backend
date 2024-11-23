package ch.cern.cmms.eamlightweb.equipment;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.equipment.entities.NonConformityObservation;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/ncrobservations")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class NonConformityObservationsRest extends EAMLightController {

    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;

    @GET
    @Path("/{ncr}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response loadNonConformityObservations(@PathParam("ncr") String ncr) {
        try {
            List<Map<String, String>> additionalCostsList = new ArrayList<>();
            if (ncr != null) {
                GridRequest gridRequest = new GridRequest("OSNCHD_OBS");
                gridRequest.setUserFunctionName("OSNCHD");
                gridRequest.addParam("param.nonconformity", ncr);
                gridRequest.addParam("param.organization", "*");
                additionalCostsList = inforClient.getTools().getGridTools().convertGridResultToMapList(
                        inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest)
                );
            }
            return ok(additionalCostsList);
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

    @POST
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response createNonConformityObservation(NonConformityObservation nonConformityObservation) {
        try {
            return ok(inforClient.getNonconformityObservationService().createNonConformityObservation(authenticationTools.getInforContext(), nonConformityObservation));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }
}
