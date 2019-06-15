package ch.cern.cmms.eamlightejb.layout;

import javax.persistence.*;

@NamedNativeQueries({
	@NamedNativeQuery(name=ScreenInfo.FETCH_USER_SCREENS, 
			query = 
			"SELECT NVL(FUN_APPLICATION, EMN_FUNCTION) FUN_APPLICATION, EMN_FUNCTION, EML_TEXT, NVL(DECODE(PRM_SELECT,'?', 1,0),0) PRM_SELECT, NVL(DECODE(PRM_UPDATE,'*', 1,0),0) PRM_UPDATE, NVL(DECODE(PRM_INSERT,'+', 1,0),0) PRM_INSERT, NVL(DECODE(PRM_DELETE,'X', 1,0),0) PRM_DELETE, GRD_GRIDID " + 
					"FROM R5EXTMENUS, R5EXTMENULANG, R5FUNCTIONS, R5USERS, R5PERMISSIONS, R5GRID " + 
					"WHERE USR_CODE = :user " + 
					"AND R5GRID.GRD_GRIDNAME = EMN_FUNCTION " + 
					"AND PRM_GROUP = USR_GROUP AND PRM_FUNCTION = EMN_FUNCTION " + 
					"AND EMN_GROUP = USR_GROUP AND EMN_CODE=EML_EXTMENU AND EMN_FUNCTION = FUN_CODE " +
					"AND COALESCE(EMN_MOBILE,'-')='-' " + 
					"AND (EMN_FUNCTION IN (:functions) OR FUN_APPLICATION IN (:functions)) " +
					"ORDER BY " +
					"(SELECT count(1) FROM R5EXTMENUS r5e WHERE (r5e.EMN_HIDE = '+') START WITH r5e.emn_code = EML_EXTMENU CONNECT BY NOCYCLE r5e.EMN_CODE = PRIOR r5e.EMN_PARENT), " +
					"FUN_APPLICATION ASC",
					resultClass=ScreenInfo.class),
	@NamedNativeQuery(name=ScreenInfo.FETCH_USER_DEFAULT_SCREEN, 
			query = 
			"SELECT NVL(FUN_APPLICATION, EMN_FUNCTION) FUN_APPLICATION, EMN_FUNCTION, EML_TEXT, NVL(DECODE(PRM_SELECT,'?', 1,0),0) PRM_SELECT, NVL(DECODE(PRM_UPDATE,'*', 1,0),0) PRM_UPDATE, NVL(DECODE(PRM_INSERT,'+', 1,0),0) PRM_INSERT, NVL(DECODE(PRM_DELETE,'X', 1),0) PRM_DELETE, GRD_GRIDID " + 
					"FROM R5EXTMENUS, R5EXTMENULANG, R5FUNCTIONS, R5USERS, R5PERMISSIONS, R5GRID " + 
					"WHERE USR_CODE = :user " + 
					"AND R5GRID.GRD_GRIDNAME = EMN_FUNCTION " + 
					"AND PRM_GROUP = USR_GROUP AND PRM_FUNCTION = EMN_FUNCTION " + 
					"AND EMN_GROUP = USR_GROUP AND EMN_CODE=EML_EXTMENU AND EMN_FUNCTION = FUN_CODE " +
					"AND COALESCE(EMN_MOBILE,'-')='-' " +
					"AND (EMN_FUNCTION = :function OR FUN_APPLICATION = :function) " +
					"ORDER BY " +
					"(SELECT count(1) FROM R5EXTMENUS r5e WHERE (r5e.EMN_HIDE = '+') START WITH r5e.emn_code = EML_EXTMENU CONNECT BY NOCYCLE r5e.EMN_CODE = PRIOR r5e.EMN_PARENT), " +
					"FUN_APPLICATION ASC",  
					resultClass=ScreenInfo.class)
})
@Entity
public class ScreenInfo {

	public static final String FETCH_USER_SCREENS = "ScreenInfo.FETCH_USER_SCREENS";
	public static final String FETCH_USER_DEFAULT_SCREEN = "ScreenInfo.FETCH_USER_DEFAULT_SCREEN";
	@Id
	@Column(name="EMN_FUNCTION")
	private String screenCode;
	@Column(name="FUN_APPLICATION")
	private String parentScreen;
	@Column(name="EML_TEXT")
	private String screenDesc;
	@Column(name="PRM_SELECT")
	private boolean readAllowed;
	@Column(name="PRM_INSERT")
	private boolean creationAllowed;
	@Column(name="PRM_DELETE")
	private boolean deleteAllowed;
	@Column(name="PRM_UPDATE")
	private boolean updateAllowed;
	@Column(name="GRD_GRIDID")
	private String gridId;

	public ScreenInfo() {

	}

	public ScreenInfo(String screenCode, String parentScreen, String screenDesc) {
		this.screenCode = screenCode;
		this.parentScreen = parentScreen;
		this.screenDesc = screenDesc;
		readAllowed = true;
		creationAllowed = true;
		deleteAllowed = true;
		updateAllowed = true;
	}

	public String getScreenCode() {
		return screenCode;
	}
	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}
	public String getScreenDesc() {
		return screenDesc;
	}
	public void setScreenDesc(String screenDesc) {
		this.screenDesc = screenDesc;
	}
	public boolean isCreationAllowed() {
		return creationAllowed;
	}
	public void setCreationAllowed(boolean creationAllowed) {
		this.creationAllowed = creationAllowed;
	}
	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}
	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}
	public boolean isUpdateAllowed() {
		return updateAllowed;
	}
	public void setUpdateAllowed(boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}

	public boolean isReadAllowed() {
		return readAllowed;
	}
	public void setReadAllowed(boolean readAllowed) {
		this.readAllowed = readAllowed;
	}
	public String getGridId() {
		return gridId;
	}
	public void setGridId(String gridId) {
		this.gridId = gridId;
	}
	
	public String getParentScreen() {
		return parentScreen;
	}
	public void setParentScreen(String parentScreen) {
		this.parentScreen = parentScreen;
	}
	@Override
	public String toString() {
		return "ScreenInfo [screenCode=" + screenCode + ", parentScreen=" + parentScreen + ", screenDesc=" + screenDesc
				+ ", readAllowed=" + readAllowed + ", creationAllowed=" + creationAllowed + ", deleteAllowed="
				+ deleteAllowed + ", updateAllowed=" + updateAllowed + ", gridId=" + gridId + "]";
	}
	
}
