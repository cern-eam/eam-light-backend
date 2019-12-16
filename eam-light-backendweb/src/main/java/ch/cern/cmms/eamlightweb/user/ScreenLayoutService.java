package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.user.entities.ElementInfo;
import ch.cern.cmms.eamlightweb.user.entities.ScreenLayout;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.GridTools.convertGridResultToMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class ScreenLayoutService {

    @Inject
    private InforClient inforClient;
    public static final Map<String, ScreenLayout> screenLayoutCache = new ConcurrentHashMap<>();
    public static final Map<String, Map<String, String>> screenLayoutLabelCache = new ConcurrentHashMap<>();

    public ScreenLayout getScreenLayout(InforContext context, String systemFunction, String userFunction, List<String> tabs, String userGroup) throws InforException {
        // Check if value not already in the cache
        String layoutCacheKey = userGroup + "_" + userFunction;
        if (screenLayoutCache.containsKey(layoutCacheKey)) {
            return screenLayoutCache.get(layoutCacheKey);
        }

        ScreenLayout screenLayout = new ScreenLayout();
        // Add the record view
        screenLayout.setFields(getTabLayout(context, userGroup, systemFunction, userFunction));
        // Add other tabs
        for (String tab : tabs) {
            screenLayout.getTabs().put(tab, getTabLayout(context, userGroup, systemFunction + "_" + tab, userFunction + "_" + tab));
        }

        // Get layout labels first
        Map<String, String> labels = getTabLayoutLabels(context, userFunction);
        // For all fields for the record view the bot_fld1 matches the upper-cased elementId
        screenLayout.getFields().values().forEach(elementInfo -> elementInfo.setText(labels.get(elementInfo.getElementId().toUpperCase())));
        // For all tab fields bot_fld1 matches upper-cased tab code + '_' + elementId
        screenLayout.getTabs().keySet().forEach(tab -> {
            screenLayout.getTabs().get(tab).values().forEach(elementInfo -> elementInfo.setText(labels.get(tab + "_" + elementInfo.getElementId().toUpperCase())));
        });

        // Cache it before returning
        screenLayoutCache.put(layoutCacheKey, screenLayout);

        return  screenLayout;
    }


    private Map<String, ElementInfo> getTabLayout(InforContext context, String userGroup, String systemFunction, String userFunction) throws InforException {
        GridRequest gridRequestLayout = new GridRequest( "EULLAY");
        gridRequestLayout.setRowCount(2000);
        gridRequestLayout.setUseNative(false);
        gridRequestLayout.addFilter("plo_usergroup", userGroup, "=", GridRequestFilter.JOINER.AND);
        gridRequestLayout.addFilter("plo_pagename", userFunction, "=", GridRequestFilter.JOINER.AND);
        gridRequestLayout.addFilter("pld_pagename", systemFunction, "=", GridRequestFilter.JOINER.AND);

        List<ElementInfo> elements = inforClient.getTools().getGridTools().convertGridResultToObject(ElementInfo.class, null, inforClient.getGridsService().executeQuery(context, gridRequestLayout));
        elements.stream().filter(element -> element.getXpath() != null).forEach(element -> element.setXpath("EAMID_" + element.getXpath().replace("\\", "_")));
        return elements.stream().collect(Collectors.toMap(ElementInfo::getElementId, element -> element));
    }

    /**
     * Reads all boiler texts (labels) for given function
     *
     * @param context
     * @param userFunction
     * @throws InforException
     */
    private Map<String, String> getTabLayoutLabels(InforContext context, String userFunction) throws InforException {
        // Check if value already not present in the cache
        if (screenLayoutLabelCache.containsKey(userFunction)) {
            return screenLayoutLabelCache.get(userFunction);
        }

        // Fetch boiler texts for given screen
        GridRequest gridRequestLabels = new GridRequest( "ASOBOT");
        gridRequestLabels.setRowCount(3000);
        gridRequestLabels.setUseNative(false);
        gridRequestLabels.addFilter("bot_function", userFunction, "EQUALS");
        Map<String, String> labels = convertGridResultToMap("bot_fld1", "bot_text", inforClient.getGridsService().executeQuery(context, gridRequestLabels));

        // Save to cache and return
        screenLayoutLabelCache.put(userFunction, labels);
        return labels;
    }

}
