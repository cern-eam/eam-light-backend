package ch.cern.cmms.eamlightweb.parts;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;
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
			if (entry == null || entry.isEmpty()) {
				return Optional.ofNullable(null);
			}
			String withoutPrefix = entry.substring(prefixCode.length());
			if (withoutPrefix.matches("\\d*")) {
				Integer newCode = Integer.parseInt(withoutPrefix) + 1;
				String newCodeString = prefixCode + String.format("%0" + withoutPrefix.length() + "d", newCode);
				if (!isAssetPresent(newCodeString, context)) {
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

	private boolean isAssetPresent(String equipmentCode, InforContext context) throws Exception {
		GridRequest gridRequestAsset = new GridRequest("OSOBJA", 1);
		gridRequestAsset.addFilter("equipmentno", equipmentCode, "EQUALS");
		GridRequestResult grdAsset = inforClient.getGridsService().executeQuery(context, gridRequestAsset);
		String asset = inforClient.getTools().getGridTools().extractSingleResult(grdAsset,
			"equipmentno");
		return asset != null;
	}

}
