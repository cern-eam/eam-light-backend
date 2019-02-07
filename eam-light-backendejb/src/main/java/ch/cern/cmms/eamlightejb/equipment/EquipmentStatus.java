package ch.cern.cmms.eamlightejb.equipment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = EquipmentStatus.GET_STATUSES_FOR_EQUIPMENT_NEW, query = "select d.des_code, d.des_text "
				+ "from r5auth a, r5descriptions d, r5entities e "
				+ "where a.aut_statnew not in(select uco_code from r5ucodes where uco_rentity=e.ent_statent and uco_rcode in('*','B','CIR','CRR','D','T')) and ((a.aut_user = :puser and a.aut_group = '*' and a.aut_status = :poldstat "
				+ "and a.aut_statnew <> '*' and d.des_code = a.aut_statnew) "
				+ "or (a.aut_user = :puser and a.aut_group = '*' and a.aut_status = '*' and a.aut_statnew <> '*' "
				+ "and d.des_code = a.aut_statnew " + "and not exists (select null from r5auth x "
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group "
				+ "and x.aut_status = :poldstat and x.aut_statnew = a.aut_statnew "
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-') " + ")"
				+ "or (a.aut_user = :puser and a.aut_group = '*' and a.aut_status = :poldstat and a.aut_statnew = '*' "
				+ "and not exists (select null from r5auth x "
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group "
				+ "and x.aut_status = a.aut_status and x.aut_statnew = d.des_code "
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')" + ")"
				+ "or (a.aut_group = :porggroup and a.aut_user = '*' " + "and not exists (select null from r5auth x "
				+ "where x.aut_user = :puser and x.aut_group = '*' "
				+ "and (x.aut_status = '*' or x.aut_status = :poldstat) "
				+ "and (x.aut_statnew = a.aut_statnew or x.aut_statnew = '*') "
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-') "
				+ "and (a.aut_status = '*' or a.aut_status = :poldstat) "
				+ "and (d.des_code = a.aut_statnew or a.aut_statnew = '*') " + ")" + ") "
				+ "and a.aut_rentity = :pfunrentity and a.aut_type = '+' and a.aut_statnew <> :poldstat "
				+ "and e.ent_rentity = a.aut_rentity and e.ent_statent = d.des_rtype "
				+ "and d.des_rentity = 'UCOD' and d.des_lang = :planguage " + "union "
				+ "select dd.des_code, dd.des_text " + "from r5descriptions dd, r5entities ee "
				+ "where dd.des_lang = :planguage and dd.des_rentity = 'UCOD' "
				+ "and ee.ent_rentity = :pfunrentity and ee.ent_statent = dd.des_rtype "
				+ "and dd.des_code = :poldstat order by des_text", resultClass = EquipmentStatus.class),
		@NamedNativeQuery(name = EquipmentStatus.GET_STATUSES_FOR_EQUIPMENT_EXT, query = "select d.des_code, d.des_text "
				+ "from r5auth a, r5descriptions d, r5entities e "
				+ "where a.aut_statnew not in(select uco_code from r5ucodes where uco_rentity=e.ent_statent and uco_rcode in('*','A','B','C','CRR','CIR','T')) and ((a.aut_user = :puser and a.aut_group = '*' and a.aut_status = :poldstat "
				+ "and a.aut_statnew <> '*' and d.des_code = a.aut_statnew) "
				+ "or (a.aut_user = :puser and a.aut_group = '*' and a.aut_status = '*' and a.aut_statnew <> '*' "
				+ "and d.des_code = a.aut_statnew " + "and not exists (select null from r5auth x "
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group "
				+ "and x.aut_status = :poldstat and x.aut_statnew = a.aut_statnew "
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-') " + ")"
				+ "or (a.aut_user = :puser and a.aut_group = '*' and a.aut_status = :poldstat and a.aut_statnew = '*' "
				+ "and not exists (select null from r5auth x "
				+ "where x.aut_user = a.aut_user and x.aut_group = a.aut_group "
				+ "and x.aut_status = a.aut_status and x.aut_statnew = d.des_code "
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-')" + ")"
				+ "or (a.aut_group = :porggroup and a.aut_user = '*' " + "and not exists (select null from r5auth x "
				+ "where x.aut_user = :puser and x.aut_group = '*' "
				+ "and (x.aut_status = '*' or x.aut_status = :poldstat) "
				+ "and (x.aut_statnew = a.aut_statnew or x.aut_statnew = '*') "
				+ "and x.aut_rentity = a.aut_rentity and x.aut_type = '-') "
				+ "and (a.aut_status = '*' or a.aut_status = :poldstat) "
				+ "and (d.des_code = a.aut_statnew or a.aut_statnew = '*') " + ")" + ") "
				+ "and a.aut_rentity = :pfunrentity and a.aut_type = '+' and a.aut_statnew <> :poldstat "
				+ "and e.ent_rentity = a.aut_rentity and e.ent_statent = d.des_rtype "
				+ "and d.des_rentity = 'UCOD' and d.des_lang = :planguage " + "union "
				+ "select dd.des_code, dd.des_text " + "from r5descriptions dd, r5entities ee "
				+ "where dd.des_lang = :planguage and dd.des_rentity = 'UCOD' "
				+ "and ee.ent_rentity = :pfunrentity and ee.ent_statent = dd.des_rtype "
				+ "and dd.des_code = :poldstat order by des_text", resultClass = EquipmentStatus.class)})
public class EquipmentStatus {

	public final static String GET_STATUSES_FOR_EQUIPMENT_NEW = "EquipmentStatus.GET_STATUSES_FOR_EQUIPMENT_NEW";
	public final static String GET_STATUSES_FOR_EQUIPMENT_EXT = "EquipmentStatus.GET_STATUSES_FOR_EQUIPMENT_EXT";

	@Id
	@Column(name = "DES_CODE")
	private String code;
	@Column(name = "DES_TEXT")
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
