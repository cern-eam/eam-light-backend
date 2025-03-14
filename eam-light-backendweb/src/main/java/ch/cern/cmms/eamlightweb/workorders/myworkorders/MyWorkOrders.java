package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.user.UserService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.grids.GridsService;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class MyWorkOrders {

    @Inject
    private AuthenticationTools authenticationTools;
    @Inject
    private InforClient inforClient;
    @Inject
    private UserService userService;

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
        String organization = authenticationTools.getR5InforContext().getOrganizationCode();
        // get the work orders that correspond to the events of the object (in the Events tab in Extended)
        GridRequest workOrdersGridRequest = new GridRequest("OSVEVT");
        workOrdersGridRequest.setUserFunctionName(typeToUserFunctionMap.getOrDefault(equipmentType, "OSOBJA"));
        workOrdersGridRequest.setRowCount(2000);
        workOrdersGridRequest.addParam("parameter.object", equipmentCode);

        // use star organization to get all work orders, independently of organization
        workOrdersGridRequest.addParam("parameter.objorganization",  organization);

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

}
