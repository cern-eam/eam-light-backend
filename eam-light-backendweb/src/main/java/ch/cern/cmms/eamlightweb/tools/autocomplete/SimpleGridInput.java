package ch.cern.cmms.eamlightweb.tools.autocomplete;

import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleGridInput {

	private List<String> fields;
	private String JPAType = "JPA_FINAL";
	private Boolean useNative = false;
	private Boolean countTotal = false;
	private GridRequest.GRIDTYPE gridType = GridRequest.GRIDTYPE.LOV;
	private Map<String, Object> inforParams = new LinkedHashMap<String, Object>();
	private List<GridRequestFilter> gridFilters = new LinkedList<>();
	private Map<String, Boolean> sortParams = new LinkedHashMap<String, Boolean>();
	private Integer rowCount = 10;
	private String dataspyID;
	private String gridCode;
	private String gridName;
	private Integer queryTimeout = null;
	private Integer cursorPosition = 1;
	private Boolean fetchAllResults = false;
	private String departmentSecurityGridColumn = null;

	public SimpleGridInput() {
	}

	public SimpleGridInput(String gridCode, String gridName, String dataspyID) {
		this.gridCode = gridCode;
		this.gridName = gridName;
		this.dataspyID = dataspyID;
	}

	public String getJPAType() {
		return JPAType;
	}

	public void setJPAType(String jPAType) {
		JPAType = jPAType;
	}

	public Boolean getUseNative() {
		return useNative;
	}

	public void setUseNative(Boolean useNative) {
		this.useNative = useNative;
	}

	public GridRequest.GRIDTYPE getGridType() {
		return gridType;
	}

	public void setGridType(GridRequest.GRIDTYPE gridType) {
		this.gridType = gridType;
	}

	public Map<String, Object> getInforParams() {
		return inforParams;
	}

	public void setInforParams(Map<String, Object> inforParams) {
		this.inforParams = inforParams;
	}

	public List<GridRequestFilter> getGridFilters() {
		return gridFilters;
	}

	public void setGridFilters(List<GridRequestFilter> gridFilters) {
		this.gridFilters = gridFilters;
	}

	public Map<String, Boolean> getSortParams() {
		return sortParams;
	}

	public void setSortParams(Map<String, Boolean> sortParams) {
		this.sortParams = sortParams;
	}

	public Integer getRowCount() {
		return rowCount;
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}

	public String getDataspyID() {
		return dataspyID;
	}

	public void setDataspyID(String dataspyID) {
		this.dataspyID = dataspyID;
	}

	public String getGridCode() {
		return gridCode;
	}

	public void setGridCode(String gridCode) {
		this.gridCode = gridCode;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getGridName() {
		return gridName;
	}

	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	public Integer getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(Integer queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public Integer getCursorPosition() {
		return cursorPosition;
	}

	public void setCursorPosition(Integer cursorPosition) {
		this.cursorPosition = cursorPosition;
	}
	public Boolean getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(Boolean countTotal) {
		this.countTotal = countTotal;
	}

	public Boolean getFetchAllResults() {
		return fetchAllResults;
	}

	public void setFetchAllResults(Boolean fetchAllResults) {
		this.fetchAllResults = fetchAllResults;
	}

	public String getDepartmentSecurityGridColumn() {
		return departmentSecurityGridColumn;
	}

	public void setDepartmentSecurityGridColumn(String departmentSecurityGridColumn) {
		this.departmentSecurityGridColumn = departmentSecurityGridColumn;
	}

	@Override
	public String toString() {
		return "SimpleGridInput [" + (fields != null ? "fields=" + fields + ", " : "")
				+ (JPAType != null ? "JPAType=" + JPAType + ", " : "")
				+ (useNative != null ? "useNative=" + useNative + ", " : "")
				+ (countTotal != null ? "countTotal=" + countTotal + ", " : "")
				+ (gridType != null ? "gridType=" + gridType + ", " : "")
				+ (inforParams != null ? "inforParams=" + inforParams + ", " : "")
				+ (gridFilters != null ? "whereParams=" + gridFilters + ", " : "")
				+ (sortParams != null ? "sortParams=" + sortParams + ", " : "")
				+ (rowCount != null ? "rowCount=" + rowCount + ", " : "")
				+ (dataspyID != null ? "dataspyID=" + dataspyID + ", " : "")
				+ (gridCode != null ? "gridCode=" + gridCode + ", " : "")
				+ (gridName != null ? "gridName=" + gridName + ", " : "")
				+ (queryTimeout != null ? "queryTimeout=" + queryTimeout + ", " : "")
				+ (cursorPosition != null ? "cursorPosition=" + cursorPosition + ", " : "")
				+ (fetchAllResults != null ? "fetchAllResults=" + fetchAllResults + ", " : "")
				+ (departmentSecurityGridColumn != null ? "departmentSecurityGridColumn=" + departmentSecurityGridColumn
						: "")
				+ "]";
	}
}
