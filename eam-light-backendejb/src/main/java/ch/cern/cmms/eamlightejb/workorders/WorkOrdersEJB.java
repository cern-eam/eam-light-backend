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

	@Inject
	private LoggingService logger;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@EJB
	private UserTools userTools;
	/**
	 * Default constructor.
	 */
	public WorkOrdersEJB() {
		// TODO Auto-generated constructor stub
	}

	//
	//
	//
	public List<MyWorkOrder> getWorkOrders(String code, int maxResults) {
		return em.createNamedQuery(MyWorkOrder.GET_WOS, MyWorkOrder.class).setParameter("codeParam", code)
				.setMaxResults(maxResults).getResultList();
	}

	//
	//
	//
	public Person getPerson(String code) {
		try {
			return em.createNamedQuery(Person.PERSON_GETPERSON, Person.class).setParameter("codeParam", code)
					.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<Person> getPersons(String code) {
		return em.createNamedQuery(Person.PERSON_GETPERSONS, Person.class).setParameter("codeParam", code)
				.setMaxResults(10).getResultList();
	}

	public List<Type> getTypes(String query, String code) {
		List<Type> types = em.createNamedQuery(query, Type.class).setParameter("codeParam", code).getResultList();
		return types;
	}

	public List<MyWorkOrder> getWOs(InforContext inforContext) throws InforException {
		String username = inforContext.getCredentials().getUsername();
		List<MyWorkOrder> types = em.createNamedQuery(MyWorkOrder.GET_MY_OPEN_WOS, MyWorkOrder.class)
				.setParameter("user", username).getResultList();
		return types;
	}

	public List<MyWorkOrder> getTeamWOs(InforContext inforContext) {
		List<String> departmentCodes = em
				.createNativeQuery("SELECT UDE_MRC FROM U5USERDEPARTMENTS WHERE UDE_CODE = :userId and UDE_NOTUSED <> '+'")
				.setParameter("userId", inforContext.getCredentials().getUsername())
				.getResultList();

		if (!departmentCodes.isEmpty()) {
			List<MyWorkOrder> types = em.createNamedQuery(MyWorkOrder.GET_MY_TEAMS_WOS, MyWorkOrder.class)
					.setParameter("user", inforContext.getCredentials().getUsername())
					.setParameter("departments", departmentCodes)
					.getResultList();
			return types;
		}
		return new ArrayList<>();
	}


	//
	// GET WORK ORDERS ASSOCIATED TO AND OBJECT (ASSET,POSITION,SYSTEM)
	//
	public List<MyWorkOrder> getObjectWorkOrders(String objectCode) {
		return em.createNamedQuery(MyWorkOrder.GET_OBJWOS, MyWorkOrder.class).setParameter("objectCode", objectCode)
				.getResultList();
	}

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

	//
	// GET WORK ORDER STATUS and TYPE
	//
	private static String SELECT_EQ_CLASS = " select obj_class from R5EVENTS left join R5OBJECTS on evt_object = obj_code where evt_code = :woid ";

	public String getEquipmentClass(String woid) throws InforException {
		Object[] results = null;
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			results = (Object[]) em.createNativeQuery(SELECT_EQ_CLASS).setParameter("woid", woid).getSingleResult();
		} catch (Exception exception) {
			logger.log(Level.ERROR, exception.getMessage());
		} finally {
			em.close();
		}

		return (results != null && results.length > 0 && results[0] != null) ? results[0].toString() : null;
	}

}
