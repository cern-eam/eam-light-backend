package ch.cern.cmms.eamlightweb.rest;

import ch.cern.cmms.eamlightejb.MonitoringService.MonitoringService;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Credentials;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
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
    MonitoringService monitoringService;

    @Inject
    ApplicationData applicationData;

    @Inject
    InforClient inforClient;

    @GET
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response monitor(@QueryParam("equipment") String equipment, @QueryParam("workorder") String number,
        @QueryParam("equipmentUpdate") String equipmentUpdate, @QueryParam("workorderUpdate") String workorderUpdate) {
        Map<String, String> responses = new HashMap<>();
        Credentials credentials = new Credentials();
        credentials.setUsername("R5");
        credentials.setPassword(applicationData.getAdminPassword());
        InforContext inforContext = inforClient.getTools().getInforContext(credentials);

        responses = monitoringService.monitorEndpoints(equipment, number,
            equipmentUpdate,
            workorderUpdate, inforContext);

        if (!checkForError(responses)) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(responses)
                .build();
        } else {
            return Response.ok(responses).build();
        }
    }

    public boolean checkForError(Map<String, String> map) {

        for (String value : map.values()) {
            if (value.startsWith(monitoringService.getErrorMessage())) {
                return false;
            }
        }
        return true;
    }

}
