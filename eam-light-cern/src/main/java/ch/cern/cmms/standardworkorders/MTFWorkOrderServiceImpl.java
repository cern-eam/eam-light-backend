package ch.cern.cmms.standardworkorders;

import ch.cern.eam.wshub.core.client.InforClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MTFWorkOrderServiceImpl implements MTFWorkOrderService {

    @Inject
    InforClient inforClient;

    @Override
    public MTFWorkOrder getEquipmentStandardWOMaxStep(String eqCode, String swo) {
        MTFWorkOrderImpl results = inforClient.getTools().getEntityManager()
                .createNamedQuery(MTFWorkOrderImpl.GET_EQUIPMENT_SWO_MAX_STEP, MTFWorkOrderImpl.class)
                .setParameter("eqCode", eqCode)
                .setParameter("swo", swo).getSingleResult();

        return results;
    }
}
