package ch.cern.cmms.eamlightweb.tools;

import ch.cern.eam.wshub.core.client.InforClient;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class EAMLightNativeRestController {


    @Inject
    protected AuthenticationTools authenticationTools;
    @Inject
    protected InforClient inforClient;

    /**
     * Return an object included within the standard WS Hub response, with a OK (200) HTTP status
     * @param Result
     * @param <T>
     * @return
     */
    public <T> Response ok(T Result) {
        return Response
                .status(Response.Status.OK)
                .entity(EAMNativeResponse.fromData(Result))
                .build();
    }

    /**
     * Return an object included within the standard WS Hub response, with a SERVER_ERROR HTTP status
     * @param exception
     * @return
     */
    public Response serverError(Exception exception) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(EAMNativeResponse.fromException(exception))
                .build();
    }

    /**
     * Return an object included within the standard WS Hub response, with a BAD_REQUEST HTTP status
     * @param exception
     * @return
     */
    public Response badRequest(Exception exception) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(EAMNativeResponse.fromException(exception))
                .build();
    }

}
