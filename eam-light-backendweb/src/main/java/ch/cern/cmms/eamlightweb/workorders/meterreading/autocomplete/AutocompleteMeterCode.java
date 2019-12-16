package ch.cern.cmms.eamlightweb.workorders.meterreading.autocomplete;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ch.cern.cmms.eamlightweb.tools.EAMLightController;
import ch.cern.cmms.eamlightweb.tools.Pair;
import ch.cern.cmms.eamlightweb.tools.interceptors.RESTLoggingInterceptor;
import ch.cern.cmms.eamlightejb.meter.MeterEJB;
import ch.cern.cmms.eamlightejb.meter.MeterReadingEntity;

@Path("/autocomplete")
@ApplicationScoped
@Interceptors({ RESTLoggingInterceptor.class })
public class AutocompleteMeterCode extends EAMLightController {

	@EJB
	private MeterEJB meterEJB;

	@GET
	@Path("/meters/meter/{code}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response complete(@PathParam("code") String code) {
		// Result
		List<MeterReadingEntity> meters = meterEJB.getMeterReadingsByMeterCode(code.toUpperCase().trim() + "%");
		return ok(meters.stream().map(m -> new Pair(m.getMeterName(), m.getUomDesc())).collect(Collectors.toList()));
	}

}
