package ch.cern.cmms.eamlightejb.MonitoringService;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.equipment.entities.Equipment;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Stateless
@LocalBean
public class MonitoringService {

    @Inject
    private InforClient inforClient;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH-mm-ss", Locale.ENGLISH);

    public Equipment monitoringReadEquipment(String equipment, InforContext inforContext) throws Exception {
        return inforClient.getEquipmentFacadeService().readEquipment(inforContext,
            equipment);
    }

    public WorkOrder monitoringReadWorkorder(String number, InforContext inforContext) throws Exception {
        return inforClient.getWorkOrderService().readWorkOrder(inforContext, number);
    }

    public Equipment monitoringUpdateEquipment(Equipment equipmentForUpdate, String EQUIPMENT_CODE,
        InforContext inforContext)
        throws Exception {

        equipmentForUpdate.setCode(EQUIPMENT_CODE);
        equipmentForUpdate.setDescription("MONITORING ASSET / " + getCurrentDate());

        inforClient.getEquipmentFacadeService().updateEquipment(inforContext, equipmentForUpdate);
        return inforClient.getEquipmentFacadeService().readEquipment(inforContext, equipmentForUpdate.getCode());
    }

    public WorkOrder monitoringUpdateWorkorder(WorkOrder workOrderForUpdate, String WORKORDER_CODE,
        InforContext inforContext)
        throws Exception {
        workOrderForUpdate.setNumber(WORKORDER_CODE);
        workOrderForUpdate.setDescription("MONITORING WO / " + getCurrentDate());

        inforClient.getWorkOrderService().updateWorkOrder(inforContext, workOrderForUpdate);
        return inforClient.getWorkOrderService().readWorkOrder(inforContext, workOrderForUpdate.getNumber());
    }

    private String getCurrentDate() {
        return simpleDateFormat.format(new Date());
    }

}
