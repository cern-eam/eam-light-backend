package ch.cern.cmms.eamlightejb.MonitoringService;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
import ch.cern.eam.wshub.core.tools.InforException;
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

    private String EQUIPMENT_CODE = "TEST_MONITORING";
    private String WORKORDER_CODE = "25316908";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH-mm-ss", Locale.ENGLISH);

    public Map<String, String> monitoring(String equipment, String number, InforContext inforContext) {
        Map<String, String> responses = new HashMap<>();
        Equipment equipmentForUpdate = new Equipment();
        WorkOrder workOrderForUpdate = new WorkOrder();

        try {
            String result = inforClient.getEquipmentFacadeService().readEquipment(inforContext,
                equipment).toString();
            responses.put("READEQUIPMENT", result);
        } catch (InforException e) {
            responses.put("READEQUIPMENT", "ERROR " + e.getMessage());
        }
        try {
            String result = inforClient.getWorkOrderService().readWorkOrder(inforContext, number).toString();
            responses.put("READWORKORDER", result);
        } catch (Exception e) {
            responses.put("READWORKORDER", "ERROR " + e.getMessage());
        }
        try {
            equipmentForUpdate.setCode(EQUIPMENT_CODE);
            equipmentForUpdate.setDescription("MONITORING ASSET / " + getCurrentDate());

            inforClient.getEquipmentFacadeService().updateEquipment(inforContext, equipmentForUpdate);
            String result =
                inforClient.getEquipmentFacadeService().readEquipment(inforContext, equipmentForUpdate.getCode())
                    .toString();
            responses.put("UPDATEEQUIPMENT", result);
        } catch (Exception e) {
            responses.put("UPDATEEQUIPMENT", e.getMessage());
        }
        try {
            workOrderForUpdate.setNumber(WORKORDER_CODE);
            workOrderForUpdate.setDescription("MONITORING WO / " + getCurrentDate());

            inforClient.getWorkOrderService().updateWorkOrder(inforContext, workOrderForUpdate);
            String result =
                inforClient.getWorkOrderService().readWorkOrder(inforContext, workOrderForUpdate.getNumber())
                    .toString();
            responses.put("UPDATEWORKORDER", result);
        } catch (Exception e) {
            responses.put("UPDATEWORKORDER", "ERROR " + e.getMessage());
        }
        return responses;

    }

    private String getCurrentDate() {
        return simpleDateFormat.format(new Date());
    }

}
