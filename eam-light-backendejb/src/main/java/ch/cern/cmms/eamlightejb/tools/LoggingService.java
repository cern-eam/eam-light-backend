package ch.cern.cmms.eamlightejb.tools;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger.Level;

@ApplicationScoped
public class LoggingService {

	private org.jboss.logging.Logger logger;
	
	@PostConstruct
	private void init() {
		logger = org.jboss.logging.Logger.getLogger("wshublogger");
	}
	
	public void log(String message) {
		this.log(Level.INFO, message);
	}
	
	public void log(Level logLevel, String message) {
		if (logger.isEnabled(logLevel)) {
			logger.log(logLevel, message);
		}
	}
	
}
