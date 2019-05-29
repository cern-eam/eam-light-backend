package ch.cern.cmms.eamlightejb.workorders;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

public class MyWorkOrder implements Serializable {


	private String number;
	private String desc;
	private String status;
	private String statusCode;
	private String jobType;
	private String object;
	private String mrc;
	private String type;
	private String priority;
	private Date schedulingEndDate;
	private Date schedulingStartDate;
	private Date createdDate;
	private Date completedDate;

	public MyWorkOrder() {}

	public MyWorkOrder(String number, String desc, String object, String status, String department, Date schedulingStartDate, Date schedulingEndDate) {
		this.number = number;
		this.desc = desc;
		this.object = object;
		this.status = status;
		this.mrc = department;
		this.schedulingStartDate = schedulingStartDate;
		this.schedulingEndDate = schedulingEndDate;
	}

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

	public String getDays() {
		if (schedulingEndDate == null) {
			return null;
		}

		Date now = new Date();
		double days = (schedulingEndDate.getTime() - now.getTime()) / (1000.0 * 60.0 * 60.0);

		if (days >= -24 && days <= 0) {
			return "TODAY";
		}

		if (days < -24) {
			return "LATE";
		}

		if (days >= 0 && days < 144) {
			return "WEEK";
		}

		return "ALL";
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
				'}';
	}
}
