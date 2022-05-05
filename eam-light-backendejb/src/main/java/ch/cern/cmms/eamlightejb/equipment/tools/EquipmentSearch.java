package ch.cern.cmms.eamlightejb.equipment.tools;

import ch.cern.cmms.eamlightejb.index.IndexEJB;
import ch.cern.cmms.eamlightejb.index.IndexGrids;
import ch.cern.cmms.eamlightejb.index.IndexResult;
import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Credentials;
import ch.cern.eam.wshub.core.services.entities.Pair;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class EquipmentSearch {

    @Inject
    private InforClient inforClient;

    @Inject
    private IndexEJB indexEJB;

    @Inject
    private IndexGrids indexGrids;

    public List<Pair> getEquipmentSearchResults(String code, InforContext inforContext) throws InforException {
        List<IndexResult> indexResults;
        if (inforClient.getTools().isDatabaseConnectionConfigured()) {
            indexResults = indexEJB.getIndexResultsFaster(
                    code,
                    inforContext.getCredentials().getUsername(),
                    Arrays.asList("A", "P", "S", "L")
            );

        } else {
            indexResults = indexGrids.search(inforContext, code, Arrays.asList("A", "P", "S", "L"));
        }
        if (indexResults.size() > 10) {
            indexResults = indexResults.subList(0, 9);
        }
        return indexResults.stream().map(r -> new Pair(r.getCode(), r.getDescription())).collect(Collectors.toList());
    }
}
