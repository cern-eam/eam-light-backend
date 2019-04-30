package ch.cern.cmms.eamlightejb.layout;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@LocalBean
public class LayoutBean {

	@PersistenceContext
	private EntityManager entityManager;

	public Map<String, ElementInfo> getTabElements(String masterPageName, String pageName, String tabName,
			String entity, String userGroup) {

		List<ElementInfo> elements = entityManager.createNamedQuery(ElementInfo.GET_TAB_FIELDS, ElementInfo.class)
				.setParameter("masterPageName", masterPageName).setParameter("pageName", pageName)
				.setParameter("userGroup", userGroup).setParameter("entity", entity).setParameter("tabName", tabName)
				.getResultList();
		return initElementsMap(elements);
	}

	public Map<String, ElementInfo> getTabElements(String masterPageName, String pageName, String tabName,
			String entity, String userGroup, String language) {

		List<ElementInfo> elements = entityManager.createNamedQuery(ElementInfo.GET_TAB_FIELDS, ElementInfo.class)
				.setParameter("masterPageName", masterPageName).setParameter("pageName", pageName)
				.setParameter("userGroup", userGroup).setParameter("entity", entity).setParameter("tabName", tabName)
				.getResultList();
		return initElementsMap(elements);
	}

	public Map<String, ElementInfo> getRecordViewElements(String masterPageName, String pageName, String entity,
			String userGroup) {
		List<ElementInfo> elements = entityManager
				.createNamedQuery(ElementInfo.GET_RECORD_VIEW_FIELDS, ElementInfo.class)
				.setParameter("masterPageName", masterPageName).setParameter("pageName", pageName)
				.setParameter("userGroup", userGroup).setParameter("entity", entity)
				.getResultList();
		return initElementsMap(elements);
	}

	public Map<String, ElementInfo> getUserDefinedScreenElements(String masterPageName, String pageName,
			String userGroup) {
		List<ElementInfo> elements = entityManager.createNamedQuery(ElementInfo.GET_UDS_FIELDS, ElementInfo.class)
				.setParameter("masterPageName", masterPageName).setParameter("pageName", pageName)
				.setParameter("userGroup", userGroup).getResultList();
		return initElementsMap(elements);
	}

	public Map<String, ElementInfo> getRecordViewElements(String masterPageName, String pageName, String entity,
			String userGroup, String language) {
		List<ElementInfo> elements = entityManager
				.createNamedQuery(ElementInfo.GET_RECORD_VIEW_FIELDS, ElementInfo.class)
				.setParameter("masterPageName", masterPageName).setParameter("pageName", pageName)
				.setParameter("userGroup", userGroup).setParameter("entity", entity)
				.getResultList();
		return initElementsMap(elements);
	}

	public Map<String, ElementInfo> getCustomFieldElements(String classCode, String entity, String language) {
		List<ElementInfo> elements = entityManager.createNamedQuery(ElementInfo.GET_CUSTOMFIELDS, ElementInfo.class)
				.setParameter("classCode", classCode).setParameter("entity", entity)
				.getResultList();
		return initElementsMap(elements);
	}

	/**
	 * Initialize map with page elements and it's ids
	 * 
	 * @param elements
	 * @return
	 */
	private Map<String, ElementInfo> initElementsMap(List<ElementInfo> elements) {
		HashMap<String, ElementInfo> elementsMap = new HashMap<String, ElementInfo>();
		elements.stream().forEach(element -> {
			elementsMap.put(element.getElementId(), element);
		});
		return elementsMap;
	}

	public Map<String, ScreenInfo> getUserScreens(List<String> functionCodes, String userCode) {
		// Fetch screen
		List<ScreenInfo> screenList = entityManager.createNamedQuery(ScreenInfo.FETCH_USER_SCREENS, ScreenInfo.class)
				.setParameter("functions", functionCodes).setParameter("user", userCode).getResultList();
		// Convert list to map
		Map<String, ScreenInfo> screenMap = new HashMap<>();
		for (ScreenInfo screenInfo : screenList) {
			if (screenInfo.isReadAllowed()) {
				screenMap.put(screenInfo.getScreenCode(), screenInfo);
			}
		}
		return screenMap;
	}

	/**
	 * Gets the information for the tabs defined in a screen
	 * 
	 * @param function
	 *            Function or screen code
	 * @param tabnames
	 *            The list of the tabs to retrieve information
	 * @param usergroup
	 *            The user group of the user in session
	 * @return A map with a Key of a tab code, and the element being a TabInfo,
	 *         containing all the information of the tab
	 */
	public Map<String, TabInfo> getScreenTabInfo(String entity, String systemFunction, String userFunction,
			List<String> tabnames, String usergroup, String language) {
		if (tabnames == null || tabnames.isEmpty())
			return new HashMap<>();
		// Gets the result in a list first
		List<TabInfo> tabs = entityManager.createNamedQuery(TabInfo.FETCH_TAB_INFO, TabInfo.class)
				.setParameter("tabnames", tabnames).setParameter("function", userFunction)
				.setParameter("usergroup", usergroup).getResultList();
		//
		for (TabInfo tab : tabs) {
			tab.setFields(getTabElements(systemFunction, userFunction, tab.getTab(), entity, usergroup, language));
		}
		// Transform the list to a map
		HashMap<String, TabInfo> tabsMap = new HashMap<String, TabInfo>();
		tabs.stream().forEach(tab -> {
			tabsMap.put(tab.getTab(), tab);
		});
		// Retuurn the map
		return tabsMap;
	}

	public ScreenInfo getUserDefaultScreen(String functionCode, String userCode) {
		try {
			ScreenInfo screenInfo = entityManager
					.createNamedQuery(ScreenInfo.FETCH_USER_DEFAULT_SCREEN, ScreenInfo.class)
					.setParameter("function", functionCode).setParameter("user", userCode).setFirstResult(0)
					.setMaxResults(1).getSingleResult();
			if (screenInfo.isReadAllowed()) {
				return screenInfo;
			} else {
				return null;
			}
		} catch (javax.persistence.NoResultException noResultException) {
			return null;
		}
	}

}
