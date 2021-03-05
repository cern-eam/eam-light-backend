package ch.cern.cmms.plugins;

import java.util.Set;

public interface LDAPPlugin {
    void init(String LDAPServer, Integer LDAPPort);
    Set<String> readEgroupMembers(String egroup);
}
