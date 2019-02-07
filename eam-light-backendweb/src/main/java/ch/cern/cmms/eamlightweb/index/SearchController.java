/**
 * 
 */
package ch.cern.cmms.eamlightweb.index;


import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.UserData;
import ch.cern.cmms.eamlightejb.index.IndexEJB;

/**
 * Controller to handle the events related with the home screen of the
 * application
 *
 */
@RequestScoped
@Path("/index")
@Interceptors({ RESTLoggingInterceptor.class })
public class SearchController extends WSHubController {

	@EJB
	private IndexEJB indexEJB;

	@Inject
	private UserData userData;

	@GET
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getSearchResults(@QueryParam("s") String searchKeyWord) {
		if (searchKeyWord != null) {
			searchKeyWord = searchKeyWord.trim();
		}

		Boolean woScreenAccess = isUserScreenAvailable(userData.getWorkOrderScreen(null));
		Boolean assetScreenAccess = isUserScreenAvailable(userData.getAssetScreen(null));
		Boolean positionScreenAccess = isUserScreenAvailable(userData.getPositionScreen(null));
		Boolean systemScreenAccess = isUserScreenAvailable(userData.getSystemScreen(null));
		Boolean partScreenAccess = isUserScreenAvailable(userData.getPartScreen(null));
		try {
			return ok(
					indexEJB.getIndexResults(searchKeyWord, userData.getEamAccount().getUserCode(), woScreenAccess,
							assetScreenAccess, positionScreenAccess, systemScreenAccess, partScreenAccess));
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

		Boolean woScreenAccess = isUserScreenAvailable(userData.getWorkOrderScreen(null));
		Boolean assetScreenAccess = isUserScreenAvailable(userData.getAssetScreen(null));
		Boolean positionScreenAccess = isUserScreenAvailable(userData.getPositionScreen(null));
		Boolean systemScreenAccess = isUserScreenAvailable(userData.getSystemScreen(null));
		Boolean partScreenAccess = isUserScreenAvailable(userData.getPartScreen(null));
		try {
			return ok(
					indexEJB.getIndexSingleResult(searchKeyword, userData.getEamAccount().getUserCode(), woScreenAccess,
							assetScreenAccess, positionScreenAccess, systemScreenAccess, partScreenAccess));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	private Boolean isUserScreenAvailable(String screen) {
		return screen == null ? false : true;
	}
}
