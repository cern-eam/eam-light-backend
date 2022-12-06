package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.client.InforClient;

import java.util.Map;

public interface SharedPlugin {
    String sayHello();

    Map<String, Map<String, String>> getDatesPermissions(InforClient inforClient, String username);
}
