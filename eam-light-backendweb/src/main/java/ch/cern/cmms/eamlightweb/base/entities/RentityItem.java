package ch.cern.cmms.eamlightweb.base.entities;

import ch.cern.eam.wshub.core.annotations.GridField;

public class RentityItem {

    @GridField(name = "userdefinedfieldvalue")
    private String code;
    @GridField(name = "description")
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
