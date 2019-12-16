package ch.cern.cmms.eamlightweb.index;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.index.IndexEJB;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;

/**
 * Controller to handle the events related with the home screen of the
 * application
 *
 */
@ApplicationScoped
@Path("/index")
@Interceptors({ RESTLoggingInterceptor.class })
public class SearchController extends EAMLightController {

	@Inject
	private IndexGrids indexGrids;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private IndexEJB indexEJB;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getSearchResults(@QueryParam("s") String searchKeyWord) {
		if (searchKeyWord != null) {
			searchKeyWord = searchKeyWord.trim();
		}

		try {
			if (inforClient.getTools().isDatabaseConnectionConfigured()) {
				return ok(indexEJB.getIndexResults(searchKeyWord, authenticationTools.getInforContext().getCredentials().getUsername()));
			} else {
				return ok(indexGrids.search(authenticationTools.getInforContext(), searchKeyWord));
			}
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/singleresult")
	@Produces("application/json")
	public Response getIndexSingleResult(@QueryParam("s") String searchKeyword) {
		if (searchKeyword != null) {
			searchKeyword = searchKeyword.trim();
		}

		try {
			if (inforClient.getTools().isDatabaseConnectionConfigured()) {
				return ok(indexEJB.getIndexSingleResult(searchKeyword, authenticationTools.getInforContext().getCredentials().getUsername()));
			} else {
				return ok(indexGrids.searchSingleResult(authenticationTools.getInforContext(), searchKeyword));
			}
		} catch(Exception e) {
			return serverError(e);
		}
	}

}
