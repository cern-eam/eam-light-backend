package ch.cern.cmms.eamlightejb.workorders;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import java.io.Serializable;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = WorkOrderStatus.GET_STATUSES_FOR_EXISTING_WO, query = "select d.des_code, d.des_text, u.uco_rcode, u.uco_system\n"
				+ "from r5auth a, r5entities e, r5descriptions d   RIGHT OUTER JOIN r5ucodes u on ( d.des_rtype = uco_rentity and d.des_code=u.uco_code and d.des_lang = 'EN' ) \n"
				+ "where a.aut_statnew not in(select uco_code from r5ucodes where uco_rentity=e.ent_statent and uco_rcode \n"
				+ "  in('A','B')) and ((a.aut_user = :user and a.aut_group = '*' and a.aut_status = :status\n"
				+ "and a.aut_statnew <> '*' and d.des_code = a.aut_statnew)\n"
				+ "or (a.aut_user = ':user' and a.aut_group = '*' and a.aut_status = '*' and a.aut_statnew <> '*'\n"
				+ "and d.des_code = a.aut_statnew\n" + "and not exists (select null from r5auth x\n"
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group\n"
				+ "and x.aut_status = :status and x.aut_statnew = a.aut_statnew\n"
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')\n" + ")\n"
				+ "or (a.aut_user = ':user' and a.aut_group = '*' and a.aut_status = :status and a.aut_statnew = '*'\n"
				+ "and not exists (select null from r5auth x\n"
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group\n"
				+ "and x.aut_status = a.aut_status and x.aut_statnew = d.des_code\n"
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')\n" + ")\n"
				+ "or (a.aut_group = :group and a.aut_user = '*'\n" + "and not exists (select null from r5auth x\n"
				+ "where x.aut_user = ':user' and x.aut_group = '*'\n"
				+ "and (x.aut_status = '*' or x.aut_status = :status)\n"
				+ "and (x.aut_statnew = a.aut_statnew or x.aut_statnew = '*')\n"
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')\n"
				+ "and (a.aut_status = '*' or a.aut_status = :status)\n"
				+ "and (d.des_code = a.aut_statnew or a.aut_statnew = '*')\n" + ")\n" + ")\n"
				+ "and a.aut_rentity = 'EVNT' and a.aut_type = '+' and a.aut_statnew <> :status\n"
				+ "and e.ent_rentity = a.aut_rentity and e.ent_statent = d.des_rtype\n"
				+ "and d.des_rentity = 'UCOD' and d.des_lang = 'EN'\n" + "union\n"
				+ "select dd.des_code, dd.des_text, null uco_rcode, null uco_system\n"
				+ "from r5descriptions dd, r5entities ee\n" + "where dd.des_lang = 'EN' and dd.des_rentity = 'UCOD'\n"
				+ "and ee.ent_rentity = 'EVNT' and ee.ent_statent = dd.des_rtype\n" + "and dd.des_code = :status \n"
				+ "order by des_text", resultClass = WorkOrderStatus.class),

		@NamedNativeQuery(name = WorkOrderStatus.GET_STATUSES_FOR_NEW_WO, query = "select d.des_code, d.des_text, u.uco_rcode, u.uco_system\n"
				+ "from r5auth a,r5entities e, r5descriptions d RIGHT OUTER JOIN r5ucodes u on ( d.des_rtype = uco_rentity and d.des_code=u.uco_code and d.des_lang = 'EN' ) \n"
				+ "where a.aut_statnew not in(select uco_code from r5ucodes where uco_rentity=e.ent_statent and uco_rcode in('*','A','B','C')) \n"
				+ "and ((a.aut_user = :user and a.aut_group = '*' and a.aut_status = '-'\n"
				+ "and a.aut_statnew <> '*' and d.des_code = a.aut_statnew)\n"
				+ "or (a.aut_user = :user and a.aut_group = '*' and a.aut_status = '*' and a.aut_statnew <> '*'\n"
				+ "and d.des_code = a.aut_statnew\n" + "and not exists (select null from r5auth x\n"
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group\n"
				+ "and x.aut_status = '-' and x.aut_statnew = a.aut_statnew\n"
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')\n" + ")\n"
				+ "or (a.aut_user = :user and a.aut_group = '*' and a.aut_status = '-' and a.aut_statnew = '*'\n"
				+ "and not exists (select null from r5auth x\n"
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group\n"
				+ "and x.aut_status = a.aut_status and x.aut_statnew = d.des_code\n"
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')\n"
				+ ") or (a.aut_group = :group and a.aut_user = '*'\n" + "and not exists (select null from r5auth x\n"
				+ "where x.aut_user = :user and x.aut_group = '*'\n"
				+ "and (x.aut_status = '*' or x.aut_status = '-')\n"
				+ "and (x.aut_statnew = a.aut_statnew or x.aut_statnew = '*')\n"
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')\n"
				+ "and (a.aut_status = '*' or a.aut_status = '-')\n"
				+ "and (d.des_code = a.aut_statnew or a.aut_statnew = '*')\n"
				+ ")) and a.aut_rentity = 'EVNT' and a.aut_type = '+' and a.aut_statnew <> '-'\n"
				+ "and e.ent_rentity = a.aut_rentity and e.ent_statent = d.des_rtype\n"
				+ "and d.des_rentity = 'UCOD' and d.des_lang = 'EN'\n"
				+ "and not exists (select null from r5ucodes where uco_rentity = d.des_rtype\n"
				+ "and uco_code = des_code and uco_rcode in ('*','A','B','C'))\n"
				+ "AND ( EXISTS ( SELECT 'x' FROM r5install i WHERE i.ins_code = 'JTAUTH' AND UPPER( i.ins_desc ) NOT IN ( 'YES', '+' ))\n"
				+ "OR EXISTS ( SELECT 'x' from r5jobtypeauth j WHERE j.jta_status = d.des_code AND j.jta_jobtype = :jobType AND ( j.jta_group = '*' or  j.jta_group =:group )  and j.jta_insert = '+' )\n"
				+ "OR (EXISTS ( SELECT 'x' from r5jobtypeauth j WHERE j.jta_status = 'ALLSTATS' AND j.jta_jobtype = :jobType AND ( j.jta_group = '*' or  j.jta_group =:group)  and j.jta_insert = '+' ) AND NOT EXISTS (SELECT 'x'  FROM   r5jobtypeauth j  WHERE  j.jta_jobtype =  :jobType  AND j.jta_status = d.des_code AND (j.jta_group= '*' OR j.jta_group =:group))))\n"
				+ "union\n" + "select dd.des_code, dd.des_text, null uco_rcode, null uco_system\n"
				+ "from r5descriptions dd, r5entities ee\n" + "where dd.des_lang = 'EN' and dd.des_rentity = 'UCOD'\n"
				+ "and ee.ent_rentity = 'EVNT' and ee.ent_statent = dd.des_rtype\n" + "and dd.des_code = '-' \n"
				+ "order by des_text", resultClass = WorkOrderStatus.class) })
