package ch.cern.cmms.eamlightejb.index;

import ch.cern.eam.wshub.core.client.InforClient;

import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

/**
 * Session Bean implementation class IndexEJB
 */
@Stateless
@LocalBean
public class IndexEJB {

    @Inject
    private InforClient inforClient;

    //
    // Get the index results
    //
    public List<IndexResult> getIndexResults(String hint, String userCode) {
        String query = indexQueryBuilder();
        inforClient.getTools().log(Level.INFO, "SEARCH / " + userCode + " / " + hint + " / " + query);
        @SuppressWarnings("unchecked")
        List<IndexResult> results = inforClient.getTools().getEntityManager().createNativeQuery(query, IndexResult.class).setParameter("hint", hint + "%")
                .setParameter("activeUser", userCode).getResultList();
        // Reyurn results
        return results;
    }

    /**
     * Get just a single result with the keyword
     *
     * @param hint     Keyword to search
     * @param userCode Username in session
     * @return The single result or null in case it does not find it
     */
    public IndexResult getIndexSingleResult(String hint, String userCode) {
        String query = indexQueryBuilderSingle();
        inforClient.getTools().log(Level.INFO, "SEARCH / " + userCode + " / " + hint + " / " + query);
        try {
            IndexResult result = (IndexResult) inforClient.getTools().getEntityManager().createNativeQuery(query, IndexResult.class).setParameter("hint", hint)
                    .setParameter("activeUser", userCode).getSingleResult();
            // Reyurn results
            return result;
        } catch (NoResultException e) {
            // No result found
            return null;
        }
    }

    /*
     * Query builder for the home screen - single result
     */
    private String indexQueryBuilderSingle() {
        StringBuilder obj_obtypeIN = new StringBuilder();
        StringJoiner sj = new StringJoiner(",", "(", ")");
        sj.setEmptyValue("");
        sj.add("\'A\'");
        sj.add("\'P\'");
        sj.add("\'S\'");
        obj_obtypeIN.append(sj.toString());

        StringJoiner sjQuery = new StringJoiner(" union ");
        if (!obj_obtypeIN.toString().isEmpty()) {
            StringBuilder query1 = new StringBuilder();
            query1.append(
                    "SELECT OBJ_OBRTYPE ENTTYPE, OBJ_CODE CODE, OBJ_DESC DESCRIPTION, OBJ_MRC MRC, OBJ_SERIALNO SERIAL, OBJ_ALIAS ALIAS");
            query1.append(" FROM r5objects");
            query1.append(" WHERE obj_obrtype IN ");
            query1.append(obj_obtypeIN.toString());
            query1.append(
                    " AND obj_mrc IN (SELECT DISTINCT DSE_MRC from R5DEPARTMENTSECURITY where DSE_USER = :activeUser)");
            query1.append(" AND (obj_code = :hint OR obj_alias = :hint OR obj_serialno = :hint OR obj_udfchar45 = :hint) ");
            sjQuery.add(query1.toString());
        }

        String query2 = "select \'PART\' ENTTYPE, PAR_CODE CODE, PAR_DESC DESCRIPTION, null MRC, null SERIAL, null ALIAS FROM r5parts WHERE"
                + " par_code = :hint ";
        sjQuery.add(query2);

        StringBuilder query3 = new StringBuilder();
        query3.append(
                "select 'JOB' ENTTYPE, EVT_CODE CODE, EVT_DESC DESCRIPTION, EVT_MRC MRC, null SERIAL, null ALIAS from r5events ");
        query3.append("WHERE evt_type in ('JOB','PPM') and EVT_STATUS <> 'A' ");
        query3.append(
                "AND evt_mrc IN (SELECT DISTINCT DSE_MRC from R5DEPARTMENTSECURITY where DSE_USER = :activeUser) ");
        query3.append("AND EVT_CODE = :hint ");
        sjQuery.add(query3.toString());

        return sjQuery.toString();
    }

    /*
     * Query builder for the home screen
     */
    private String indexQueryBuilder() {
        StringBuilder obj_obtypeIN = new StringBuilder();
        StringJoiner sj = new StringJoiner(",", "(", ")");
        sj.setEmptyValue("");

        sj.add("\'A\'");

        sj.add("\'P\'");

        sj.add("\'S\'");

        obj_obtypeIN.append(sj.toString());

        StringJoiner sjQuery = new StringJoiner(" union ");
        if (!obj_obtypeIN.toString().isEmpty()) {
            StringBuilder query1 = new StringBuilder();
            query1.append(
                    "SELECT OBJ_OBRTYPE ENTTYPE, OBJ_CODE CODE, OBJ_DESC DESCRIPTION, OBJ_MRC MRC, OBJ_SERIALNO SERIAL, OBJ_ALIAS ALIAS");
            query1.append(" FROM r5objects");
            query1.append(" WHERE obj_obrtype IN ");
            query1.append(obj_obtypeIN.toString());
            query1.append(
                    " AND obj_mrc IN (SELECT DISTINCT DSE_MRC from R5DEPARTMENTSECURITY where DSE_USER = :activeUser)");
            query1.append(
                    " AND (obj_code like :hint ESCAPE '\\' OR obj_alias like :hint ESCAPE '\\' OR obj_serialno like :hint ESCAPE '\\' OR obj_udfchar45 like :hint ESCAPE '\\') and ROWNUM < 101 ");
            sjQuery.add(query1.toString());
        }


        String query2 = "select \'PART\' ENTTYPE, PAR_CODE CODE, PAR_DESC DESCRIPTION, null MRC, null SERIAL, null ALIAS FROM r5parts WHERE"
                + " par_code like :hint ESCAPE '\\' and ROWNUM < 101 ";
        sjQuery.add(query2);

        StringBuilder query3 = new StringBuilder();
        query3.append(
                "select 'JOB' ENTTYPE, EVT_CODE CODE, EVT_DESC DESCRIPTION, EVT_MRC MRC, null SERIAL, null ALIAS from r5events ");
        query3.append("WHERE evt_type in ('JOB','PPM') and EVT_STATUS <> 'A' ");
        query3.append(
                "AND evt_mrc IN (SELECT DISTINCT DSE_MRC from R5DEPARTMENTSECURITY where DSE_USER = :activeUser) ");
        query3.append("AND EVT_CODE like :hint ESCAPE '\\' and ROWNUM < 101 ");
        sjQuery.add(query3.toString());

        return sjQuery.toString();
    }

}
