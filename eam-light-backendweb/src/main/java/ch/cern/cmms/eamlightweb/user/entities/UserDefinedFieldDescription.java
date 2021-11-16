package ch.cern.cmms.eamlightweb.user.entities;

import ch.cern.eam.wshub.core.annotations.GridField;

public class UserDefinedFieldDescription {

    @GridField(name="udf_lookuprentity")
    private String lookupREntity;

    @GridField(name="udf_lookuptype")
    private String lookupType;

    @GridField(name="udf_uom")
    private String uom;

    public String getLookupREntity() {
        return lookupREntity;
    }

    public void setLookupREntity(String lookupREntity) {
        this.lookupREntity = lookupREntity;
    }

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
