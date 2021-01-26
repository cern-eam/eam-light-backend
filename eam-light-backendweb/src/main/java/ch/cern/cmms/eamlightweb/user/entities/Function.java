package ch.cern.cmms.eamlightweb.user.entities;

import ch.cern.eam.wshub.core.annotations.GridField;

public class Function {
    @GridField(name="isuserdefinedscreen")
    Boolean isUserDefinedScreen;
    @GridField(name="formtype_display")
    String formTypeDisplay;
    @GridField(name="parentscreencode")
    String parentScreenCode;
    @GridField(name="screencode")
    String screenCode;
    @GridField(name="updateallowed")
    Boolean updateAllowed;
    @GridField(name="queryallowed")
    Boolean queryAllowed;
    @GridField(name="classcode")
    String classCode;
    @GridField(name="classorg")
    String classOrg;
    @GridField(name="icon")
    String icon;
    @GridField(name="insertallowed")
    Boolean insertAllowed;
    @GridField(name="systementity")
    String systemEntity;
    @GridField(name="formtype")
    String formType;
    @GridField(name="screendescription")
    String screenDesctiption;
    @GridField(name="deleteallowed")
    Boolean deleteAllowed;
    @GridField(name="lastvalue")
    String lastValue;
    @GridField(name="urlpath")
    String urlPath;
    @GridField(name="openurl")
    Boolean openURL;
    @GridField(name="employeefilter")
    String employeeFilter;
    @GridField(name="screenreport")
    String screenReport;
    @GridField(name="startupmode_display")
    String startUpModeDisplayDescription;
    @GridField(name="startupview_display")
    String startUpViewDisplay;

    String startUpModeDisplayCode;

    public Boolean getUserDefinedScreen() {
        return isUserDefinedScreen;
    }

    public void setUserDefinedScreen(Boolean userDefinedScreen) {
        isUserDefinedScreen = userDefinedScreen;
    }

    public String getFormTypeDisplay() {
        return formTypeDisplay;
    }

    public void setFormTypeDisplay(String formTypeDisplay) {
        this.formTypeDisplay = formTypeDisplay;
    }

    public String getParentScreenCode() {
        return parentScreenCode;
    }

    public void setParentScreenCode(String parentScreenCode) {
        this.parentScreenCode = parentScreenCode;
    }

    public String getScreenCode() {
        return screenCode;
    }

    public void setScreenCode(String screenCode) {
        this.screenCode = screenCode;
    }

    public Boolean getUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(Boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public Boolean getQueryAllowed() {
        return queryAllowed;
    }

    public void setQueryAllowed(Boolean queryAllowed) {
        this.queryAllowed = queryAllowed;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassOrg() {
        return classOrg;
    }

    public void setClassOrg(String classOrg) {
        this.classOrg = classOrg;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getInsertAllowed() {
        return insertAllowed;
    }

    public void setInsertAllowed(Boolean insertAllowed) {
        this.insertAllowed = insertAllowed;
    }

    public String getSystemEntity() {
        return systemEntity;
    }

    public void setSystemEntity(String systemEntity) {
        this.systemEntity = systemEntity;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getScreenDesctiption() {
        return screenDesctiption;
    }

    public void setScreenDesctiption(String screenDesctiption) {
        this.screenDesctiption = screenDesctiption;
    }

    public Boolean getDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(Boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public Boolean getOpenURL() {
        return openURL;
    }

    public void setOpenURL(Boolean openURL) {
        this.openURL = openURL;
    }

    public String getEmployeeFilter() {
        return employeeFilter;
    }

    public void setEmployeeFilter(String employeeFilter) {
        this.employeeFilter = employeeFilter;
    }

    public String getScreenReport() {
        return screenReport;
    }

    public void setScreenReport(String screenReport) {
        this.screenReport = screenReport;
    }

    public String getStartUpModeDisplayDescription() {
        return startUpModeDisplayDescription;
    }

    public void setStartUpModeDisplayDescription(String startUpModeDisplayDescription) {
        this.startUpModeDisplayDescription = startUpModeDisplayDescription;
    }

    public String getStartUpModeDisplayCode() {
        return startUpModeDisplayCode;
    }

    public void setStartUpModeDisplayCode(String startUpModeDisplayCode) {
        this.startUpModeDisplayCode = startUpModeDisplayCode;
    }

    public String getStartUpViewDisplay() {
        return startUpViewDisplay;
    }

    public void setStartUpViewDisplay(String startUpViewDisplay) {
        this.startUpViewDisplay = startUpViewDisplay;
    }
}
