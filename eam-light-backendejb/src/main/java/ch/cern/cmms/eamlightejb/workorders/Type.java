package ch.cern.cmms.eamlightejb.workorders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import java.io.Serializable;

@Entity
@NamedNativeQueries({
	@NamedNativeQuery(
			name="problemCodes", 
			query=
			"SELECT c.RQM_CODE CODE,  c.RQM_DESC DESCRIPTION " +
					"FROM R5OBJECTS o, R5REQCLASSES s , R5REQUIRCODES c " +
					"WHERE OBJ_NOTUSED='-' " +
					"AND OBJ_OBTYPE IN ('A', 'L', 'S', 'P') " +
					"AND o.OBJ_CLASS = s.RCL_CLASS  " +
					"AND s.RCL_REQM =  c.RQM_CODE " +
					"AND o.OBJ_CODE = :codeParam " +
					"AND c.RQM_GEN= '-' " +
					"UNION  " +
					"SELECT c.RQM_CODE CODE, c.RQM_DESC DESCRIPTION " +
					"FROM R5REQUIRCODES c " +
					"WHERE c.RQM_GEN = '+' " +
					"ORDER BY 1",
					resultClass=Type.class),
	@NamedNativeQuery(
			name="actionCodes",
			query=
			"SELECT c.ACC_CODE CODE,  c.ACC_DESC DESCRIPTION "+
					"FROM R5OBJECTS o, r5actclasses s , r5actioncodes c "+
					"WHERE OBJ_NOTUSED='-' "+
					"AND OBJ_OBTYPE IN ('A', 'L', 'S', 'P') "+
					"AND o.OBJ_CLASS = s.ACL_CLASS "+
					"AND s.ACL_ACTION =  c.ACC_CODE "+
					"AND o.OBJ_CODE = :codeParam "+
					"AND c.ACC_GEN = '-' "+
					"UNION "+
					"SELECT c.ACC_CODE CODE,  c.ACC_DESC DESCRIPTION "+
					"FROM r5actioncodes c "+
					"WHERE c.ACC_GEN = '+' "+
					"ORDER BY 1",
					resultClass=Type.class),
	@NamedNativeQuery(
			name="causeCodes",
			query=
			"SELECT c.CAU_CODE CODE, c.CAU_DESC DESCRIPTION " +
					"FROM R5OBJECTS o, r5causeclasses s, r5causes   c " +
					"WHERE o.OBJ_NOTUSED='-' " +
					"AND o.OBJ_OBTYPE IN ('A', 'L', 'S', 'P') " +
					"AND o.OBJ_CLASS = s.CAC_CLASS " +
					"AND  s.CAC_CAUSE = c.CAU_CODE " +
					"AND o.OBJ_CODE = :codeParam " +
					"AND c.CAU_GEN= '-' " +
					"UNION  " +
					"SELECT c.CAU_CODE CODE, c.CAU_DESC DESCRIPTION " +
					"FROM r5causes   c " +
					"WHERE c.CAU_GEN= '+' " +
					"ORDER BY 1",
					resultClass=Type.class),

	@NamedNativeQuery(
			name="failureCodes",
			query=
			"SELECT c.FAL_CODE CODE,  c.FAL_DESC DESCRIPTION " +
					"FROM R5OBJECTS o, r5failureclasses s, r5failures c " +
					"WHERE OBJ_NOTUSED='-' " +
					"AND OBJ_OBTYPE IN ('A', 'L', 'S', 'P') " +
					"AND o.OBJ_CLASS = s.FCA_CLASS " +
					"AND s.FCA_FAILURE =  c.FAL_CODE " +
					"AND o.OBJ_CODE = :codeParam " +
					"AND c.FAL_GEN= '-' " +
					"UNION  " +
					"SELECT c.FAL_CODE CODE, c.FAL_DESC DESCRIPTION " +
					"FROM r5failures   c " +
					"WHERE c.FAL_GEN= '+' " +
					"ORDER BY 1",
					resultClass=Type.class)
})
public class Type  implements Serializable {

	@Id
	@Column(name="CODE")
	private String code;
	@Column(name="DESCRIPTION")
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
