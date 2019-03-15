package ch.cern.cmms.eamlightejb.tools.entities;

import javax.persistence.*;

@Entity
@Table(name="U5APPSETTINGS")
@IdClass(PropertyPK.class)
@NamedQuery(name = Property.GET_PROPERTIES, query="select prop from Property prop where prop.application in ('*', :application) order by prop.application")
public class Property {

	public static final String GET_PROPERTIES = "Property.GET_PROPERTIES";
	@Id
	@Column(name="ASE_APPLICATION")
	private String application;
	@Id
	@Column(name="ASE_CODE")
	private String code;
	@Column(name="ASE_VALUE")
	private String value;
	
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
