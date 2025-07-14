package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightejb.cache.CacheUtils;
import ch.cern.cmms.eamlightejb.cache.Cacheable;
import ch.cern.cmms.eamlightweb.user.entities.EamFunction;
import ch.cern.cmms.eamlightweb.user.entities.ScreenInfo;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.MenuEntryNode;
import ch.cern.eam.wshub.core.services.administration.entities.MenuRequestType;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter.JOINER;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;
import ch.cern.eam.wshub.core.tools.Tools;
import com.github.benmanes.caffeine.cache.Cache;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ScreenService implements Cacheable {

    private static final String EAM_REPORTS_MENU = "Lists & Reports"; // Possibly a better way of doing this

    private final Cache<String, Map<String, ScreenInfo>> screenCache = CacheUtils.buildDefaultCache();
    private final Cache<String, Map<String, List<Map<String, String>>>> reportsCache = CacheUtils.buildDefaultCache();

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
        screens.add("OSOBJL");
        screens.add("OSNCHD"); // NonConformities
    }

    @Override
    public void clearCache() {
        screenCache.invalidateAll();
        reportsCache.invalidateAll();
    }

    @Override
    public void setExpiresAfter(long l, TimeUnit timeUnit) {
        CacheUtils.updateCacheTimeout(screenCache, l, timeUnit);
        CacheUtils.updateCacheTimeout(reportsCache, l, timeUnit);
    }

    public Map<String, ScreenInfo> getScreens(InforContext context, String userGroup) throws InforException {
        try {
            String screenCacheKey = Tools.getCacheKeyWithLang(context, userGroup);
            return screenCache.get(screenCacheKey, key -> loadScreens(context, userGroup));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InforException) {
                throw (InforException) e.getCause();
            }
            throw new InforException("Failed to read screens", null, null);
        }
    }

    private Map<String, ScreenInfo> loadScreens(InforContext context, String userGroup) {
        try {
            Map<String, EamFunction> functions = getFunctions(context);

            GridRequest gridRequestLayout = new GridRequest("BSMESP_HDR", 2000);
            gridRequestLayout.addFilter("usergroup", userGroup, "=", JOINER.AND);

            // Record view filter
            gridRequestLayout.addFilter("tab", null, "IS EMPTY", JOINER.AND);

            // Tabs filter
            gridRequestLayout.addFilter("functionname", String.join(",", functions.keySet()), "IN");

            Map<String, ScreenInfo> screens = GridTools.convertGridResultToMap(ScreenInfo.class,
                    "functionname",
                    null,
                    inforClient.getGridsService().executeQuery(context, gridRequestLayout));

            // Populate the parent screen for fetched screens
            screens.values().forEach(screen -> {
                screen.setParentScreen(functions.get(screen.getScreenCode()).getParentScreenCode());
                screen.setStartupAction(functions.get(screen.getScreenCode()).getStartUpModeDisplayCode());
                screen.setEntity(functions.get(screen.getScreenCode()).getSystemEntity());
            });

            return screens;
        } catch (InforException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, EamFunction> getFunctions(InforContext context) throws InforException {
        GridRequest gridRequestLayout = new GridRequest("BSFUNC", 1000);
        gridRequestLayout.addFilter("parentscreencode", String.join(",", screens), "IN", JOINER.OR);
        gridRequestLayout.addFilter("screencode", String.join(",", screens), "IN");

        Map<String, EamFunction> functions = GridTools.convertGridResultToMap(EamFunction.class,
                "screencode",
                null,
                inforClient.getGridsService().executeQuery(context, gridRequestLayout));

        GridRequest startUpActionTypesGridRequest = new GridRequest("BSUCOD_HDR", 300);
        startUpActionTypesGridRequest.addParam("param.entitycode", "FAQU");

        Map<String, String> startUpActionDescriptionToCode = GridTools.convertGridResultToMap(
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
        try {
            String reportsCacheKey = Tools.getCacheKey(context, userGroup);
            return reportsCache.get(reportsCacheKey, key -> loadReports(context, userGroup));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InforException) {
                throw (InforException) e.getCause();
            }
            throw new InforException("Failed to read screens", null, null);
        }
    }

    private Map<String, List<Map<String, String>>> loadReports(InforContext context, String userGroup) {
        try {
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

            if (eamReportsMenuFunctionIds.isEmpty()) {
                return new HashMap<>();
            }

            // Get the metadata of the menu entries collected above (includes indirect children)
            GridRequest gridRequestLayout = new GridRequest("BSFUNC", 1000);
            gridRequestLayout.addFilter("screencode", String.join(",", eamReportsMenuFunctionIds), "IN");

            List<Map<String, String>> eamReportsMetadata = GridTools.convertGridResultToMapList(
                    inforClient.getGridsService().executeQuery(context, gridRequestLayout));

            // Get menu entries that have children (thus are submenus of the EAM reports menu) and create a map of the
            // submenu name to the list of children function IDs
            Map<String, List<String>> eamReportsSubMenusChildren = eamReportsMenuEntries.stream()
                    .filter(option -> !option.getChildren().isEmpty())
                    .collect(Collectors.toMap(
                            MenuEntryNode::getDescription,
                            option -> option.getChildren().stream().map(MenuEntryNode::getFunctionId).collect(Collectors.toList())
                    ));

            // Create a map with the key being either the submenu name, or the name of the EAM reports menu in the case
            // where the report is its direct child, and the value being the list of the corresponding menu entries metadata
            return eamReportsMetadata.stream()
                    .collect(Collectors.groupingBy(
                            report -> eamReportsSubMenusChildren.entrySet().stream()
                                    .filter(entry -> entry.getValue().contains(report.get("screencode")))
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElse(EAM_REPORTS_MENU)
                    ));

        } catch (InforException e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<MenuEntryNode> flattenMenuTree(MenuEntryNode n) {
        return Stream.concat(Stream.of(n), n.getChildren().stream().flatMap(ScreenService::flattenMenuTree));
    }

}