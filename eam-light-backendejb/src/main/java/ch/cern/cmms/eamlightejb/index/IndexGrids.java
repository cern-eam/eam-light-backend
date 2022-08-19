package ch.cern.cmms.eamlightejb.index;

import ch.cern.eam.wshub.core.client.InforClient;
import ch.cern.eam.wshub.core.client.InforContext;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequest;
import ch.cern.eam.wshub.core.services.grids.entities.GridRequestFilter;
import ch.cern.eam.wshub.core.tools.InforException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class IndexGrids {

    @Inject
    private InforClient inforClient;

    private List<IndexResult> searchWorkOrders(InforContext inforContext, String keyword, String operator) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("workordernum", "code");
            map.put("description", "description");
            map.put("department", "mrc");
            GridRequest gridRequest = new GridRequest(null, "WSJOBS", null);
            gridRequest.addFilter("workordernum", keyword, operator);
            List<IndexResult> result = inforClient.getTools().getGridTools().convertGridResultToObject(IndexResult.class,
                    map,
                    inforClient.getGridsService().executeQuery(inforContext, gridRequest));
            result.forEach(indexResult -> indexResult.setType("JOB"));
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    private List<IndexResult> searchEquipment(InforContext inforContext,String keyword, String operator, String gridId, String gridName, String dataspy, String type, boolean searchExtraColumns) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("equipmentno", "code");
            map.put("equipmentdesc", "description");
            map.put("department", "mrc");
            map.put("alias", "alias");
            map.put("serialnumber", "serial");
            GridRequest gridRequest = new GridRequest(gridId, gridName, dataspy);
            gridRequest.addFilter("equipmentno", keyword, operator, GridRequestFilter.JOINER.OR);

            if (searchExtraColumns) {
                gridRequest.addFilter("alias", keyword, operator, GridRequestFilter.JOINER.OR);
                gridRequest.addFilter("serialnumber", keyword, operator, GridRequestFilter.JOINER.OR);
            }

            //gridRequest.addFilter("udfchar45", keyword, operator, GridRequestFilter.JOINER.OR);
            List<IndexResult> result = inforClient.getTools().getGridTools().convertGridResultToObject(IndexResult.class,
                    map,
                    inforClient.getGridsService().executeQuery(inforContext, gridRequest));
            result.forEach(indexResult -> indexResult.setType(type));
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    private List<IndexResult> searchParts(InforContext inforContext, String keyword, String operator) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("partcode", "code");
            map.put("description", "description");
            GridRequest gridRequest = new GridRequest("80", "SSPART", "82");
            gridRequest.addFilter("partcode", keyword, operator);
            List<IndexResult> result = inforClient.getTools().getGridTools().convertGridResultToObject(IndexResult.class,
                    map,
                    inforClient.getGridsService().executeQuery(inforContext, gridRequest));
            result.forEach(indexResult -> indexResult.setType("PART"));
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    public List<IndexResult> search(InforContext inforContext, String keyword, List<String> entityTypes) throws InforException {
        List<IndexResult> result = new LinkedList<>();
        List<Runnable> runnables = new LinkedList<>();

        Map<String, Runnable> entityTypeRunnableMap = new HashMap();
        entityTypeRunnableMap.put("JOB", () -> result.addAll(searchWorkOrders(inforContext, keyword,"BEGINS")));
        entityTypeRunnableMap.put("A", () -> result.addAll(searchEquipment(inforContext, keyword,"BEGINS", "84", "OSOBJA", "85", "A", true)));
        entityTypeRunnableMap.put("P", () -> result.addAll(searchEquipment(inforContext, keyword,"BEGINS", "113", "OSOBJP", "111", "P", true)));
        entityTypeRunnableMap.put("S", () -> result.addAll(searchEquipment(inforContext, keyword,"BEGINS", "88", "OSOBJS", "89", "S", true)));
        entityTypeRunnableMap.put("L", () -> result.addAll(searchEquipment(inforContext, keyword,"BEGINS", "118", "OSOBJL", "117", "L", false)));
        entityTypeRunnableMap.put("PART", () -> result.addAll(searchParts(inforContext, keyword, "BEGINS")));

        entityTypeRunnableMap.entrySet().stream()
                .filter(entry -> entityTypes.contains(entry.getKey()))
                .forEach(entry -> runnables.add(entry.getValue()));

        inforClient.getTools().processRunnables(runnables);
        return result;
    }

    public IndexResult searchSingleResult(InforContext inforContext, String keyword) throws InforException {
        List<IndexResult> result = new LinkedList<>();
        List<Runnable> runnables = new LinkedList<>();

        runnables.add(() -> result.addAll(searchWorkOrders(inforContext, keyword,"EQUALS")));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS", "84", "OSOBJA", "85", "A", true)));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS", "113", "OSOBJP", "111", "P", true)));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS", "88", "OSOBJS", "89", "S", true)));
        runnables.add(() -> result.addAll(searchEquipment(inforContext, keyword,"EQUALS", "118", "OSOBJL", "117", "L", false)));
        runnables.add(() -> result.addAll(searchParts(inforContext, keyword, "EQUALS")));

        inforClient.getTools().processRunnables(runnables);

        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

}
