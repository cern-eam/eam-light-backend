package ch.cern.cmms.eamlightejb.watchers;

import ch.cern.cmms.index.WatcherInfo;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.administration.entities.EAMUser;
import ch.cern.eam.wshub.core.services.userdefinedscreens.entities.UDTRow;
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
    public String addWatchersToWorkOrder(InforContext context, List<String> userNames, String woCode) throws InforException {
        List<WatcherInfo> filteredUserNames = WatcherInfo.getFilteredWatcherInfo(inforClient, userNames, woCode);

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
    public int removeWatchersFromWorkOrder(InforContext context, List<String> userNames, String woCode) {
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
}
