package ch.cern.cmms.eamlightejb.data;

import ch.cern.cmms.eamlightejb.tools.ApplicationDataReader;
import ch.cern.cmms.eamlightejb.tools.Tools;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

@Dependent
public class ApplicationData {

    private Map<String, String> eamlightValues;
    private Map<String, String> kioskValues;
    private Map<String, String> edmsValues;

    @Inject
    private ApplicationDataReader configReader;

    @PostConstruct
    private void init() {
        eamlightValues = configReader.getProperties("EAMLIGHT");
        kioskValues = configReader.getProperties("KIOSK");
        edmsValues = configReader.getProperties("EDMS");
    }

    //
    // Getters for individual properties
    //
    public String getTenant() {
        return Tools.getVariableValue("EAMLIGHT_INFOR_TENANT");
    }

    public String getDefaultOrganization() { return Tools.getVariableValue("EAMLIGHT_INFOR_ORGANIZATION"); }

    public String getInforWSURL() {
        return Tools.getVariableValue("EAMLIGHT_INFOR_WS_URL");
    }

    public String getAuthenticationMode() { return Tools.getVariableValue("EAMLIGHT_AUTHENTICATION_MODE"); }

    public String getDefaultUser() { return Tools.getVariableValue("EAMLIGHT_DEFAULT_USER"); }

    @XmlTransient
    public String getPassphrase() { return Tools.getVariableValue("EAMLIGHT_PASSPHRASE"); }

    public String getEdmsDocListLink() { return edmsValues.get("DOCLIST_LINK"); }


    /************************
     * LINKS TO EXTENDED
     *
     ************************/

    public String getExtendedWOLink() {
        return eamlightValues.get("EXT_WOLINK");
    }

    public String getExtendedAssetLink() {
        return eamlightValues.get("EXT_ASSETLINK");
    }

    public String getExtendedPositionLink() {
        return eamlightValues.get("EXT_POSITIONLINK");
    }

    public String getExtendedSystemLink() {
        return eamlightValues.get("EXT_SYSTEMLINK");
    }

    public String getExtendedPartLink() {
        return eamlightValues.get("EXT_PARTLINK");
    }

    /************************
     * LINKS TO EAM INTEGRATIONS
     *
     ************************/

    public String getLinkToEAMIntegration() {
        return eamlightValues.get("EXT_EAMINTEG");
    }

    public String getEamlightOldURL() {
        return eamlightValues.get("EAMLIGHT_OLD_URL");
    }

    public String getServiceNowURL() {
        return eamlightValues.get("SERVICE_NOW_URL");
    }

    public String getPrintingLinkToAIS() {
        return eamlightValues.get("AISBI_PRINT");
    }

    public String getPrintingChecklistLinkToAIS() {
        return eamlightValues.get("AISBI_PRINT_CHECKLIST");
    }

    public String getGISProcedureLinkWO() {
        return eamlightValues.get("GIS_PROCEDURE_LINK_WO");
    }

    public String getGISProcedureLinkEQP() {
        return eamlightValues.get("GIS_PROCEDURE_LINK_EQP");
    }

    public String getDismacURL() {
        return eamlightValues.get("DISMAC_URL");
    }

    public String[] getDismacUserGroups() { return eamlightValues.get("DISMAC_USER_GROUPS").replaceAll("\\s+", "").trim().split(","); }

    public String[] getCryoEqpReplacementClasses() { return eamlightValues.get("CRYP_EQP_RPL_CLASSES").replaceAll("\\s+", "").trim().split(","); }

    public String getEDMSDoclightURL() {
        return eamlightValues.get("EDMS_DOCLIGHT_URL");
    }

}
