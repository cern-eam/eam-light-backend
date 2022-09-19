package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.services.entities.Pair;

import java.util.Set;

public interface LDAPPlugin {
    void init(String LDAPServer, Integer LDAPPort);
    Set<String> readEgroupMembers(String egroup);
    Set<Pair> readEgroups(String key) ;
}
