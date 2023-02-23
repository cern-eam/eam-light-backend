package ch.cern.cmms.plugins;

import ch.cern.eam.wshub.core.client.InforClient;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.ok;

public class SharedPluginImpl implements SharedPlugin {

    private final static String GET_DATES_PERMISSIONS = "SELECT USRGROUP, PLO_ELEMENTID, FUNC, ATTR " +
            " , DECODE(ATTR, 'P', 'READONLY', 'WRITE') AS ACCESSRIGHTS " +
            "FROM (SELECT EMN_GROUP                                                                                  " +
            "               USRGROUP " +
            "           , PLO_ELEMENTID " +
            "           , MAX(EMN_FUNCTION) " +
            "                 KEEP ( DENSE_RANK FIRST ORDER BY DECODE(PLO_ATTRIBUTE, 'O', 0, 'R', 1, 'P', 2, 3) )    " +
            "            AS FUNC " +
            "           , MAX(PLO_ATTRIBUTE) " +
            "                 KEEP ( DENSE_RANK FIRST ORDER BY DECODE(PLO_ATTRIBUTE, 'O', 0, 'R', 1, 'P', 2, 3) )    " +
            "            AS ATTR " +
            "      FROM R5EXTMENUS E1 " +
            "               LEFT JOIN r5pagelayout " +
            "                         ON PLO_PAGENAME = EMN_FUNCTION " +
            "                             AND PLO_USERGROUP = EMN_GROUP " +
            "                             AND PLO_ELEMENTID IN ( " +
            "                                                   'schedenddate', " +
            "                                                   'reqenddate', " +
            "                                                   'reqstartdate', " +
            "                                                   'schedstartdate' " +
            "                                 ) " +
            "      WHERE EMN_GROUP = (SELECT USR_GROUP FROM R5USERS WHERE USR_CODE = :usrCode ) " +
            "        AND EMN_FUNCTION IN (SELECT FUN_CODE " +
            "                             FROM R5FUNCTIONS " +
            "                             WHERE NVL(FUN_APPLICATION, FUN_CODE) = 'WSJOBS') " +
            "      AND PLO_ATTRIBUTE IN ('O', 'R', 'P') " +
            "      AND PLO_PRESENTINJSP = 'Y' " +
            " AND NOT EXISTS ( " +
            "        SELECT E2.EMN_HIDE, E2.EMN_CODE, E2.EMN_PARENT " +
            "        FROM R5EXTMENUS E2 " +
            "        WHERE EMN_HIDE = '+' " +
            "        CONNECT BY PRIOR E2.EMN_PARENT = E2.EMN_CODE " +
            "        START WITH E2.EMN_CODE = E1.EMN_CODE " +
            "    )" +
            "      GROUP BY EMN_GROUP, PLO_ELEMENTID) A ";

    @Override
    public Map<String, Map<String, String>> getDatesPermissions(InforClient inforClient, String username) {
        final List<Object[]> rows =
                inforClient.getTools().getEntityManager().createNativeQuery(GET_DATES_PERMISSIONS)
                .setParameter("usrCode", username)
                .getResultList()
                ;

        final Map<String, Map<String, String>> permissions = rows.stream()
                .map(obj -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("USRGROUP", (String) obj[0]);
                    map.put("PLO_ELEMENTID", (String) obj[1]);
                    map.put("FUNC", (String) obj[2]);
                    map.put("ATTR", (String) obj[3]);
                    map.put("ACCESSRIGHTS", (String) obj[4]);
                    return map;
                })
                .collect(Collectors.toMap(s -> s.get("PLO_ELEMENTID"), Function.identity()));
        ;
        return permissions;
    }

    @Override
    public String sayHello() {
        return "Hello from CERN!";
    }
}
