package ch.cern.cmms.eamlightejb.layout;

import javax.persistence.*;

@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = ElementInfo.GET_RECORD_VIEW_FIELDS, query = "select PLD_PAGENAME,  "
		+ "PLD_ELEMENTID,  "
		+ "CASE WHEN (PLD_XPATH IS NOT NULL) THEN replace('EAMID_' || PLD_XPATH,'\\','_')  ELSE replace('EAMID_' || PLD_ELEMENTID,'\\','_') END AS XPATH,  "
		+ "NVL(PLD_MAXLENGTH,120) as PLD_MAXLENGTH, PLD_CASE,   "
		+ "CASE WHEN (PLO_PRESENTINJSP = 'N') THEN 'H' ELSE PLO_ATTRIBUTE END AS PLO_ATTRIBUTE,  " + "PLO_USERGROUP,  "
		+ "CASE WHEN (SELECT UDF_DATETYPE FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity)='DATI' then 'datetime' else PLD_FIELDTYPE end as PLD_FIELDTYPE,  "
		+ "PLO_DEFAULTVALUE,  " + "PLD_DDFIELDNAME,  "
		+ "NVL((SELECT TRA_TEXT FROM U5TRANSLATIONS WHERE TRA_LANGUAGE = :lang and TRA_PAGENAME = :pageName and UPPER(TRA_ELEMENTID) = UPPER(PLD_ELEMENTID)),BOT_TEXT) BOT_TEXT,  "
		+ "NVL((SELECT UDF_LOOKUPTYPE FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity),'NONE') UDF_LOOKUPTYPE, "
		+ "NVL((SELECT UDF_LOOKUPRENTITY FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity),:entity) UDF_LOOKUPRENTITY, "
		+ "(SELECT UDF_UOM FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity) UDF_UOM "
		+ "from (r5defaultpagelayout join r5pagelayout on plo_elementid = PLD_ELEMENTID) left outer join r5boilertexts on LOWER(PLD_ELEMENTID) = LOWER(BOT_FLD1) and bot_function = :pageName where   "
		+ "PLD_PAGENAME = :masterPageName AND PLO_PAGENAME = :pageName AND PLO_USERGROUP = :userGroup ORDER BY PLD_ELEMENTID", resultClass = ElementInfo.class),
		@NamedNativeQuery(name = ElementInfo.GET_TAB_FIELDS, query = "select PLD_PAGENAME, " + "PLD_ELEMENTID, "
				+ "CASE WHEN (PLD_XPATH IS NOT NULL) THEN replace('EAMID_' || PLD_XPATH,'\\','_')  ELSE replace('EAMID_' || PLD_ELEMENTID,'\\','_') END AS XPATH, "
				+ "NVL(PLD_MAXLENGTH,120) as PLD_MAXLENGTH, PLD_CASE,  "
				+ "CASE WHEN (PLO_PRESENTINJSP = 'N') THEN 'H' ELSE PLO_ATTRIBUTE END AS PLO_ATTRIBUTE, "
				+ "PLO_USERGROUP, "
				+ "CASE WHEN (SELECT UDF_DATETYPE FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity)='DATI' then 'datetime' else PLD_FIELDTYPE end as PLD_FIELDTYPE, "
				+ "PLO_DEFAULTVALUE, " + "PLD_DDFIELDNAME, "
				+ "NVL((SELECT TRA_TEXT FROM U5TRANSLATIONS WHERE TRA_LANGUAGE = :lang and TRA_PAGENAME = :pageName and UPPER(TRA_ELEMENTID) = UPPER(PLD_ELEMENTID)),BOT_TEXT) BOT_TEXT, "
				+ "NVL((SELECT UDF_LOOKUPTYPE FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity),'NONE') UDF_LOOKUPTYPE, "
				+ "NVL((SELECT UDF_LOOKUPRENTITY FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity),:entity) UDF_LOOKUPRENTITY, "
				+ "(SELECT UDF_UOM FROM R5USERDEFINEDFIELDSETUP WHERE UDF_FIELD = PLD_ELEMENTID AND UDF_RENTITY = :entity) UDF_UOM "
				+ "from r5defaultpagelayout ,r5pagelayout,r5boilertexts  where "
				+ "plo_elementid = PLD_ELEMENTID and pld_elementtype = 'F' AND "
				+ "bot_function = :pageName and LOWER(PLD_ELEMENTID) = LOWER(substr(BOT_FLD1, 5)) and bot_fld1 like :tabName || '/_%' escape '/' and "
				+ "PLD_PAGENAME = :masterPageName || '_' || :tabName AND PLO_PAGENAME = :pageName  || '_' || :tabName AND PLO_USERGROUP = :userGroup ORDER BY PLD_ELEMENTID", resultClass = ElementInfo.class),
		@NamedNativeQuery(name = ElementInfo.GET_CUSTOMFIELDS, query = "SELECT PRO_CODE AS PLD_ELEMENTID, APR_CLASS AS PLD_PAGENAME, ('EAMID_' || PRO_CODE) AS XPATH, "
				+ "120 AS PLD_MAXLENGTH,'mixed' AS PLD_CASE,'O' AS PLO_ATTRIBUTE, 'R5' AS PLO_USERGROUP,  "
				+ "DECODE(PRO_TYPE,'CODE','text','CHAR','text','NUM','number','DATE','date','RENT','text','DATI','datetime') AS PLD_FIELDTYPE, "
				+ "NULL AS PLO_DEFAULTVALUE, "
				+ "NVL((SELECT TRA_TEXT FROM U5TRANSLATIONS WHERE TRA_LANGUAGE = :lang and TRA_PAGENAME = :classCode and UPPER(TRA_ELEMENTID) = UPPER(PRO_CODE)),PRO_TEXT) AS BOT_TEXT, "
				+ "null as UDF_LOOKUPTYPE, null as UDF_LOOKUPRENTITY, null as UDF_UOM "
				+ "FROM R5PROPERTIES, R5ADDPROPERTIES WHERE APR_RENTITY = :entity AND APR_CLASS = :classCode AND APR_PROPERTY = PRO_CODE", resultClass = ElementInfo.class),
		@NamedNativeQuery(name = ElementInfo.GET_UDS_FIELDS, query = "select distinct usf_screenname as PLD_PAGENAME, plo_elementid as PLD_elementid, BOT_FLD1 as XPATH, NVL(USF_MAXLENGTH,450) as PLD_MAXLENGTH, "
				+ "'mixed' as PLD_CASE, PLO_ATTRIBUTE, PLO_USERGROUP, USF_FIELDTYPE as PLD_FIELDTYPE, PLO_DEFAULTVALUE, BOT_TEXT, 'NONE' as UDF_LOOKUPTYPE, usf_screenname as UDF_LOOKUPRENTITY, null as UDF_UOM "
				+ "from R5UDFSCREENFIELDS join r5pagelayout on LOWER(plo_elementid) = LOWER('wspf_10_' || usf_fieldname) join R5BOILERTEXTS on LOWER(plo_elementid) = LOWER(BOT_FLD1) "
				+ "and bot_function = :pageName where usf_screenname = :masterPageName AND PLO_PAGENAME = :masterPageName AND PLO_USERGROUP = :userGroup  "
				+ "ORDER BY PLO_ELEMENTID", resultClass = ElementInfo.class) })
