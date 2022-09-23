package ch.cern.cmms.plugins;

import java.util.Set;
import ch.cern.cmms.ldaptools.LDAPTools;
import ch.cern.eam.wshub.core.services.entities.Pair;
import static java.util.stream.Collectors.toSet;

public class LDAPPluginImpl implements LDAPPlugin {
    private LDAPTools ldapTools;

    @Override
    public void init(String ldapServer, Integer ldapPort) {
        if (ldapPort != null) {
            ldapTools = new LDAPTools(ldapServer, ldapPort);
        }
    }

    @Override
    public Set<String> readEgroupMembers(String egroup) {
        try {
            return ldapTools.readEgroupMembers(egroup);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Pair> readEgroups(String key) {
        try {
            return ldapTools.getEgroupsContains(key).stream().map(egroup -> new Pair(egroup.getName(), egroup.getDescription())).collect(toSet());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
