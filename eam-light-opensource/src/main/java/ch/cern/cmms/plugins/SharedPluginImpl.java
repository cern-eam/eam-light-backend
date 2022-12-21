package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.tools.InforException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
public class SharedPluginImpl implements SharedPlugin {
    @Override
    public String sayHello() {
        return "Hello from Open Source!";
    }

    @Override
    public Map<String, Map<String, String>> getDatesPermissions(final InforClient inforClient, final String username) {
        return new HashMap<>();
    }

    @Override
    public List<Pair> getUdsLots(String partCode, InforClient inforClient, InforContext context) throws InforException {
        return new LinkedList<>();
    }
}
