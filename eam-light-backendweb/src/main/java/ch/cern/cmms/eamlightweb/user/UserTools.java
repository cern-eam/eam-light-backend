package ch.cern.cmms.eamlightweb.user;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.EAMUser;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
@LocalBean
public class UserTools {

    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;

    public String getUserGroup(InforContext inforContext) throws InforException {
        EAMUser eamUser = inforClient.getUserSetupService().readUserSetup(authenticationTools.getR5InforContext(), authenticationTools.getInforContext().getCredentials().getUsername());
        return eamUser.getUserGroup();
    }

}




