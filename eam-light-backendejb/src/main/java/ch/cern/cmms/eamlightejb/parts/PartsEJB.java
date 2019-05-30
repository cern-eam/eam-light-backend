package ch.cern.cmms.eamlightejb.parts;

import java.util.List;
import java.util.Optional;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import ch.cern.eam.wshub.core.client.InforClient;
import org.jboss.logging.Logger.Level;

import ch.cern.cmms.eamlightejb.tools.LoggingService;

/**
 * Session Bean implementation class PartsEJB
 */
@Stateless
@LocalBean
public class PartsEJB {

	@Inject
	private InforClient inforClient;

    @Inject
    private LoggingService logger;

	//
	// PartStock
	//
	public List<PartStock> getPartStock(String code, String user) {
		return inforClient.getTools().getEntityManager().createNamedQuery(PartStock.STOCK_PARTSTOCK, PartStock.class).setParameter("part_code", code)
				.setParameter("eamUser", user).getResultList();
	}
	
	//
	// Used to generate new code for parts
	//
	private static String GET_NEXT_PART_CODE = "select lpad(NVL(max(substr(par_code, - 6)),0) + 1, 6, '0') as next_obj_code from r5parts "
											 +" where par_code like CONCAT(:prefixcode,'%') and regexp_like(par_code, CONCAT(:prefixcode,'[[:digit:]]{6}$')) ";
	
	public Optional<String> getNextAvailablePartCode(String prefixcode) {
		if (prefixcode == null || prefixcode.isEmpty()) 
			return Optional.ofNullable(null);
		
		prefixcode = prefixcode.replaceAll("[^a-zA-Z_-]","");  
		if (prefixcode.isEmpty()) 
			return Optional.ofNullable(null);
		
		String newPartCode = null; 
        try {
        	newPartCode = (String) inforClient.getTools().getEntityManager().createNativeQuery(GET_NEXT_PART_CODE)
                    .setParameter("prefixcode", prefixcode)
                    .getSingleResult();
        } catch (NoResultException exception) {
            // nothing to do in this case
            logger.log(Level.ERROR, exception.getMessage());
        } catch (Exception exception) {
            logger.log(Level.ERROR, exception.getMessage());
        }  
        
		if (newPartCode == null) 
			return Optional.ofNullable(null);
        
        return Optional.ofNullable(prefixcode+newPartCode);
	}


}
