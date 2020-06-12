package ch.cern.cmms.eamlightweb.codegenerator;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.jboss.logging.Logger.Level;


@ApplicationScoped
public class CodeGeneratorService {

	@Inject
	private InforClient inforClient;

	@Inject
	private LoggingService logger;


	public Optional<String> getNextAvailableCode(String prefixCode, InforContext context, String code, String grid) {

		if (prefixCode == null || prefixCode.isEmpty()) {
			return Optional.ofNullable(null);
		}
		GridRequest gridRequest = new GridRequest(grid, 1);
		gridRequest.addFilter(code, prefixCode, "BEGINS");
		gridRequest.sortBy(code, "DESC");

		try {
			GridRequestResult grd =
				inforClient.getGridsService().executeQuery(context,
					gridRequest);
			String entry = inforClient.getTools().getGridTools().extractSingleResult(grd,
				code);
			if (entry == null || entry.isEmpty()) {
				return Optional.ofNullable(null);
			}
			String withoutPrefix = entry.substring(prefixCode.length());
			if (withoutPrefix.matches("\\d*")) {
				Integer newCode = Integer.parseInt(withoutPrefix) + 1;
				String newCodeString = prefixCode + String.format("%0" + withoutPrefix.length() + "d", newCode);
				if (!isEquipmentPresent(newCodeString, context)) {
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

	private boolean isEquipmentPresent(String equipmentCode, InforContext context) throws Exception {
		boolean asset = gridRequest(equipmentCode, context, "OSOBJA");
		boolean position = gridRequest(equipmentCode, context, "OSOBJP");
		boolean system = gridRequest(equipmentCode, context, "OSOBJS");
		return asset || position || system;
	}

	private boolean gridRequest(String equipmentCode, InforContext context, String grid) throws Exception {
		GridRequest gridRequest = new GridRequest(grid, 1);
		gridRequest.addFilter("equipmentno", equipmentCode, "EQUALS");
		GridRequestResult grd = inforClient.getGridsService().executeQuery(context, gridRequest);
		String item = inforClient.getTools().getGridTools().extractSingleResult(grd,
			"equipmentno");

		return item != null;

	}

}
