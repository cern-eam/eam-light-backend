package ch.cern.cmms.eamlightweb.grid;

import ch.cern.cmms.eamlightweb.tools.AuthenticationTools;
import ch.cern.cmms.eamlightweb.tools.WSHubController;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.grids.entities.GridDDSpyFieldsResult;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("/grids")
@Interceptors({ RESTLoggingInterceptor.class })
public class GridController extends WSHubController {

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

			// fetch fields names and labels
			GridDDSpyFieldsResult fieldsInfo = inforClient.getGridsService().getDDspyFields(authenticationTools.getInforContext(),gridRequest.getGridID(), "LIST",
					gridRequest.getDataspyID(), gridRequest.getLang());

			fileContent = Arrays.asList(fieldsInfo.getGridFields()).stream()
					.sorted((a, b) -> Integer.compare(Integer.parseInt(a.getOrder()), Integer.parseInt(b.getOrder())))
					.map(h -> h.getLabel()).reduce("", (a, b) -> {
						return (a == null ? "" : a) + (b == null ? "" : b) + ",";
					});

			// reset the cursor position to 1
			gridRequest.setCursorPosition("1");
			// and set up a maximum of 10000 rows
			gridRequest.setRowCount("10000");

			// prepare the response
			GridRequestResult gridRequestResult = inforClient.getGridsService().executeQuery(authenticationTools.getInforContext(), gridRequest);

			// build the csv
			fileContent += Arrays.asList(gridRequestResult.getRows()).stream().map(row -> {
				return Arrays.asList(row.getCell()).stream().filter(cell -> {
					return cell.getOrder() > -1;
				}).map(cell -> cell.getContent()).reduce("", (acc, text) -> {
					return acc + prepareCellData(text);
				});
			}).reduce("", (a, b) -> {
				return a + "\n" + b;
			});
		} catch (InforException exception) {
			return badRequest(exception);
		}

		return Response.ok(fileContent).header("Content-Disposition", "attachment; filename=" + fileName).build();
	}

	private String prepareCellData(String text) {
		text = text == null ? "" : text;
		return text.length() > 0 ? "\"" + text.replaceAll("\"", "\"\"") + "\"," : ",";
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
