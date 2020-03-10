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


    private String EQUIPMENT_CODE = "TEST_MONITORING";
    private String WORKORDER_CODE = "25316908";

    @GET
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response monitor(@QueryParam("equipment") String equipment, @QueryParam("workorder") String number) {
        Map<String, String> responses = new HashMap<>();
        Equipment equipmentForUpdate = new Equipment();
        WorkOrder workorderForUpdate = new WorkOrder();
        boolean check = true;
        try {
            String result = monitoringService.monitoringReadEquipment(equipment,
                authenticationTools.getInforContext()).toString();
            responses.put("READEQUIPMENT", result);
        } catch (Exception e) {
            check = false;
            responses.put("READEQUIPMENT", "ERROR " + e.getMessage());
        }
        try {
            String result = monitoringService.monitoringReadWorkorder(number,
                authenticationTools.getInforContext()).toString();
            responses.put("READWORKORDER", result);
        } catch (Exception e) {
            check = false;
            responses.put("READWORKORDER", "ERROR " + e.getMessage());
        }
        try {
            String result = monitoringService.monitoringUpdateEquipment(equipmentForUpdate, EQUIPMENT_CODE,
                authenticationTools.getInforContext()).toString();
            responses.put("UPDATEEQUIPMENT", result);
        } catch (Exception e) {
            check = false;
            responses.put("UPDATEEQUIPMENT", "ERROR " + e.getMessage());
        }
        try {
            String result = monitoringService.monitoringUpdateWorkorder(workorderForUpdate, WORKORDER_CODE,
                authenticationTools.getInforContext()).toString();
            responses.put("UPDATEWORKORDER", result);
        } catch (Exception e) {
            check = false;
            responses.put("UPDATEWORKORDER", "ERROR " + e.getMessage());
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
