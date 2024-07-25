package ch.cern.cmms.eamlightweb.tools.autocomplete;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({RESTLoggingInterceptor.class})
public class AutocompleteSupervisor extends EAMLightController {

    @GET
    @Path("/supervisor/{code}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response complete(@PathParam("code") String code) {
        GridRequest gridRequest = new GridRequest("LVSCHEDGROUP", GridRequest.GRIDTYPE.LOV, 10);
        String uppercasedCode = code.toUpperCase();

        gridRequest.addFilter("schedgroup", uppercasedCode, "BEGINS", GridRequestFilter.JOINER.OR);

        Arrays.stream(uppercasedCode.split(" ")).forEach(name -> {
            gridRequest.addFilter("schedgroupdesc", " " + name, "CONTAINS",
                    GridRequestFilter.JOINER.OR, true, false);

            gridRequest.addFilter("schedgroupdesc", name, "BEGINS",
                    GridRequestFilter.JOINER.AND, false, true);
        });

        gridRequest.sortBy("schedgroupdesc");

        return getMapListResponse(gridRequest, "schedgroup", "schedgroupdesc");
    }
}