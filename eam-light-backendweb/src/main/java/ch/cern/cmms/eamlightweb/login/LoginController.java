package ch.cern.cmms.eamlightweb.login;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/login")
@ApplicationScoped
public class LoginController extends EAMLightController {

    @Inject
    private InforClient inforClient;
    @Inject
    private AuthenticationTools authenticationTools;

    @GET
    @Path("/")
    @Produces("application/json")
    public Response getWorkOrderEquipment() throws InforException {
        try {
            return ok(inforClient.getUserSetupService().login(authenticationTools.getInforContext(), ""));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

}
