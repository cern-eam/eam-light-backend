package ch.cern.cmms.eamlightweb.rest;

import ch.cern.cmms.eamlightejb.MonitoringService.MonitoringService;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
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
    public Response monitor(@QueryParam("equipment") String equipment, @QueryParam("workorder") String number,
        @QueryParam("equipmentUpdate") String equipmentUpdate, @QueryParam("workorderUpdate") String workorderUpdate) {
        Map<String, String> responses = new HashMap<>();
        try {
            responses = monitoringService.monitorEndpoints(equipment, number,
                equipmentUpdate,
                workorderUpdate, authenticationTools.getInforContext());
        } catch (Exception e) {
            Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getMessage())
                .build();
        }

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
        String str = "ERROR ";

        for (String key : map.values()) {
            if (key.startsWith(str)) {
                return false;
            }
        }
        return true;
    }

}
