package ch.cern.cmms.eamlightejb.workorders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import java.io.Serializable;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = WorkOrderType.GET_TYPES_FOR_EXISTING_WO, query = "SELECT d.des_code, d.des_text, u.uco_rcode, u.uco_system"
				+ " FROM   r5descriptions d" + " RIGHT  OUTER JOIN r5ucodes u ON (d.des_rtype = uco_rentity AND"
				+ " d.des_code = u.uco_code AND" + " d.des_lang = 'EN')" + " WHERE  d.des_rentity = 'UCOD'"
				+ " AND    u.uco_rentity = 'JBTP'"
				+ " and (('BR' in ('BR', '*') and u.uco_rcode in ('BR', '*'))  or ('BR' not in ('BR', '*') and u.uco_rcode = 'BR' ))"
				+ " AND    ( ('view' = 'view' AND d.des_code = COALESCE(:oldType, d.des_code )) OR EXISTS"
				+ " (SELECT 'x'" + "  FROM   r5install i" + " WHERE  i.ins_code = 'JTAUTH'"
				+ " AND    upper(i.ins_desc) NOT IN ('YES', '+')) OR EXISTS" + " (SELECT 'x'" + " FROM   r5install i"
				+ " WHERE  i.ins_code = 'JTAUTHLV'" + " AND    upper(i.ins_desc) <> 'YES') OR EXISTS" + " (SELECT 'x'"
				+ " FROM   r5jobtypeauth j" + " WHERE  j.jta_jobtype = d.des_code" + " AND    j.jta_status = :status"
				+ " AND    (j.jta_group = '*' OR j.jta_group = :userGroup)" + " AND    j.jta_update = '+') OR"
				+ " (EXISTS (SELECT 'x'" + " FROM   r5jobtypeauth j" + " WHERE  j.jta_jobtype = d.des_code"
				+ " AND    j.jta_status = 'ALLSTATS'" + " AND    (j.jta_group = '*' OR j.jta_group = :userGroup)"
				+ " AND    j.jta_update = '+') AND NOT EXISTS" + " (SELECT 'x'" + " FROM   r5jobtypeauth j"
				+ " WHERE  j.jta_jobtype = d.des_code" + " AND    j.jta_status = :status"
				+ " AND    (j.jta_group = '*' OR j.jta_group = :userGroup))))" + " AND ( EXISTS( SELECT 'x'"
				+ " FROM r5fieldfiltertype" + " WHERE fft_function = 'WSJOBS'" + " AND d.des_code = fft_type )" + " OR"
				+ " NOT EXISTS(SELECT 'x'" + " FROM r5fieldfiltertype" + " WHERE fft_function = 'WSJOBS'" + " ) )"
				+ " ORDER  BY d.des_text", resultClass = WorkOrderType.class),
		@NamedNativeQuery(name = WorkOrderType.GET_TYPES_FOR_EXISTING_WO_PPM, query = "SELECT d.des_code, d.des_text, u.uco_rcode, u.uco_system"
				+ " FROM   r5descriptions d" + " RIGHT  OUTER JOIN r5ucodes u ON (d.des_rtype = uco_rentity AND"
				+ " d.des_code = u.uco_code AND" + " d.des_lang = 'EN')" + " WHERE  d.des_rentity = 'UCOD'"
				+ " AND    u.uco_rentity = 'JBTP'"
				+ " and (('PM' in ('BR', '*') and u.uco_rcode in ('BR', '*'))  or ('PM' not in ('BR', '*') and u.uco_rcode = 'PM' ))"
				+ " AND    ( ('view' = 'view' AND d.des_code = COALESCE(:oldType, d.des_code )) OR EXISTS"
				+ " (SELECT 'x'" + " FROM   r5install i" + " WHERE  i.ins_code = 'JTAUTH'"
				+ " AND    upper(i.ins_desc) NOT IN ('YES', '+')) OR EXISTS" + " (SELECT 'x'" + " FROM   r5install i"
				+ " WHERE  i.ins_code = 'JTAUTHLV'" + " AND    upper(i.ins_desc) <> 'YES') OR EXISTS" + " (SELECT 'x'"
				+ " FROM   r5jobtypeauth j" + " WHERE  j.jta_jobtype = d.des_code" + " AND    j.jta_status = :status"
				+ " AND    (j.jta_group = '*' OR j.jta_group = :userGroup)" + " AND    j.jta_update = '+') OR"
				+ " (EXISTS (SELECT 'x'" + " FROM   r5jobtypeauth j" + " WHERE  j.jta_jobtype = d.des_code"
				+ " AND    j.jta_status = 'ALLSTATS'" + " AND    (j.jta_group = '*' OR j.jta_group = :userGroup)"
				+ " AND    j.jta_update = '+') AND NOT EXISTS" + " (SELECT 'x'" + " FROM   r5jobtypeauth j"
				+ " WHERE  j.jta_jobtype = d.des_code" + " AND    j.jta_status = :status"
				+ " AND    (j.jta_group = '*' OR j.jta_group = :userGroup))))" + " AND ( EXISTS( SELECT 'x'"
				+ " FROM r5fieldfiltertype" + " WHERE fft_function = 'WSJOBS'" + " AND d.des_code = fft_type )" + " OR"
				+ " NOT EXISTS(SELECT 'x'" + " FROM r5fieldfiltertype" + " WHERE fft_function = 'WSJOBS'" + " ) )"
				+ " ORDER  BY d.des_text", resultClass = WorkOrderType.class),
		@NamedNativeQuery(name = WorkOrderType.GET_TYPES_FOR_NEW_WO, query = "select d.des_code, d.des_text, u.uco_rcode, u.uco_system from r5descriptions d RIGHT OUTER JOIN r5ucodes u on ( d.des_rtype = uco_rentity and d.des_code=u.uco_code and d.des_lang = 'EN' ) where d.des_rentity = 'UCOD' and u.uco_rentity = 'JBTP'   and u.uco_rcode IN ( '*','BR', 'ST','CAL','RP')  and ( exists ( select 'x' from r5jobtypeauth j where j.jta_jobtype = d.des_code  and j.jta_insert = '+'  and ( j.jta_group = :group or j.jta_group= '*' )) or  exists ( select 'x' from r5install i where i.ins_code = 'JTAUTH' and upper( i.ins_desc ) not in ( 'YES', '+' ) ) ) \n"
				+ "and ( exists( select 'x'\n" + "              from r5fieldfiltertype\n"
				+ "              where fft_function = 'WSJOBS' \n" + "              and d.des_code = fft_type ) \n"
				+ "      or\n" + "      not exists(select 'x'\n" + "              from r5fieldfiltertype\n"
				+ "              where fft_function = 'WSJOBS' \n" + "              ) )\n"
				+ "order by d.des_text", resultClass = WorkOrderType.class) })

