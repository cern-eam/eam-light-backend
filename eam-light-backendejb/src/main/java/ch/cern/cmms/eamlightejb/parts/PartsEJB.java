package ch.cern.cmms.eamlightejb.parts;

import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestResult;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    // Used to generate new code for parts
    //
    private static String GET_NEXT_PART_CODE =
        "select lpad(NVL(max(substr(par_code, - 6)),0) + 1, 6, '0') as next_obj_code from r5parts "
            + " where par_code like CONCAT(:prefixcode,'%') and regexp_like(par_code, CONCAT(:prefixcode,'[[:digit:]]{6}$')) ";

    public Optional<String> getNextAvailablePartCode(String prefixcode) {
        if (prefixcode == null || prefixcode.isEmpty()) {
            return Optional.ofNullable(null);
        }

        prefixcode = prefixcode.replaceAll("[^a-zA-Z_-]", "");
        if (prefixcode.isEmpty()) {
            return Optional.ofNullable(null);
        }

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

        if (newPartCode == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(prefixcode + newPartCode);
    }


    public Optional<String> getNextAvailablePartCodeGrid(String prefixCode, InforContext context) {
        GridRequest gridRequest = new GridRequest("SSPART");
        gridRequest.addFilter("partCode", prefixCode, "BEGINS");

        String newPartCode = null;

        try {
            GridRequestResult grd =
                inforClient.getGridsService().executeQuery(context,
                    gridRequest);

            Map<String, String> parts = inforClient.getTools().getGridTools().convertGridResultToMap("partCode",
                "partCode", grd);

            //This is the entry with highest value
            String entry = parts.values().stream().sorted(Comparator.reverseOrder()).findFirst().get();

            boolean testForLetters = false;

            String whatsLeft = entry.substring(prefixCode.length());
            for (int i = 0; i < whatsLeft.length(); i++) {
                if (!Character.isDigit(whatsLeft.charAt(i))) {
                    for (int j = entry.length() - 1; j >= 0; j--) {
                        if (!Character.isDigit(entry.charAt(j))) {
                            testForLetters = true;
                            break;
                        }
                    }
                }
            }
            if (!testForLetters) {

                if (!Character.isDigit(entry.charAt(entry.length() - 1))) {
                    newPartCode = prefixCode + "000001";
                } else {
                    for (int i = prefixCode.length() - 1; i >= 0; i--) {
                        if (!Character.isDigit(entry.charAt(i))) {
                            Integer newCode = Integer.parseInt(entry.substring(i + 1)) + 1;
                            newPartCode = entry.replace(entry.substring(i + 1), newCode.toString());
                            break;
                        }
                    }

                    return Optional.ofNullable(newPartCode);
                }
            }

        } catch (NoResultException exception) {
            logger.log(Level.ERROR, exception.getMessage());
        } catch (Exception exception) {
            logger.log(Level.ERROR, exception.getMessage());
        }

        if (newPartCode == null) {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(newPartCode);
    }


}
