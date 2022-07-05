package ch.cern.cmms.eamlightweb.workorders.myworkorders;

import ch.cern.eam.wshub.core.annotations.GridField;
import java.io.Serializable;
import java.util.Date;

public class MyWorkOrder implements Serializable {

	@GridField(name="workordernum", alternativeNames = { "eventno" })
	private String number;

	@GridField(name="description")
	private String desc;

	@GridField(name="workorderstatus_display", alternativeNames = { "statusdisplay" })
	private String status;

	@GridField(name="workorderstatus")
	private String statusCode;

	@GridField(name="jobtype")
	private String jobType;

	@GridField(name="equipment")
	private String object;

	@GridField(name="department")
	private String mrc;

	@GridField(name="workordertype_display")
	private String type;

	@GridField(name="priority_display")
	private String priority;

	@GridField(name="schedenddate")
	private Date schedulingEndDate;

	@GridField(name="schedstartdate")
	private Date schedulingStartDate;

	@GridField(name="datecreated")
	private Date createdDate;

	@GridField(name="datecompleted", alternativeNames={"completiondate"})
	private Date completedDate;

	@GridField(name="equipmenttype")
	private String equipmentType;

	public MyWorkOrder() {}

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

	public String getStatus() {
		return status;
	}

	public String getStatusCode () {
		return statusCode;
	}

	public void setStatusCode (String statusCode) {
		this.statusCode = statusCode;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getMrc() {
		return mrc;
	}

	public void setMrc(String mrc) {
		this.mrc = mrc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getSchedulingEndDate() {
		return schedulingEndDate;
	}

	public void setSchedulingEndDate(Date schedulingEndDate) {
		this.schedulingEndDate = schedulingEndDate;
	}

	public Date getSchedulingStartDate() {
		return schedulingStartDate;
	}

	public void setSchedulingStartDate(Date schedulingStartDate) {
		this.schedulingStartDate = schedulingStartDate;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the completedDate
	 */
	public Date getCompletedDate() {
		return completedDate;
	}

	/**
	 * @param completedDate
	 *            the completedDate to set
	 */
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	@Override
	public String toString() {
		return "MyWorkOrder{" +
				"completedDate=" + completedDate +
				", createdDate=" + createdDate +
				", desc='" + desc + '\'' +
				", jobType='" + jobType + '\'' +
				", mrc='" + mrc + '\'' +
				", number='" + number + '\'' +
				", object='" + object + '\'' +
				", priority='" + priority + '\'' +
				", schedulingEndDate=" + schedulingEndDate +
				", schedulingStartDate=" + schedulingStartDate +
				", status='" + status + '\'' +
				", statusCode='" + statusCode + '\'' +
				", type='" + type + '\'' +
				", equipmentType='" + equipmentType + '\'' +
				'}';
	}
}
