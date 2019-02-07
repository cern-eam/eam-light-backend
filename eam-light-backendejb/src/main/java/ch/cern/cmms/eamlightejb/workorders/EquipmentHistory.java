package ch.cern.cmms.eamlightejb.workorders;

import ch.cern.eam.wshub.core.adapters.DateAdapter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = EquipmentHistory.GET_OBJHISTORY, query = "SELECT EVT_CODE, EVT_OBJECT, DESCRIPTION, " +
				"RELATED_OBJECT, EVT_COMPLETED, EVT_ENTEREDBY, EVT_TYPE, EVT_JOBTYPE FROM CERN_VW_WO_HISTORY " +
				"WHERE evt_object =  :objectCode ", resultClass = EquipmentHistory.class)})
public class  EquipmentHistory  implements Serializable {

	public static final String GET_OBJHISTORY = "EquipmentHistory.GET_OBJHISTORY";

	@Id
	@Column(name = "EVT_CODE")
	private String number;
	@Column(name = "DESCRIPTION")
	private String desc;
	@Column(name = "EVT_OBJECT")
	private String object;
	@Column(name = "RELATED_OBJECT")
	private String relatedObject;
	@Column(name = "EVT_COMPLETED")
	private Date completedDate;
	@Column(name = "EVT_ENTEREDBY")
	private String enteredBy;
	@Column(name = "EVT_TYPE")
	private String type;
	@Column(name = "EVT_JOBTYPE")
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
