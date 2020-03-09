package ch.cern.cmms.eamlightweb.rest;

import ch.cern.cmms.eamlightejb.MonitoringService.MonitoringService;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.tools.InforException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/monitoring")
public class Monitoring {

    @Inject
    private AuthenticationTools authenticationTools;

    @Inject
    MonitoringService monitoringService;

    @GET
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response monitor(@QueryParam("equipment") String equipment, @QueryParam("workorder") String number) {
        Map<String, String> responses = new HashMap<>();
        boolean check = true;
        try {
            responses = monitoringService.monitoring(equipment, number, authenticationTools.getInforContext());
        } catch (InforException e) {
            e.printStackTrace();
        }
        String str = "ERROR ";

        for (String key : responses.values()) {
            if (key.contains(str)) {
                check = false;
            }
        }
        if (!check) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(responses)
                .build();
        } else {
            return Response.ok(responses).build();
        }
    }

}
