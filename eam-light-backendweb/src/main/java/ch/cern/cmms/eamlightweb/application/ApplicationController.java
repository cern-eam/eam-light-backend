package ch.cern.cmms.eamlightweb.application;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightejb.layout.LayoutBean;
import ch.cern.cmms.eamlightejb.layout.ScreenLayout;
import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightweb.user.UserTools;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.tools.InforException;

@Path("/application")
@RequestScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class ApplicationController extends WSHubController {

	@EJB
	private LayoutBean layoutBean;
	@EJB
	private UserTools userTools;
	@Inject
	private AuthenticationTools authenticationTools;
	@Inject
	private ApplicationData applicationData;
	@Inject
	private InforClient inforClient;

	@GET
	@Path("/applicationdata")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readApplicationData() {
		try {
			return ok(applicationData.getValues());
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/screenlayout/{entity}/{systemFunction}/{userFunction}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readScreenLayout(@PathParam("entity") String entity,
			@PathParam("systemFunction") String systemFunction, @PathParam("userFunction") String userFunction,
			@QueryParam("lang") String language, @QueryParam("tabname") List<String> tabs) throws InforException {
		try {
			ScreenLayout screenLayout = new ScreenLayout();
			if (!inforClient.getTools().isDatabaseConnectionConfigured()) {
				return ok(applicationData.getScreenLayout(systemFunction));
			} else {
				String userGroup = userTools.getUserGroup(authenticationTools.getInforContext());
				screenLayout.setFields(layoutBean.getRecordViewElements(systemFunction, userFunction, entity, userGroup, language));
				screenLayout.setTabs(layoutBean.getScreenTabInfo(entity, systemFunction, userFunction, tabs, userGroup, language));
			}
			return ok(screenLayout);
		} catch(Exception e) {
			e.printStackTrace();
			return serverError(e);
		}
	}

}
