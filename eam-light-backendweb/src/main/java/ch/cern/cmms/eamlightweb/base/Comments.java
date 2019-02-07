package ch.cern.cmms.eamlightweb.base;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.comments.entities.Comment;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/comments")
@Interceptors({ RESTLoggingInterceptor.class })
public class Comments extends WSHubController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@GET
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readComments(@QueryParam("entityCode") String entityCode,
								 @QueryParam("entityKeyCode") String entityKeyCode) {
		try {
			Comment comment = new Comment();
			comment.setEntityCode(entityCode);
			comment.setEntityKeyCode(entityKeyCode);
			return ok(inforClient.getCommentService().readComments(authenticationTools.getInforContext(), entityCode, entityKeyCode, null));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createComment(Comment comment) {
		try {
			// Create the comment
			inforClient.getCommentService().createComment(authenticationTools.getInforContext(), comment);
			// Read the comments again
			return ok(inforClient.getCommentService().readComments(authenticationTools.getInforContext(),
					comment.getEntityCode(),
					comment.getEntityKeyCode(),
					null));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@PUT
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateComment(Comment comment) {
		try {
			// Remove update count
			comment.setUpdateCount(null);
			// Update comment
			inforClient.getCommentService().updateComment(authenticationTools.getInforContext(), comment);
			// Read the comments again
			return ok(inforClient.getCommentService().readComments(authenticationTools.getInforContext(),
					comment.getEntityCode(),
					comment.getEntityKeyCode(),
					null));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
