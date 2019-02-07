package ch.cern.cmms.eamlightweb.tools.interceptors;

import ch.cern.cmms.eamlightejb.tools.LoggingService;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class RESTLoggingInterceptor {

	@Inject
	private LoggingService loggingService;

	@AroundInvoke
	public Object log(InvocationContext ic) throws Exception {
		// Length of parameters
		int length = ic.getParameters().length;
		// Initial time of execution
		long initialTime = new Date().getTime();
		// Proceed to the next step
		Object proceed = null;
		// Indicator of error
		String error = null;
		try {
			// Execute operation
			proceed = ic.proceed();
		} catch (Exception e) {/* Error */
			// Error
			error = e.getMessage();
			// Throw the exception
			throw e;
		} finally {
			// Calculate total time of execution
			long totalTime = new Date().getTime() - initialTime;
			// Message to print
			String message = "REST Operation: [" + ic.getMethod().getName() + "] [Time(" + totalTime + ")] [Request: "
					+ Arrays.asList(Arrays.copyOfRange(ic.getParameters(), 0, length));
			if (proceed instanceof javax.ws.rs.core.Response) {
				message += "] [Response: " + ((javax.ws.rs.core.Response) proceed).getEntity() + "]";
			}
			// If error, attach
			if (error != null)
				message += "  ERROR [" + error + "]";
			// Print log
			loggingService.log(Logger.Level.INFO, message);
		}
		return proceed;
	}

}
