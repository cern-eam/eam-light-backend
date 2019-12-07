package ch.cern.cmms.eamlightweb.workorders;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import ch.cern.cmms.eamlightweb.workorders.myworkorders.MyWorkOrder;

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

}
