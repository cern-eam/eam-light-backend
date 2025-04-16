package ch.cern.cmms.eamlightweb.grid;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestCell;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestRow;
import ch.cern.eam.wshub.core.tools.GridTools;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Path("/grids")
@Interceptors({ RESTLoggingInterceptor.class })
public class GridController extends EAMLightController {

	@Inject
	private InforClient inforClient;
	@Inject
	private AuthenticationTools authenticationTools;

	@POST
	@Path("/data")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readGridData(GridRequest gridRequest) {
		try {
			return ok(inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@POST
	@Path("/export")
	@Produces("text/csv")
	@Consumes("application/json")
	public Response exportGridDataToCSV(GridRequest gridRequest) {
		String fileName = "dataGridExport.csv";
		String fileContent = "";
		try {
			// change the grid 'range'
			gridRequest.setCursorPosition(1);
			gridRequest.setRowCount(10000);
			// build the csv
			fileContent = inforClient.getGridsService().getGridCsvData(authenticationTools.getInforContext(), gridRequest);
		} catch (InforException exception) {
			return badRequest(exception);
		}

		return Response.ok(fileContent).header("Content-Disposition", "attachment; filename=" + fileName).build();
	}



	@GET
	@Path("/{gridid}/metadata")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readGridMetaData(@PathParam("gridid") String gridID, @QueryParam("lang") String lang) {
		try {
			if (lang == null || (!"EN".equals(lang) && !"FR".equals(lang)))
				lang = "EN";
			return ok(inforClient.getGridsService().getGridMetadata(authenticationTools.getInforContext(), gridID, "LIST", lang));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}

	@GET
	@Path("/{gridid}/dataspy")
	@Produces("application/json")
	@Consumes("application/json")
	public Response readDataspyFields(@PathParam("gridid") String gridCode, @QueryParam("dataspyid") String ddSpyId,
			@QueryParam("lang") String lang) {
		try {
			if (lang == null || (!"EN".equals(lang) && !"FR".equals(lang)))
				lang = "EN";
			return ok(inforClient.getGridsService().getDDspyFields(authenticationTools.getInforContext(), gridCode, "LIST", ddSpyId, lang));
		} catch (InforException e) {
			return badRequest(e);
		} catch(Exception e) {
			return serverError(e);
		}
	}



}
