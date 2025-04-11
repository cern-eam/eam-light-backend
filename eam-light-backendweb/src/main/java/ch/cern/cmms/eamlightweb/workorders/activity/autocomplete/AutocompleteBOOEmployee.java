package ch.cern.cmms.eamlightweb.workorders.activity.autocomplete;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({RESTLoggingInterceptor.class})
public class AutocompleteBOOEmployee extends EAMLightController {

    @GET
    @Path("/boo/event/{event}/activity/{act}/trade/{trade}/octype/{octype}/employee/{code}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response complete(@PathParam("event") String event, @PathParam("act") String act, @PathParam("trade") String trade, @PathParam("octype") String octype, @PathParam("code") String code) {
        GridRequest gridRequest = new GridRequest("146", "LVEMP", "151");
        gridRequest.setGridType(GridRequest.GRIDTYPE.LOV);
        gridRequest.addParam("param.date", null);
        gridRequest.addParam("parameter.per_type", null);
        gridRequest.addParam("param.trade", trade);
        gridRequest.addParam("param.act", act);
        gridRequest.addParam("param.booplan", "true");
        gridRequest.addParam("param.event", event);
        gridRequest.addParam("param.octype", octype);

        gridRequest.getGridRequestFilters().add(new GridRequestFilter("personcode", code.toUpperCase(), "BEGINS", GridRequestFilter.JOINER.OR));
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("description", code.toUpperCase(), "CONTAINS"));

        gridRequest.sortBy("description");

        return getPairListResponse(gridRequest, "personcode", "description");
    }

}