public class ElementInfo {

	public static final String GET_RECORD_VIEW_FIELDS = "ElementInfo.GET_RECORD_VIEW_FIELDS";
	public static final String GET_TAB_FIELDS = "ElementInfo.GET_TAB_FIELDS";
	public static final String GET_CUSTOMFIELDS = "ElementInfo.GET_CUSTOMFIELDS";
	public static final String GET_UDS_FIELDS = "ElementInfo.GET_UDS_FIELDS";

	public ElementInfo() {

	}

	public ElementInfo(String xpath, String attribute) {
		this.xpath = xpath;
		this.attribute = attribute;
	}

	public ElementInfo(String elementId, String pageName, String xpath, String maxLength, String characterCase,
			String attribute, String userGroup, String fieldType, String defaultValue, String text, boolean readonly) {
		this.elementId = elementId;
		this.pageName = pageName;
		this.xpath = xpath;
		this.maxLength = maxLength;
		this.characterCase = characterCase;
		this.attribute = attribute;
		this.userGroup = userGroup;
		this.fieldType = fieldType;
		this.defaultValue = defaultValue;
		this.text = text;
		this.readonly = readonly;
	}

	@Id
	@Column(name = "PLD_ELEMENTID")
	private String elementId;
	//
	@Column(name = "PLD_PAGENAME")
	private String pageName;
	//
	@Column(name = "XPATH")
	private String xpath;
	// For string only
	@Column(name = "PLD_MAXLENGTH")
	private String maxLength;
	// mixed, uppercase
	@Column(name = "PLD_CASE")
	private String characterCase;
	// H = Hidden, O = Optional, R = Required, S = System Required,
	@Column(name = "PLO_ATTRIBUTE")
	private String attribute;
	@Column(name = "PLO_USERGROUP")
	private String userGroup;
	// text, date, integer, number, button ...
	@Column(name = "PLD_FIELDTYPE")
	private String fieldType;
	//
	@Column(name = "PLO_DEFAULTVALUE")
	private String defaultValue;
	// Label
	@Column(name = "BOT_TEXT")
	private String text;
	// Lookup type for UDFs
	@Column(name = "UDF_LOOKUPTYPE")
	private String udfLookupType;
	// Lookup entity for UDFs
	@Column(name = "UDF_LOOKUPRENTITY")
	private String udfLookupEntity;
	// UOM from UDF
	@Column(name = "UDF_UOM")
	private String udfUom;
	@Transient
	private boolean readonly;
	@Transient
	private boolean notValid;

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getCharacterCase() {
		return characterCase;
	}

