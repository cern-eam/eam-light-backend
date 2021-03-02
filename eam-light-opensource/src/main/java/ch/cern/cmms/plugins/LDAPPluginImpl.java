package ch.cern.cmms.plugins;

import java.util.Collections;
import java.util.Set;

public class LDAPPluginImpl implements LDAPPlugin {
    @Override
    public void init(String ldapServer, Integer ldapPort) {
    }

    @Override
    public Set<String> readEgroupMembers(String egroup) {
        return Collections.emptySet();
    }
}
