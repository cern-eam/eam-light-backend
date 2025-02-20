package ch.cern.cmms.eamlightweb.login;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
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
    public Response login() throws InforException {
        try {
            InforContext inforContext = authenticationTools.getInforContext();
            inforContext.setKeepSession(true);
            return ok(inforClient.getUserSetupService().login(inforContext, ""));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

}