public class WorkOrderStatus implements Serializable {

	public final static String GET_STATUSES_FOR_EXISTING_WO = "WorkOrderStatus.GET_STATUSES_FOR_EXISTING_WO";
	public final static String GET_STATUSES_FOR_NEW_WO = "WorkOrderStatus.GET_STATUSES_FOR_NEW_WO";

	@Id
	@Column(name = "DES_CODE")
	private String code;
	@Column(name = "DES_TEXT")
	private String desc;
	@Column(name = "UCO_RCODE")
	private String rCode;
	@Column(name = "UCO_SYSTEM")
	private String system;

	public WorkOrderStatus() {
	}

	public WorkOrderStatus(String code, String desc) {
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
		return "WorkOrderStatus [" + (code != null ? "code=" + code + ", " : "")
				+ (desc != null ? "desc=" + desc + ", " : "") + (rCode != null ? "rCode=" + rCode + ", " : "")
				+ (system != null ? "system=" + system : "") + "]";
	}

	/**
	 * To Identify the default value
	 * 
	 * @return Defualt value in creation mode
	 */
	public boolean isDefaultValue() {
		return "R".equals(rCode) && "+".equals(system);
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
		if (!(obj instanceof WorkOrderStatus)) {
			return false;
		}
		WorkOrderStatus other = (WorkOrderStatus) obj;
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
