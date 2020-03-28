package ch.cern.cmms.eamlightweb.tools;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

public class EAMLightController {


    @Inject
    protected AuthenticationTools authenticationTools;
    @Inject
    protected InforClient inforClient;

    /**
     * Return an object included within the standard WS Hub response, with a OK (200) HTTP status
     * @param data
     * @param <T>
     * @return
     */
    public <T> Response ok(T data) {
        return Response
                .status(Response.Status.OK)
                .entity(EAMResponse.fromData(data))
                .build();
    }

    /**
     * Return an object included within the standard WS Hub response, with a NO_CONTENT (204) HTTP status
     * @param <T>
     * @return
     */
    public <T> Response noConent() {
        return Response
                .status(Response.Status.NO_CONTENT)
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
                .entity(EAMResponse.fromException(exception))
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
                .entity(EAMResponse.fromException(exception))
                .build();
    }

    public Response getPairListResponse(GridRequest gridRequest, String codeKey, String descKey) {
        try {
            return ok(inforClient.getTools().getGridTools().convertGridResultToObject(Pair.class,
                    Pair.generateGridPairMap(codeKey, descKey),
                    inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest)));
        } catch (InforException e) {
            return badRequest(e);
        } catch(Exception e) {
            return serverError(e);
        }
    }

}
