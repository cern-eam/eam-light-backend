/**
 * 
 */
package ch.cern.cmms.eamlightejb.index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Result of index when the search is executed
 *
 */
@Entity
public class IndexResult {

	@Id
	@Column(name = "CODE")
	private String code;
	@Column(name = "ENTTYPE")
	private String type;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "MRC")
	private String mrc;
	@Column(name = "SERIAL")
	private String serial;
	@Column(name = "ALIAS")
	private String alias;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Calculates the link to go to the object
	 * 
	 * @return The link to navigate to the proper page in the system
	 */
	public String getLink() {
		switch (type) {
		case "A":/* Asset */
			return "asset/" + code;
		case "P":/* Position */
			return "position/" + code;
		case "S":/* System */
			return "system/" + code;
		case "PART":/* Part */
			return "part/" + code;
		default:/* WorkOrder */
			return "workorder/" + code;
		}
	}

	public String getMrc() {
		return mrc;
	}

	public void setMrc(String mrc) {
		this.mrc = mrc;
	}

	public String getTypeDesc() {
		switch (type) {
		case "JOB":
			return "Work Order";
		case "A":
			return "Asset";
		case "P":
			return "Position";
		case "S":
			return "System";
		case "PART":
			return "Part";
		default:
			return "Code";

		}
	}


	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * @param serial the serial to set
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IndexResult [" + (code != null ? "code=" + code + ", " : "")
				+ (type != null ? "type=" + type + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (mrc != null ? "mrc=" + mrc + ", " : "") + (serial != null ? "serial=" + serial + ", " : "")
				+ (alias != null ? "alias=" + alias : "") + "]";
	}
}
