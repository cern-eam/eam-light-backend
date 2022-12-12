package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.client.InforClient;

import java.util.HashMap;
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
}
