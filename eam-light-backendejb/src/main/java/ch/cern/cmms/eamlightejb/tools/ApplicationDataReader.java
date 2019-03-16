package ch.cern.cmms.eamlightejb.tools;

import ch.cern.cmms.eamlightejb.tools.entities.Property;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ApplicationDataReader {

	@PersistenceContext
	private EntityManager entityManager;
	
	public Map<String, String> getProperties(String application) {
		Map<String, String> propertiesMap = new HashMap<>();
		try {
			// Fetch properties from DB
			List<Property> propertiesList = entityManager.createNamedQuery(Property.GET_PROPERTIES, Property.class).setParameter("application", application).getResultList();
			// Transform to map and return
			propertiesList.stream().forEach(property -> propertiesMap.put(property.getCode().trim(), property.getValue().trim()));
		} catch (Exception e) {
			//
		} finally {
			return propertiesMap;
		}
	}
	
}
