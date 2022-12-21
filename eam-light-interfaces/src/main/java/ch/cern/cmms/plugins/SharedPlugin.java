package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.tools.InforException;

import java.util.List;
import java.util.Map;

public interface SharedPlugin {
    String sayHello();

    Map<String, Map<String, String>> getDatesPermissions(InforClient inforClient, String username);

    List<Pair> getUdsLots(String partCode, InforClient inforClient, InforContext context) throws InforException;
}
