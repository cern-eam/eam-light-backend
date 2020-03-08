package ch.cern.cmms.eamlightweb.workorders.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteStandardWorkOrder extends EAMLightController {

    @GET
    @Path("/standardworkorder")
    @Produces("application/json")
    @Consumes("application/json")
    public Response complete(@QueryParam("userGroup") String userGroup, @QueryParam("s") String code) {
        GridRequest gridRequest = new GridRequest( "LVSTANDWOREP", GridRequest.GRIDTYPE.LOV, ApplicationData.AUTOCOMPLETE_RESULT_SIZE);

        gridRequest.addParam("param.excludetype", null);
        gridRequest.addParam("param.equipclass", null);
        gridRequest.addParam("param.category", null);
        gridRequest.addParam("param.equipclassorg", null);
        gridRequest.addParam("param.pagemode", "display");
        gridRequest.addParam("param.rjobtype", null);
        gridRequest.addParam("param.group", userGroup);
        gridRequest.addParam("param.priority", null);
        gridRequest.addParam("param.problemcode", null);
        gridRequest.addParam("param.description", null);
        gridRequest.addParam("param.woclass", null);
        gridRequest.addParam("param.woclassorg", null);
        gridRequest.addParam("control.org", authenticationTools.getOrganizationCode());

        gridRequest.addFilter("standardwo", code.toUpperCase(), "BEGINS");
        gridRequest.sortBy("standardwo");

        return getPairListResponse(gridRequest, "standardwo", "standardwodesc");
    }

}
