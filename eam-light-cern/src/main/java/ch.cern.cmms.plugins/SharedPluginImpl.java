package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SharedPluginImpl implements SharedPlugin {
    @Override
    public String sayHello() {
        return "Hello from CERN!";
    }

    // Retrieve lots from the user defined screen
    @Override
    public List<Pair> getUdsLots(String partCode, InforClient inforClient, InforContext context,
                                 Map<String, String> applicationData) throws InforException {

        String udsPartLotGrid = applicationData.get("EL_USPLT");

        GridRequest udsLotsRequest = new GridRequest(udsPartLotGrid);
        udsLotsRequest.addParam("parameter.partcode", partCode);

        GridRequestResult udsLotsResult = inforClient.getGridsService().executeQuery(context, udsLotsRequest);

        Map<String, String> lotColumns = new HashMap<String, String>(){{
            put("wspf_10_plo_lot", "code");
            put("", "desc");
        }};

        return GridTools.convertGridResultToObject(Pair.class, lotColumns, udsLotsResult);
    }
}
