package ch.cern.cmms.eamlightweb.parts;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.jboss.logging.Logger.Level;

@ApplicationScoped
public class PartService {

	@Inject
	private InforClient inforClient;

	@Inject
	private LoggingService logger;


	public Optional<String> getNextAvailablePartCode(String prefixCode, InforContext context) {

		if (prefixCode == null || prefixCode.isEmpty()) {
			return Optional.ofNullable(null);
		}
		GridRequest gridRequest = new GridRequest("SSPART", 1);
		gridRequest.addFilter("partCode", prefixCode, "BEGINS");
		gridRequest.sortBy("partCode", "DESC");

		try {
			GridRequestResult grd =
				inforClient.getGridsService().executeQuery(context,
					gridRequest);

			String entry = inforClient.getTools().getGridTools().extractSingleResult(grd,
				"partCode");
			String withoutPrefix = entry.substring(prefixCode.length());
			GridRequest gridRequestAsset = new GridRequest("OSOBJA", 1);

			if (entry.matches(Pattern.quote(prefixCode))) {
				gridRequestAsset.addFilter("equipmentno", prefixCode + "000001", "EQUALS");
				GridRequestResult grdAsset = inforClient.getGridsService().executeQuery(context, gridRequestAsset);
				String asset = inforClient.getTools().getGridTools().extractSingleResult(grdAsset,
					"equipmentno");
				if (asset == null) {
					return Optional.ofNullable(prefixCode + "000001");
				}
			} else if (withoutPrefix.matches("\\d*")) {
				Integer newCode = Integer.parseInt(withoutPrefix) + 1;
				String newCodeFormatted = String.format("%0" + withoutPrefix.length() + "d", newCode);
				String newCodeString = entry.replaceAll(withoutPrefix + "$", newCodeFormatted);
				gridRequestAsset.addFilter("equipmentno", newCodeString, "EQUALS");
				GridRequestResult grdAsset = inforClient.getGridsService().executeQuery(context, gridRequestAsset);
				String asset = inforClient.getTools().getGridTools().extractSingleResult(grdAsset,
					"equipmentno");
				if (asset == null) {
					return Optional.ofNullable(newCodeString);
				}
			}

		} catch (NoResultException exception) {
			logger.log(Level.ERROR, exception.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, exception.getMessage());
		}

		return Optional.ofNullable(null);
	}

}
