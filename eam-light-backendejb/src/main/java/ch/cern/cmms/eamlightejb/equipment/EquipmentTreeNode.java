package ch.cern.cmms.eamlightejb.equipment;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Transient;

/**
 * Tree Node for Equipment Structure
 */
@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = EquipmentTreeNode.GET_TREE_ASBMGR,
            query = "select id as id, name as name, null as parent from ASBMGR.C_NODES " +
                    " where id = :equipment " +
                    " union " +
                    " select id2 as id, childnode.name as name, id1 as parent from ASBMGR.C_NOD_STR " +
                    " left join ASBMGR.C_NODES childnode on childnode.id = id2 " +
                    " where id1 = :equipment ",
            resultClass = EquipmentTreeNode.class),
    @NamedNativeQuery(name = EquipmentTreeNode.GET_TREE,
            query = "SELECT * FROM ( " +
            "    WITH tree AS ( " +
            "        SELECT " +
            "            STC_childtype, " +
            "            stc_childrtype, " +
            "            stc_child, " +
            "            obj_desc AS child_desc, " +
            "            stc_parenttype, " +
            "            stc_parentrtype, " +
            "            stc_parent, " +
            "            stc_sequence, " +
            "            NVL(stc_lastsaved, stc_updated) stc_upd, " +
            "            stc_child_org, " +
            "            stc_parent_org " +
            "        FROM r5structures " +
            "             INNER JOIN r5objects " +
            "                ON obj_code = stc_child " +
            "                AND obj_notused = '-' " +
            "        WHERE STC_CHILDTYPE NOT IN ('PB', 'PM') " +
            "        UNION ALL " +
            "        SELECT " +
            "            placeholder02 AS stc_childtype, " +
            "            placeholder02 AS stc_childrtype, " +
            "            part || ' - ' || lot AS stc_child, " +
            "            description AS child_desc, " +
            "            obj_obtype AS stc_parenttype, " +
            "            obj_obrtype AS stc_parentrtype, " +
            "            main_equipment AS stc_parent, " +
            "            NULL AS stc_sequence, " +
            "            last_issue_date AS stc_upd, " +
            "            placeholder03 AS stc_child_org, " +
            "            obj_org AS stc_parent_org " +
            "        FROM CERN_VW_EQP_PARTS " +
            "             INNER JOIN r5objects ON obj_code = main_equipment " +
            "        WHERE placeholder01 = '+' " +
            "        UNION ALL " +
            "        SELECT " +
            "            'LOT' AS stc_childtype, " +
            "            'LOT' AS stc_childrtype, " +
            "            evt_udfchar28 || ' - ' || evt_udfchar29 AS stc_child, " +
            "            CASE WHEN trim(BOTH FROM upper(chp.PAR_DESC)) = trim(BOTH FROM upper(lot_desc)) THEN chp.PAR_DESC " +
            "                 ELSE chp.PAR_DESC|| ', ' ||l.lot_desc " +
            "            END AS child_desc, " +
            "            'LOT' AS stc_parenttype, " +
            "            'LOT' AS stc_parentrtype, " +
            "            evt_udfchar12 || ' - ' ||evt_udfchar16 AS stc_parent, " +
            "            NULL AS stc_sequence, " +
            "            NVL(evt_created, evt_date) AS stc_upd, " +
            "            chp.par_org AS stc_child_org, " +
            "            pp.par_org AS stc_parent_org " +
            "        FROM r5events e " +
            "             INNER JOIN r5lots l ON lot_code = evt_udfchar29 " +
            "             INNER JOIN r5parts chp ON chp.par_code = e.evt_udfchar28 " +
            "             INNER JOIN r5parts pp ON pp.par_code = e.evt_udfchar12 " +
            "        WHERE e.evt_org = '*' " +
            "          AND evt_class ='BATCH' " +
            "          AND chp.par_udfchkbox02 = '+' " +
            "    ) " +
            "    SELECT " +
            "        obj_code AS ID, " +
            "        NULL AS PARENT, " +
            "        obj_desc AS NAME, " +
            "        obj_obrtype AS TYPE, " +
            "        0 AS TREELEVEL, " +
            "        0 AS SEQUENCE, " +
            "        NULL AS STC_PARENTRTYPE " +
            "    FROM r5objects " +
            "    WHERE obj_code = :equipment " +
            "    UNION ALL " +
            "    SELECT " +
            "        stc_child AS ID, " +
            "        stc_parent AS PARENT, " +
            "        child_desc AS NAME, " +
            "        stc_childrtype AS TYPE, " +
            "        MIN(LEVEL) AS TREELEVEL, " +
            "        MIN(stc_sequence) AS SEQUENCE, " +
            "        stc_parentrtype " +
            "    FROM tree " +
            "    WHERE stc_childtype IN ('P', 'A', 'S', 'L', 'LOT', 'PART') " +
            "    START WITH tree.stc_parent = :equipment " +
            "    CONNECT BY NOCYCLE PRIOR tree.stc_child = tree.stc_parent " +
            "    GROUP BY stc_child, stc_parent, child_desc, stc_childrtype, stc_parentrtype " +
            "    ORDER BY treelevel, sequence, ID " +
            ") " +
            "WHERE ROWNUM < 20000", // LIMIT FOR MEMORY
            resultClass = EquipmentTreeNode.class),
})
public class EquipmentTreeNode implements Serializable {

    public final static String GET_TREE = "EquipmentChildren.GET_TREE";
    public final static String GET_TREE_ASBMGR = "EquipmentChildren.GET_TREE_ASBMGR";

    @Id
    @Column(name = "ID")
    private String id;

    @Id
    @Column(name = "PARENT")
    private String parent;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Transient
    private List<EquipmentChildren> parents;

    @Transient
    private Integer nodeparentid;

    /**
     *
     */
    public EquipmentTreeNode() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<EquipmentChildren> getParents() {
        return parents;
    }

    public void setParents(List<EquipmentChildren> parents) {
        this.parents = parents;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNodeparentid() {
        return nodeparentid;
    }

    public void setNodeparentid(Integer nodeparentid) {
        this.nodeparentid = nodeparentid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EquipmentTreeNode other = (EquipmentTreeNode) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        return true;
    }

}
