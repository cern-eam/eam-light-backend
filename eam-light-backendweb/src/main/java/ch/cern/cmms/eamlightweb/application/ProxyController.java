package ch.cern.cmms.eamlightweb.application;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import javax.enterprise.context.ApplicationScoped;
import java.net.URI;

@ApplicationScoped
@Path("proxy")
public class ProxyController {
    private static final String TARGET_URL = "https://testeam.cern.ch/axis/restservices";

    @Inject
    private AuthenticationTools authenticationTools;

    @Path("{path: .*}")
    @GET
    @POST
    @PUT
    @DELETE
    @HEAD
    @OPTIONS
    public Response proxy(@PathParam("path") String path, String body, @Context Request request) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URI.create(TARGET_URL + "/" + path.replace("#", "%23")));
        Invocation.Builder builder = target.request();

        // Only send explicitly defined headers
        String credentials = null;
        try {
            credentials = authenticationTools.getInforContext().getCredentials().getUsername() + ":" + authenticationTools.getInforContext().getCredentials().getPassword();
        } catch (Exception e) {

        }

        builder.header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes()));
        builder.header("tenant", "infor");
        builder.header("organization", authenticationTools.getOrganizationCode());
        builder.header("accept", "application/json");
        String method = request.getMethod();
        Entity<?> entity = (method.equals("GET") || method.equals("HEAD") || method.equals("OPTIONS")) ? null : Entity.json(body);

        Response response = builder.method(method, entity);
        return Response.fromResponse(response).build();
    }
}