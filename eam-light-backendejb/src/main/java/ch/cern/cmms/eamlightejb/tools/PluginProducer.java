package ch.cern.cmms.eamlightejb.tools;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.plugins.LDAPPlugin;
import ch.cern.cmms.plugins.LDAPPluginImpl;
import ch.cern.cmms.plugins.SharedPlugin;
import ch.cern.cmms.plugins.SharedPluginImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

@ApplicationScoped
public class PluginProducer {
    @Produces
    private SharedPlugin sharedPlugin;

    @Produces
    private LDAPPlugin ldapPlugin;

    @Inject
    private ApplicationData applicationData;

    @PostConstruct
    public void init() {
        sharedPlugin = new SharedPluginImpl();

        LDAPPlugin ldapPlugin = new LDAPPluginImpl();
        ldapPlugin.init(applicationData.getLDAPServer(), applicationData.getLDAPPort());
        this.ldapPlugin = ldapPlugin;
    }
}
