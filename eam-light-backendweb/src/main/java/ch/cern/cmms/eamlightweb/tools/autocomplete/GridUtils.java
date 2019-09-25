package ch.cern.cmms.eamlightweb.tools.autocomplete;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import org.jboss.logging.Logger.Level;

import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestSort;
import ch.cern.eam.wshub.core.tools.ExceptionInfo;
import ch.cern.eam.wshub.core.tools.InforException;

@Dependent
public class GridUtils {

	@Inject
	private InforClient inforClient;

	@Inject
	private LoggingService logger;

	public GridRequestResult getGridRequestResult(SimpleGridInput input, InforContext inforContext) throws InforException {
		if (input.getGridCode() == null)
			return null;
		if (input.getGridName() == null)
			return null;
		if (input.getDataspyID() == null)
			return null;
		if (input.getGridType() == null)
			return null;
		if (input.getRowCount() == null)
			return null;

		GridRequestResult res = null;
		try {
			GridRequest gridRequest = new GridRequest();

			if (input.getJPAType() != null) {
				gridRequest.setJPAType(input.getJPAType());
			}

			if (input.getInforParams() != null) {
				gridRequest.setParams(input.getInforParams());
			}

			if (input.getDepartmentSecurityGridColumn() != null && !input.getDepartmentSecurityGridColumn().isEmpty()) {
				gridRequest.setDepartmentSecurityGridColumn(input.getDepartmentSecurityGridColumn());
			}

			gridRequest.setCursorPosition(input.getCursorPosition());
			gridRequest.setDataspyID(input.getDataspyID());
			gridRequest.setGridID(input.getGridCode());
			gridRequest.setGridName(input.getGridName());
			gridRequest.setGridType(input.getGridType());
			gridRequest.setRowCount(input.getRowCount());
			gridRequest.setCountTotal(input.getCountTotal());
			gridRequest.setFetchAllResults(input.getFetchAllResults());

			// Activate query timeout
			if (input.getQueryTimeout() != null) {
				gridRequest.setQueryTimeout(true);
				gridRequest.setQueryTimeoutWaitingTime(input.getQueryTimeout());
			}

			// Sorting
			for (String k : input.getSortParams().keySet()) {
				GridRequestSort s = new GridRequestSort();
				s.setSortBy(k);
				s.setSortType(input.getSortParams().get(k) ? "ASC" : "DESC");
				GridRequestSort[] sorts = { s };
				gridRequest.setGridRequestSorts(sorts);
			}

			if (input.getGridFilters() != null) {
				gridRequest.setGridRequestFilters(input.getGridFilters());
			}

			setIfUseNative(gridRequest, input.getUseNative());

			res = inforClient.getGridsService().executeQuery(inforContext, gridRequest);

		} catch (Exception e) {
			logger.log(Level.ERROR, e.getMessage());
			throw inforClient.getTools().generateFault(e.getMessage(), new ExceptionInfo[0]);
		}

		return res;
	}

	private void setIfUseNative(GridRequest gridRequest, Boolean useNative) {
		// Check if use native is set in REQUEST
		if (useNative != null && useNative)
			gridRequest.setUseNative(true);
		else // get it from property
			gridRequest.setUseNative(false);
	}

}
