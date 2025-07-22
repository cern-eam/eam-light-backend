package ch.cern.cmms.eamlightweb.application;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.cache.CacheManager;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.plugins.LDAPPlugin;
import ch.cern.cmms.plugins.SharedPlugin;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/application")
@ApplicationScoped
@Interceptors({RESTLoggingInterceptor.class})
public class ApplicationController extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private ApplicationData applicationData;
	@Inject
	private CacheManager cacheManager;
	@Inject
	private ApplicationService applicationService;
	@Inject
	private SharedPlugin sharedPlugin;
	@Inject
	private LDAPPlugin ldapPlugin;
    @Inject
    private AuthenticationTools authenticationTools;

	@GET
	@Path("/hello")
	@Produces("application/json")
	@Consumes("application/json")
	public Response sayHello() {
		return ok(sharedPlugin.sayHello() + " (EAMLIGHT_INFOR_WS_URL=" + applicationData.getInforWSURL() + ")");
	}

	@GET
	@Path("/applicationdata")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readApplicationData() {
		try {
			final InforContext inforContext = authenticationTools.getInforContext();
			return ok(applicationService.getParams(inforContext.getTenant()));
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/refreshCache")
	@Produces("application/json")
	@Consumes("application/json")
	public Response cleanCache() {
		cacheManager.clearAllCaches();
		return ok("EAM Light cache has been successfully refreshed.");
	}

	@GET
	@Path("/version")
	@Produces("application/json")
	public Response readVersion() {
		String version = applicationData.getVersion();
		return ok(version);
	}

}