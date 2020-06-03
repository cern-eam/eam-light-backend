package ch.cern.cmms.eamlightejb.parts;

import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import ch.cern.eam.wshub.core.client.InforClient;
import org.jboss.logging.Logger.Level;

import ch.cern.cmms.eamlightejb.tools.LoggingService;

/**
 * Session Bean implementation class PartsEJB
 */
@Stateless
@LocalBean
public class PartsEJB {

	@Inject
	private InforClient inforClient;

	@Inject
	private LoggingService logger;


	public Optional<String> getNextAvailablePartCodeGrid(String prefixCode, InforContext context) {
		GridRequest gridRequest = new GridRequest("SSPART", 1);
		gridRequest.addFilter("partCode", prefixCode, "BEGINS");
		gridRequest.sortBy("partCode", "DESC");

		if (prefixCode == null || prefixCode.isEmpty()) {
			return Optional.ofNullable(null);
		}

		String newPartCode = null;

		try {
			GridRequestResult grd =
				inforClient.getGridsService().executeQuery(context,
					gridRequest);

			String entry = inforClient.getTools().getGridTools().extractSingleResult(grd,
				"partCode");
			String withoutPrefix = entry.substring(prefixCode.length());

			if (entry.matches(".*\\D") && entry.matches(prefixCode)) {
				newPartCode = prefixCode + "1";
			} else {
				String withoutNumbers = withoutPrefix.replaceAll("\\d*", "");
				if (!withoutNumbers.matches(".")) {
					Integer newCode = Integer.parseInt(withoutPrefix) + 1;
					newPartCode = entry.replace(withoutPrefix, newCode.toString());
				}
			}

		} catch (NoResultException exception) {
			logger.log(Level.ERROR, exception.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, exception.getMessage());
		}

		return Optional.ofNullable(newPartCode);
	}


}
