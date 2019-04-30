package ch.cern.cmms.eamlightejb.workorders;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

@Entity
@NamedNativeQueries({
	
		@NamedNativeQuery(name = MyWorkOrder.GET_MY_OPEN_WOS, query = "SELECT a.EVT_CODE, b.UCO_DESC, a.EVT_OBJECT, a.EVT_DESC, a.EVT_MRC, a.EVT_RTYPE, a.EVT_TARGET, a.EVT_SCHEDEND, a.EVT_CREATED, a.EVT_COMPLETED, a.EVT_PRIORITY, a.EVT_STATUS, a.EVT_JOBTYPE  "
				+ "FROM R5EVENTS a, R5UCOdES b, R5PERSONNEL c, R5OBJECTS d  "
				+ "WHERE a.EVT_STATUS=b.UCO_CODE AND b.UCO_RENTITY='EVST' " + "AND a.EVT_PERSON = c.PER_CODE "
				+ "AND a.EVT_RSTATUS NOT IN('C','A') AND c.PER_USER = :user "
				+ "AND d.OBJ_CODE = a.EVT_OBJECT AND a.EVT_RTYPE in('JOB','PPM') AND rownum < 5001 order by EVT_TARGET", resultClass = MyWorkOrder.class),
		@NamedNativeQuery(name = MyWorkOrder.GET_MY_TEAMS_WOS, query = "SELECT * FROM (  "
				+ "SELECT a.EVT_CODE, b.UCO_DESC, a.EVT_OBJECT, a.EVT_DESC, a.EVT_MRC, a.EVT_RTYPE, a.EVT_TARGET, a.EVT_SCHEDEND, a.EVT_CREATED, a.EVT_COMPLETED, a.EVT_PRIORITY, a.EVT_STATUS, a.EVT_JOBTYPE, ROWNUM RNUM  "
				+ "FROM R5EVENTS a, R5UCODES b, R5USERS c     "
				+ "WHERE a.EVT_STATUS=b.UCO_CODE AND b.UCO_RENTITY='EVST'    "
				+ "AND a.EVT_MRC in (:departments) AND a.EVT_RSTATUS NOT IN('C','A') AND c.USR_CODE = :user AND a.EVT_RTYPE in('JOB','PPM')) WHERE rnum < 5001 order by EVT_TARGET", resultClass = MyWorkOrder.class),
		@NamedNativeQuery(name = MyWorkOrder.GET_WOS, query = "select EVT_CODE, EVT_DESC, EVT_OBJECT, EVT_MRC, EVT_RTYPE, null as UCO_DESC, EVT_TARGET, EVT_SCHEDEND,"
				+ " EVT_CREATED, EVT_COMPLETED from r5events where EVT_CODE like :codeParam AND EVT_RTYPE in('JOB','PPM')", resultClass = MyWorkOrder.class),
		@NamedNativeQuery(name = MyWorkOrder.GET_OBJWOS, query = "SELECT a.EVT_CODE, b.UCO_DESC, a.EVT_STATUS, a.EVT_JOBTYPE, a.EVT_OBJECT, a.EVT_DESC,"
				+ " a.EVT_MRC, a.EVT_RTYPE, a.EVT_TARGET, a.EVT_SCHEDEND, a.EVT_CREATED, a.EVT_COMPLETED, a.EVT_PRIORITY FROM"
				+ " R5EVENTS a, R5UCOdES b  WHERE (a.EVT_OBJECT = :objectCode) and a.EVT_RTYPE in('JOB','PPM') AND a.EVT_JOBTYPE != 'EDH' AND a.EVT_RSTATUS != 'A' "
				+ "AND a.EVT_STATUS=b.UCO_CODE AND b.UCO_RENTITY='EVST' order by EVT_CREATED DESC", resultClass = MyWorkOrder.class)
		})
public class MyWorkOrder implements Serializable {

	public static final String GET_MY_OPEN_WOS = "WorkOrder.GET_MY_OPEN_WOS";
	public static final String GET_MY_TEAMS_WOS = "WorkOrder.GET_MY_TEAMS_WOS";
	public static final String GET_WOS = "WorkOrder.GET_WOS";
	public static final String GET_OBJWOS = "WorkOrder.GET_OBJWOS";

	@Id
	@Column(name = "EVT_CODE")
	private String number;
	@Column(name = "EVT_DESC")
	private String desc;
	@Column(name = "UCO_DESC")
	private String status;
	@Column(name="EVT_STATUS")
	private String statusCode;
	@Column(name="EVT_JOBTYPE")
	private String jobType;
	@Column(name = "EVT_OBJECT")
	private String object;
	@Column(name = "EVT_MRC")
	private String mrc;
	@Column(name = "EVT_RTYPE")
	private String type;
	@Column(name = "EVT_PRIORITY")
	private String priority;
	@Column(name = "EVT_SCHEDEND")
	private Date schedulingEndDate;
	@Column(name = "EVT_TARGET")
	private Date schedulingStartDate;
	@Column(name = "EVT_CREATED")
	private Date createdDate;
	@Column(name = "EVT_COMPLETED")
	private Date completedDate;

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
}
