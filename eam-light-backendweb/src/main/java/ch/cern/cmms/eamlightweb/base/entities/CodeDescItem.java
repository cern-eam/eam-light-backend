package ch.cern.cmms.eamlightweb.base.entities;

import ch.cern.eam.wshub.core.annotations.GridField;

public class CodeDescItem {

    @GridField(name = "lookupvalue")
    private String code;
    @GridField(name = "lookupdescription")
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
