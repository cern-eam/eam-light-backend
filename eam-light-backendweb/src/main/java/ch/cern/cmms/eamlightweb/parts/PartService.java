package ch.cern.cmms.eamlightweb.parts;

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

			if (entry.matches(prefixCode)) {
				return Optional.ofNullable(prefixCode + "1");
			} else if (withoutPrefix.matches("\\d*")) {
				Integer newCode = Integer.parseInt(withoutPrefix) + 1;
				return Optional.ofNullable(entry.replaceAll(withoutPrefix + "$", newCode.toString()));
			}

		} catch (NoResultException exception) {
			logger.log(Level.ERROR, exception.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, exception.getMessage());
		}

		return Optional.ofNullable(null);
	}

}
