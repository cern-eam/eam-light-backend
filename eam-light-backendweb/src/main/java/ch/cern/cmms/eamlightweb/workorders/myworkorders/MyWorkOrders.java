package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import ch.cern.cmms.eamlightejb.workorders.MyWorkOrder;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.EAMUser;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class MyWorkOrders {

    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;

    public List<MyWorkOrder> getMyOpenWorkOrders() throws InforException {
        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        EAMUser eamUser = inforClient.getUserSetupService().readUserSetup(authenticationTools.getInforContext(), userCode);
        //
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("assignedto", eamUser.getCernId(), "=", GridRequestFilter.JOINER.AND));
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("evt_rstatus", "R", "="));
        return inforClient.getTools().getGridTools().converGridResultToObject(MyWorkOrder.class,
                createMap(),
                inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));
    }


    public List<MyWorkOrder> getMyTeamsWorkOrders() throws InforException {
        String userDepartments = readUserDepartments();
        if (userDepartments.isEmpty()) {
            return new LinkedList<>();
        }

        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("department", userDepartments, "IN", GridRequestFilter.JOINER.AND));
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("evt_rstatus", "R", "="));
        return inforClient.getTools().getGridTools().converGridResultToObject(MyWorkOrder.class,
                createMap(),
                inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));
    }

    public List<MyWorkOrder> getObjectWorkOrders(String equipmentCode) throws InforException {
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("equipment", equipmentCode, "="));
        return inforClient.getTools().getGridTools().converGridResultToObject(MyWorkOrder.class,
                createMap(),
                inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));
    }

    private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        map.put("279", "number"); // wo number
        map.put("757", "desc"); // description
        map.put("5", "object");   // equipment code
        map.put("16", "statusCode");  // status
        map.put("9", "mrc");   // department
        map.put("19", "schedulingStartDate");  // scheduled start date
        map.put("426", "schedulingEndDate"); // scheduled end date
        return map;
    }

    private String readUserDepartments() throws InforException {
        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        EAMUser eamUser = inforClient.getUserSetupService().readUserSetup(authenticationTools.getInforContext(), userCode);
        String departments = "";
        if (eamUser.getDepartment() != null) {
            departments = eamUser.getDepartment();
        }
        if (eamUser.getUserDefinedFields().getUdfchar10() != null) {
            if (!departments.isEmpty()) {
                departments += ",";
            }
            departments += eamUser.getUserDefinedFields().getUdfchar10();
        }
        return departments;
    }

}
