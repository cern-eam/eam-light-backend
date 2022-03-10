package ch.cern.cmms.eamlightejb.watchers;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@NamedNativeQueries({
        @NamedNativeQuery(
                name = WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS_HINT,
                query = " SELECT USR_CODE, USR_DESC, USR_EMAILADDRESS " +
                        " FROM R5USERS " +
                        "     INNER JOIN R5DEPARTMENTSECURITY " +
                        "        ON DSE_MRC = (SELECT EVT_MRC FROM R5EVENTS WHERE EVT_CODE = :evtCode ) " +
                        "        AND DSE_USER = USR_CODE " +
                        " WHERE USR_CODE LIKE :hint " +
                        " OR  REGEXP_LIKE(( " +
                        "         SELECT LISTAGG(TRIM(TRIM(CHR(9) FROM UPPER(NAME))), ',') WITHIN GROUP ( ORDER BY NAME) " +
                            " FROM ( " +
                            "         SELECT regexp_substr(USR_DESC, '[^ ]+', 1, level) AS NAME " +
                            "         FROM DUAL " +
                            "         CONNECT BY regexp_substr(USR_DESC, '[^ ]+', 1, level) IS NOT NULL " +
                            " ) " +
                        " ) " +
                        "     , :regex )",
                resultClass = WatcherInfo.class
        ),
        @NamedNativeQuery(
                name = WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS_LIST,
                query = "SELECT USR_CODE, USR_DESC, USR_EMAILADDRESS " +
                        "FROM R5USERS " +
                        "     INNER JOIN R5DEPARTMENTSECURITY " +
                        "        ON DSE_MRC = (SELECT EVT_MRC FROM R5EVENTS WHERE EVT_CODE = :evtCode ) " +
                        "        AND DSE_USER = USR_CODE " +
                        " WHERE USR_CODE IN :usrList ",
                resultClass = WatcherInfo.class
        )
})
public class WatcherInfo implements Serializable {
    public final static String FILTER_WATCHERS_BY_WO_ACCESS_HINT = "WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS_HINT";
    public final static String FILTER_WATCHERS_BY_WO_ACCESS_LIST = "WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS_LIST";

    @Id
    @Column(name = "USR_CODE")
    private String userCode;

    @Column(name = "USR_DESC")
    private String description;

    @Column(name = "USR_EMAILADDRESS")
    private String emailAddress;

    public WatcherInfo() {

    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}





