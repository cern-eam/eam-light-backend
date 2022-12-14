package ch.cern.cmms.eamlightejb.index;

import java.io.Serializable;
import java.util.Objects;

public class IndexResultId implements Serializable {

    private String code;
    private String type;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final IndexResultId that = (IndexResultId) o;
        return Objects.equals(code, that.code) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, type);
    }
}
