package ch.cern.cmms.eamlightweb.equipment;

import ch.cern.eam.wshub.core.adapters.DateAdapter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class  EquipmentHistory  implements Serializable {

	private String number;
	private String desc;
	private String object;
	private String relatedObject;
	private Date completedDate;
	private String enteredBy;
	private String type;
	private String jobType;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getRelatedObject() {
		return relatedObject;
	}

	public void setRelatedObject(String relatedObject) {
		this.relatedObject = relatedObject;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	@Override
	public String toString() {
		return "EquipmentHistory{" +
				"number='" + number + '\'' +
				", desc='" + desc + '\'' +
				", object='" + object + '\'' +
				", relatedObject='" + relatedObject + '\'' +
				", completedDate=" + completedDate +
				", enteredBy='" + enteredBy + '\'' +
				", type='" + type + '\'' +
				", jobType='" + jobType + '\'' +
				'}';
	}
}
