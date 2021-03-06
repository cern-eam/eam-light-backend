package ch.cern.cmms.eamlightweb.codegenerator;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.tools.InforException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class CodeGeneratorService {

	@Inject
	private InforClient inforClient;


	public String getNextPartCode(String prefixCode, InforContext context) throws InforException {
		return getNextAvailableCode(prefixCode, context, "SSPART", "partcode");
	}

	public String getNextEquipmentCode(String prefixCode, InforContext context, String type) throws InforException {
		return getNextAvailableCode(prefixCode, context, "OSOBJ" + type, "equipmentno");
	}

	private String getNextAvailableCode(String prefixCode, InforContext context, String grid, String code)
		throws InforException {

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
				return newCodeString;
			}

		}
		throw inforClient.getTools().generateFault("Wrong code provided after '@'");
	}

	public boolean isCodePrefix(String code) {
		return code.startsWith("@");
	}

}
