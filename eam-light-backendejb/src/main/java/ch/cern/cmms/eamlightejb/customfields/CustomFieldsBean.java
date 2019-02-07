package ch.cern.cmms.eamlightejb.customfields;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
@LocalBean
public class CustomFieldsBean {

	@PersistenceContext
	private EntityManager em;

	public List<CustomFieldLookupValue> getCFEntities(String propentity, String code) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.ENTITY_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("propEntity", propentity).setParameter("code", code).setMaxResults(10).getResultList();
		return types;
	}

	public CustomFieldLookupValue getCFEntity(String propentity, String code) {
		try {
			return em.createNamedQuery(CustomFieldLookupValue.ENTITY_LOOKUP_VALUES, CustomFieldLookupValue.class)
					.setParameter("propEntity", propentity).setParameter("code", code).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<CustomFieldLookupValue> getCFCodeDescValues(String property, String lookupType, String entity,
			String classCode) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.CODEDESC_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("property", property).setParameter("lookupType", lookupType)
				.setParameter("entity", entity).setParameter("class", classCode).setParameter("lang", "EN")
				.getResultList();
		return types;
	}

	public List<CustomFieldLookupValue> getCFCodeDescValues(String property, String lookupType, String entity,
			String classCode, String language) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.CODEDESC_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("property", property).setParameter("lookupType", lookupType)
				.setParameter("entity", entity).setParameter("class", classCode).setParameter("lang", language)
				.getResultList();
		return types;
	}

	public List<CustomFieldLookupValue> getCFDateValues(String property, String lookupType, String entity,
			String classCode) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.DATE_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("property", property).setParameter("lookupType", lookupType)
				.setParameter("entity", entity).setParameter("class", classCode).getResultList();
		return types;
	}

	public List<CustomFieldLookupValue> getCFDateTimeValues(String property, String lookupType, String entity,
			String classCode) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.DATETIME_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("property", property).setParameter("lookupType", lookupType)
				.setParameter("entity", entity).setParameter("class", classCode).getResultList();
		return types;
	}

	public List<CustomFieldLookupValue> getCFCharValues(String property, String lookupType, String entity,
			String classCode) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.CHAR_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("property", property).setParameter("lookupType", lookupType)
				.setParameter("entity", entity).setParameter("class", classCode).getResultList();
		return types;
	}

	public List<CustomFieldLookupValue> getCFNumValues(String property, String lookupType, String entity,
			String classCode) {
		List<CustomFieldLookupValue> types = em
				.createNamedQuery(CustomFieldLookupValue.NUMBER_LOOKUP_VALUES, CustomFieldLookupValue.class)
				.setParameter("property", property).setParameter("lookupType", lookupType)
				.setParameter("entity", entity).setParameter("class", classCode).getResultList();
		return types;
	}
}
