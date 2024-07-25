package ch.cern.cmms.eamlightweb.utilities;

public class AutocompleteEntityFilter {
    private String code;
    private String entityClass;

    public AutocompleteEntityFilter(String code, String entityClass) {
        this.code = code;
        this.entityClass = entityClass;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }
}
