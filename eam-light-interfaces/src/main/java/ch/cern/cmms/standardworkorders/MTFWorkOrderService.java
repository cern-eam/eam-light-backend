package ch.cern.cmms.standardworkorders;

public interface MTFWorkOrderService {
    MTFWorkOrder getEquipmentStandardWOMaxStep(String eqCode, String swo);
}
