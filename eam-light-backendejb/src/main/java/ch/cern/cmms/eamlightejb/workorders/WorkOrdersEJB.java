package ch.cern.cmms.eamlightejb.workorders;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import ch.cern.cmms.eamlightejb.UserTools;
import ch.cern.eam.wshub.core.client.InforContext;
import org.jboss.logging.Logger.Level;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.eam.wshub.core.tools.InforException;

/**
 * Session Bean implementation class WorkOrdersEJB
 */
@Stateless
@LocalBean
public class WorkOrdersEJB {

	@PersistenceContext
	private EntityManager em;

	//
	// GET HISTORY OF ASSET,POSITION,SYSTEM
	//
	public List<EquipmentHistory> getObjectHistory(String objectCode) {
		return em.createNamedQuery(EquipmentHistory.GET_OBJHISTORY, EquipmentHistory.class).setParameter("objectCode", objectCode)
				.getResultList();
	}

	//
	// GET WORK ORDER EQUIPMENT
	//
	public List<WorkOrderEquipment> getWorkOrderEquipment(String wonumber) {
		return em.createNamedQuery(WorkOrderEquipment.GET_WO_EQUIPMENT, WorkOrderEquipment.class)
				.setParameter("wonumber", wonumber).getResultList();
	}

}
