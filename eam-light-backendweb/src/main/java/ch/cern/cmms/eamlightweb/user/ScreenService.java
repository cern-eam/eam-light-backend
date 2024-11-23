package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.user.entities.EamFunction;
import ch.cern.cmms.eamlightweb.user.entities.ScreenInfo;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.MenuEntryNode;
import ch.cern.eam.wshub.core.services.administration.entities.MenuRequestType;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter.JOINER;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ScreenService {

    @Inject
    private InforClient inforClient;
    private List<String> screens;
    public static final Map<String, Map<String, ScreenInfo>> screenCache = new ConcurrentHashMap<>();
    public static final Map<String, Map<String, List<Map<String, String>>>> reportsCache = new ConcurrentHashMap<>();
    public static final String EAM_REPORTS_MENU = "Lists & Reports"; // Possibly a better way of doing this

    @PostConstruct
    private void init() {
        screens = new LinkedList<>();
        screens.add("WSJOBS");
        screens.add("OSOBJA");
        screens.add("OSOBJP");
        screens.add("OSOBJS");
        screens.add("SSPART");
        screens.add("OSOBJL");
        screens.add("OSNCHD"); // NonConformities
    }

    public Map<String, ScreenInfo> getScreens(InforContext context, String userGroup) throws InforException {
        if (screenCache.containsKey(userGroup)) {
            return screenCache.get(userGroup);
        }

        Map<String, EamFunction> functions = getFunctions(context);

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
        screens.values().forEach(screen -> {
            screen.setParentScreen(functions.get(screen.getScreenCode()).getParentScreenCode());
            screen.setStartupAction(functions.get(screen.getScreenCode()).getStartUpModeDisplayCode());
            screen.setEntity(functions.get(screen.getScreenCode()).getSystemEntity());
        });

        screenCache.put(userGroup, screens);
        return screens;
    }

    public Map<String, EamFunction> getFunctions(InforContext context) throws InforException {
        GridRequest gridRequestLayout = new GridRequest("BSFUNC", 1000);
        gridRequestLayout.addFilter("parentscreencode", String.join(",", screens), "IN", JOINER.OR);
        gridRequestLayout.addFilter("screencode", String.join(",", screens), "IN");

        Map<String, EamFunction> functions =  inforClient.getTools().getGridTools().convertGridResultToMap(EamFunction.class,
                "screencode",
                null,
                inforClient.getGridsService().executeQuery(context, gridRequestLayout));

        GridRequest startUpActionTypesGridRequest = new GridRequest("BSUCOD_HDR", 300);
        startUpActionTypesGridRequest.addParam("param.entitycode", "FAQU");

        Map<String, String> startUpActionDescriptionToCode = inforClient.getTools().getGridTools().convertGridResultToMap(
                "usercodedescription",
                "systemcode",
                inforClient.getGridsService().executeQuery(context, startUpActionTypesGridRequest));

        functions.values().forEach(
                eamFunction -> eamFunction.setStartUpModeDisplayCode(startUpActionDescriptionToCode.get(eamFunction.getStartUpModeDisplayDescription()))
        );

        screens.forEach(screen -> functions.computeIfPresent(screen, (screenCode, eamFunction) -> {
            eamFunction.setParentScreenCode(screenCode);
            return eamFunction;
        }));

        return functions;
    }

    public Map<String, List<Map<String, String>>> getReports(InforContext context, String userGroup) throws InforException {

        if (!reportsCache.containsKey(userGroup)) {
            // Get all menus for the user group
            MenuEntryNode menus = inforClient.getUserGroupMenuService().getExtMenuHierarchyAsTree(
                    context,
                    userGroup,
                    MenuRequestType.EXCLUDE_PERMISSIONSAND_TABS
            );

            // Get all menu entries that are children of the EAM Reports menu
            List<MenuEntryNode> eamReportsMenuEntries = menus.getChildren().stream()
                    .flatMap(ScreenService::flattenMenuTree)
                    .filter(option -> option.getParentMenuEntry().getDescription().equals(EAM_REPORTS_MENU))
                    .collect(Collectors.toList());

            // Get function IDs of all children (direct or indirect) of the EAM Reports menu
            List<String> eamReportsMenuFunctionIds = eamReportsMenuEntries.stream()
                    .flatMap(ScreenService::flattenMenuTree)
                    .map(MenuEntryNode::getFunctionId)
                    .distinct()
                    .collect(Collectors.toList());

            // Get the metadata of the menu entries collected above (includes indirect children)
            GridRequest gridRequestLayout = new GridRequest("BSFUNC", 1000);
            gridRequestLayout.addFilter("screencode", String.join(",", eamReportsMenuFunctionIds), "IN");

            List<Map<String,String>> eamReportsMetadata = inforClient.getTools().getGridTools().convertGridResultToMapList(
                    inforClient.getGridsService().executeQuery(context, gridRequestLayout));

            // Get menu entries that have children (thus are sub-menus of the EAM reports menu) and create a map of the
            // sub-menu name to the list of children function IDs
            Map<String, List<String>> eamReportsSubMenusChildren = eamReportsMenuEntries.stream()
                    .filter(option -> option.getChildren().size() > 0)
                    .collect(Collectors.toMap(
                            MenuEntryNode::getDescription,
                            option -> option.getChildren().stream().map(MenuEntryNode::getFunctionId).collect(Collectors.toList())
                    ));

            // Create a map with the key being either the sub-menu name, or the name of the EAM reports menu in the case
            // where the report is its direct child, and the value being the list of the corresponding menu entries metadata
            Map<String, List<Map<String, String>>> eamReportsMenuMetadataMap = eamReportsMetadata.stream()
                    .collect(Collectors.groupingBy(
                            report -> eamReportsSubMenusChildren.entrySet().stream()
                                    .filter(entry -> entry.getValue().contains(report.get("screencode")))
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElse(EAM_REPORTS_MENU)
                    ));

            reportsCache.put(userGroup, eamReportsMenuMetadataMap);
        }

        return reportsCache.get(userGroup);
    }

    private static Stream<MenuEntryNode> flattenMenuTree(MenuEntryNode n) {
        return Stream.concat(Stream.of(n), n.getChildren().stream().flatMap(ScreenService::flattenMenuTree));
    }

}