public class WorkOrderType implements Serializable {

	public final static String GET_TYPES_FOR_EXISTING_WO = "WorkOrderStatus.GET_TYPES_FOR_EXISTING_WO";
	public final static String GET_TYPES_FOR_EXISTING_WO_PPM = "WorkOrderStatus.GET_TYPES_FOR_EXISTING_WO_PPM";
	public final static String GET_TYPES_FOR_NEW_WO = "WorkOrderStatus.GET_TYPES_FOR_NEW_WO";

	@Id
	@Column(name = "DES_CODE")
	private String code;
	@Column(name = "DES_TEXT")
	private String desc;
	@Column(name = "UCO_RCODE")
	private String rCode;
	@Column(name = "UCO_SYSTEM")
	private String system;

	public WorkOrderType() {
	}

	public WorkOrderType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

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

	/**
	 * @return the rCode
	 */
	public String getrCode() {
		return rCode;
	}

	/**
	 * @param rCode
	 *            the rCode to set
	 */
	public void setrCode(String rCode) {
		this.rCode = rCode;
	}

	/**
	 * @return the system
	 */
	public String getSystem() {
		return system;
	}

	/**
	 * @param system
	 *            the system to set
	 */
	public void setSystem(String system) {
		this.system = system;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WorkOrderType [" + (code != null ? "code=" + code + ", " : "")
				+ (desc != null ? "desc=" + desc + ", " : "") + (rCode != null ? "rCode=" + rCode + ", " : "")
				+ (system != null ? "system=" + system : "") + "]";
	}

	/**
	 * To Identify the default value
	 * 
	 * @return Defualt value in creation mode
	 */
	public boolean isDefaultValue() {
		return "BR".equals(rCode) && "+".equals(system);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof WorkOrderType)) {
			return false;
		}
		WorkOrderType other = (WorkOrderType) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		return true;
	}

}
