package ch.cern.cmms.eamlightweb.application;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.base.CustomFieldsController;
import ch.cern.cmms.eamlightweb.base.UserDefinedFieldsController;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.ScreenService;
import ch.cern.cmms.eamlightweb.user.UserService;
import ch.cern.cmms.plugins.LDAPPlugin;
import ch.cern.cmms.plugins.SharedPlugin;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.administration.impl.ScreenLayoutServiceImpl;
import ch.cern.eam.wshub.core.services.grids.impl.GridsServiceImpl;
import ch.cern.eam.wshub.core.services.grids.impl.InforGrids;
import ch.cern.eam.wshub.core.services.workorders.impl.ChecklistServiceImpl;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/application")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class ApplicationController extends EAMLightController {
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private InforClient inforClient;
	@Inject
	private ApplicationData applicationData;
	@Inject
	private ApplicationService applicationService;
	@Inject
	private SharedPlugin sharedPlugin;
	@Inject
	private LDAPPlugin ldapPlugin;

	@GET
	@Path("/dates/permissions")
	@Produces("application/json")
	@Consumes("application/json")
	public Response getDatesPermisisons() {
		try {
			return ok(sharedPlugin.getDatesPermissions(inforClient,
					authenticationTools.getInforContext().getCredentials().getUsername()));
		} catch (InforException e) {
			return serverError(e);
		}
	}

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
			return ok(applicationService.getParams());
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/refreshCache")
	@Produces("application/json")
	@Consumes("application/json")
	public Response cleanCache() {
		ScreenLayoutServiceImpl.screenLayoutCache.clear();
		ScreenLayoutServiceImpl.screenLayoutLabelCache.clear();
		ScreenService.screenCache.clear();
		ScreenService.reportsCache.clear();
		InforGrids.gridFieldCache.clear();
		GridsServiceImpl.gridIdCache.clear();
		CustomFieldsController.customFieldsLookupValuesCache.clear();
		ChecklistServiceImpl.findingsCache.clear();
		UserService.userCache.clear();
		ApplicationService.paramFieldCache.clear();
		UserDefinedFieldsController.rentityAutocompleteCache.clear();
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