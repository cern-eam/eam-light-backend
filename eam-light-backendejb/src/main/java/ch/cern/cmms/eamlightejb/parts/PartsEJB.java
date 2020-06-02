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

		String newPartCode = null;

		try {
			GridRequestResult grd =
				inforClient.getGridsService().executeQuery(context,
					gridRequest);

			String entry = inforClient.getTools().getGridTools().extractSingleResult(grd,
				"partCode");

			boolean testForLetters = false;

			String whatsLeft = entry.substring(prefixCode.length());
			for (int i = 0; i < whatsLeft.length(); i++) {
				if (!Character.isDigit(whatsLeft.charAt(i))) {
					for (int j = entry.length() - 1; j >= 0; j--) {
						if (!Character.isDigit(entry.charAt(j))) {
							testForLetters = true;
							break;
						}
					}
				}
			}
			if (!testForLetters) {

				if (!Character.isDigit(entry.charAt(entry.length() - 1))) {
					newPartCode = prefixCode + "000001";
				} else {
					for (int i = prefixCode.length() - 1; i >= 0; i--) {
						if (!Character.isDigit(entry.charAt(i))) {
							Integer newCode = Integer.parseInt(entry.substring(i + 1)) + 1;
							newPartCode = entry.replace(entry.substring(i + 1), newCode.toString());
							break;
						}
					}

					return Optional.ofNullable(newPartCode);
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
