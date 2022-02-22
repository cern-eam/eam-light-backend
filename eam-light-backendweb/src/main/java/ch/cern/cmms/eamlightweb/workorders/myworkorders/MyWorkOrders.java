package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.OrganizationTools;
import ch.cern.cmms.eamlightweb.user.UserService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.grids.GridsService;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestSort;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class MyWorkOrders {

    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;
    @Inject
    private UserService userService;

    public List<MyWorkOrder> getMyOpenWorkOrders() throws InforException {
        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        EAMUser eamUser = userService.readUserSetup(authenticationTools.getInforContext(), userCode);

        // If EAM User has no associated employee, return an empty list
        if (eamUser.getEmployeeCode() == null || "".equals(eamUser.getEmployeeCode().trim())) {
            return new ArrayList<>();
        }
        //
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.addFilter("assignedto", eamUser.getEmployeeCode(), "=", GridRequestFilter.JOINER.AND);
        gridRequest.addFilter("evt_rstatus", "R", "=");
        return GridTools.convertGridResultToObject(MyWorkOrder.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    public List<MyWorkOrder> getMyTeamsWorkOrders() throws InforException {
        String userDepartments = readUserDepartments();
        if (userDepartments.isEmpty()) {
            return new LinkedList<>();
        }

        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.addFilter("department", userDepartments, "IN", GridRequestFilter.JOINER.AND);
        gridRequest.addFilter("evt_rstatus", "R", "=");
        return inforClient.getTools().getGridTools().convertGridResultToObject(MyWorkOrder.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    public List<MyWorkOrder> getObjectWorkOrders(String equipmentCode) throws InforException {
        GridRequest gridRequest = new GridRequest("93", "WSJOBS", "2005");
        gridRequest.setUserFunctionName("WSJOBS");
        gridRequest.setRowCount(2000);
        gridRequest.setUseNative(false);
        gridRequest.addFilter("equipment", equipmentCode, "=");
        gridRequest.sortBy("datecreated", "DESC");

        return inforClient.getTools().getGridTools().convertGridResultToObject(MyWorkOrder.class,
                null,
                inforClient.getGridsService().executeQuery(authenticationTools.getR5InforContext(), gridRequest));
    }

    private static final Map<String, String> typeToUserFunctionMap = new HashMap<>();
    static {
        typeToUserFunctionMap.put("A", "OSOBJA");
        typeToUserFunctionMap.put("P", "OSOBJP");
        typeToUserFunctionMap.put("L", "OSOBJL");
        typeToUserFunctionMap.put("S", "OSOBJS");
    }

    public List<MyWorkOrder> getObjectEvents(String equipmentCode, String equipmentType) throws InforException {
        GridsService gridsService = inforClient.getGridsService();
        GridTools gridTools = inforClient.getTools().getGridTools();

        // get the work orders that correspond to the events of the object (in the Events tab in Extended)
        GridRequest workOrdersGridRequest = new GridRequest("OSVEVT");
        workOrdersGridRequest.setUserFunctionName(typeToUserFunctionMap.getOrDefault(equipmentType, "OSOBJA"));
        workOrdersGridRequest.setRowCount(2000);
        workOrdersGridRequest.addParam("parameter.object", equipmentCode);

        // use star organization to get all work orders, independently of organization
        workOrdersGridRequest.addParam("parameter.objorganization", "*");

        workOrdersGridRequest.addFilter("eventtype", "JOB", "=", GridRequestFilter.JOINER.OR, true, false);
        workOrdersGridRequest.addFilter("eventtype", "PPM", "=", GridRequestFilter.JOINER.AND , false, true);
        workOrdersGridRequest.addFilter("wotype", "IS - ", "NOTCONTAINS");
        workOrdersGridRequest.sortBy("datecreated", "DESC");

        List<MyWorkOrder> workOrders = gridTools.convertGridResultToObject(MyWorkOrder.class, null,
                gridsService.executeQuery(authenticationTools.getR5InforContext(), workOrdersGridRequest));

        // get the mapping between the description of the object type and its type (e.g. "A", "P")
        GridRequest equipmentTypesGridRequest = new GridRequest("BSUCOD_HDR", 300);
        equipmentTypesGridRequest.addParam("param.entitycode", "OBTP");

        Map<String, String> equipmentTypeMap = gridTools.convertGridResultToMap("usercodedescription","systemcode",
                gridsService.executeQuery(authenticationTools.getR5InforContext(), equipmentTypesGridRequest));

        // map the equipment type in each work order from the description to the equipment type code
        workOrders.forEach(workOrder -> workOrder.setEquipmentType(equipmentTypeMap.get(workOrder.getEquipmentType())));

        return workOrders;
    }


    private String readUserDepartments() throws InforException {
        String userCode = authenticationTools.getInforContext().getCredentials().getUsername();
        EAMUser eamUser = userService.readUserSetup(authenticationTools.getInforContext(), userCode);
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
