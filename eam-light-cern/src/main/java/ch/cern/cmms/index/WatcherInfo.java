package ch.cern.cmms.index;

import ch.cern.eam.wshub.core.client.InforClient;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@NamedNativeQueries({
        @NamedNativeQuery(
                name = WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS,
                query = "SELECT USR_CODE, USR_DESC, USR_EMAILADDRESS " +
                        "FROM R5USERS " +
                        "WHERE EXISTS(" +
                        "    SELECT 1 FROM R5DEPARTMENTSECURITY " +
                        "    WHERE DSE_MRC = (SELECT EVT_MRC FROM R5EVENTS WHERE EVT_CODE = :evtCode)" +
                        ") " +
                        "AND USR_CODE IN (:usrList) ",
                resultClass = WatcherInfo.class
        )
})
public class WatcherInfo implements Serializable {
    public final static String FILTER_WATCHERS_BY_WO_ACCESS = "WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS";

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

    public static List<WatcherInfo> getFilteredWatcherInfo(InforClient inforClient, List<String> userCodes, String woCode) {
        if (userCodes.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        userCodes.forEach(user -> sb.append("'").append(user).append("',"));
        sb.deleteCharAt(sb.length() - 1);

        return inforClient.getTools().getEntityManager()
                .createNamedQuery(WatcherInfo.FILTER_WATCHERS_BY_WO_ACCESS, WatcherInfo.class)
                .setParameter("evtCode", woCode)
                .setParameter("usrList", sb.toString())
                .getResultList();
    }

}





