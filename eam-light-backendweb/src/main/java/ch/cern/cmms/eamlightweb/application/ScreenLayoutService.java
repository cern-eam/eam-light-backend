package ch.cern.cmms.eamlightweb.application;

import ch.cern.cmms.eamlightejb.layout.ElementInfo;
import ch.cern.cmms.eamlightejb.layout.ScreenLayout;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;
import static ch.cern.eam.wshub.core.tools.GridTools.convertGridResultToMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@RequestScoped
public class ScreenLayoutService {

    @Inject
    private InforClient inforClient;

    public ScreenLayout getScreenLayout(InforContext context, String systemFunction, String userFunction, List<String> tabs, String userGroup) throws InforException {
        ScreenLayout screenLayout = new ScreenLayout();

        List<Runnable> runnables = new LinkedList<>();
        // Add the record view
        runnables.add(() -> screenLayout.setFields(getLayout(context, userGroup, systemFunction, userFunction)));
        // Add other tabs
        for (String tab : tabs) {
            runnables.add(() -> screenLayout.getTabs().put(tab, getLayout(context, userGroup, systemFunction + "_" + tab, userFunction + "_" + tab)));
        }

        inforClient.getTools().processRunnables(runnables);
        return  screenLayout;
    }


    private Map<String, ElementInfo> getLayout(InforContext context, String userGroup, String systemFunction, String userFunction) {
        try {
            //
            // LAYOUT
            //
            GridRequest gridRequestLayout = new GridRequest( "EULLAY");
            gridRequestLayout.setRowCount(1000);
            gridRequestLayout.setUseNative(false);
            gridRequestLayout.getGridRequestFilters().add(new GridRequestFilter("plo_usergroup", userGroup, "=", GridRequestFilter.JOINER.AND));
            gridRequestLayout.getGridRequestFilters().add(new GridRequestFilter("plo_pagename", userFunction, "=", GridRequestFilter.JOINER.AND));
            gridRequestLayout.getGridRequestFilters().add(new GridRequestFilter("pld_pagename", systemFunction, "=", GridRequestFilter.JOINER.AND));

            List<ElementInfo> elements = inforClient.getTools().getGridTools().converGridResultToObject(ElementInfo.class, null, inforClient.getGridsService().executeQuery(context, gridRequestLayout));
            elements.stream().filter(element -> element.getXpath() != null).forEach(element -> element.setXpath("EAMID_" + element.getXpath().replace("\\", "_")));
            //
            // LABELS
            //
            GridRequest gridRequestLabels = new GridRequest( "BSGRID_GFD");
            gridRequestLabels.setRowCount(1000);
            gridRequestLabels.setUseNative(false);
            gridRequestLabels.getParams().put("param.gridid", inforClient.getGridsService().getGridMetadataInfor(context, userFunction).getGridCode());

            Map<String, String> labels = convertGridResultToMap("9514", "803", inforClient.getGridsService().executeQuery(context, gridRequestLabels));
            elements.forEach(element -> element.setText(labels.get(element.getElementId())));

            return elements.stream().collect(Collectors.toMap(ElementInfo::getElementId, element -> element));
        } catch (Exception e) {
            inforClient.getTools().log(Level.SEVERE, e.getMessage());
            return new HashMap<>();
        }
    }

}
