package ch.cern.cmms.eamlightweb.utilities;

import ch.cern.cmms.eamlightejb.equipment.EquipmentEJB;
import ch.cern.cmms.eamlightejb.index.IndexGrids;
import ch.cern.cmms.eamlightejb.index.IndexResult;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.entities.Entity;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static ch.cern.cmms.eamlightejb.data.ApplicationData.AUTOCOMPLETE_RESULT_SIZE;


@Stateless
@LocalBean
public class AutocompleteEntityResolver {

    @Inject
    private EquipmentEJB equipmentEJB;
    @Inject
    private IndexGrids indexGrids;

    private Map<String, BiFunction<String, InforContext, List<Entity>>> autocompleteEntityMap;

    @PostConstruct
    public void init() {
        autocompleteEntityMap = new HashMap<>();
        autocompleteEntityMap.put("OBJ", (code, inforContext) -> {
            try {
                return autocompleteObj(code, inforContext);
            } catch (InforException e) {
                throw new RuntimeException(e);
            }
        });
        autocompleteEntityMap.put("PART", (code, inforContext) -> {
            try {
                return autocompletePart(code, inforContext);
            } catch (InforException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Entity> autocomplete(String entityType, String code, InforContext inforContext){
        return autocompleteEntityMap.get(entityType).apply(code, inforContext);
    }

    private List<Entity> autocompleteObj(String code, InforContext inforContext) throws InforException {
        return equipmentEJB.getEquipmentSearchResults(code, null, inforContext);
    }

    private List<Entity> autocompletePart(String code, InforContext inforContext) throws InforException {
        List<IndexResult> indexResults = indexGrids.search(inforContext, code, Collections.singletonList("PART"));
        if (indexResults.size() > AUTOCOMPLETE_RESULT_SIZE) {
            indexResults = indexResults.subList(0, AUTOCOMPLETE_RESULT_SIZE - 1);
        }
        return indexResults.stream().map(r -> new Entity(r.getCode(), r.getDescription(), r.getOrganization())).collect(Collectors.toList());
    }
}
