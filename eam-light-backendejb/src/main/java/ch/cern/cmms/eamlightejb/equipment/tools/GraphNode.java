package ch.cern.cmms.eamlightejb.equipment.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.cern.cmms.eamlightejb.equipment.EquipmentChildren;
import ch.cern.eam.wshub.core.annotations.GridField;

public class GraphNode {

	@GridField(name="stc_child")
	private String id;

	@GridField(name="stc_child_org")
	private String idOrg;

	private String name;
	@GridField(name="stc_childtype")
	private String type;
	
	private GraphNode parent;
	
	private List<GraphNode> children = new ArrayList<>();
	private List<EquipmentChildren> parents;
	
	public GraphNode(String id, String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public GraphNode() {

	}

	
	public GraphNode clone() {
		return new GraphNode(id, name, type);
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
	
	public GraphNode getParent() {
		return parent;
	}
	
	public void setParent(GraphNode parent) {
		this.parent = parent;
	}
	
	public List<GraphNode> getChildren() {
		return children;
	}
	
	public void setChildren(List<GraphNode> children) {
		this.children = children;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<EquipmentChildren> getParents() {
		return parents;
	}
	
	public void setParents(List<EquipmentChildren> parents) {
		this.parents = parents;
	}

	public String getIdOrg() {
		return idOrg;
	}

	public void setIdOrg(String idOrg) {
		this.idOrg = idOrg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((parents == null) ? 0 : parents.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GraphNode other = (GraphNode) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
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
		if (parents == null) {
			if (other.parents != null)
				return false;
		} else if (!parents.equals(other.parents))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GraphNode [id=" + id + ", name=" + name + ", type=" + type + ", parent=" + parent + ", children="
				+ children.size() + ", parents=" + parents + "]";
	}
	
}
