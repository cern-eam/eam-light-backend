package ch.cern.cmms.eamlightweb.workorders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import ch.cern.cmms.eamlightejb.layout.ElementInfo;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrder;
import ch.cern.eam.wshub.core.services.workorders.entities.WorkOrder;

@Named
@ApplicationScoped
public class WorkOrderTools {

	public enum WorkOrderPriority {
		HIGH, MEDIUM, LOW
	};

	public static final List<String> C_STATUS_LIST = Arrays
			.asList(new String[] { "RP", "C", "TF", "RV", "CANC", "TO", "TT", "TX", "TP", "T", "REJ" });

	public WorkOrderPriority getWorkOrderPriority(MyWorkOrder workOrder) {
		if (workOrder.getPriority() == null) {
			return WorkOrderPriority.LOW;
		}
		switch (workOrder.getPriority()) {
		case "L":
			return WorkOrderPriority.LOW;
		case "M":
			return WorkOrderPriority.MEDIUM;
		case "H":
			return WorkOrderPriority.HIGH;
		default:
			return WorkOrderPriority.LOW;
		}
	}


	public static void assignDefaultValues(WorkOrder workOrder, Map<String, ElementInfo> fields,
			ApplicationData applicationData) {
		// Iterate over the list of fields
		for (String key : fields.keySet()) {
			// ElementInfo
			ElementInfo element = fields.get(key);
			// Skip this field if default value is null
			if (element.getDefaultValue() == null) {
				continue;
			}
			// Get default value
			String defaultValue = element.getDefaultValue().trim();
			// Switch according to the key
			switch (key) {
			case "description":
				workOrder.setDescription(defaultValue);
				break;
			case "equipment":
				workOrder.setEquipmentCode(defaultValue);
				break;
			case "location":
				workOrder.setLocationCode(defaultValue);
				break;
			case "department":
				workOrder.setDepartmentCode(defaultValue);
				break;
			case "workorderstatus":
				workOrder.setStatusCode(defaultValue);
				break;
			case "workordertype":
				workOrder.setTypeCode(defaultValue);
				break;
			case "reportedby":
				workOrder.setReportedBy(defaultValue);
				break;
			case "assignedto":
				workOrder.setAssignedTo(defaultValue);
				break;
			case "priority":
				workOrder.setPriorityCode(defaultValue);
				break;
			case "udfchar07":
				workOrder.getUserDefinedFields().setUdfchar07(defaultValue);
				break;
			case "woclass":
				workOrder.setClassCode(defaultValue);
				break;
			// TODO
				/*
			case "reqstartdate":
				try {
					workOrder.setRequestedStartDate(
							new SimpleDateFormat(applicationData.getDateTimeDbFormat()).parse(defaultValue));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
			case "reqenddate":
				try {
					workOrder.setRequestedEndDate(
							new SimpleDateFormat(applicationData.getDateTimeDbFormat()).parse(defaultValue));
				} catch (ParseException e) {
					// e.printStackTrace();
				}
				break;
			case "schedstartdate":
				try {
					workOrder.setScheduledStartDate(
							new SimpleDateFormat(applicationData.getDateTimeDbFormat()).parse(defaultValue));
				} catch (ParseException e) {
					// e.printStackTrace();
				}
				break;
			case "schedenddate":
				try {
					workOrder.setRequestedEndDate(
							new SimpleDateFormat(applicationData.getDateTimeDbFormat()).parse(defaultValue));
				} catch (ParseException e) {
					// e.printStackTrace();
				}
				break;
			case "datecompleted":
				try {
					workOrder.setCompletedDate(
							new SimpleDateFormat(applicationData.getDateTimeDbFormat()).parse(defaultValue));
				} catch (ParseException e) {
					// e.printStackTrace();
				}
				break;
				*/
			case "problemcode":
				workOrder.setProblemCode(defaultValue);
				break;
			case "failurecode":
				workOrder.setFailureCode(defaultValue);
				break;
			case "causecode":
				workOrder.setCauseCode(defaultValue);
				break;
			case "actioncode":
				workOrder.setActionCode(defaultValue);
				break;
			case "costcode":
				workOrder.setCostCode(defaultValue);
				break;
			case "standardwo":
				workOrder.setStandardWO(defaultValue);
				break;
			}
		}
	}

}
