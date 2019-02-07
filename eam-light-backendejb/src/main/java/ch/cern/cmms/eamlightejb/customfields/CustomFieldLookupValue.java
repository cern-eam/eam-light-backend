package ch.cern.cmms.eamlightejb.customfields;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = CustomFieldLookupValue.ENTITY_LOOKUP_VALUES, query = "SELECT DES_CODE CODE, NVL(DES_TEXT, DES_CODE) DESCRIPTION "
				+ "from r5descriptions d, r5entities "
				+ "WHERE NOT (:propEntity = 'OBJ' and d.des_code LIKE 'AUTO@E@%') " + "AND d.des_rentity=ent_rentity "
				+ "and ( (d.des_rentity=:propEntity and :propEntity NOT IN  ('CATG','LOC')) "
				+ "OR ( :propEntity = 'CATG' " + "AND  d.des_rentity = 'OBJ' " + "AND  d.des_rtype = 'C') "
				+ "OR ( :propEntity = 'LOC' " + "AND  d.des_rentity = 'OBJ' " + "AND  d.des_rtype = 'L'))"
				+ "AND (DES_CODE LIKE :code OR DES_TEXT LIKE :code)", resultClass = CustomFieldLookupValue.class),
		@NamedNativeQuery(name = CustomFieldLookupValue.CHAR_LOOKUP_VALUES, query = "select PRV_VALUE CODE, PRV_VALUE DESCRIPTION from r5propertyvalues where \n"
				+ "prv_property = :property AND " + "prv_code is null AND " + "COALESCE(prv_notused, '-') <> '+' AND "
				+ "((:lookupType = 'P' AND prv_rentity = '*') " + " OR"
				+ " (:lookupType = 'E' AND prv_rentity = :entity AND prv_class = '*') " + " OR"
				+ " (:lookupType = 'C' AND prv_rentity = :entity AND prv_class = :class) "
				+ ") order by PRV_SEQNO", resultClass = CustomFieldLookupValue.class),
		@NamedNativeQuery(name = CustomFieldLookupValue.NUMBER_LOOKUP_VALUES, query = "select PRV_NVALUE CODE, PRV_NVALUE DESCRIPTION from r5propertyvalues where \n"
				+ "prv_property = :property AND " + "prv_code is null AND " + "COALESCE(prv_notused, '-') <> '+' AND "
				+ "((:lookupType = 'P' AND prv_rentity = '*') " + " OR"
				+ " (:lookupType = 'E' AND prv_rentity = :entity AND prv_class = '*') " + " OR"
				+ " (:lookupType = 'C' AND prv_rentity = :entity AND prv_class = :class) "
				+ ") order by PRV_NVALUE", resultClass = CustomFieldLookupValue.class),
		@NamedNativeQuery(name = CustomFieldLookupValue.DATE_LOOKUP_VALUES, query = "select TO_CHAR(PRV_DVALUE, 'DD-MON-YYYY') CODE, TO_CHAR(PRV_DVALUE, 'DD-MON-YYYY') DESCRIPTION "
				+ " from r5propertyvalues where " + "prv_property = :property AND " + "prv_code is null AND "
				+ "COALESCE(prv_notused, '-') <> '+' AND " + "((:lookupType = 'P' AND prv_rentity = '*') " + " OR"
				+ " (:lookupType = 'E' AND prv_rentity = :entity AND prv_class = '*') " + " OR"
				+ " (:lookupType = 'C' AND prv_rentity = :entity AND prv_class = :class) "
				+ ") order by PRV_VALUE", resultClass = CustomFieldLookupValue.class),
		@NamedNativeQuery(name = CustomFieldLookupValue.DATETIME_LOOKUP_VALUES, query = "select TO_CHAR(PRV_DVALUE, 'DD-MON-YYYY HH24:MI') CODE, TO_CHAR(PRV_DVALUE, 'DD-MON-YYYY HH24:MI') DESCRIPTION "
				+ " from r5propertyvalues where " + "prv_property = :property AND " + "prv_code is null AND "
				+ "COALESCE(prv_notused, '-') <> '+' AND " + "((:lookupType = 'P' AND prv_rentity = '*') " + " OR"
				+ " (:lookupType = 'E' AND prv_rentity = :entity AND prv_class = '*') " + " OR"
				+ " (:lookupType = 'C' AND prv_rentity = :entity AND prv_class = :class) "
				+ ") order by PRV_VALUE", resultClass = CustomFieldLookupValue.class),
		@NamedNativeQuery(name = CustomFieldLookupValue.CODEDESC_LOOKUP_VALUES, query = "SELECT PVD_VALUE CODE,"
				+ "(NVL((SELECT TRA_TEXT FROM U5TRANSLATIONS WHERE TRA_LANGUAGE = :lang and TRA_PAGENAME = :property and UPPER(TRA_ELEMENTID) = UPPER(PVD_VALUE)),NVL(PVD_DESC, PVD_VALUE)))  DESCRIPTION "
				+ "from r5propertyvalues, r5pvdescriptions " + "where prv_property = :property "
				+ "and prv_code is null and pvd_property(+)=prv_property  "
				+ "and pvd_value(+)=prv_value AND COALESCE(prv_notused, '-') <> '+' AND "
				+ "((:lookupType = 'P' AND prv_rentity = '*') " + " OR"
				+ "(:lookupType = 'E' AND prv_rentity = :entity AND prv_class = '*') " + "OR"
				+ "(:lookupType = 'C' AND prv_rentity = :entity AND prv_class = :class) "
				+ ") order by PRV_SEQNO", resultClass = CustomFieldLookupValue.class) })
public class CustomFieldLookupValue implements Serializable {

	public static final String CODEDESC_LOOKUP_VALUES = "CustomFieldLookupValue.CODEDESC_LOOKUP_VALUES";
	public static final String CHAR_LOOKUP_VALUES = "CustomFieldLookupValue.CHAR_LOOKUP_VALUES";
	public static final String ENTITY_LOOKUP_VALUES = "CustomFieldLookupValue.ENTITY_LOOKUP_VALUES";
	public static final String DATE_LOOKUP_VALUES = "CustomFieldLookupValue.DATE_LOOKUP_VALUES";
	public static final String DATETIME_LOOKUP_VALUES = "CustomFieldLookupValue.DATETIME_LOOKUP_VALUES";
	public static final String NUMBER_LOOKUP_VALUES = "CustomFieldLookupValue.NUMBER_LOOKUP_VALUES";

	@Id
	@Column(name = "CODE")
	private String code;
	@Column(name = "DESCRIPTION")
	private String desc;

	@XmlElement(name="code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@XmlElement(name="desc")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
