package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightejb.layout.ScreenInfo;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter.JOINER;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;
import com.sun.glass.ui.Screen;

import static ch.cern.eam.wshub.core.tools.DataTypeTools.isEmpty;
import static ch.cern.eam.wshub.core.tools.DataTypeTools.isNotEmpty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

@RequestScoped
public class ScreenService {

    @Inject
    private InforClient inforClient;
    private List<String> screens;

    @PostConstruct
    private void init() {
        screens = new LinkedList<>();
        screens.add("WSJOBS");
        screens.add("OSOBJA");
        screens.add("OSOBJP");
        screens.add("OSOBJS");
        screens.add("SSPART");
    }

    public Map<String, ScreenInfo> getScreens(InforContext context, String userGroup) throws InforException {

        Map<String, String> test = getTest(context);

        GridRequest gridRequestLayout = new GridRequest("BSMESP_HDR", 2000);
        gridRequestLayout.addFilter("usergroup", userGroup, "=", JOINER.AND);

        // TABS
        gridRequestLayout.addFilter("tab", null, "IS EMPTY", JOINER.AND);

        // SCREENS
        gridRequestLayout.addFilter("functionname", String.join(",", test.keySet()), "IN");

        Map<String, ScreenInfo> screens = inforClient.getTools().getGridTools().convertGridResultToMap(ScreenInfo.class,
                                            "functionname",
                                            null,
                                            inforClient.getGridsService().executeQuery(context, gridRequestLayout));

        screens.values().stream().forEach(screen -> screen.setParentScreen(test.get(screen.getScreenCode())));

        return screens;
    }

    public Map<String, String> getTest(InforContext context) throws InforException {

        GridRequest gridRequestLayout = new GridRequest("BSFUNC");
        screens.forEach(screen -> gridRequestLayout.addFilter("parentscreencode", screen, "=", JOINER.OR));

        Map<String, String> functions =  inforClient.getTools().getGridTools().convertGridResultToMap("screencode",
                "parentscreencode",
                inforClient.getGridsService().executeQuery(context, gridRequestLayout));

        screens.forEach(screen -> functions.put(screen, screen));

        return functions;
    }

}