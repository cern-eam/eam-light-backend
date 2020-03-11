package ch.cern.cmms.eamlightejb.MonitoringService;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Stateless
@LocalBean
public class MonitoringService {

    @Inject
    private InforClient inforClient;

    private String errorMessage = "ERROR ";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH-mm-ss", Locale.ENGLISH);
    private Map<String, String> responses;

    public Map<String, String> monitorEndpoints(String equipment, String number,
        String equipmentNumber, String workorderNumber,
        InforContext inforContext) {
        responses = new HashMap<>();

        try {
            String result = monitoringReadEquipment(equipment,
                inforContext).toString();
            responses.put("READEQUIPMENT", result);
        } catch (Exception e) {
            responses.put("READEQUIPMENT", errorMessage + e.getMessage());
        }
        try {
            String result = monitoringReadWorkorder(number,
                inforContext).toString();
            responses.put("READWORKORDER", result);
        } catch (Exception e) {
            responses.put("READWORKORDER", errorMessage + e.getMessage());
        }
        try {
            String result = monitoringUpdateEquipment(equipmentNumber,
                inforContext).toString();
            responses.put("UPDATEEQUIPMENT", result);
        } catch (Exception e) {
            responses.put("UPDATEEQUIPMENT", errorMessage + e.getMessage());
        }
        try {
            String result = monitoringUpdateWorkorder(workorderNumber,
                inforContext).toString();
            responses.put("UPDATEWORKORDER", result);
        } catch (Exception e) {
            responses.put("UPDATEWORKORDER", errorMessage + e.getMessage());
        }

        return responses;

    }


    public Equipment monitoringReadEquipment(String equipment, InforContext inforContext) throws Exception {
        return inforClient.getEquipmentFacadeService().readEquipment(inforContext,
            equipment);
    }

    public WorkOrder monitoringReadWorkorder(String number, InforContext inforContext) throws Exception {
        return inforClient.getWorkOrderService().readWorkOrder(inforContext, number);
    }

    public Equipment monitoringUpdateEquipment(String equipmentCode,
        InforContext inforContext)
        throws Exception {
        Equipment equipmentForUpdate = new Equipment();
        equipmentForUpdate.setCode(equipmentCode);
        equipmentForUpdate.setDescription("MONITORING ASSET / " + getCurrentDate());

        inforClient.getEquipmentFacadeService().updateEquipment(inforContext, equipmentForUpdate);
        return inforClient.getEquipmentFacadeService().readEquipment(inforContext, equipmentForUpdate.getCode());
    }

    public WorkOrder monitoringUpdateWorkorder(String workorderCode,
        InforContext inforContext) throws Exception {

        WorkOrder workOrderForUpdate = new WorkOrder();

        workOrderForUpdate.setNumber(workorderCode);
        workOrderForUpdate.setDescription("MONITORING WO / " + getCurrentDate());

        inforClient.getWorkOrderService().updateWorkOrder(inforContext, workOrderForUpdate);
        return inforClient.getWorkOrderService().readWorkOrder(inforContext, workOrderForUpdate.getNumber());
    }

    private String getCurrentDate() {
        return simpleDateFormat.format(new Date());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
