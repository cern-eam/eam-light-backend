package ch.cern.cmms.watchers;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.userdefinedscreens.entities.UDTRow;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class WatchersService {

    @Inject
    private InforClient inforClient;

    private List<Map<String, String>> getAutocompleteOptions(String code, InforContext r5Context) throws InforException {
        GridRequest gridRequest = new GridRequest("BSUSER", GridRequest.GRIDTYPE.LIST, 10);

        String uppercasedCode = code.toUpperCase();

        gridRequest.addFilter("usercode", uppercasedCode, "BEGINS", GridRequestFilter.JOINER.OR);

        Arrays.stream(uppercasedCode.split(" ")).forEach(name -> {
            gridRequest.addFilter("description", " " + name, "CONTAINS",
                    GridRequestFilter.JOINER.OR, true, false);

            gridRequest.addFilter("description", name, "BEGINS",
                    GridRequestFilter.JOINER.AND, false, true);
        });

        gridRequest.sortBy("description");

        return GridTools.convertGridResultToMapList(inforClient.getGridsService()
                .executeQuery(r5Context, gridRequest));
    }

    private UDTRow buildUDTRow() {
        UDTRow values = new UDTRow();

        HashMap<String, String> strings = new HashMap<>();
        strings.put("WAT_ENTITY", "EVNT");
        strings.put("WAT_TABLE", "R5EVENTS");
        strings.put("WAT_ORG", "*");
        strings.put("WAT_LINK", "*");

        values.setStrings(strings);

        return values;
    }

    public List<Map<String, String>> getAutocompleteOptions(InforContext r5Context, String code) throws InforException {
        GridRequest gridRequest = new GridRequest("BSUSER", GridRequest.GRIDTYPE.LIST, 10);

        String uppercasedCode = code.toUpperCase();

        gridRequest.addFilter("usercode", uppercasedCode, "BEGINS", GridRequestFilter.JOINER.OR);

        Arrays.stream(uppercasedCode.split(" ")).forEach(name -> {
            gridRequest.addFilter("description", " " + name, "CONTAINS",
                    GridRequestFilter.JOINER.OR, true, false);

            gridRequest.addFilter("description", name, "BEGINS",
                    GridRequestFilter.JOINER.AND, false, true);
        });

        gridRequest.sortBy("description");

        return GridTools.convertGridResultToMapList(inforClient.getGridsService()
                .executeQuery(r5Context, gridRequest));
    }

    public List<EAMUser> getWatchersForWorkOrder(InforContext context, String woCode) throws InforException {
        UDTRow filters = new UDTRow();
        filters.addString("WAT_PK_VALUE", woCode);
        List<Map<String, Object>> rows = inforClient.getUserDefinedTableServices().readUserDefinedTableRows(
                context,
                "U5WATCHERSNOTIFY",
                filters,
                Collections.emptyList()
        );

        return rows.stream().map((row) -> {
            String usercode = (String) row.get("WAT_PERSON");
            try {
                return inforClient.getUserSetupService().readUserSetup(context, usercode);
            } catch (InforException ignored) {
                EAMUser unknownUser = new EAMUser();
                unknownUser.setUserCode(usercode);
                return unknownUser;
            }
        }).collect(Collectors.toList());
    }

    @Transactional
    public String addWatchersToWorkOrder(InforContext context, InforContext r5Context, String woCode, List<String> userNames)
                                          throws InforException {
        List<WatcherInfo> filteredUserNames = getFilteredWatcherInfo(woCode, userNames);

        List<UDTRow> rows = filteredUserNames.stream().map(watcher -> {
            UDTRow values = buildUDTRow();
            values.addString("WAT_PK_VALUE", woCode);
            values.addString("WAT_PERSON", watcher.getUserCode());
            return values;
        }).collect(Collectors.toList());

        return inforClient.getUserDefinedTableServices().createUserDefinedTableRows(
                context,
                "U5WATCHERSNOTIFY",
                rows
        );
    }

    @Transactional
    public int removeWatchersFromWorkOrder(InforContext context, String woCode, List<String> userNames) {
        int rowsChanged = 0;

        // Done transactionally, is performant
        for (String n : userNames) {
            UDTRow filters = new UDTRow();
            filters.addString("WAT_PERSON", n);
            filters.addString("WAT_PK_VALUE", woCode);
            try {
                rowsChanged += inforClient.getUserDefinedTableServices().deleteUserDefinedTableRows(
                        context,
                        "U5WATCHERSNOTIFY",
                        filters
                );
            }
            catch (InforException ignored) {}
        }
        return rowsChanged;
    }

    public List<WatcherInfo> getFilteredWatcherInfo(String woCode, List<String> userCodes) {
        if (userCodes.isEmpty()) {
            return new ArrayList<>();
        }

        return inforClient.getTools().getEntityManager()
                .createNamedQuery(WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS_LIST, WatcherInfo.class)
                .setParameter("evtCode", woCode)
                .setParameter("usrList", userCodes)
                .getResultList();
    }


    public List<WatcherInfo> getFilteredWatcherInfo(String woCode, String hint) {
        if (hint == null) {
            return new ArrayList<>();
        }
        return inforClient.getTools().getEntityManager()
                .createNamedQuery(WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS_HINT, WatcherInfo.class)
                .setParameter("evtCode", woCode)
                .setParameter("hint", hint.trim().toUpperCase() + "%")
                .setMaxResults(30)
                .getResultList();
    }
}