	public void setCharacterCase(String characterCase) {
		this.characterCase = characterCase;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	public boolean isReadonly() {
		return this.readonly || "P".equals(attribute);
	}

	public void setReadonly(boolean readOnly) {
		this.readonly = readOnly;
	}

	public boolean isNotValid() {
		return notValid;
	}

	public void setNotValid(boolean notValid) {
		this.notValid = notValid;
	}

	/**
	 * @return the udfLookupType
	 */
	public String getUdfLookupType() {
		return udfLookupType;
	}

	/**
	 * @param udfLookupType
	 *            the udfLookupType to set
	 */
	public void setUdfLookupType(String udfLookupType) {
		this.udfLookupType = udfLookupType;
	}

	/**
	 * @return the udfLookupEntity
	 */
	public String getUdfLookupEntity() {
		return udfLookupEntity;
	}

	/**
	 * @param udfLookupEntity
	 *            the udfLookupEntity to set
	 */
	public void setUdfLookupEntity(String udfLookupEntity) {
		this.udfLookupEntity = udfLookupEntity;
	}

	/**
	 * @return the udfUom
	 */
	public String getUdfUom() {
		return udfUom;
	}

	/**
	 * @param udfUom
	 *            the udfUom to set
	 */
	public void setUdfUom(String udfUom) {
		this.udfUom = udfUom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ElementInfo [" + (elementId != null ? "elementId=" + elementId + ", " : "")
				+ (pageName != null ? "pageName=" + pageName + ", " : "")
				+ (xpath != null ? "xpath=" + xpath + ", " : "")
				+ (maxLength != null ? "maxLength=" + maxLength + ", " : "")
				+ (characterCase != null ? "characterCase=" + characterCase + ", " : "")
				+ (attribute != null ? "attribute=" + attribute + ", " : "")
				+ (userGroup != null ? "userGroup=" + userGroup + ", " : "")
				+ (fieldType != null ? "fieldType=" + fieldType + ", " : "")
				+ (defaultValue != null ? "defaultValue=" + defaultValue + ", " : "")
				+ (text != null ? "text=" + text + ", " : "")
				+ (udfLookupType != null ? "udfLookupType=" + udfLookupType + ", " : "")
				+ (udfLookupEntity != null ? "udfLookupEntity=" + udfLookupEntity + ", " : "")
				+ (udfUom != null ? "udfUom=" + udfUom + ", " : "") + "readonly=" + readonly + ", notValid=" + notValid
				+ "]";
	}

}
