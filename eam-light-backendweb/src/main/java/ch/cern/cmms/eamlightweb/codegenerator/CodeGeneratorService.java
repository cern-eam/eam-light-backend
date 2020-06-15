package ch.cern.cmms.eamlightweb.codegenerator;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;
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


	public String getNextPartCode(String prefixCode, InforContext context) throws InforException{
		return getNextAvailableCode(prefixCode, context, "SSPART", "partcode");
	}

	public String getNextEquipmentCode(String prefixCode, InforContext context, String type) throws InforException{
		return getNextAvailableCode(prefixCode, context, "OSOBJ" + type, "equipmentno");
	}

	private String getNextAvailableCode(String prefixCode, InforContext context, String grid, String code) throws InforException {

		String prefix = prefixCode.substring(1);

		GridRequest gridRequest = new GridRequest(grid, 1);
		gridRequest.addFilter(code, prefix, "BEGINS");
		gridRequest.sortBy(code, "DESC");

		GridRequestResult grd =
			inforClient.getGridsService().executeQuery(context,
				gridRequest);
		String entry = inforClient.getTools().getGridTools().extractSingleResult(grd,
			code);
		if (entry != null) {
			String withoutPrefix = entry.substring(prefix.length());
			if (withoutPrefix.matches("\\d*")) {
				Integer newCode = Integer.parseInt(withoutPrefix) + 1;
				String newCodeString = prefix + String.format("%0" + withoutPrefix.length() + "d", newCode);
				if (!isEquipmentPresent(newCodeString, context)) {
					return newCodeString;
				}
			}
		}
		throw new InforException("Wrong code provided after '@'", null, null);
	}

	private boolean isEquipmentPresent(String itemCode, InforContext context) throws InforException {
		boolean asset = checkForItem(itemCode, context, "OSOBJA", "equipmentno");
		boolean position = checkForItem(itemCode, context, "OSOBJP", "equipmentno");
		boolean system = checkForItem(itemCode, context, "OSOBJS", "equipmentno");
		boolean part = checkForItem(itemCode, context, "SSPART", "partcode");
		return asset || position || system || part;
	}

	private boolean checkForItem(String itemCode, InforContext context, String grid, String code)
		throws InforException {
		GridRequest gridRequest = new GridRequest(grid, 1);
		gridRequest.addFilter(code, itemCode, "EQUALS");
		GridRequestResult grd = inforClient.getGridsService().executeQuery(context, gridRequest);
		String item = inforClient.getTools().getGridTools().extractSingleResult(grd,
			code);

		return item != null;

	}

	public boolean isCodePrefix(String code) {
		return code.startsWith("@");
	}

}
