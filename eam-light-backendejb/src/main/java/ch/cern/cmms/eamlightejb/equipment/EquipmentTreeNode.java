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
				query = "select id as id, name as name, null as parent from ASBMGR.C_NODES "+
						" where id = :equipment "+
						" union "+
						" select id2 as id, childnode.name as name, id1 as parent from ASBMGR.C_NOD_STR "+
						" left join ASBMGR.C_NODES childnode on childnode.id = id2 "+
						" where id1 = :equipment ",
				resultClass = EquipmentTreeNode.class),
		@NamedNativeQuery(name = EquipmentTreeNode.GET_TREE,
				query = "SELECT * FROM (  " +
						"    SELECT   " + // INCLUDE ROOT NODE WITH NO PARENT
						"        obj_code AS ID,   " +
						"        null AS PARENT,   " +
						"        obj_desc AS NAME,   " +
						"        obj_obrtype AS TYPE,   " +
						"        0 AS TREELEVEL,   " +
						"        0 AS SEQUENCE,  " +
						"        NULL AS STC_PARENTRTYPE  " +
						"    FROM r5objects   " +
						"    WHERE obj_code = :equipment   " +
						"    UNION ALL  " +
						"    SELECT   " +
						"        obj_code AS ID,   " +
						"        stc_parent AS PARENT,   " +
						"        obj_desc AS NAME,   " +
						"        obj_obrtype AS TYPE,   " +
						"        MIN(LEVEL) AS TREELEVEL,   " + // DONT VISIT NODES WE ALREADY KNOW THE CHILDREN OF
						"        MIN(stc_sequence) AS SEQUENCE,  " + // DONT VISIT NODES WE ALREADY KNOW THE CHILDREN OF
						"        stc_parentrtype  " +
						"    FROM   " +
						"        r5structures tree  " +
						"        INNER JOIN r5objects  " + // IF THEY ARE NOT USED (OUT OF SERVICE), DON'T INCLUDE THEM
						"            ON obj_code = tree.stc_child  " + // UNIDIRECTIONAL ONLY
						"            AND obj_notused = '-'   " +
						"    WHERE  " +
						"        stc_childtype IN ('P', 'A', 'S')  " +
						"    START WITH tree.stc_parent = :equipment   " +
						"    CONNECT BY NOCYCLE PRIOR tree.stc_child = tree.stc_parent   " +
						"    GROUP BY obj_code, stc_parent, obj_desc, obj_obrtype, stc_parentrtype " + // DONT VISIT NODES WE ALREADY KNOW THE CHILDREN OF
						")   " +
						"WHERE ROWNUM < 20000 " + // LIMIT FOR MEMORY
						"ORDER BY treelevel, sequence  ",
				resultClass = EquipmentTreeNode.class),

})
public class EquipmentTreeNode  implements Serializable{

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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EquipmentTreeNode other = (EquipmentTreeNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	} 
	
}
