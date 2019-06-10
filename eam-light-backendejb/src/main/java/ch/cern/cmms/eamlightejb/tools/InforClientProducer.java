package ch.cern.cmms.eamlightejb.tools;

import ch.cern.cmms.eamlightejb.tools.soaphandler.SOAPHandlerResolver;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.interceptors.InforInterceptor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
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
            e.printStackTrace();
        }

        inforClient = new InforClient.Builder(Tools.getVariableValue("EAMLIGHT_INFOR_WS_URL"), Tools.getVariableValue("EAMLIGHT_INFOR_TENANT"))
                .withDefaultOrganizationCode(Tools.getVariableValue("EAMLIGHT_INFOR_ORGANIZATION"))
                .withSOAPHandlerResolver(new SOAPHandlerResolver())
                .withDataSource(dataSource)
                .withEntityManagerFactory(entityManagerFactory)
                .withExecutorService(executorService)
                .withInforInterceptor(inforInterceptor)
                .withLogger(Logger.getLogger("wshublogger"))
                .build();
    }

}