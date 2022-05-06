package ch.cern.cmms.eamlightweb.workorders.additionalcosts;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.WorkOrderPart;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransaction;
import ch.cern.eam.wshub.core.services.material.entities.IssueReturnPartTransactionType;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrderAdditionalCosts;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/workorders")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AdditionalCostsRest extends EAMLightController {

    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;

    @GET
    @Path("/{workorder}/additionalcosts")
    @Produces("application/json")
    @Consumes("application/json")
    public Response loadAdditionalCostsList(@PathParam("workorder") String workorder) {
        try {
            List<Map<String, String>> additionalCostsList = new ArrayList<>();
            if (workorder != null) {
                GridRequest gridRequest = new GridRequest("WSJOBS_ACO");
                gridRequest.setUserFunctionName("WSJOBS");
                gridRequest.addParam("param.workordernum", workorder);
                gridRequest.addParam("param.headeractivity", "0");
                gridRequest.addParam("param.headerjob", "0");
                gridRequest.sortBy("additionalcostsdate");

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
    @Path("/{workorder}/additionalcosts")
    @Produces("application/json")
    @Consumes("application/json")
    public Response createAdditionalCost(@PathParam("workorder") String workorder, WorkOrderAdditionalCosts additionalCost) {
        try {
            additionalCost.setWorkOrderNumber(workorder);
            return ok(inforClient.getWorkOrderMiscService().createWorkOrderAdditionalCost(authenticationTools.getInforContext(), additionalCost));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }
}
