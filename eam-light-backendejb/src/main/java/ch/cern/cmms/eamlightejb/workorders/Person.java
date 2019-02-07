package ch.cern.cmms.eamlightejb.workorders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="R5PERSONNEL")
@NamedQueries({
	@NamedQuery(name=Person.PERSON_GETPERSON, query="SELECT per FROM Person per WHERE per.code = :codeParam"),
	@NamedQuery(name=Person.PERSON_GETPERSONS, query="SELECT per FROM Person per WHERE per.code LIKE :codeParam OR per.desc LIKE :codeParam")
})
public class Person  implements Serializable {

	public static final String PERSON_GETPERSONS = "PERSON_GETPERSONS";
	public static final String PERSON_GETPERSON = "PERSON_GETPERSON";
	
	@Id
	@Column(name="PER_CODE")
	private String code;
	@Column(name="PER_DESC")
	private String desc;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}



}
