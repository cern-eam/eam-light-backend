package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.user.entities.ScreenInfo;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter.JOINER;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ScreenService {

    @Inject
    private InforClient inforClient;
    private List<String> screens;
    public static final Map<String, Map<String, ScreenInfo>> screenCache = new ConcurrentHashMap<>();

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
        if (screenCache.containsKey(userGroup)) {
            return screenCache.get(userGroup);
        }

        Map<String, String> functions = getFunctions(context);

        GridRequest gridRequestLayout = new GridRequest("BSMESP_HDR", 2000);
        gridRequestLayout.addFilter("usergroup", userGroup, "=", JOINER.AND);

        // Record view filter
        gridRequestLayout.addFilter("tab", null, "IS EMPTY", JOINER.AND);

        // Tabs filter
        gridRequestLayout.addFilter("functionname", String.join(",", functions.keySet()), "IN");

        Map<String, ScreenInfo> screens = inforClient.getTools().getGridTools().convertGridResultToMap(ScreenInfo.class,
                                            "functionname",
                                            null,
                                            inforClient.getGridsService().executeQuery(context, gridRequestLayout));

        // Populate the parent screen for fetched screens
        screens.values().stream().forEach(screen -> screen.setParentScreen(functions.get(screen.getScreenCode())));

        screenCache.put(userGroup, screens);
        return screens;
    }

    public Map<String, String> getFunctions(InforContext context) throws InforException {

        GridRequest gridRequestLayout = new GridRequest("BSFUNC");
        screens.forEach(screen -> gridRequestLayout.addFilter("parentscreencode", screen, "=", JOINER.OR));

        Map<String, String> functions =  inforClient.getTools().getGridTools().convertGridResultToMap("screencode",
                "parentscreencode",
                inforClient.getGridsService().executeQuery(context, gridRequestLayout));

        screens.forEach(screen -> functions.put(screen, screen));

        return functions;
    }

}