/**
 * 
 */
package ch.cern.cmms.eamlightejb.equipment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

/**
 * Children of an Equipment
 *
 */

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(
				name = EquipmentChildren.GET_EQUIPMENT_CHILDREN, 
				query = "SELECT STC_PARENT, STC_CHILD,"
				+ " STC_PARENTRTYPE, DECODE(STC_PARENTRTYPE,'A','Asset','P','Position','S','System','L','Location') STC_PARENTRTYPE_DESC,"
				+ " STC_CHILDRTYPE, DECODE(STC_CHILDRTYPE,'A','Asset','P','Position','S','System','L','Location') STC_CHILDRTYPE_DESC,"
				+ " DECODE(STC_ROLLDOWN,'+','true','false') STC_ROLLDOWN, DECODE(STC_ROLLUP,'+','true','false') STC_ROLLUP"
				+ " FROM R5STRUCTURES WHERE STC_PARENT = :equipment", 
				resultClass = EquipmentChildren.class),
		@NamedNativeQuery(
				name = EquipmentChildren.GET_EQUIPMENT_PARENTS, 
				query = "SELECT STC_PARENT, STC_CHILD,"
				+ " STC_PARENTRTYPE, DECODE(STC_PARENTRTYPE,'A','Asset','P','Position','S','System','L','Location') STC_PARENTRTYPE_DESC,"
				+ " STC_CHILDRTYPE, DECODE(STC_CHILDRTYPE,'A','Asset','P','Position','S','System','L','Location') STC_CHILDRTYPE_DESC,"
				+ " DECODE(STC_ROLLDOWN,'+','true','false') STC_ROLLDOWN, DECODE(STC_ROLLUP,'+','true','false') STC_ROLLUP"
				+ " FROM R5STRUCTURES WHERE STC_CHILD = :equipment", 
				resultClass = EquipmentChildren.class)})
public class EquipmentChildren implements Serializable {

	public final static String GET_EQUIPMENT_CHILDREN = "EquipmentChildren.GET_EQUIPMENT_CHILDREN";
	public final static String GET_EQUIPMENT_PARENTS = "EquipmentChildren.GET_EQUIPMENT_PARENTS";

	@Id
	@Column(name = "STC_PARENT")
	private String parentCode;

	@Id
	@Column(name = "STC_CHILD")
	private String childCode;

	@Id
	@Column(name = "STC_PARENTRTYPE")
	private String parentType;

	@Column(name = "STC_PARENTRTYPE_DESC")
	private String parentTypeDesc;

	@Id
	@Column(name = "STC_CHILDRTYPE")
	private String childType;

	@Column(name = "STC_CHILDRTYPE_DESC")
	private String childTypeDesc;

	@Column(name = "STC_ROLLUP")
	private String costRollUp;

	@Column(name = "STC_ROLLDOWN")
	private String dependent;

	/**
	 * 
	 */
	public EquipmentChildren() {

	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getChildCode() {
		return childCode;
	}

	public void setChildCode(String childCode) {
		this.childCode = childCode;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}

	public String getCostRollUp() {
		return costRollUp;
	}

	public void setCostRollUp(String costRollUp) {
		this.costRollUp = costRollUp;
	}

	public String getDependent() {
		return dependent;
	}

	public void setDependent(String dependent) {
		this.dependent = dependent;
	}

	public String getParentTypeDesc() {
		return parentTypeDesc;
	}

	public void setParentTypeDesc(String parentTypeDesc) {
		this.parentTypeDesc = parentTypeDesc;
	}

	public String getChildTypeDesc() {
		return childTypeDesc;
	}

	public void setChildTypeDesc(String childTypeDesc) {
		this.childTypeDesc = childTypeDesc;
	}

	@Override
	public String toString() {
		return "EquipmentChildren [" + (parentCode != null ? "parentCode=" + parentCode + ", " : "")
				+ (childCode != null ? "childCode=" + childCode + ", " : "")
				+ (parentType != null ? "parentType=" + parentType + ", " : "")
				+ (parentTypeDesc != null ? "parentTypeDesc=" + parentTypeDesc + ", " : "")
				+ (childType != null ? "childType=" + childType + ", " : "")
				+ (childTypeDesc != null ? "childTypeDesc=" + childTypeDesc + ", " : "")
				+ (costRollUp != null ? "costRollUp=" + costRollUp + ", " : "")
				+ (dependent != null ? "dependent=" + dependent : "") + "]";
	}

}
