package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.EAMUser;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestSort;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;

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
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
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
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    public List<MyWorkOrder> getObjectWorkOrders(String equipmentCode) throws InforException {
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.getGridRequestFilters().add(new GridRequestFilter("equipment", equipmentCode, "="));
        gridRequest.setGridRequestSorts(new GridRequestSort[] {new GridRequestSort("datecreated", "DESC")});
        return inforClient.getTools().getGridTools().converGridResultToObject(MyWorkOrder.class,
                createMap(),
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        map.put("workordernum", "number"); // wo number
        map.put("description", "desc"); // description
        map.put("equipment", "object");   // equipment code
        map.put("workorderstatus_display", "status");  // status
        map.put("department", "mrc");   // department
        map.put("schedstartdate", "schedulingStartDate");  // scheduled start date
        map.put("schedenddate", "schedulingEndDate"); // scheduled end date
        map.put("datecreated", "createdDate"); // scheduled end date
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
