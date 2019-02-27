package ch.cern.cmms.eamlightejb;

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

    public String getUserGroup(InforContext inforContext) throws InforException {
        EntityManager em = inforClient.getTools().getEntityManager();
        try {
            // Load the user
            EAMUser eamUser = em.find(EAMUser.class, inforContext.getCredentials().getUsername().toUpperCase());
            // If the user is found, load its departments
            if (eamUser != null) {
				return eamUser.getUserGroup();
            }
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw inforClient.getTools().generateFault("The user couldn't be found.");
        } finally {
            em.close();
        }
        return null;
    }

}
