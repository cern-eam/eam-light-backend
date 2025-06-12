package ch.cern.cmms.eamlightejb.tools;

import ch.cern.cmms.eamlightejb.cache.ExternalCache;
import ch.cern.cmms.eamlightejb.data.ApplicationData;
import ch.cern.cmms.eamlightejb.tools.soaphandler.SOAPHandlerResolver;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.interceptors.InforInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.logging.Logger;

@ApplicationScoped
public class InforClientProducer {

    @Produces
    private InforClient inforClient;
    @Resource
    private ManagedExecutorService executorService;

    @Inject
    private ApplicationData applicationData;

    @Inject
    private ExternalCache externalCache;

    @PostConstruct
    public void init() {
        EntityManagerFactory entityManagerFactory = null;
        DataSource dataSource = null;
        InforInterceptor inforInterceptor = null;

        try {
            Context context = new InitialContext();
            entityManagerFactory = (EntityManagerFactory) context.lookup("java:jboss/eamLightEntityManagerFactory");
            dataSource = (DataSource) context.lookup("java:/datasources/asbmgrDS");
            inforInterceptor = CDI.current().select(InforInterceptor.class).get();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            // Build the Infor Client
            inforClient = new InforClient.Builder(Tools.getVariableValue("EAMLIGHT_INFOR_WS_URL"))
                    .withDefaultTenant(Tools.getVariableValue("EAMLIGHT_INFOR_TENANT"))
                    .withDefaultOrganizationCode(Tools.getVariableValue("EAMLIGHT_INFOR_ORGANIZATION"))
                    .withSOAPHandlerResolver(new SOAPHandlerResolver())
                    .withDataSource(dataSource)
                    .withEntityManagerFactory(entityManagerFactory)
                    .withExecutorService(executorService)
                    .withInforInterceptor(inforInterceptor)
                    .withLogger(Logger.getLogger("wshublogger"))
                    .withCache(externalCache.getCacheMap())
                    .localizeResults(false)
                    .build();

            // Avoid 'Chunked transfer encoding is currently not supported' error which might be thrown by some web servers
            HTTPConduit conduit = (HTTPConduit)ClientProxy.getClient(inforClient.getInforWebServicesToolkitClient()).getConduit();
            // Should be only executed conditionally
            if (applicationData.trustAllCertificates()) {
                conduit.setTlsClientParameters(Tools.tlsClientParameters());
            }
            HTTPClientPolicy client = conduit.getClient();
            client.setAllowChunking(false);
        } catch (Exception exception) {
            System.out.println("Infor Client could not be initialized: " + exception.getMessage());
            exception.printStackTrace();
        }

    }

}