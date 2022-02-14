package ch.cern.cmms.eamlightweb.tools.autocomplete;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.index.WatcherInfo;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteUser extends EAMLightController {

    private List<Map<String, String>> getAutocompleteOptions(String code) throws InforException {
        GridRequest gridRequest = new GridRequest("BSUSER", GridRequest.GRIDTYPE.LIST, 10);

        String uppercasedCode = code.toUpperCase();

        gridRequest.addFilter("usercode", uppercasedCode, "BEGINS", GridRequestFilter.JOINER.OR);

        Arrays.stream(uppercasedCode.split(" ")).forEach(name -> {
            gridRequest.addFilter("description", " " + name, "CONTAINS",
                    GridRequestFilter.JOINER.OR, true, false);

            gridRequest.addFilter("description", name, "BEGINS",
                    GridRequestFilter.JOINER.AND, false, true);
        });

        gridRequest.sortBy("description");

        return GridTools.convertGridResultToMapList(inforClient.getGridsService()
                .executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    @GET
    @Path("/user/{code}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response complete(@PathParam("code") String code) throws InforException {
        return ok(getAutocompleteOptions(code));
    }

    @GET
    @Path("/user/{code}/{wo}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response completeFilteredByWOAccess(@PathParam("code") String code, @PathParam("wo") String woCode) throws InforException {
        List<String> userCodes = getAutocompleteOptions(code).stream().map(p -> p.get("usercode")).collect(Collectors.toList());
        return ok(WatcherInfo.getFilteredWatcherInfo(inforClient, userCodes, woCode));
    }
}