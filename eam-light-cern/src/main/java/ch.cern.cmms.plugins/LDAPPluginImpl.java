package ch.cern.cmms.plugins;

import java.util.Set;
import ch.cern.cmms.ldaptools.LDAPTools;

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
}
