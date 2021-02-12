package ch.cern.cmms.eamlightweb.tools;

import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.material.entities.Part;
import ch.cern.eam.wshub.core.services.workorders.entities.MeterReading;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;

public class OrganizationTools {
    public static void assumeEquipmentMonoOrg(WorkOrder workOrder) {
        workOrder.setEquipmentOrganization("*");
        workOrder.setDepartmentOrganization("*");

        if (workOrder.getLocationCode() != null && !workOrder.getLocationCode().isEmpty()) workOrder.setLocationOrganization("*");
        else workOrder.setLocationOrganization(null);

        if (workOrder.getClassCode() != null && !workOrder.getClassCode().isEmpty()) workOrder.setClassOrganization("*");
        else workOrder.setClassOrganization(null);
    }

    public static void assumeEquipmentMonoOrg(MeterReading meterReading) {
        meterReading.setEquipmentOrganization("*");
    }

    public static void assumeMonoOrg(InforContext context) {
        context.setOrganizationCode("*");
    }

    public static void assumeMonoOrg(Part part) {
        part.setOrganization("*");
    }
}
